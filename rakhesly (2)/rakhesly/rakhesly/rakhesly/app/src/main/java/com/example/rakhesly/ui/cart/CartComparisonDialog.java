package com.example.rakhesly.ui.cart;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.data.repo.CartManager;
import com.example.rakhesly.data.repo.ProductRepo;
import com.example.rakhesly.data.repo.SupermarketRepo;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartComparisonDialog extends DialogFragment {

    private static final String ARG_CART_ITEMS = "cart_items";
    private static final String ARG_CURRENT_SUPERMARKET_ID = "current_supermarket_id";

    private List<CartItem> cartItems;
    private String currentSupermarketId;
    private String suggestedSupermarketId;
    private double currentCartTotal;
    private double suggestedCartTotal;
    private Supermarket currentSupermarket;
    private Supermarket suggestedSupermarket;

    private OnCartSelectionListener listener;

    public interface OnCartSelectionListener {
        void onKeepCurrentCart();
        void onSwitchToSuggestedCart(String supermarketId, List<CartItem> updatedCartItems);
    }

    public static CartComparisonDialog newInstance(List<CartItem> cartItems, String currentSupermarketId) {
        CartComparisonDialog fragment = new CartComparisonDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_CART_ITEMS, new ArrayList<>(cartItems));
        args.putString(ARG_CURRENT_SUPERMARKET_ID, currentSupermarketId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cartItems = getArguments().getParcelableArrayList(ARG_CART_ITEMS);
            currentSupermarketId = getArguments().getString(ARG_CURRENT_SUPERMARKET_ID);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnCartSelectionListener) {
            listener = (OnCartSelectionListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnCartSelectionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_cart_comparison, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup UI references
        TextView currentSupermarketName = view.findViewById(R.id.currentSupermarketName);
        TextView currentCartTotal = view.findViewById(R.id.currentCartTotal);
        TextView suggestedSupermarketName = view.findViewById(R.id.suggestedSupermarketName);
        TextView suggestedCartTotal = view.findViewById(R.id.suggestedCartTotal);
        TextView savingsText = view.findViewById(R.id.savingsText);
        MaterialButton keepCurrentCartButton = view.findViewById(R.id.keepCurrentCartButton);
        MaterialButton switchToSuggestedButton = view.findViewById(R.id.switchToSuggestedButton);

        // Set initial placeholder values
        currentSupermarketName.setText("Current Supermarket");
        currentCartTotal.setText("Total: $0.00");
        suggestedSupermarketName.setText("Suggested Supermarket");
        suggestedCartTotal.setText("Total: $0.00");
        savingsText.setText("You save: $0.00");

        // Set button listeners
        keepCurrentCartButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onKeepCurrentCart();
            }
            dismiss();
        });

        switchToSuggestedButton.setOnClickListener(v -> {
            if (listener != null && suggestedSupermarketId != null) {
                // Create updated cart items with prices from the suggested supermarket
                List<CartItem> updatedCartItems = updateCartItemsForSupermarket(suggestedSupermarketId);
                listener.onSwitchToSuggestedCart(suggestedSupermarketId, updatedCartItems);
            }
            dismiss();
        });

        // Find the best alternative supermarket
        findBestAlternativeSupermarket();
    }

    private void findBestAlternativeSupermarket() {
        // Calculate current cart total first
        currentCartTotal = calculateCartTotalForCurrentItems();
        
        // Get all supermarkets
        SupermarketRepo supermarketRepo = new SupermarketRepo();
        ProductRepo productRepo = new ProductRepo();

        supermarketRepo.getSupermarkets(new SupermarketRepo.SupermarketCallback() {
            @Override
            public void onSuccess(List<Supermarket> supermarkets) {
                // Calculate total for each supermarket
                Map<String, Double> supermarketTotals = new HashMap<>();
                Map<String, Supermarket> supermarketMap = new HashMap<>();

                for (Supermarket supermarket : supermarkets) {
                    supermarketMap.put(supermarket.getId(), supermarket);
                    if (!supermarket.getId().equals(currentSupermarketId)) {
                        double total = calculateCartTotalForSupermarket(supermarket.getId());
                        supermarketTotals.put(supermarket.getId(), total);
                    }
                }

                // Find the supermarket with the lowest total
                String lowestPriceSupermarketId = null;
                double lowestTotal = Double.MAX_VALUE;

                for (Map.Entry<String, Double> entry : supermarketTotals.entrySet()) {
                    if (entry.getValue() < lowestTotal) {
                        lowestTotal = entry.getValue();
                        lowestPriceSupermarketId = entry.getKey();
                    }
                }

                // Set the current and suggested supermarkets
                currentSupermarket = supermarketMap.get(currentSupermarketId);
                suggestedSupermarket = supermarketMap.get(lowestPriceSupermarketId);
                suggestedSupermarketId = lowestPriceSupermarketId;

                // Calculate totals
                currentCartTotal = calculateCartTotalForSupermarket(currentSupermarketId);
                suggestedCartTotal = lowestTotal;

                // Update UI
                updateUI();
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
                if (getActivity() != null) {
                    dismiss();
                }
            }
        });
    }

    private double calculateCartTotalForCurrentItems() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        return total;
    }

    private double calculateCartTotalForSupermarket(String supermarketId) {
        double total = 0.0;
        ProductRepo productRepo = new ProductRepo();

        for (CartItem item : cartItems) {
            // Get the product
            String productId = item.getProductId();
            int quantity = item.getQuantity();

            // Calculate price for this supermarket
            double price = getProductPriceForSupermarket(productId, supermarketId);
            total += price * quantity;
        }

        return total;
    }

    private double getProductPriceForSupermarket(String productId, String supermarketId) {
        // This is a simplified implementation
        // In a real app, you would query the database or API to get the price
        // For now, we'll use a random variation of the current price
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                if (supermarketId.equals(currentSupermarketId)) {
                    return item.getPrice();
                } else {
                    // Simulate different prices at different supermarkets
                    // In a real app, you would get the actual price from the database
                    double variation = 0.0;
                    if (supermarketId.equals("spinneys")) {
                        variation = 1.2; // 20% more expensive
                    } else if (supermarketId.equals("carrefour")) {
                        variation = 0.9; // 10% cheaper
                    } else if (supermarketId.equals("charcutier")) {
                        variation = 1.1; // 10% more expensive
                    } else {
                        variation = 0.95; // 5% cheaper by default
                    }
                    return item.getPrice() * variation;
                }
            }
        }
        return 0.0;
    }

    private List<CartItem> updateCartItemsForSupermarket(String supermarketId) {
        List<CartItem> updatedItems = new ArrayList<>();

        for (CartItem item : cartItems) {
            // Create a copy of the item
            CartItem updatedItem = new CartItem(
                    item.getId(),
                    item.getProductId(),
                    supermarketId,
                    item.getQuantity(),
                    getProductPriceForSupermarket(item.getProductId(), supermarketId),
                    item.getProductName(),
                    item.getProductImage()
            );
            updatedItems.add(updatedItem);
        }

        return updatedItems;
    }

    private void updateUI() {
        if (getView() == null) return;

        TextView currentSupermarketName = getView().findViewById(R.id.currentSupermarketName);
        TextView currentCartTotal = getView().findViewById(R.id.currentCartTotal);
        TextView suggestedSupermarketName = getView().findViewById(R.id.suggestedSupermarketName);
        TextView suggestedCartTotal = getView().findViewById(R.id.suggestedCartTotal);
        TextView savingsText = getView().findViewById(R.id.savingsText);

        if (currentSupermarket != null) {
            currentSupermarketName.setText(currentSupermarket.getName());
            currentCartTotal.setText(String.format("Total: $%.2f", this.currentCartTotal));
        }

        if (suggestedSupermarket != null) {
            suggestedSupermarketName.setText(suggestedSupermarket.getName());
            suggestedCartTotal.setText(String.format("Total: $%.2f", this.suggestedCartTotal));
            double savings = this.currentCartTotal - this.suggestedCartTotal;
            savingsText.setText(String.format("You save: $%.2f", savings));
        }
    }
}
