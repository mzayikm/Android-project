package com.example.rakhesly.data.repo;

import com.example.rakhesly.data.model.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Manages orders locally within the app session.
 */
public class OrderManager {
    private static final OrderManager instance = new OrderManager();
    private final List<Order> orders;

    private OrderManager() {
        orders = new ArrayList<>();
    }

    public static OrderManager getInstance() {
        return instance;
    }

    /**
     * Returns a copy of current orders.
     */
    public List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    /**
     * Retrieves an order by its ID, or null if not found.
     */
    public Order getOrderById(String id) {
        for (Order o : orders) {
            if (o.getId().equals(id)) {
                return o;
            }
        }
        return null;
    }
    
    /**
     * Retrieves the most recent order based on creation date, or null if no orders exist.
     */
    public Order getMostRecentOrder() {
        if (orders.isEmpty()) {
            return null;
        }
        
        // Sort by creation date in descending order (newest first)
        return orders.stream()
                .filter(order -> order.getCreatedAt() != null)
                .max(Comparator.comparing(Order::getCreatedAt))
                .orElse(orders.get(orders.size() - 1)); // Fallback to last added if dates are missing
    }

    /**
     * Adds a new order, assigning a UUID if no ID is set.
     */
    public void addOrder(Order order) {
        if (order.getId() == null || order.getId().isEmpty()) {
            order.setId(UUID.randomUUID().toString());
        }
        if (order.getCreatedAt() == null) {
            order.setCreatedAt(new Date());
        }
        orders.add(order);
    }

    /**
     * Clears all stored orders.
     */
    public void clearOrders() {
        orders.clear();
    }
}
