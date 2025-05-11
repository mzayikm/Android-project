package com.example.rakhesly.ui.supermarkets;

import android.app.BroadcastOptions;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.databinding.FragmentSupermarketDetailBinding;
import com.example.rakhesly.ui.products.ProductAdapter;

public class SupermarketDetailFragment extends Fragment {

    private FragmentSupermarketDetailBinding binding;
    private SupermarketDetailViewModel viewModel;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter popularProductsAdapter;
    private String supermarketId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSupermarketDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            supermarketId = getArguments().getString("supermarketId");
        }

        setupViewModel();
        setupRecyclerViews();
        observeData();

        binding.buttonViewAll.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("supermarketId", supermarketId);
            Navigation.findNavController(view)
                    .navigate(R.id.action_supermarketDetailFragment_to_productListFragment, args);
        });
    }

        private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter();
        popularProductsAdapter = new ProductAdapter();
        
        // Set category click listener
        categoryAdapter.setOnCategoryClickListener(category -> {
            // Navigate to product list with category filter
            Bundle args = new Bundle();
            args.putString("supermarketId", supermarketId);
            args.putString("categoryId", category.getId());
            args.putString("categoryName", category.getName());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_supermarketDetailFragment_to_productListFragment, args);
        });

        binding.recyclerViewCategories.setAdapter(categoryAdapter);
        binding.recyclerViewPopularProducts.setAdapter(popularProductsAdapter);
    }

    private void observeData() {
        viewModel.getSupermarket().observe(getViewLifecycleOwner(), supermarket -> {
            if (supermarket != null) {
                updateSupermarketUI(supermarket);
            } else {
                Toast.makeText(requireContext(), "Error loading supermarket details", Toast.LENGTH_LONG).show();
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
        
        // Observe categories
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                categoryAdapter.submitList(categories);
                binding.recyclerViewCategories.setVisibility(View.VISIBLE);
                binding.textViewCategories.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewCategories.setVisibility(View.GONE);
                binding.textViewCategories.setVisibility(View.GONE);
            }
        });
        
        // Observe popular products
        viewModel.getPopularProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null && !products.isEmpty()) {
                popularProductsAdapter.setSupermarketId(supermarketId);
                popularProductsAdapter.submitList(products);
                binding.recyclerViewPopularProducts.setVisibility(View.VISIBLE);
                binding.textViewPopularProducts.setVisibility(View.VISIBLE);
                binding.buttonViewAll.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewPopularProducts.setVisibility(View.GONE);
                binding.textViewPopularProducts.setVisibility(View.GONE);
                binding.buttonViewAll.setVisibility(View.GONE);
                
                // Show a message if no products are found
                Toast.makeText(requireContext(), "No products found for this supermarket", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(SupermarketDetailViewModel.class);
        if (supermarketId != null) {
            viewModel.loadSupermarket(supermarketId);
            viewModel.loadCategories(supermarketId);
            viewModel.loadPopularProducts(supermarketId);

            // Observe error state
            viewModel.getError().observe(getViewLifecycleOwner(), errorMessage -> {
                if (errorMessage != null && !errorMessage.isEmpty()) {
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Error: Supermarket ID not found", Toast.LENGTH_LONG).show();
            Navigation.findNavController(requireView()).navigateUp();
        }
    }

    

   

    private void updateSupermarketUI(Supermarket supermarket) {
        binding.textViewSupermarketName.setText(supermarket.getName());
        binding.textViewDeliveryTime.setText(supermarket.getEstimatedDeliveryTime());
        binding.textViewDeliveryFee.setText(String.format("Delivery: $%.2f", supermarket.getDeliveryFee()));
        binding.textViewMinOrder.setText(String.format("Min. Order: $%.2f", supermarket.getMinOrder()));
        binding.textViewRating.setText(String.format("%.1f", supermarket.getRating()));
        binding.textViewAddress.setText(supermarket.getAddress());
        binding.textViewOpeningHours.setText(supermarket.getOpeningHours());

        // Load banner image
        String bannerImageName = supermarket.getBannerImageUrl();
        if (bannerImageName != null && !bannerImageName.isEmpty()) {
            // Get the resource ID for the banner image
            int resourceId = getResources().getIdentifier(
                    bannerImageName, "drawable", requireContext().getPackageName());
            
            if (resourceId != 0) {
                // If resource exists, load it directly
                binding.imageViewBanner.setImageResource(resourceId);
            } else {
                // Fallback to placeholder if resource not found
                binding.imageViewBanner.setImageResource(R.drawable.placeholder_banner);
            }
        } else {
            binding.imageViewBanner.setImageResource(R.drawable.placeholder_banner);
        }

        // Set status badge
        if (supermarket.isOpen()) {
            binding.textViewStatus.setText("Open");
            binding.textViewStatus.setBackgroundResource(R.drawable.bg_status_open);
        } else {
            binding.textViewStatus.setText("Closed");
            binding.textViewStatus.setBackgroundResource(R.drawable.bg_status_closed);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}