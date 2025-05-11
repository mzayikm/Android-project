package com.example.rakhesly.data.repo;

import com.example.rakhesly.data.model.CartItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CartManager {
    // Singleton instance — created once and reused throughout the app session
    private static final CartManager instance = new CartManager();
    private final List<CartItem> cartItems;
    private Runnable onCartChangedListener;

    // Private constructor
    private CartManager() {
        cartItems = new ArrayList<>();
    }

    // Public method to get the singleton instance
    public static CartManager getInstance() {
        return instance;
    }

    // Get a copy of current cart items
    public List<CartItem> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    // Set cart change listener
    public void setOnCartChangedListener(Runnable listener) {
        this.onCartChangedListener = listener;
    }

    // Notify cart changes
    private void notifyCartChanged() {
        if (onCartChangedListener != null) {
            onCartChangedListener.run();
        }
    }

    // Add an item to the cart, or update quantity if it exists
    public void addItem(CartItem item) {
        for (CartItem existing : cartItems) {
            if (existing.getProductId().equals(item.getProductId())) {
                existing.setQuantity(existing.getQuantity() + item.getQuantity());
                notifyCartChanged();
                return;
            }
        }
        cartItems.add(item);
        notifyCartChanged();
    }

    // Update item quantity or remove if zero
    public void updateItemQuantity(CartItem item, int quantity) {
        Iterator<CartItem> iterator = cartItems.iterator();
        while (iterator.hasNext()) {
            CartItem existing = iterator.next();
            if (existing.getProductId().equals(item.getProductId())) {
                if (quantity <= 0) {
                    iterator.remove();
                } else {
                    existing.setQuantity(quantity);
                }
                notifyCartChanged();
                return;
            }
        }
    }

    // Remove a specific item
    public void removeItem(CartItem item) {
        boolean removed = cartItems.removeIf(existing -> existing.getProductId().equals(item.getProductId()));
        if (removed) {
            notifyCartChanged();
        }
    }

    // Clear the entire cart
    public void clearCart() {
        if (!cartItems.isEmpty()) {
            cartItems.clear();
            notifyCartChanged();
        }
    }
}
