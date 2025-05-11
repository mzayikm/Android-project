package com.example.rakhesly.ui.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Product;
import com.example.rakhesly.data.repo.ProductRepo;
import com.example.rakhesly.databinding.FragmentProductDetailBinding;

public class productdetailfragment extends Fragment {
    private FragmentProductDetailBinding binding;
    private String productId;
    private String supermarketId;
    private ProductRepo productRepo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            productId = getArguments().getString("productId");
            supermarketId = getArguments().getString("supermarketId");
        }

        if (productId == null || productId.isEmpty()) {
            showError("Product ID not found");
            return;
        }

        if (supermarketId == null || supermarketId.isEmpty()) {
            showError("Supermarket ID not found");
            return;
        }

        loadProductDetails();

        // Set up add to cart button
        binding.buttonAddToCart.setOnClickListener(v -> addToCart());
    }

    private void loadProductDetails() {
        productRepo = new ProductRepo();

        // In a real app, you would get the product by ID from Firebase
        // For now, let's simulate loading a product
        productRepo.getProductsBySupermarket(supermarketId)
                .addOnSuccessListener(products -> {
                    if (products != null && !products.isEmpty()) {
                        // Find the product with the matching ID
                        for (Product product : products) {
                            if (product.getId().equals(productId)) {
                                updateUI(product);
                                return;
                            }
                        }
                        showError("Product not found");
                    } else {
                        showError("No products found");
                    }
                })
                .addOnFailureListener(e -> showError("Error loading product: " + e.getMessage()));
    }

    private void updateUI(Product product) {
        binding.progressBar.setVisibility(View.GONE);
        binding.contentLayout.setVisibility(View.VISIBLE);

        binding.textViewProductName.setText(product.getName());
        binding.textViewProductDescription.setText(product.getDescription());
        binding.textViewBrand.setText("Brand: " + product.getBrand());
        binding.textViewCategory.setText("Category: " + product.getCategory());

        Double price = product.getSupermarketPrices().get(supermarketId);
        if (price != null) {
            binding.textViewPrice.setText(String.format("$%.2f", price));
        } else {
            binding.textViewPrice.setText("Price not available");
        }

        binding.textViewUnit.setText("Unit: " + product.getUnit());
        binding.textViewWeight.setText("Weight: " + product.getWeight() + " " + product.getUnit());
        binding.textViewAvailability.setText(product.isAvailable() ? "In Stock" : "Out of Stock");
        binding.textViewAvailability.setTextColor(requireContext().getColor(
                product.isAvailable() ? android.R.color.holo_green_dark : android.R.color.holo_red_dark
        ));

        // Load product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(requireContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.placeholder_product)
                    .into(binding.imageViewProduct);
        } else {
            binding.imageViewProduct.setImageResource(R.drawable.placeholder_product);
        }
    }

    private void addToCart() {
        // In a real app, you would add the product to the cart
        // For now, just show a toast message
        Toast.makeText(requireContext(), "Product added to cart", Toast.LENGTH_SHORT).show();
    }

    private void showError(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.contentLayout.setVisibility(View.GONE);
        binding.textViewError.setVisibility(View.VISIBLE);
        binding.textViewError.setText(message);
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
