package com.example.rakhesly.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> items;
    private final OnCartItemActionListener listener;

    public interface OnCartItemActionListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveClicked(CartItem item);
    }

    public CartAdapter(List<CartItem> items, OnCartItemActionListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<CartItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView quantityText;
        private final MaterialButton decreaseButton;
        private final MaterialButton increaseButton;
        private final ImageButton removeButton;
        private final TextView itemTotalText;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            quantityText = itemView.findViewById(R.id.quantityText);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            removeButton = itemView.findViewById(R.id.removeButton);
            itemTotalText = itemView.findViewById(R.id.itemTotalText);
        }

        public void bind(CartItem item) {
            // Set product details
            productName.setText(item.getProductName());
            productPrice.setText(String.format("$%.2f", item.getPrice()));
            quantityText.setText(String.valueOf(item.getQuantity()));
            
            // Calculate and set item total
            double itemTotal = item.getTotalPrice();
            itemTotalText.setText(String.format("$%.2f", itemTotal));

            // Load product image
            try {
                String imageUrl = item.getProductImage();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    int imageResId = Integer.parseInt(imageUrl);
                    if (imageResId != 0) {
                        productImage.setImageResource(imageResId);
                    } else {
                        productImage.setImageResource(R.drawable.placeholder_product);
                    }
                } else {
                    productImage.setImageResource(R.drawable.placeholder_product);
                }
            } catch (Exception e) {
                productImage.setImageResource(R.drawable.placeholder_product);
            }

            // Set click listeners
            decreaseButton.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    listener.onQuantityChanged(item, item.getQuantity() - 1);
                }
            });

            increaseButton.setOnClickListener(v -> {
                // Remove the stock quantity check that was causing crashes
                // Allow increasing quantity up to a reasonable limit (e.g., 99)
                if (item.getQuantity() < 99) {
                    listener.onQuantityChanged(item, item.getQuantity() + 1);
                }
            });

            removeButton.setOnClickListener(v -> listener.onRemoveClicked(item));

            // Update button states
            decreaseButton.setEnabled(item.getQuantity() > 1);
            increaseButton.setEnabled(item.getQuantity() < 99);
        }
    }
} 