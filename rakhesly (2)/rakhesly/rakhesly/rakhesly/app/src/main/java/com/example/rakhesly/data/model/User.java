package com.example.rakhesly.data.model;

public class User {
    private String id;
    private String email;
    private String name;
    private String phone;
    private String address;
    private UserType userType;
    private double rating;
    private boolean isAvailable; // For drivers only

    public enum UserType {
        CUSTOMER,
        DRIVER
    }

    public User() {
        // Required empty constructor for Firebase
    }

    public User(String id, String email, String name, String phone, String address, UserType userType) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.userType = userType;
        this.rating = 5.0; // Default rating
        this.isAvailable = userType == UserType.DRIVER; // Drivers start as available
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}