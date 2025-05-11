package com.example.rakhesly.ui.products;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Product;
import com.example.rakhesly.data.repo.CartManager;
import com.google.android.material.button.MaterialButton;

public class ProductAdapter extends ListAdapter<Product, ProductAdapter.ProductViewHolder> {
    private String supermarketId;

    public void setSupermarketId(String supermarketId) {
        this.supermarketId = supermarketId;
        notifyDataSetChanged();
    }

    public ProductAdapter() {
        super(new DiffUtil.ItemCallback<Product>() {
            @Override
            public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                return oldItem.getId().equals(newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = getItem(position);
        holder.bind(product, supermarketId);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView productName;
        private final TextView productPrice;
        private final TextView stockStatus;
        private final MaterialButton buttonAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            stockStatus = itemView.findViewById(R.id.stockStatus);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }

        public void bind(Product product, String supermarketId) {
            if (supermarketId == null) {
                throw new IllegalStateException("SupermarketId must be set before binding products");
            }
            productName.setText(product.getName());
            Double price = product.getSupermarketPrices().get(supermarketId);
            if (price != null) {
                productPrice.setText(String.format("$%.2f", price));
            } else {
                productPrice.setText("Price not available");
            }
            stockStatus.setText(product.isAvailable() ? "In Stock" : "Out of Stock");
            stockStatus.setTextColor(itemView.getContext().getColor(
                    product.isAvailable() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
            ));

            // Load product image
            if (product.getImageResource() != 0) {
                // Use the image resource if available
                productImage.setImageResource(product.getImageResource());
            } else if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                // Fall back to image URL if available
                Glide.with(itemView)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.placeholder_product)
                        .into(productImage);
            } else {
                // Use placeholder as last resort
                productImage.setImageResource(R.drawable.placeholder_product);
            }

            // Add to cart click listener
            buttonAddToCart.setOnClickListener(v -> {
                double priceValue = product.getSupermarketPrices().getOrDefault(supermarketId, 0.0);
                CartItem cartItem = new CartItem(product.getId(), product.getId(), supermarketId, 1, priceValue, product.getName(), product.getImageUrl());
                CartManager.getInstance().addItem(cartItem);
                Toast.makeText(itemView.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
