package com.example.rakhesly.ui.cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Order;
import com.example.rakhesly.data.repo.OrderManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;

public class checkoutfragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        // Get cart items and totals from arguments
        if (getArguments() != null) {
            cartItems = getArguments().getParcelableArrayList("cart_items");
            subtotal = getArguments().getDouble("subtotal", 0.0);
            deliveryFee = getArguments().getDouble("delivery_fee", 0.0);
        }

        if (cartItems == null || cartItems.isEmpty()) {
            requireActivity().onBackPressed();
            return;
        }

        // Initialize views
        subtotalText = view.findViewById(R.id.subtotalText);
        deliveryFeeText = view.findViewById(R.id.deliveryFeeText);
        totalText = view.findViewById(R.id.totalText);
        deliveryAddressInput = view.findViewById(R.id.deliveryAddressInput);
        notesInput = view.findViewById(R.id.notesInput);
        paymentMethodGroup = view.findViewById(R.id.paymentMethodGroup);
        confirmButton = view.findViewById(R.id.confirmButton);
        loadingView = view.findViewById(R.id.loadingView);

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
        // Clear the cart from Firebase
        db.collection("carts")
            .document(userId)
            .collection("items")
            .get()
            .addOnSuccessListener(documents -> {
                if (documents.isEmpty()) {
                    // Also clear the local cart manager
                    com.example.rakhesly.data.repo.CartManager.getInstance().clearCart();
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
                                // Clear the local cart manager after Firebase is cleared
                                com.example.rakhesly.data.repo.CartManager.getInstance().clearCart();
                                onComplete.run();
                            }
                        });
                }
            })
            .addOnFailureListener(e -> {
                // Even if Firebase fails, clear the local cart
                com.example.rakhesly.data.repo.CartManager.getInstance().clearCart();
                onComplete.run();
            });
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
        // Navigate to order confirmation fragment
        androidx.navigation.fragment.NavHostFragment.findNavController(this)
            .navigate(R.id.orderConfirmationFragment);
    }
}
