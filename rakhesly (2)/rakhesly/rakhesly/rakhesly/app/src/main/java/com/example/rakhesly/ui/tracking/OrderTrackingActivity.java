package com.example.rakhesly.ui.tracking;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import com.example.rakhesly.data.model.Order;
import com.example.rakhesly.data.repo.OrderManager;
import com.example.rakhesly.data.repo.SupermarketRepo;
import com.example.rakhesly.data.model.Supermarket;

import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Geocoder;
import android.location.Address;
import java.io.IOException;
import java.util.List;

import com.example.rakhesly.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import android.graphics.Color;

public class OrderTrackingActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker deliveryMarker;
    private LatLng[] path;
    private int pathIndex;
    private SupportMapFragment mapFragment;
    private LatLng origin, destination;
    private Handler handler = new Handler();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private Order currentOrder; // Store the current order

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking);

        TextView idText = findViewById(R.id.orderIdText);
        TextView dateText = findViewById(R.id.orderDateText);
        TextView addressText = findViewById(R.id.deliveryAddressText);
        View mapCard = findViewById(R.id.mapCard);

        String orderId = getIntent().getStringExtra("orderId");
        idText.setText("Order #" + orderId);
        Order order = OrderManager.getInstance().getOrderById(orderId);
        if (order != null) {
            Date created = order.getCreatedAt();
            if (created != null) {
                String d = new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(created);
                dateText.setText("Placed on " + d);
            }
            addressText.setText("Delivery to: " + order.getDeliveryAddress());
        }
        mapCard.setVisibility(View.VISIBLE);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        initLocations(order);
    }

    private void initLocations(Order order) {
        if (order == null) {
            Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Store the current order for later use
        currentOrder = order;
        
        // Get the delivery address string
        String deliveryAddress = order.getDeliveryAddress();
        if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
            Toast.makeText(this, "Delivery address is missing", Toast.LENGTH_LONG).show();
            return;
        }
        
        // Add location context to improve geocoding accuracy
        // Assuming the app is used in Lebanon
        if (!deliveryAddress.toLowerCase().contains("lebanon")) {
            deliveryAddress += ", Lebanon";
        }
        
        // Use Geocoder to convert address to coordinates
        geocodeAddressWithFallbacks(deliveryAddress);
    }

    private void tryStartMap() {
        if (origin != null && destination != null && mapFragment != null) {
            path = new LatLng[]{ origin, destination };
            pathIndex = 0;
            mapFragment.getMapAsync(this);
        } else if (origin != null && mapFragment != null) {
            // If we have origin but no destination, use our specified coordinates
            // This is a fallback to prevent crashes
            destination = new LatLng(33.888997, 35.473330); // Specified coordinates
            path = new LatLng[]{ origin, destination };
            pathIndex = 0;
            mapFragment.getMapAsync(this);
            
            // Show a message to the user
            Toast.makeText(this, "Could not find the exact delivery location on the map", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            deliveryMarker = mMap.addMarker(new MarkerOptions().position(path[0]).title("On the way"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(path[0], 15));
            mMap.addPolyline(new PolylineOptions().add(path[0], path[path.length - 1]).width(8).color(Color.BLUE));
            simulateMovement();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void simulateMovement() {
        // Create more interpolation points for smoother animation
        final int STEPS = 100;
        final LatLng[] smoothPath = new LatLng[STEPS];
        
        // Linear interpolation between start and end points
        for (int i = 0; i < STEPS; i++) {
            double ratio = (double) i / (STEPS - 1);
            double lat = path[0].latitude + ratio * (path[1].latitude - path[0].latitude);
            double lng = path[0].longitude + ratio * (path[1].longitude - path[0].longitude);
            smoothPath[i] = new LatLng(lat, lng);
        }
        
        // Add markers for start and destination
        mMap.addMarker(new MarkerOptions()
                .position(path[0])
                .title("Supermarket")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                
        mMap.addMarker(new MarkerOptions()
                .position(path[1])
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        
        // Replace default marker with car icon
        if (deliveryMarker != null) {
            deliveryMarker.remove();
        }
        
        deliveryMarker = mMap.addMarker(new MarkerOptions()
                .position(path[0])
                .title("Delivery Vehicle")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .flat(true)
                .anchor(0.5f, 0.5f));
        
        // Animate car along the route
        pathIndex = 0;
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (pathIndex < smoothPath.length) {
                    // Calculate bearing for car rotation
                    float bearing = 0;
                    if (pathIndex < smoothPath.length - 1) {
                        bearing = getBearing(smoothPath[pathIndex], smoothPath[pathIndex + 1]);
                    }
                    
                    // Update car position and rotation
                    deliveryMarker.setPosition(smoothPath[pathIndex]);
                    deliveryMarker.setRotation(bearing);
                    
                    // Follow the car with camera
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(smoothPath[pathIndex]));
                    
                    pathIndex++;
                    handler.postDelayed(this, 1000); // Faster animation (5 updates per second)
                }
            }
        });
    }
    
    /**
     * Calculates the bearing between two points
     * @return The bearing in degrees
     */
    private float getBearing(LatLng start, LatLng end) {
        double startLat = Math.toRadians(start.latitude);
        double startLng = Math.toRadians(start.longitude);
        double endLat = Math.toRadians(end.latitude);
        double endLng = Math.toRadians(end.longitude);
        
        double dLng = endLng - startLng;
        
        double y = Math.sin(dLng) * Math.cos(endLat);
        double x = Math.cos(startLat) * Math.sin(endLat) - 
                   Math.sin(startLat) * Math.cos(endLat) * Math.cos(dLng);
        
        double bearing = Math.atan2(y, x);
        bearing = Math.toDegrees(bearing);
        bearing = (bearing + 360) % 360;
        
        return (float) bearing;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE
            && grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                onMapReady(mMap);
            }
        }
    }
    
    /**
     * Sets the destination to the specified coordinates regardless of the address
     * @param address The delivery address (not used for geocoding anymore)
     */
    private void geocodeAddressWithFallbacks(String address) {
        // Use the specific coordinates provided (33.888997, 35.473330)
        destination = new LatLng(33.888997, 35.473330);
        
        // Debug message to show we're using fixed coordinates
        Toast.makeText(this, "Using fixed location: 33.888997, 35.473330", Toast.LENGTH_SHORT).show();
        
        // Continue with getting the origin location
        getOriginAndStartMap();
    }
    
    /**
     * Gets the origin location from the supermarket and starts the map
     */
    private void getOriginAndStartMap() {
        if (currentOrder != null && currentOrder.getItems() != null && !currentOrder.getItems().isEmpty()) {
            String smId = currentOrder.getItems().get(0).getSupermarketId();
            new SupermarketRepo().getSupermarket(smId, new SupermarketRepo.SupermarketCallback() {
                @Override
                public void onSuccess(List<Supermarket> result) {
                    if (result != null && !result.isEmpty()) {
                        origin = result.get(0).getLocation();
                    } else {
                        // Default origin in Beirut if supermarket not found
                        origin = new LatLng(33.8938, 35.5018);
                    }
                    tryStartMap();
                }
                @Override
                public void onError(String errorMessage) {
                    // Default origin in Beirut if error
                    origin = new LatLng(33.8938, 35.5018);
                    tryStartMap();
                }
            });
        } else {
            // Default origin in Beirut if no items
            origin = new LatLng(33.8938, 35.5018);
            tryStartMap();
        }
    }
}