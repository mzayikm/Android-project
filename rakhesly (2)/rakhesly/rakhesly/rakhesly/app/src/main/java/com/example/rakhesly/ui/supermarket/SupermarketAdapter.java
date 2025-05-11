// SupermarketAdapter.java
package com.example.rakhesly.ui.supermarket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.data.model.Supermarket;

import java.util.List;

public class SupermarketAdapter extends RecyclerView.Adapter<SupermarketAdapter.SupermarketViewHolder> {

    private List<com.example.rakhesly.data.model.Supermarket> supermarkets;
    private OnSupermarketClickListener listener;

    public interface OnSupermarketClickListener {
        void onSupermarketClick(com.example.rakhesly.data.model.Supermarket supermarket);
    }

    public SupermarketAdapter(List<com.example.rakhesly.data.model.Supermarket> supermarkets, OnSupermarketClickListener listener) {
        this.supermarkets = supermarkets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SupermarketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_supermarket, parent, false);
        return new SupermarketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SupermarketViewHolder holder, int position) {
        com.example.rakhesly.data.model.Supermarket supermarket = supermarkets.get(position);
        holder.bind(supermarket, listener);
    }

    @Override
    public int getItemCount() {
        return supermarkets.size();
    }

    public void updateSupermarkets(List<com.example.rakhesly.data.model.Supermarket> supermarkets) {
        this.supermarkets = supermarkets;
        notifyDataSetChanged();
    }

    static class SupermarketViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewLogo;
        private TextView textViewName;
        private TextView textViewDeliveryTime;
        private TextView textViewMinOrder;
        private TextView textViewDeliveryFee;
        private TextView textViewRating;

        public SupermarketViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLogo = itemView.findViewById(R.id.imageViewLogo);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDeliveryTime = itemView.findViewById(R.id.textViewDeliveryTime);
            textViewMinOrder = itemView.findViewById(R.id.textViewMinOrder);
            textViewDeliveryFee = itemView.findViewById(R.id.textViewDeliveryFee);
            textViewRating = itemView.findViewById(R.id.textViewRating);
        }

        void bind(final com.example.rakhesly.data.model.Supermarket supermarket, final OnSupermarketClickListener listener) {
            textViewName.setText(supermarket.getName());
            textViewDeliveryTime.setText(supermarket.getEstimatedDeliveryTime());
            textViewMinOrder.setText(String.format("Min. Order: $%.2f", supermarket.getMinOrder()));
            textViewDeliveryFee.setText(String.format("Delivery: $%.2f", supermarket.getDeliveryFee()));
            textViewRating.setText(String.format("%.1f", supermarket.getRating()));

            // Load supermarket logo with Glide
            String logoUrl = supermarket.getLogo();
            if (logoUrl != null && logoUrl.startsWith("http")) {
                // Load from URL
                Glide.with(itemView.getContext())
                        .load(logoUrl)
                        .placeholder(R.drawable.rakheslylogo)
                        .error(R.drawable.supermarket_error_logo)
                        .into(imageViewLogo);
            } else {
                // Load local drawable based on supermarket name
                String name = supermarket.getName().toLowerCase().replace(" ", "_");
                try {
                    int resourceId = itemView.getContext().getResources()
                            .getIdentifier(name, "drawable", itemView.getContext().getPackageName());
                    if (resourceId != 0) {
                        Glide.with(itemView.getContext())
                                .load(resourceId)
                                .into(imageViewLogo);
                    } else {
                        // Fallback to error logo if resource not found
                        imageViewLogo.setImageResource(R.drawable.supermarket_error_logo);
                    }
                } catch (Exception e) {
                    imageViewLogo.setImageResource(R.drawable.supermarket_error_logo);
                }
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSupermarketClick(supermarket);
                }
            });
        }
    }
}