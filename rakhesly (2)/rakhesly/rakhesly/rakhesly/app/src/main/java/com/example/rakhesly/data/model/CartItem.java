package com.example.rakhesly.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private String id;
    private String productId;
    private String supermarketId;
    private int quantity;
    private double price;
    private String productName;
    private String productImage;

    // Empty constructor for Firebase (and general use)
    public CartItem() { }

    // Full constructor
    public CartItem(String id,
                    String productId,
                    String supermarketId,
                    int quantity,
                    double price,
                    String productName,
                    String productImage) {
        this.id             = id;
        this.productId      = productId;
        this.supermarketId  = supermarketId;
        this.quantity       = quantity;
        this.price          = price;
        this.productName    = productName;
        this.productImage   = productImage;
    }

    // Convenience constructor from a Product (if you need it)
    public CartItem(Product selectedProduct, int quantity) {
        this.id            = selectedProduct.getId();
        this.productId     = selectedProduct.getId();
        this.quantity      = quantity;
        this.price         = selectedProduct.getPrice();
        this.productName   = selectedProduct.getName();
        this.productImage  = selectedProduct.getImageUrl();
    }

    // Parcel constructor
    protected CartItem(Parcel in) {
        id            = in.readString();
        productId     = in.readString();
        supermarketId = in.readString();
        quantity      = in.readInt();
        price         = in.readDouble();
        productName   = in.readString();
        productImage  = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(productId);
        dest.writeString(supermarketId);
        dest.writeInt(quantity);
        dest.writeDouble(price);
        dest.writeString(productName);
        dest.writeString(productImage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }
        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // === Your existing getters & setters ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getSupermarketId() { return supermarketId; }
    public void setSupermarketId(String supermarketId) { this.supermarketId = supermarketId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    // Total price helper
    public double getTotalPrice() {
        return price * quantity;
    }

    public Product getProduct() {
        return null;
    }
}
