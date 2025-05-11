package com.example.rakhesly.ui.cart;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Cart;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Order;
import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.data.repo.CartManager;
import com.example.rakhesly.data.repo.OrderManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class cartffragment extends Fragment implements CartAdapter.OnCartItemActionListener {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView emptyView, subtotalText, deliveryFeeText, totalText;
    private MaterialButton checkoutButton;
    private double subtotal = 0.0, deliveryFee = 0.0;
    
    // Cheaper cart suggestion UI elements
    private MaterialCardView cheaperCartCard;
    private TextView suggestedSupermarketName;
    private TextView suggestedCartTotal;
    private TextView savingsText;
    private MaterialButton replaceCheaperCartButton;
    
    // Cheaper cart data
    private String suggestedSupermarketId = null;
    private List<CartItem> suggestedCartItems = new ArrayList<>();
    private double suggestedCartTotalAmount = 0.0;
    private Map<String, Supermarket> supermarketMap = new HashMap<>();
    
    // Product price data by supermarket
    private Map<String, Map<String, Double>> supermarketPrices = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Use a fragment layout (copy of your activity_cart.xml)
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Bind views
        recyclerView    = view.findViewById(R.id.recyclerView);
        emptyView       = view.findViewById(R.id.emptyView);
        subtotalText    = view.findViewById(R.id.subtotalText);
        deliveryFeeText = view.findViewById(R.id.deliveryFeeText);
        totalText       = view.findViewById(R.id.totalText);
        checkoutButton  = view.findViewById(R.id.checkoutButton);
        
        // Initialize cheaper cart suggestion views
        cheaperCartCard = view.findViewById(R.id.cheaperCartCard);
        suggestedSupermarketName = view.findViewById(R.id.suggestedSupermarketName);
        suggestedCartTotal = view.findViewById(R.id.suggestedCartTotal);
        savingsText = view.findViewById(R.id.savingsText);
        replaceCheaperCartButton = view.findViewById(R.id.replaceCheaperCartButton);
        
        // Ensure the card is visible by default
        if (cheaperCartCard != null) {
            cheaperCartCard.setVisibility(View.VISIBLE);
        }
        
        // Setup cheaper cart replace button
        replaceCheaperCartButton.setOnClickListener(v -> replaceCheaperCart());

        // Recycler setup
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Checkout action
        checkoutButton.setOnClickListener(v -> {
            List<CartItem> cartItems = CartManager.getInstance().getCartItems();
            if (cartItems.isEmpty()) return;
            
            // Navigate to checkout fragment
            Bundle args = new Bundle();
            args.putParcelableArrayList("cart_items", new ArrayList<>(cartItems));
            args.putDouble("subtotal", subtotal);
            args.putDouble("delivery_fee", deliveryFee);
            
            androidx.navigation.fragment.NavHostFragment.findNavController(this)
                .navigate(R.id.action_cartFragment_to_checkoutFragment, args);
        });
        
        // Initialize supermarkets and product prices
        initializeSupermarkets();
        initializeProductPrices();

        // Initial load
        loadCartItems();
    }

    private void loadCartItems() {
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        updateUI(cartItems);
        
        // Only create cheaper cart if we have items
        if (!cartItems.isEmpty()) {
            createCheaperCartSuggestion(cartItems);
        } else {
            // Hide cheaper cart suggestion if cart is empty
            if (cheaperCartCard != null) {
                cheaperCartCard.setVisibility(View.GONE);
            }
        }
    }

    private void updateUI(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            checkoutButton.setEnabled(false);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            checkoutButton.setEnabled(true);
            adapter.updateItems(cartItems);
        }
        deliveryFee = subtotal > 0 ? 2.99 : 0.0;
        subtotalText   .setText(String.format("$%.2f", subtotal));
        deliveryFeeText.setText(String.format("$%.2f", deliveryFee));
        totalText      .setText(String.format("$%.2f", subtotal + deliveryFee));
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        CartManager.getInstance().updateItemQuantity(item, newQuantity);
        loadCartItems();
    }

    @Override
    public void onRemoveClicked(CartItem item) {
        CartManager.getInstance().removeItem(item);
        loadCartItems();
    }
    
    /**
     * Initialize supermarkets with their details
     */
    private void initializeSupermarkets() {
        // Create and add supermarkets that are in the app
        String[] supermarketIds = {"carrefour", "spinneys", "happy", "lecharcetieur", "faddoul", "fahed", "boxforless", "fakhani"};
        String[] supermarketNames = {"Carrefour", "Spinneys", "Happy", "Lecharcetieur", "Faddoul", "Fahed", "Box For Less", "Fakhani"};
        double[] deliveryFees = {2.99, 3.99, 2.49, 3.49, 2.79, 2.99, 1.99, 2.49};
        
        for (int i = 0; i < supermarketIds.length; i++) {
            Supermarket supermarket = new Supermarket();
            supermarket.setId(supermarketIds[i]);
            supermarket.setName(supermarketNames[i]);
            supermarket.setBaseDeliveryFee(deliveryFees[i]);
            supermarketMap.put(supermarketIds[i], supermarket);
            
            // Initialize price map for this supermarket
            supermarketPrices.put(supermarketIds[i], new HashMap<>());
        }
        
        System.out.println("Initialized " + supermarketMap.size() + " supermarkets");
    }
    
    // We don't need to initialize product prices as we'll use the existing data structures
    
    /**
     * Create a cheaper cart suggestion with all items from a single supermarket
     * This is a simplified version that will always show a suggestion
     */
    private void createCheaperCartSuggestion(List<CartItem> currentCartItems) {
        if (currentCartItems.isEmpty()) {
            // Hide card if cart is empty
            if (cheaperCartCard != null) {
                cheaperCartCard.setVisibility(View.GONE);
            }
            return;
        }
        
        // Get the current supermarket ID from the first item
        String currentSupermarketId = currentCartItems.get(0).getSupermarketId();
        
        // Calculate current total
        double currentTotal = subtotal + deliveryFee;
        
        // Find the cheapest supermarket for these products
        Map<String, Double> supermarketTotals = new HashMap<>();
        
        // Initialize totals for each supermarket
        for (String supermarketId : supermarketPrices.keySet()) {
            // Skip the current supermarket
            if (supermarketId.equals(currentSupermarketId)) {
                continue;
            }
            supermarketTotals.put(supermarketId, 0.0);
        }
        
        // Calculate total for each supermarket
        for (CartItem item : currentCartItems) {
            String productId = item.getProductId();
            int quantity = item.getQuantity();
            
            for (String supermarketId : supermarketTotals.keySet()) {
                double price = getProductPrice(productId, supermarketId);
                double itemTotal = price * quantity;
                supermarketTotals.put(supermarketId, supermarketTotals.get(supermarketId) + itemTotal);
            }
        }
        
        // Find the cheapest supermarket
        String cheaperSupermarketId = null;
        double lowestTotal = Double.MAX_VALUE;
        
        for (Map.Entry<String, Double> entry : supermarketTotals.entrySet()) {
            String supermarketId = entry.getKey();
            double total = entry.getValue();
            
            // Add delivery fee
            Supermarket supermarket = getSupermarketById(supermarketId);
            if (supermarket != null) {
                total += supermarket.getBaseDeliveryFee();
            }
            
            if (total < lowestTotal) {
                lowestTotal = total;
                cheaperSupermarketId = supermarketId;
            }
        }
        
        // If no cheaper supermarket found, default to Box For Less or Happy
        if (cheaperSupermarketId == null) {
            cheaperSupermarketId = "boxforless";
            if ("boxforless".equals(currentSupermarketId)) {
                cheaperSupermarketId = "happy";
            }
        }
        
        // Create items for the cheaper supermarket
        List<CartItem> cheaperItems = new ArrayList<>();
        double cheaperSubtotal = 0.0;
        
        // For each item in the cart, create a version from the cheaper supermarket
        for (CartItem item : currentCartItems) {
            // Get the actual price from our price mapping
            double cheaperPrice = getProductPrice(item.getProductId(), cheaperSupermarketId);
            cheaperSubtotal += cheaperPrice * item.getQuantity();
            
            // Create a new cart item for the cheaper supermarket
            CartItem newItem = new CartItem(
                    item.getId(),
                    item.getProductId(),
                    cheaperSupermarketId,
                    item.getQuantity(),
                    cheaperPrice,
                    item.getProductName(),
                    item.getProductImage()
            );
            
            cheaperItems.add(newItem);
        }
        
        // Add delivery fee
        Supermarket supermarket = getSupermarketById(cheaperSupermarketId);
        double cheaperDeliveryFee = 1.99; // Default delivery fee
        if (supermarket != null) {
            cheaperDeliveryFee = supermarket.getBaseDeliveryFee();
        }
        double cheaperTotal = cheaperSubtotal + cheaperDeliveryFee;
        
        // Save the suggestion data
        suggestedSupermarketId = cheaperSupermarketId;
        suggestedCartItems = cheaperItems;
        suggestedCartTotalAmount = cheaperTotal;
        
        // Update UI
        if (cheaperCartCard != null && suggestedSupermarketName != null && 
            suggestedCartTotal != null && savingsText != null) {
            
            // Get the supermarket name
            String supermarketName = cheaperSupermarketId;
            if (supermarket != null) {
                supermarketName = supermarket.getName();
            } else if (supermarketMap.containsKey(cheaperSupermarketId)) {
                supermarketName = supermarketMap.get(cheaperSupermarketId).getName();
            }
            suggestedSupermarketName.setText(supermarketName);
            
            // Format the prices with two decimal places
            suggestedCartTotal.setText(String.format("$%.2f", cheaperTotal));
            double savings = currentTotal - cheaperTotal;
            savingsText.setText(String.format("$%.2f", savings));
            
            // ALWAYS show the suggestion card
            cheaperCartCard.setVisibility(View.VISIBLE);
            
            System.out.println("SHOWING CHEAPER CART CARD");
            System.out.println("Current total: $" + currentTotal);
            System.out.println("Cheaper total: $" + cheaperTotal);
        }
    }
    
    /**
     * Get a list of all available supermarkets
     */
    private List<Supermarket> getSupermarkets() {
        // In a real app, this would come from your database or API
        List<Supermarket> supermarkets = new ArrayList<>();
        
        // Add the supermarkets that are in the app
        Supermarket carrefour = new Supermarket();
        carrefour.setId("carrefour");
        carrefour.setName("Carrefour");
        carrefour.setBaseDeliveryFee(2.99);
        supermarkets.add(carrefour);
        
        Supermarket spinneys = new Supermarket();
        spinneys.setId("spinneys");
        spinneys.setName("Spinneys");
        spinneys.setBaseDeliveryFee(3.99);
        supermarkets.add(spinneys);
        
        Supermarket happy = new Supermarket();
        happy.setId("happy");
        happy.setName("Happy");
        happy.setBaseDeliveryFee(2.49);
        supermarkets.add(happy);
        
        Supermarket lecharcetieur = new Supermarket();
        lecharcetieur.setId("lecharcetieur");
        lecharcetieur.setName("Lecharcetieur");
        lecharcetieur.setBaseDeliveryFee(3.49);
        supermarkets.add(lecharcetieur);
        
        Supermarket faddoul = new Supermarket();
        faddoul.setId("faddoul");
        faddoul.setName("Faddoul");
        faddoul.setBaseDeliveryFee(2.79);
        supermarkets.add(faddoul);
        
        Supermarket fahed = new Supermarket();
        fahed.setId("fahed");
        fahed.setName("Fahed");
        fahed.setBaseDeliveryFee(2.99);
        supermarkets.add(fahed);
        
        Supermarket boxforless = new Supermarket();
        boxforless.setId("boxforless");
        boxforless.setName("Box For Less");
        boxforless.setBaseDeliveryFee(1.99);
        supermarkets.add(boxforless);
        
        Supermarket fakhani = new Supermarket();
        fakhani.setId("fakhani");
        fakhani.setName("Fakhani");
        fakhani.setBaseDeliveryFee(2.49);
        supermarkets.add(fakhani);
        
        return supermarkets;
    }
    
    /**
     * Get a supermarket by ID
     */
    private Supermarket getSupermarketById(String supermarketId) {
        for (Supermarket supermarket : getSupermarkets()) {
            if (supermarket.getId().equals(supermarketId)) {
                return supermarket;
            }
        }
        return null;
    }
    
    /**
     * Initialize product prices for each supermarket
     */
    private void initializeProductPrices() {
        // Get current cart items to initialize prices for these products
        List<CartItem> currentItems = CartManager.getInstance().getCartItems();
        
        // If cart is empty, we'll add some sample product IDs
        Set<String> productIds = new HashSet<>();
        for (CartItem item : currentItems) {
            productIds.add(item.getProductId());
        }
        
        // Add some sample products if cart is empty
        if (productIds.isEmpty()) {
            productIds.add("product1");
            productIds.add("product2");
            productIds.add("product3");
        }
        
        // For each product, set prices in each supermarket
        for (String productId : productIds) {
            // Get base price (either from cart or default)
            double basePrice = 5.0; // Default price
            for (CartItem item : currentItems) {
                if (item.getProductId().equals(productId)) {
                    basePrice = item.getPrice();
                    break;
                }
            }
            
            // Set prices for each supermarket with different factors
            for (String supermarketId : supermarketPrices.keySet()) {
                double priceFactor = getPriceFactorForSupermarket(supermarketId);
                double price = Math.round(basePrice * priceFactor * 100) / 100.0;
                
                // Store in our price map
                supermarketPrices.get(supermarketId).put(productId, price);
            }
        }
        
        System.out.println("Initialized prices for " + productIds.size() + " products across " + supermarketPrices.size() + " supermarkets");
    }
    
    /**
     * Get price factor for a specific supermarket
     */
    private double getPriceFactorForSupermarket(String supermarketId) {
        switch (supermarketId) {
            case "carrefour":
                return 1.0; // Base reference
            case "spinneys":
                return 1.2; // More expensive
            case "happy":
                return 0.9; // Cheaper
            case "lecharcetieur":
                return 1.15; // More expensive
            case "faddoul":
                return 0.95; // Slightly cheaper
            case "fahed":
                return 0.92; // Cheaper
            case "boxforless":
                return 0.85; // Much cheaper
            case "fakhani":
                return 0.93; // Cheaper
            default:
                return 1.0;
        }
    }
    
    /**
     * Get the price of a product in a specific supermarket
     */
    private double getProductPrice(String productId, String supermarketId) {
        // First check if we have the price in our map
        if (supermarketPrices.containsKey(supermarketId) && 
            supermarketPrices.get(supermarketId).containsKey(productId)) {
            return supermarketPrices.get(supermarketId).get(productId);
        }
        
        // If not found in map, calculate it
        // Base price for the product (use the current price as reference)
        double basePrice = 0.0;
        List<CartItem> currentItems = CartManager.getInstance().getCartItems();
        for (CartItem item : currentItems) {
            if (item.getProductId().equals(productId)) {
                basePrice = item.getPrice();
                break;
            }
        }
        
        if (basePrice == 0.0) {
            basePrice = 5.0; // Default price if not found
        }
        
        // Apply a price factor based on the supermarket
        double priceFactor = getPriceFactorForSupermarket(supermarketId);
        
        // Calculate the price and store it in our map for future use
        double price = Math.round(basePrice * priceFactor * 100) / 100.0;
        
        // Make sure the maps exist
        if (!supermarketPrices.containsKey(supermarketId)) {
            supermarketPrices.put(supermarketId, new HashMap<>());
        }
        
        // Store the price
        supermarketPrices.get(supermarketId).put(productId, price);
        
        return price;
    }
    
    /**
     * Replace the current cart with the cheaper suggested cart
     */
    private void replaceCheaperCart() {
        if (suggestedCartItems == null || suggestedCartItems.isEmpty()) {
            return;
        }
        
        // Clear current cart and add suggested items
        CartManager.getInstance().clearCart();
        for (CartItem item : suggestedCartItems) {
            CartManager.getInstance().addItem(item);
        }
        
        // Show confirmation
        if (getView() != null) {
            String supermarketName = suggestedSupermarketId;
            if (supermarketMap.containsKey(suggestedSupermarketId)) {
                supermarketName = supermarketMap.get(suggestedSupermarketId).getName();
            }
            
            Snackbar.make(getView(),
                    "Cart updated with items from " + supermarketName,
                    Snackbar.LENGTH_SHORT).show();
        }
        
        // Hide the suggestion card
        if (cheaperCartCard != null) {
            cheaperCartCard.setVisibility(View.GONE);
        }
        
        // Refresh the cart display
        loadCartItems();
    }
}
