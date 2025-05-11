package com.example.rakhesly.data.model;

public class Category {
    private String id;
    private String name;
    private String imageUrl;
    private String supermarketId;
    private int productCount;

    private int imageResource; // Added for local drawable resources

    public Category() {
        // Required empty constructor for Firebase
    }

    public Category(String id, String name, String imageUrl, String supermarketId, int productCount) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.supermarketId = supermarketId;
        this.productCount = productCount;
    }

    public Category(String id, String name, int imageResource) {
        this.id = id;
        this.name = name;
        this.imageResource = imageResource;
        this.productCount = 0;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSupermarketId() {
        return supermarketId;
    }

    public void setSupermarketId(String supermarketId) {
        this.supermarketId = supermarketId;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}