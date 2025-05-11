package com.example.rakhesly.data.model;

import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart {
    private String id;
    private String userId;
    private List<CartItem> items;
    private String selectedSupermarketId;
    private double totalAmount;
    private double deliveryFee;
    private LatLng deliveryLocation;
    private String status; // ACTIVE, PROCESSING, DELIVERED, CANCELLED

    public Cart() {
        // Required empty constructor for Firebase
        items = new ArrayList<>();
    }

    public Cart(String id, String userId, LatLng deliveryLocation) {
        this.id = id;
        this.userId = userId;
        this.items = new ArrayList<>();
        this.deliveryLocation = deliveryLocation;
        this.status = "ACTIVE";
        this.totalAmount = 0.0;
        this.deliveryFee = 0.0;
    }

    // Add item to cart
    public void addItem(CartItem item) {
        // Check if item from same supermarket exists
        for (CartItem existingItem : items) {
            if (existingItem.getProductId().equals(item.getProductId()) &&
                existingItem.getSupermarketId().equals(item.getSupermarketId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                updateTotalAmount();
                return;
            }
        }
        items.add(item);
        updateTotalAmount();
    }

    // Remove item from cart
    public void removeItem(String itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
        updateTotalAmount();
    }

    // Update item quantity
    public void updateItemQuantity(String itemId, int quantity) {
        for (CartItem item : items) {
            if (item.getId().equals(itemId)) {
                item.setQuantity(quantity);
                updateTotalAmount();
                break;
            }
        }
    }

    // Calculate total amount for items
    private void updateTotalAmount() {
        totalAmount = 0.0;
        for (CartItem item : items) {
            totalAmount += item.getTotalPrice();
        }
    }

    // Compare prices across supermarkets
    public Map<String, Double> comparePrices(List<Supermarket> supermarkets) {
        Map<String, Double> supermarketTotals = new HashMap<>();

        for (Supermarket supermarket : supermarkets) {
            double total = 0.0;
            boolean allItemsAvailable = true;

            // Calculate total for each item if available in this supermarket
            for (CartItem item : items) {
                Product product = getProduct(item.getProductId()); // You'll need to implement this
                if (product != null) {
                    double price = product.getPriceForSupermarket(supermarket.getId());
                    if (price > 0) {
                        total += price * item.getQuantity();
                    } else {
                        allItemsAvailable = false;
                        break;
                    }
                }
            }

            // Only add supermarket if all items are available
            if (allItemsAvailable) {
                // Add delivery fee
                total += supermarket.calculateDeliveryFee(deliveryLocation);
                supermarketTotals.put(supermarket.getId(), total);
            }
        }

        return supermarketTotals;
    }

    // Select supermarket for order
    public void selectSupermarket(String supermarketId, double deliveryFee) {
        this.selectedSupermarketId = supermarketId;
        this.deliveryFee = deliveryFee;
    }

    // Get product details (this should be implemented based on your data source)
    private Product getProduct(String productId) {
        // TODO: Implement product retrieval from database or cache
        return null;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { 
        this.items = items;
        updateTotalAmount();
    }

    public String getSelectedSupermarketId() { return selectedSupermarketId; }
    public void setSelectedSupermarketId(String selectedSupermarketId) { 
        this.selectedSupermarketId = selectedSupermarketId; 
    }

    public double getTotalAmount() { return totalAmount; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public LatLng getDeliveryLocation() { return deliveryLocation; }
    public void setDeliveryLocation(LatLng deliveryLocation) { this.deliveryLocation = deliveryLocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    // Get final total including delivery fee
    public double getFinalTotal() {
        return totalAmount + deliveryFee;
    }
} 