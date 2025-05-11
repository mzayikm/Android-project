package com.example.rakhesly.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order implements Parcelable {
    private String id;
    private String userId;
    private String driverId;
    private ArrayList<CartItem> items;
    private double subtotal;
    private double deliveryFee;
    private double total;
    private String deliveryAddress;
    private double deliveryAddressLat; // Latitude of delivery address
    private double deliveryAddressLng; // Longitude of delivery address
    private String notes;
    private PaymentMethod paymentMethod;
    private Status status;
    private GeoPoint currentLocation;
    private Date estimatedDeliveryTime;
    private Float rating;
    private String feedback;
    private String name; // Custom name for the order

    @ServerTimestamp
    private Date createdAt;

    public void setItems(ArrayList<CartItem> cartItems) {
        this.items = cartItems;
    }

    public enum Status {
        PENDING, CONFIRMED, PREPARING, PICKED_UP, ON_THE_WAY, DELIVERED, CANCELLED
    }
    public enum PaymentMethod { CASH_ON_DELIVERY, CREDIT_CARD }

    public Order() {
        items = new ArrayList<>();
    }

    protected Order(Parcel in) {
        id           = in.readString();
        userId       = in.readString();
        driverId     = in.readString();
        items        = in.createTypedArrayList(CartItem.CREATOR); // typed read
        subtotal     = in.readDouble();
        deliveryFee  = in.readDouble();
        total        = in.readDouble();
        deliveryAddress = in.readString();
        deliveryAddressLat = in.readDouble();
        deliveryAddressLng = in.readDouble();
        notes        = in.readString();
        paymentMethod = PaymentMethod.valueOf(in.readString());
        status       = Status.valueOf(in.readString());
        double lat   = in.readDouble();
        double lng   = in.readDouble();
        currentLocation = new GeoPoint(lat, lng);
        long edt    = in.readLong();
        estimatedDeliveryTime = edt != -1 ? new Date(edt) : null;
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readFloat();
        }
        feedback     = in.readString();
        name         = in.readString();
        long cat    = in.readLong();
        createdAt   = cat != -1 ? new Date(cat) : null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(driverId);
        dest.writeTypedList(items);  // typed write
        dest.writeDouble(subtotal);
        dest.writeDouble(deliveryFee);
        dest.writeDouble(total);
        dest.writeString(deliveryAddress);
        dest.writeDouble(deliveryAddressLat);
        dest.writeDouble(deliveryAddressLng);
        dest.writeString(notes);
        dest.writeString(paymentMethod.name());
        dest.writeString(status.name());
        dest.writeDouble(currentLocation != null ? currentLocation.getLatitude() : 0);
        dest.writeDouble(currentLocation != null ? currentLocation.getLongitude() : 0);
        dest.writeLong(estimatedDeliveryTime != null
                ? estimatedDeliveryTime.getTime() : -1);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(rating);
        }
        dest.writeString(feedback);
        dest.writeString(name);
        dest.writeLong(createdAt != null
                ? createdAt.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }
        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    // — Getters & setters —

    public ArrayList<CartItem> getItems() {
        return items;
    }



    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }


    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    
    public double getDeliveryAddressLat() { return deliveryAddressLat; }
    public void setDeliveryAddressLat(double deliveryAddressLat) { this.deliveryAddressLat = deliveryAddressLat; }
    
    public double getDeliveryAddressLng() { return deliveryAddressLng; }
    public void setDeliveryAddressLng(double deliveryAddressLng) { this.deliveryAddressLng = deliveryAddressLng; }
    
    public LatLng getDeliveryAddressLatLng() {
        return new LatLng(deliveryAddressLat, deliveryAddressLng);
    }
    
    public void setDeliveryAddressLatLng(LatLng latLng) {
        if (latLng != null) {
            this.deliveryAddressLat = latLng.latitude;
            this.deliveryAddressLng = latLng.longitude;
        }
    }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public GeoPoint getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(GeoPoint currentLocation) { this.currentLocation = currentLocation; }

    public Date getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(Date estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }

    public Float getRating() { return rating; }
    public void setRating(Float rating) { this.rating = rating; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
