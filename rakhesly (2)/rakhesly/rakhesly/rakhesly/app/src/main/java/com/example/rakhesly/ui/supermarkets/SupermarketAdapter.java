// SupermarketAdapter.java
package com.example.rakhesly.ui.supermarkets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Supermarket;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SupermarketAdapter extends RecyclerView.Adapter<SupermarketAdapter.SupermarketViewHolder> {

    private List<Supermarket> supermarkets;
    private OnSupermarketClickListener listener;

    public interface OnSupermarketClickListener {
        void onSupermarketClick(Supermarket supermarket);
    }

    public SupermarketAdapter(List<Supermarket> supermarkets, OnSupermarketClickListener listener) {
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
        Supermarket supermarket = supermarkets.get(position);
        holder.bind(supermarket, listener);
    }

    @Override
    public int getItemCount() {
        return supermarkets.size();
    }

    public void updateSupermarkets(List<Supermarket> supermarkets) {
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
        private TextView supermarketStatus;
        private TextView supermarketAddress;
        private TextView textViewHours;

        public SupermarketViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLogo = itemView.findViewById(R.id.imageViewLogo);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDeliveryTime = itemView.findViewById(R.id.textViewDeliveryTime);
            textViewMinOrder = itemView.findViewById(R.id.textViewMinOrder);
            textViewDeliveryFee = itemView.findViewById(R.id.textViewDeliveryFee);
            textViewRating = itemView.findViewById(R.id.textViewRating);
            supermarketStatus = itemView.findViewById(R.id.supermarketStatus);
            supermarketAddress = itemView.findViewById(R.id.supermarketAddress);
            textViewHours = itemView.findViewById(R.id.textViewHours);
        }

        void bind(final Supermarket supermarket, final OnSupermarketClickListener listener) {
            textViewName.setText(supermarket.getName());
            textViewDeliveryTime.setText(supermarket.getEstimatedDeliveryTime());
            textViewMinOrder.setText(String.format("Min: $%.2f", supermarket.getMinOrder()));
            textViewDeliveryFee.setText(String.format("$%.2f", supermarket.getDeliveryFee()));
            textViewRating.setText(String.format("%.1f", supermarket.getRating()));

            // Set the address
            supermarketAddress.setText(getLocationForSupermarket(supermarket.getName()));

            // Set opening hours
            String hours = getOpeningHoursForSupermarket(supermarket.getName());
            textViewHours.setText(hours);

            // Set open/closed status
            boolean isOpen = isCurrentlyOpen(supermarket.getName());
            if (isOpen) {
                supermarketStatus.setText("Open Now");
                supermarketStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                supermarketStatus.getBackground().setTint(ContextCompat.getColor(itemView.getContext(), R.color.primary_light));
            } else {
                supermarketStatus.setText("Closed");
                supermarketStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                supermarketStatus.getBackground().setTint(ContextCompat.getColor(itemView.getContext(), R.color.error));
            }

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

        private String getLocationForSupermarket(String name) {
            switch (name) {
                case "Spinneys": return "Hamra";
                case "Carrefour": return "City Mall";
                case "Le Charcutier": return "Achrafieh";
                case "Fahed Supermarket": return "Zalka";
                case "Happy": return "Centro Mall";
                case "Box For Less": return "Jounieh Highway";
                case "Fakhani": return "Hamra";
                case "Faddoul": return "Sarba Highway, Jounieh";
                default: return "";
            }
        }

        private String getOpeningHoursForSupermarket(String name) {
            switch (name) {
                case "Spinneys": return "8:00 AM - 10:00 PM";
                case "Carrefour": return "10:00 AM - 10:00 PM";
                case "Le Charcutier": return "8:00 AM - 10:00 PM";
                case "Fahed Supermarket": return "Open 24/7";
                case "Happy": return "9:00 AM - 10:00 PM";
                case "Box For Less": return "Open 24/7";
                case "Fakhani": return "6:00 AM - 12:00 AM";
                case "Faddoul": return "Open 24/7";
                default: return "Hours not available";
            }
        }

        private boolean isCurrentlyOpen(String name) {
            String hours = getOpeningHoursForSupermarket(name);

            // If open 24/7, always return true
            if (hours.equals("Open 24/7")) {
                return true;
            }

            try {
                // Get current time
                Calendar now = Calendar.getInstance();
                int currentHour = now.get(Calendar.HOUR_OF_DAY);
                int currentMinute = now.get(Calendar.MINUTE);

                // Parse opening hours
                if (hours.contains(" - ")) {
                    String[] parts = hours.split(" - ");
                    String openTime = parts[0];
                    String closeTime = parts[1];

                    SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.US);
                    Date openDate = format.parse(openTime);
                    Date closeDate = format.parse(closeTime);

                    Calendar openCal = Calendar.getInstance();
                    openCal.setTime(openDate);
                    int openHour = openCal.get(Calendar.HOUR_OF_DAY);
                    int openMinute = openCal.get(Calendar.MINUTE);

                    Calendar closeCal = Calendar.getInstance();
                    closeCal.setTime(closeDate);
                    int closeHour = closeCal.get(Calendar.HOUR_OF_DAY);
                    int closeMinute = closeCal.get(Calendar.MINUTE);

                    // Check if current time is between open and close times
                    if (currentHour > openHour || (currentHour == openHour && currentMinute >= openMinute)) {
                        if (currentHour < closeHour || (currentHour == closeHour && currentMinute <= closeMinute)) {
                            return true;
                        }
                    }
                    return false;
                }
            } catch (Exception e) {
                // If any error occurs, default to open
                return true;
            }

            // Default to open if we can't determine
            return true;
        }
    }
}