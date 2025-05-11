package com.example.rakhesly.ui.orders;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Order;
import com.example.rakhesly.data.repo.OrderManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderListFragment extends Fragment {

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate your order‑list layout (e.g. fragment_orders.xml)
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        TextView emptyView = view.findViewById(R.id.emptyView);
        List<Order> orders = OrderManager.getInstance().getOrders();
        if (orders.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            OrderAdapter adapter = new OrderAdapter(orders, this::showOrderDetails);
            recyclerView.setAdapter(adapter);
        }
    }
    
    /**
     * Shows a dialog with the details of the selected order
     */
    private void showOrderDetails(Order order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            return;
        }
        
        // Create a dialog to show order details
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_order_details);
        
        // Make the dialog larger
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        
        // Set up dialog views
        TextView titleText = dialog.findViewById(R.id.orderDetailsTitleText);
        RecyclerView itemsRecyclerView = dialog.findViewById(R.id.orderItemsRecyclerView);
        TextView totalText = dialog.findViewById(R.id.orderDetailsTotalText);
        
        // Set order name or default title
        String orderTitle = order.getName();
        if (orderTitle == null || orderTitle.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            orderTitle = "Order from " + dateFormat.format(order.getCreatedAt());
        }
        titleText.setText(orderTitle);
        
        // Set up items recycler view
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        OrderItemAdapter itemAdapter = new OrderItemAdapter(order.getItems());
        itemsRecyclerView.setAdapter(itemAdapter);
        
        // Set total
        totalText.setText(String.format("Total: $%.2f", order.getTotal()));
        
        // Set up close button
        dialog.findViewById(R.id.closeButton).setOnClickListener(v -> dialog.dismiss());
        
        // Show dialog
        dialog.show();
    }
    
    /**
     * Adapter for displaying order items in the details dialog
     */
    private static class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
        private final List<CartItem> items;
        
        public OrderItemAdapter(List<CartItem> items) {
            this.items = items;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_order_detail, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CartItem item = items.get(position);
            holder.nameText.setText(item.getProductName());
            holder.quantityText.setText(String.format("x%d", item.getQuantity()));
            holder.priceText.setText(String.format("$%.2f", item.getTotalPrice()));
            
            // Add unit price information
            double unitPrice = item.getPrice();
            holder.unitPriceText.setText(String.format("$%.2f each", unitPrice));
        }
        
        @Override
        public int getItemCount() {
            return items.size();
        }
        
        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView quantityText;
            TextView priceText;
            TextView unitPriceText;
            
            ViewHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.itemNameText);
                quantityText = itemView.findViewById(R.id.itemQuantityText);
                priceText = itemView.findViewById(R.id.itemPriceText);
                unitPriceText = itemView.findViewById(R.id.itemUnitPriceText);
            }
        }
    }
}
