package com.example.rakhesly.ui.supermarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products;
    private final OnProductClickListener listener;
    private Context context = null;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
        this.context = context;
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
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<Product> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView productImage;
        private final TextView nameText;
        private final TextView priceText;
        private final TextView stockText;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            nameText = itemView.findViewById(R.id.productName);
            priceText = itemView.findViewById(R.id.productPrice);
            stockText = itemView.findViewById(R.id.stockStatus);
        }

        public void bind(Product product) {
            nameText.setText(product.getName());
            priceText.setText(String.format("$%.2f", product.getPrice()));
            
            // Set stock status
            if (product.getStockQuantity() > 10) {
                stockText.setText("In Stock");
                stockText.setTextColor(itemView.getContext().getColor(R.color.green));
            } else if (product.getStockQuantity() > 0) {
                stockText.setText("Low Stock");
                stockText.setTextColor(itemView.getContext().getColor(R.color.yellow));
            } else {
                stockText.setText("Out of Stock");
                stockText.setTextColor(itemView.getContext().getColor(R.color.red));
            }

            // Load product image
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_product)
                    .error(R.drawable.placeholder_product)
                    .centerCrop()
                    .into(productImage);
            } else {
                productImage.setImageResource(R.drawable.placeholder_product);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (product.isAvailable() && product.getStockQuantity() > 0) {
                    listener.onProductClick(product);
                }
            });
        }
    }
} 