package com.example.rakhesly.data.model;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

public class Supermarket {
    private String id;
    private String name;
    private String description;
    private String address;
    private LatLng location;
    private String phone;
    private String imageUrl;      // logo URL
    private String bannerImageUrl; // banner image URL
    private String website;
    private double rating;
    private boolean isOpen;
    private List<String> operatingHours;
    private double baseDeliveryFee;
    private double pricePerKm;
    private double minimumOrderAmount;
    private int prepTime = 15; // Default prep time in minutes

    public Supermarket() {
        // Required empty constructor for Firebase
    }

    public Supermarket(String id,
                       String name,
                       String address,
                       LatLng location,
                       String phone,
                       String imageUrl,
                       String bannerImageUrl,
                       String website,
                       List<String> operatingHours,
                       double baseDeliveryFee,
                       double pricePerKm,
                       double minimumOrderAmount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.location = location;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.bannerImageUrl = bannerImageUrl;
        this.website = website;
        this.rating = 5.0;
        this.isOpen = true;
        this.operatingHours = operatingHours;
        this.baseDeliveryFee = baseDeliveryFee;
        this.pricePerKm = pricePerKm;
        this.minimumOrderAmount = minimumOrderAmount;
    }

    // Calculate delivery fee based on distance
    double calculateDeliveryFee(LatLng customerLocation) {
        double distance = calculateDistance(location, customerLocation);
        return baseDeliveryFee + (distance * pricePerKm);
    }

    // Haversine formula for distance
    private double calculateDistance(LatLng point1, LatLng point2) {
        final int R = 6371;
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double lon1 = Math.toRadians(point1.longitude);
        double lon2 = Math.toRadians(point2.longitude);
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    // Exposed methods for adapters/UI

    /**
     * Minimum order amount
     */
    public double getMinOrder() {
        return minimumOrderAmount;
    }

    /**
     * Base delivery fee (does not include distance surcharge)
     */
    public double getDeliveryFee() {
        return baseDeliveryFee;
    }

    /**
     * Estimated delivery time in minutes (prep + travel)
     */
    public CharSequence getEstimatedDeliveryTime() {
        final double AVG_SPEED_KM_PER_HOUR = 30.0;
        // Assume average delivery distance of 5 km
        double avgDistance = 5.0;
        int travelTime = (int) ((avgDistance / AVG_SPEED_KM_PER_HOUR) * 60);
        int totalTime = prepTime + travelTime;
        return totalTime + " mins";
    }

    /**
     * Logo URL for Glide
     */
    public String getLogo() {
        return imageUrl;
    }

    /**
     * Banner image URL for Glide
     */
    public String getBannerImage() {
        return bannerImageUrl;
    }

    /**
     * Human-readable opening hours
     */
    public CharSequence getOpeningHours() {
        if (operatingHours == null || operatingHours.isEmpty()) {
            return "No hours available";
        }
        StringBuilder sb = new StringBuilder();
        for (String hours : operatingHours) {
            sb.append(hours).append("\n");
        }
        return sb.toString().trim();
    }

    // Standard getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LatLng getLocation() { return location; }
    public void setLocation(LatLng location) { this.location = location; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getBannerImageUrl() { return bannerImageUrl; }
    public void setBannerImageUrl(String bannerImageUrl) { this.bannerImageUrl = bannerImageUrl; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }

    public List<String> getOperatingHours() { return operatingHours; }
    public void setOperatingHours(List<String> operatingHours) {
        this.operatingHours = operatingHours;
    }

    public double getBaseDeliveryFee() { return baseDeliveryFee; }
    public void setBaseDeliveryFee(double baseDeliveryFee) {
        this.baseDeliveryFee = baseDeliveryFee;
    }

    public double getPricePerKm() { return pricePerKm; }
    public void setPricePerKm(double pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public void setMinimumOrderAmount(double minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public int getPrepTime() { return prepTime; }
    public void setPrepTime(int prepTime) { this.prepTime = prepTime; }
}
