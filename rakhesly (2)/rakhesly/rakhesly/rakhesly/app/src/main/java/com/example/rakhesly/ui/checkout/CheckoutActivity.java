package com.example.rakhesly.ui.checkout;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Order;
import com.example.rakhesly.data.repo.OrderManager;
import com.example.rakhesly.ui.main.MainActivity;
import com.example.rakhesly.ui.tracking.OrderTrackingActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private TextInputEditText deliveryAddressInput;
    private TextInputEditText notesInput;
    private RadioGroup paymentMethodGroup;
    private MaterialButton confirmButton;
    private View loadingView;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;
    private ArrayList<CartItem> cartItems;
    private double subtotal;
    private double deliveryFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        // Get cart items and totals from intent
        cartItems = getIntent().getParcelableArrayListExtra("cart_items");
        subtotal = getIntent().getDoubleExtra("subtotal", 0.0);
        deliveryFee = getIntent().getDoubleExtra("delivery_fee", 0.0);

        if (cartItems == null || cartItems.isEmpty()) {
            finish();
            return;
        }

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        subtotalText = findViewById(R.id.subtotalText);
        deliveryFeeText = findViewById(R.id.deliveryFeeText);
        totalText = findViewById(R.id.totalText);
        deliveryAddressInput = findViewById(R.id.deliveryAddressInput);
        notesInput = findViewById(R.id.notesInput);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        confirmButton = findViewById(R.id.confirmButton);
        loadingView = findViewById(R.id.loadingView);

        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Load user's default address
        loadUserAddress();

        // Update price displays
        updatePriceDisplays();

        // Setup confirm button
        confirmButton.setOnClickListener(v -> confirmOrder());
    }

    private void loadUserAddress() {
        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(document -> {
                String address = document.getString("address");
                if (address != null && !address.isEmpty()) {
                    deliveryAddressInput.setText(address);
                }
            });
    }

    private void updatePriceDisplays() {
        subtotalText.setText(String.format("$%.2f", subtotal));
        deliveryFeeText.setText(String.format("$%.2f", deliveryFee));
        totalText.setText(String.format("$%.2f", subtotal + deliveryFee));
    }

    private void confirmOrder() {
        String deliveryAddress = deliveryAddressInput.getText().toString().trim();
        if (deliveryAddress.isEmpty()) {
            deliveryAddressInput.setError("Delivery address is required");
            return;
        }

        showLoading(true);

        // Create the order object
        Order order = new Order();
        order.setUserId(userId);
        order.setItems(cartItems);
        order.setSubtotal(subtotal);
        order.setDeliveryFee(deliveryFee);
        order.setTotal(subtotal + deliveryFee);
        order.setDeliveryAddress(deliveryAddress);
        order.setNotes(notesInput.getText().toString().trim());
        order.setPaymentMethod(getSelectedPaymentMethod());
        order.setStatus(Order.Status.PENDING);
        order.setCreatedAt(new Date());
        
        // Add order locally
        OrderManager.getInstance().addOrder(order);

        // Clear cart and show order success
        clearCart(() -> {
            showLoading(false);
            showOrderSuccess(order);
        });
    }

    private void clearCart(Runnable onComplete) {
        db.collection("carts")
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener(documents -> {
                if (documents.isEmpty()) {
                    onComplete.run();
                    return;
                }

                int[] completedDeletes = {0};
                int totalDeletes = documents.size();

                for (com.google.firebase.firestore.QueryDocumentSnapshot doc : documents) {
                    doc.getReference().delete()
                        .addOnCompleteListener(task -> {
                            completedDeletes[0]++;
                            if (completedDeletes[0] == totalDeletes) {
                                onComplete.run();
                            }
                        });
                }
            })
            .addOnFailureListener(e -> onComplete.run());
    }

    private Order.PaymentMethod getSelectedPaymentMethod() {
        return paymentMethodGroup.getCheckedRadioButtonId() == R.id.radioCreditCard
            ? Order.PaymentMethod.CREDIT_CARD
            : Order.PaymentMethod.CASH_ON_DELIVERY;
    }

    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        confirmButton.setEnabled(!show);
    }

    private void showOrderSuccess(Order order) {
        // Hide checkout form
        findViewById(R.id.deliveryAddressInput).setEnabled(false);
        findViewById(R.id.notesInput).setEnabled(false);
        findViewById(R.id.paymentMethodGroup).setEnabled(false);
        confirmButton.setVisibility(View.GONE);

        // Show success layout
        View orderSuccessLayout = findViewById(R.id.orderSuccessLayout);
        TextView orderIdText = findViewById(R.id.orderIdText);
        TextView totalAmountText = findViewById(R.id.totalAmountText);
        MaterialButton trackOrderButton = findViewById(R.id.trackOrderButton);

        orderIdText.setText("Order ID: " + order.getId());
        totalAmountText.setText(String.format("Total Amount: $%.2f", order.getTotal()));
        orderSuccessLayout.setVisibility(View.VISIBLE);

        trackOrderButton.setOnClickListener(v -> {
            // Navigate to tracking activity with order ID
            Intent intent = new Intent(this, OrderTrackingActivity.class);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        });
    }
    

}