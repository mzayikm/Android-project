package com.example.rakhesly.ui.supermarket;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Product;
import com.google.android.material.button.MaterialButton;

public class AddToCartDialog extends DialogFragment {
    private static final String ARG_PRODUCT = "product";
    private Product product;
    private OnAddToCartListener listener;
    private int quantity = 1;

    public interface OnAddToCartListener {
        void onAddToCart(Product product, int quantity);
    }

    public static AddToCartDialog newInstance(Product product) {
        AddToCartDialog fragment = new AddToCartDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT, (Parcelable) product);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnAddToCartListener(OnAddToCartListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            product = getArguments().getParcelable(ARG_PRODUCT);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_to_cart, null);

        // Initialize views
        ImageView productImage = view.findViewById(R.id.productImage);
        TextView productName = view.findViewById(R.id.productName);
        TextView productPrice = view.findViewById(R.id.productPrice);
        TextView quantityText = view.findViewById(R.id.quantityText);
        MaterialButton decreaseButton = view.findViewById(R.id.decreaseButton);
        MaterialButton increaseButton = view.findViewById(R.id.increaseButton);
        MaterialButton addToCartButton = view.findViewById(R.id.addToCartButton);

        // Set product details
        productName.setText(product.getName());
        productPrice.setText(String.format("$%.2f", product.getPrice()));
        updateQuantityText(quantityText);

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(this)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_product)
                .error(R.drawable.placeholder_product)
                .centerCrop()
                .into(productImage);
        }

        // Set click listeners
        decreaseButton.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityText(quantityText);
                updateAddToCartButton(addToCartButton);
            }
        });

        increaseButton.setOnClickListener(v -> {
            if (quantity < product.getStockQuantity()) {
                quantity++;
                updateQuantityText(quantityText);
                updateAddToCartButton(addToCartButton);
            }
        });

        addToCartButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddToCart(product, quantity);
            }
            dismiss();
        });

        builder.setView(view);
        return builder.create();
    }

    private void updateQuantityText(TextView quantityText) {
        quantityText.setText(String.valueOf(quantity));
    }

    private void updateAddToCartButton(MaterialButton addToCartButton) {
        double total = quantity * product.getPrice();
        addToCartButton.setText(String.format("Add to Cart - $%.2f", total));
    }
} 