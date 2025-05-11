package com.example.rakhesly.ui.products;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakhesly.R;
import com.example.rakhesly.databinding.FragmentProductListBinding;
import com.example.rakhesly.data.model.Product;
import com.example.rakhesly.data.repo.CartManager;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.repo.ProductRepo;
import com.google.android.material.button.MaterialButton;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProductListFragment extends Fragment {
    private FragmentProductListBinding binding;
    private SimpleProductAdapter adapter;
    private String supermarketId;
    private String categoryId;
    private String categoryName;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            supermarketId = getArguments().getString("supermarketId");
            categoryId = getArguments().getString("categoryId");
            categoryName = getArguments().getString("categoryName");
            
            // Update title if category is specified
            if (categoryName != null && !categoryName.isEmpty()) {
                binding.textViewTitle.setText(categoryName);
            }
        }

        // Hide progress bar and error message
        binding.progressBar.setVisibility(View.GONE);
        binding.textViewEmpty.setVisibility(View.GONE);
        
        // Setup RecyclerView with a simple adapter
        setupSimpleRecyclerView();
        
        // Log the selected category for debugging
        if (categoryId != null) {
            Log.d("ProductListFragment", "Selected category: " + categoryId);
        }
    }
    
    private void setupSimpleRecyclerView() {
        // Create sample products
        List<SimpleProduct> products = new ArrayList<>();
        
        // Get products from the ProductRepo to ensure consistent pricing
        ProductRepo productRepo = new ProductRepo();
        
        // Initialize sample products if needed
        productRepo.initializeSampleProducts();
        
        // Use the same supermarket ID as in the popular products section
        // If no supermarket ID is provided, default to "spinneys"
        String marketId = supermarketId != null ? supermarketId : "spinneys";
        String selectedCategory = categoryId != null ? categoryId : "all";
        
        // Create a map for category ID to category name mapping
        Map<String, String> categoryMapping = new HashMap<>();
        categoryMapping.put("1", "produce");   // Fruits & Vegetables
        categoryMapping.put("2", "dairy");     // Dairy & Eggs
        categoryMapping.put("3", "meat");      // Meat & Poultry
        categoryMapping.put("4", "bakery");    // Bakery
        categoryMapping.put("5", "beverages"); // Beverages
        categoryMapping.put("6", "snacks");    // Snacks
        categoryMapping.put("7", "canned");    // Canned & Preserved Foods
        categoryMapping.put("8", "grains");    // Grains & Legumes
        categoryMapping.put("9", "spices");    // Spices & Condiments
        categoryMapping.put("10", "sweets");   // Sweets & Desserts
        categoryMapping.put("11", "nuts");     // Nuts & Dried Fruits
        
        // Map the selected category ID to its name
        if (selectedCategory != null && !selectedCategory.equals("all") && categoryMapping.containsKey(selectedCategory)) {
            selectedCategory = categoryMapping.get(selectedCategory);
            Log.d("ProductListFragment", "Mapped category ID " + categoryId + " to category name: " + selectedCategory);
        }
        
        // Create a map of products by category
        Map<String, List<SimpleProduct>> productsByCategory = new HashMap<>();
        
        // Dairy Products
        List<SimpleProduct> dairyProducts = new ArrayList<>();
        addProductToCategory(dairyProducts, "Laban Ayran", 1.99, marketId, R.drawable.laban_ayran);
        addProductToCategory(dairyProducts, "Labneh", 4.50, marketId, R.drawable.labneh);
        addProductToCategory(dairyProducts, "Halloumi Cheese", 6.99, marketId, R.drawable.halloumi);
        addProductToCategory(dairyProducts, "Greek Yogurt", 3.25, marketId, R.drawable.yogurt);
        productsByCategory.put("dairy", dairyProducts);
        
        // Bakery Products
        List<SimpleProduct> bakeryProducts = new ArrayList<>();
        addProductToCategory(bakeryProducts, "Kaak", 0.50, marketId, R.drawable.kaak);
        addProductToCategory(bakeryProducts, "Man'oushe Zaatar", 0.99, marketId, R.drawable.mankoushi_zaatar);
        addProductToCategory(bakeryProducts, "Pita Bread", 1.25, marketId, R.drawable.pita);
        addProductToCategory(bakeryProducts, "Zaatar Croissant", 1.50, marketId, R.drawable.croissant);
        productsByCategory.put("bakery", bakeryProducts);
        
        // Beverages
        List<SimpleProduct> beverageProducts = new ArrayList<>();
        addProductToCategory(beverageProducts, "Bonjus", 2.75, marketId, R.drawable.bonjus);
        addProductToCategory(beverageProducts, "Fanta Orange", 0.99, marketId, R.drawable.fanta);
        addProductToCategory(beverageProducts, "Coca-Cola", 0.99, marketId, R.drawable.coca_cola);
        productsByCategory.put("beverages", beverageProducts);
        
        // Snacks
        List<SimpleProduct> snackProducts = new ArrayList<>();
        addProductToCategory(snackProducts, "Master Chips", 1.25, marketId, R.drawable.master_chips);
        addProductToCategory(snackProducts, "Kinder Chocolate", 2.50, marketId, R.drawable.kinder);
        addProductToCategory(snackProducts, "DrFood Wafer", 1.75, marketId, R.drawable.drfood);
        addProductToCategory(snackProducts, "Milka Bar", 1.99, marketId, R.drawable.milka);
        productsByCategory.put("snacks", snackProducts);
        
        // Produce (Fruits & Vegetables)
        List<SimpleProduct> produceProducts = new ArrayList<>();
        addProductToCategory(produceProducts, "Lebanese Cucumber", 2.25, marketId, R.drawable.cucumber);
        addProductToCategory(produceProducts, "Bekaa Potatoes", 1.75, marketId, R.drawable.potato);
        addProductToCategory(produceProducts, " Tomatoes", 2.50, marketId, R.drawable.tomato);
        addProductToCategory(produceProducts, " Lettuce", 1.25, marketId, R.drawable.lettuce);
        productsByCategory.put("produce", produceProducts);
        
        // Meat & Poultry
        List<SimpleProduct> meatProducts = new ArrayList<>();
        addProductToCategory(meatProducts, "Fresh Local Chicken", 8.50, marketId, R.drawable.chicken);
        addProductToCategory(meatProducts, "Lamb Kofta", 19.99, marketId, R.drawable.lamb_kafta);
        addProductToCategory(meatProducts, "Beef Steak", 24.99, marketId, R.drawable.beef_steak);
        addProductToCategory(meatProducts, "Turkey Thighs", 9.99, marketId, R.drawable.turkey);
        productsByCategory.put("meat", meatProducts);
        
        // Canned & Preserved Foods
        List<SimpleProduct> cannedProducts = new ArrayList<>();
        addProductToCategory(cannedProducts, "Hummus", 3.50, marketId, R.drawable.hummus);
        addProductToCategory(cannedProducts, "Tahini", 4.99, marketId, R.drawable.tahini);
        productsByCategory.put("canned", cannedProducts);
        
        // Grains & Legumes
        List<SimpleProduct> grainProducts = new ArrayList<>();
        addProductToCategory(grainProducts, "Fine Bulgur", 2.99, marketId, R.drawable.bulgur);
        addProductToCategory(grainProducts, "Brown Lentils", 3.25, marketId, R.drawable.lentils);
        productsByCategory.put("grains", grainProducts);
        
        // Spices & Condiments
        List<SimpleProduct> spiceProducts = new ArrayList<>();
        addProductToCategory(spiceProducts, "Zaatar Mix", 4.50, marketId, R.drawable.zaatar);
        addProductToCategory(spiceProducts, "Sumac", 3.75, marketId, R.drawable.sumac);
        productsByCategory.put("spices", spiceProducts);
        
        // Sweets & Desserts
        List<SimpleProduct> sweetProducts = new ArrayList<>();
        addProductToCategory(sweetProducts, "Baklava", 18.99, marketId, R.drawable.baklava);
        addProductToCategory(sweetProducts, "Maamoul", 12.50, marketId, R.drawable.maamoul);
        productsByCategory.put("sweets", sweetProducts);
        
        // Nuts & Dried Fruits
        List<SimpleProduct> nutProducts = new ArrayList<>();
        addProductToCategory(nutProducts, "Pistachios", 15.99, marketId, R.drawable.pistachios);
        addProductToCategory(nutProducts, "Dried Apricots", 7.99, marketId, R.drawable.dried_apricots);
        productsByCategory.put("nuts", nutProducts);
        
        // Filter products based on selected category
        if (selectedCategory == null || selectedCategory.equals("all")) {
            // Add all products if no specific category is selected
            for (List<SimpleProduct> categoryProducts : productsByCategory.values()) {
                products.addAll(categoryProducts);
            }
            Log.d("ProductListFragment", "Showing all products: " + products.size());
        } else {
            // Add only products from the selected category
            List<SimpleProduct> categoryProducts = productsByCategory.get(selectedCategory.toLowerCase());
            if (categoryProducts != null && !categoryProducts.isEmpty()) {
                products.addAll(categoryProducts);
                Log.d("ProductListFragment", "Showing products for category '" + selectedCategory + 
                      "': " + products.size());
            } else {
                // If no products found for the category, log this and show all products
                Log.d("ProductListFragment", "No products found for category: " + selectedCategory);
                for (List<SimpleProduct> allCategoryProducts : productsByCategory.values()) {
                    products.addAll(allCategoryProducts);
                }
                Log.d("ProductListFragment", "Showing all products instead: " + products.size());
            }
        }
        
        // If we have no products at all, show a message
        if (products.isEmpty()) {
            binding.recyclerViewProducts.setVisibility(View.GONE);
            binding.textViewEmpty.setVisibility(View.VISIBLE);
            binding.textViewEmpty.setText("No products found in this category");
        } else {
            binding.recyclerViewProducts.setVisibility(View.VISIBLE);
            binding.textViewEmpty.setVisibility(View.GONE);
        }
        
        // Log all available categories for debugging
        StringBuilder categories = new StringBuilder("Available categories: ");
        for (String category : productsByCategory.keySet()) {
            categories.append(category).append(", ");
        }
        Log.d("ProductListFragment", categories.toString());
        
        // Create and set adapter
        adapter = new SimpleProductAdapter(products);
        
        // Set up RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerViewProducts.setLayoutManager(layoutManager);
        binding.recyclerViewProducts.setAdapter(adapter);
        
        // Add spacing decoration
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        binding.recyclerViewProducts.addItemDecoration(new GridSpacingItemDecoration(2, spacingInPixels, true));
    }

    // Simple Product class that doesn't depend on any external code
    static class SimpleProduct {
        private String name;
        private String price;
        private int imageResId;
        
        public SimpleProduct(String name, String price, int imageResId) {
            this.name = name;
            this.price = price;
            this.imageResId = imageResId;
        }
        
        public String getName() { return name; }
        public String getPrice() { return price; }
        public int getImageResId() { return imageResId; }
    }
    
    // Simple adapter that doesn't depend on any external code
    class SimpleProductAdapter extends RecyclerView.Adapter<SimpleProductAdapter.ViewHolder> {
        private List<SimpleProduct> products;
        
        public SimpleProductAdapter(List<SimpleProduct> products) {
            this.products = products;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SimpleProduct product = products.get(position);
            holder.productName.setText(product.getName());
            holder.productPrice.setText(product.getPrice());
            holder.productImage.setImageResource(product.getImageResId());
            holder.stockStatus.setText("In Stock");
            holder.stockStatus.setTextColor(requireContext().getColor(android.R.color.holo_green_dark));
            holder.buttonAddToCart.setOnClickListener(v -> {
                String priceRaw = product.getPrice().replaceAll("[^\\d.]", "");
                double priceValue = 0;
                if (!priceRaw.isEmpty()) {
                    try { priceValue = Double.parseDouble(priceRaw); } catch (NumberFormatException e) { }
                }
                int imageResId = product.getImageResId();
                CartItem cartItem = new CartItem(product.getName(), product.getName(), supermarketId, 1, priceValue, product.getName(), imageResId != 0 ? String.valueOf(imageResId) : null);
                CartManager.getInstance().addItem(cartItem);
                Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show();
            });
        }
        
        @Override
        public int getItemCount() {
            return products.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView productImage;
            TextView productName;
            TextView productPrice;
            TextView stockStatus;
            MaterialButton buttonAddToCart;
            
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.productImage);
                productName = itemView.findViewById(R.id.productName);
                productPrice = itemView.findViewById(R.id.productPrice);
                stockStatus = itemView.findViewById(R.id.stockStatus);
                buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
            }
        }
    }
    
    /**
     * Item decoration for grid spacing
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position >= 0) {  // Prevent crashes with -1 position
                int column = position % spanCount;

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount;
                    outRect.right = (column + 1) * spacing / spanCount;
                    if (position < spanCount) {
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing;
                } else {
                    outRect.left = column * spacing / spanCount;
                    outRect.right = spacing - (column + 1) * spacing / spanCount;
                    if (position >= spanCount) {
                        outRect.top = spacing;
                    }
                }
            }
        }
    }

    // Helper method to create price variations for different supermarkets
    private Map<String, Double> createPriceMap(double basePrice, String currentSupermarketId) {
        Map<String, Double> prices = new HashMap<>();
        // Add slight variations for different supermarkets
        prices.put("spinneys", basePrice * 1.2);  // Spinneys tends to be more expensive
        prices.put("carrefour", basePrice * 1.0); // Carrefour as baseline
        prices.put("lecharcutier", basePrice * 1.1); // Le Charcutier
        // Add more supermarkets
        prices.put("fahed", basePrice * 0.95); // Fahed
        prices.put("happy", basePrice * 1.05); // Happy
        prices.put("boxforless", basePrice * 0.9); // Box For Less
        prices.put("fakhani", basePrice * 1.0); // Fakhani
        prices.put("faddoul", basePrice * 1.05); // Faddoul
        
        // If the current supermarket is not in the map, add it with the base price
        if (!prices.containsKey(currentSupermarketId)) {
            prices.put(currentSupermarketId, basePrice);
        }
        
        return prices;
    }
    
    // Helper method to add a product to a category list
    private void addProductToCategory(List<SimpleProduct> categoryList, String name, double basePrice, String marketId, int imageResId) {
        Map<String, Double> prices = createPriceMap(basePrice, marketId);
        categoryList.add(new SimpleProduct(name, String.format("$%.2f", prices.get(marketId)), imageResId));
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
