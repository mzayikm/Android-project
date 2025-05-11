package com.example.rakhesly.data.model;

import java.util.Map;

public class Product {
    private String id;
    private String name;
    private String description;
    private String category;
    private String imageUrl;
    private String brand;
    private String unit; // e.g., kg, piece, pack
    private double weight;
    private Map<String, Double> supermarketPrices; // Map of supermarket IDs to prices
    private boolean isAvailable;
    private int stockQuantity;
    private int imageResource; // Added for local drawable resources
    private double price; // Added for local price

    public Product() {
        // Required empty constructor for Firebase
    }

    public Product(String id, String name, String description, String category,
                  String imageUrl, String brand, String unit, double weight,
                  Map<String, Double> supermarketPrices) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.unit = unit;
        this.weight = weight;
        this.supermarketPrices = supermarketPrices;
        this.isAvailable = true;
        this.stockQuantity = 0;
    }

    public Product(String id, String name, String unit, double price, int imageResource) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
        this.imageResource = imageResource;
        this.isAvailable = true;
        this.stockQuantity = 10;
    }

    // Get price for specific supermarket
    public double getPriceForSupermarket(String supermarketId) {
        return supermarketPrices.getOrDefault(supermarketId, 0.0);
    }

    // Update price for specific supermarket
    public void updatePrice(String supermarketId, double price) {
        supermarketPrices.put(supermarketId, price);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public Map<String, Double> getSupermarketPrices() { return supermarketPrices; }
    public void setSupermarketPrices(Map<String, Double> supermarketPrices) { 
        this.supermarketPrices = supermarketPrices; 
    }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public double getPrice() {
        if (price > 0) {
            return price;
        }
        // Return the minimum price across all supermarkets, or 0 if no prices are available
        if (supermarketPrices == null || supermarketPrices.isEmpty()) {
            return 0.0;
        }
        return supermarketPrices.values().stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }
}