package com.example.rakhesly.ui.tracking;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Order;
import com.example.rakhesly.data.repo.OrderManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TrackingFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private TextView textOrderStatus;
    private TextView textEstimatedTime;
    private Handler handler;
    private Random random;
    private LatLng deliveryLocation;
    private LatLng currentLocation;
    private LatLng destinationLocation;
    private List<LatLng> routePoints;
    private int currentRouteIndex = 0;
    private Order currentOrder;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);

        // Initialize views
        MaterialCardView cardDeliveryStatus = view.findViewById(R.id.cardDeliveryStatus);
        textOrderStatus = view.findViewById(R.id.textOrderStatus);
        textEstimatedTime = view.findViewById(R.id.textEstimatedTime);
        mapView = view.findViewById(R.id.mapView);
        
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
            mapView.getMapAsync(this);
        }
        
        handler = new Handler(Looper.getMainLooper());
        random = new Random();
        
        // Initialize with default values
        textOrderStatus.setText("Order in Progress");
        textEstimatedTime.setText("Estimated delivery: 25 mins");
        
        // Get the most recent order from OrderManager
        currentOrder = OrderManager.getInstance().getMostRecentOrder();
        if (currentOrder != null) {
            // Update UI with order details if available
            textOrderStatus.setText("Order #" + currentOrder.getId() + " in Progress");
        }
        
        return view;
    }
    
    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        
        // Set up initial map locations
        // Restaurant location in Beirut, Lebanon
        deliveryLocation = new LatLng(33.8938, 35.5018);
        
        // Get destination from the current order's delivery address
        if (currentOrder != null && currentOrder.getDeliveryAddress() != null) {
            // Check if the order already has coordinates
            if (currentOrder.getDeliveryAddressLat() != 0 && currentOrder.getDeliveryAddressLng() != 0) {
                // Use the stored coordinates
                destinationLocation = new LatLng(currentOrder.getDeliveryAddressLat(), currentOrder.getDeliveryAddressLng());
                Toast.makeText(requireContext(), "Using stored coordinates for delivery", Toast.LENGTH_SHORT).show();
            } else {
                // Convert address to coordinates using Geocoder
                geocodeAddress(currentOrder.getDeliveryAddress());
            }
        } else {
            // Fallback to default coordinates if no order is available
            destinationLocation = new LatLng(33.888997, 35.473330);
            Toast.makeText(requireContext(), "Using default coordinates: 33.888997, 35.473330", Toast.LENGTH_SHORT).show();
        }
        
        // Generate a route between the points
        generateRoute();
        
        // Add markers
        googleMap.addMarker(new MarkerOptions()
                .position(deliveryLocation)
                .title("Restaurant")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // Add destination marker with the actual delivery address
        String destinationTitle = "Your Location";
        if (currentOrder != null && currentOrder.getDeliveryAddress() != null) {
            destinationTitle = currentOrder.getDeliveryAddress();
        }
        googleMap.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .title(destinationTitle));
                
        // Draw the route
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints)
                .width(5)
                .color(getResources().getColor(R.color.primary));
        googleMap.addPolyline(polylineOptions);
        
        // Start at the beginning of the route
        currentLocation = routePoints.get(0);
        updateDeliveryMarker();
        
        // Move camera to show both points
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(deliveryLocation, 14));
        
        // Start simulating movement
        startDeliverySimulation();
    }
    
    private void generateRoute() {
        // In a real app, this would use the Google Directions API
        // For this demo, we'll create a simple route with some points
        routePoints = new ArrayList<>();
        
        // Add starting point
        routePoints.add(deliveryLocation);
        
        // Generate intermediate points based on the start and end locations
        generateIntermediatePoints(deliveryLocation, destinationLocation);
        
        // Add destination
        routePoints.add(destinationLocation);
    }
    
    private void updateDeliveryMarker() {
        googleMap.clear();
        
        // Re-add the markers and route
        googleMap.addMarker(new MarkerOptions()
                .position(deliveryLocation)
                .title("Restaurant")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        // Add destination marker with the actual delivery address
        String destinationTitle = "Your Location";
        if (currentOrder != null && currentOrder.getDeliveryAddress() != null) {
            destinationTitle = currentOrder.getDeliveryAddress();
        }
        googleMap.addMarker(new MarkerOptions()
                .position(destinationLocation)
                .title(destinationTitle));
                
        // Add delivery person marker
        googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title("Delivery Person")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                
        // Draw the route
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(routePoints)
                .width(5)
                .color(getResources().getColor(R.color.primary));
        googleMap.addPolyline(polylineOptions);
        
        // Update the camera to follow the delivery person
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }
    
    private void startDeliverySimulation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentRouteIndex < routePoints.size() - 1) {
                    // Move to the next point in the route
                    currentRouteIndex++;
                    currentLocation = routePoints.get(currentRouteIndex);
                    updateDeliveryMarker();
                    
                    // Update the estimated time
                    int remainingMinutes = 25 - (currentRouteIndex * 5);
                    if (remainingMinutes < 0) remainingMinutes = 0;
                    textEstimatedTime.setText("Estimated delivery: " + remainingMinutes + " mins");
                    
                    // Continue the simulation
                    startDeliverySimulation();
                } else {
                    // Reached destination
                    textOrderStatus.setText("Order Delivered");
                    textEstimatedTime.setText("Delivered");
                }
            }
        }, 3000); // Move every 3 seconds
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }
    
    @Override
    public void onPause() {
        if (mapView != null) {
            mapView.onPause();
        }
        super.onPause();
    }
    
    @Override
    public void onDestroy() {
        if (mapView != null) {
            mapView.onDestroy();
        }
        // Remove any pending callbacks
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }
    
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }
    
    /**
     * This method is no longer used since we're using fixed coordinates.
     * Kept for reference only.
     */
    private LatLng getCoordinatesFromAddress(String address) {
        // Always return our fixed coordinates
        return new LatLng(33.888997, 35.473330);
    }
    
    /**
     * Generates intermediate points between the start and end locations.
     */
    private void generateIntermediatePoints(LatLng start, LatLng end) {
        // Calculate the direction vector
        double dLat = (end.latitude - start.latitude) / 5;
        double dLng = (end.longitude - start.longitude) / 5;
        
        // Add 4 intermediate points (we already added the start point)
        for (int i = 1; i <= 4; i++) {
            // Add some randomness to make the route look more natural
            double jitter = 0.0005 * (Math.random() - 0.5);
            
            LatLng point = new LatLng(
                start.latitude + dLat * i + jitter,
                start.longitude + dLng * i + jitter
            );
            
            routePoints.add(point);
        }
    }
    
    /**
     * Converts a string address to coordinates using Geocoder
     */
    private void geocodeAddress(String address) {
        // Add location context to improve geocoding accuracy
        // Assuming the app is used in Lebanon
        if (!address.toLowerCase().contains("lebanon")) {
            address += ", Lebanon";
        }
        
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                
                // Update the destination location
                destinationLocation = new LatLng(lat, lng);
                
                // If we have a current order, update its coordinates
                if (currentOrder != null) {
                    currentOrder.setDeliveryAddressLat(lat);
                    currentOrder.setDeliveryAddressLng(lng);
                }
                
                Toast.makeText(requireContext(), "Delivery to: " + lat + ", " + lng, Toast.LENGTH_SHORT).show();
            } else {
                // Fallback to default coordinates if geocoding fails
                destinationLocation = new LatLng(33.888997, 35.473330);
                Toast.makeText(requireContext(), "Geocoding failed, using default location", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // Handle the exception
            destinationLocation = new LatLng(33.888997, 35.473330);
            Toast.makeText(requireContext(), "Error geocoding address: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
