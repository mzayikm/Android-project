package com.example.rakhesly.ui.orders;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Order;
import com.example.rakhesly.data.repo.CartManager;
import com.example.rakhesly.ui.main.MainActivity;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    public interface OnOrderClickListener {
        void onOrderClick(Order order);
    }

    private final List<Order> orders;
    private final OnOrderClickListener listener;
    private final SimpleDateFormat dateFormat;

    public OrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = orders.get(position);
        
        // Set order name (if available) or generate a default name
        String orderName = order.getName();
        if (orderName == null || orderName.isEmpty()) {
            orderName = "Order from " + dateFormat.format(order.getCreatedAt());
        }
        holder.orderNameEditText.setText(orderName);
        holder.orderNameEditText.setEnabled(false); // Initially disabled
        
        // Format and set the order date
        if (order.getCreatedAt() != null) {
            holder.orderDateText.setText(dateFormat.format(order.getCreatedAt()));
        } else {
            holder.orderDateText.setText("No date available");
        }
        
        // Display the first 1-3 items in the order
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            StringBuilder itemsText = new StringBuilder("Items: ");
            int itemCount = Math.min(3, order.getItems().size());
            
            for (int i = 0; i < itemCount; i++) {
                CartItem item = order.getItems().get(i);
                itemsText.append(item.getProductName());
                if (item.getQuantity() > 1) {
                    itemsText.append(" (x").append(item.getQuantity()).append(")");
                }
                
                if (i < itemCount - 1) {
                    itemsText.append(", ");
                }
            }
            
            if (order.getItems().size() > 3) {
                itemsText.append(", and ").append(order.getItems().size() - 3).append(" more");
            }
            
            holder.orderItemsText.setText(itemsText.toString());
        } else {
            holder.orderItemsText.setText("No items");
        }
        
        // Set the total price
        holder.orderTotalText.setText(String.format("$%.2f", order.getTotal()));
        
        // Set up edit name button click listener
        holder.editNameButton.setOnClickListener(v -> {
            if (holder.orderNameEditText.isEnabled()) {
                // Save the new name
                String newName = holder.orderNameEditText.getText().toString().trim();
                order.setName(newName);
                holder.orderNameEditText.setEnabled(false);
                holder.editNameButton.setImageResource(android.R.drawable.ic_menu_edit);
                Toast.makeText(v.getContext(), "Order name saved", Toast.LENGTH_SHORT).show();
            } else {
                // Enable editing
                holder.orderNameEditText.setEnabled(true);
                holder.orderNameEditText.requestFocus();
                holder.editNameButton.setImageResource(android.R.drawable.ic_menu_save);
            }
        });
        
        // Set up reorder button click listener
        holder.reorderButton.setOnClickListener(v -> {
            if (order.getItems() != null && !order.getItems().isEmpty()) {
                // Clear current cart before adding items from the order
                CartManager.getInstance().clearCart();
                
                // Add all items from the order to the cart
                for (CartItem item : order.getItems()) {
                    CartManager.getInstance().addItem(item);
                }
                
                // Show success message
                Toast.makeText(v.getContext(), "Items added to cart", Toast.LENGTH_SHORT).show();
                
                // Navigate to MainActivity to show the cart
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                v.getContext().startActivity(intent);
            } else {
                Toast.makeText(v.getContext(), "Cannot reorder: Order is empty", Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set up card click listener to view order details
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOrderClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText orderNameEditText;
        ImageButton editNameButton;
        TextView orderDateText;
        TextView orderItemsText;
        TextView orderTotalText;
        MaterialButton reorderButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNameEditText = itemView.findViewById(R.id.orderNameEditText);
            editNameButton = itemView.findViewById(R.id.editNameButton);
            orderDateText = itemView.findViewById(R.id.orderDateText);
            orderItemsText = itemView.findViewById(R.id.orderItemsText);
            orderTotalText = itemView.findViewById(R.id.orderTotalText);
            reorderButton = itemView.findViewById(R.id.reorderButton);
        }
    }
}
