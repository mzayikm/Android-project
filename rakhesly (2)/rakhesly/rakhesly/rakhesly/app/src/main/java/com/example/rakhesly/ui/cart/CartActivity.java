package com.example.rakhesly.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.data.repo.CartManager;
import com.example.rakhesly.data.repo.ProductRepo;
import com.example.rakhesly.data.repo.SupermarketRepo;
import com.example.rakhesly.ui.checkout.CheckoutActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartItemActionListener, CartComparisonDialog.OnCartSelectionListener {
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView emptyView;
    private TextView subtotalText;
    private TextView deliveryFeeText;
    private TextView totalText;
    private MaterialButton checkoutButton;
    private double subtotal = 0.0;
    private double deliveryFee = 0.0;
    
    // Cheaper cart suggestion UI elements
    private MaterialCardView cheaperCartCard;
    private TextView suggestedSupermarketName;
    private TextView suggestedCartTotal;
    private TextView savingsText;
    private MaterialButton replaceCheaperCartButton;
    
    // Cheaper cart data
    private String suggestedSupermarketId;
    private List<CartItem> suggestedCartItems;
    private double suggestedCartTotalAmount;
    private Map<String, Supermarket> supermarketMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        
        // Initialize supermarket map
        supermarketMap = new HashMap<>();

        // Initialize views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        subtotalText = findViewById(R.id.subtotalText);
        deliveryFeeText = findViewById(R.id.deliveryFeeText);
        totalText = findViewById(R.id.totalText);
        checkoutButton = findViewById(R.id.checkoutButton);
        
        // Initialize cheaper cart suggestion views
        cheaperCartCard = findViewById(R.id.cheaperCartCard);
        suggestedSupermarketName = findViewById(R.id.suggestedSupermarketName);
        suggestedCartTotal = findViewById(R.id.suggestedCartTotal);
        savingsText = findViewById(R.id.savingsText);
        replaceCheaperCartButton = findViewById(R.id.replaceCheaperCartButton);
        
        // Setup cheaper cart replace button
        replaceCheaperCartButton.setOnClickListener(v -> replaceCheaperCart());

        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Force the cheaper cart card to be visible
        cheaperCartCard.setVisibility(View.VISIBLE);
        
        // Set initial values for testing
        suggestedSupermarketName.setText("Carrefour");
        suggestedCartTotal.setText("$4.57");
        savingsText.setText("$0.81");

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        // Setup checkout button
        checkoutButton.setOnClickListener(v -> proceedToCheckout());
        
        // Hide the view cart sheet if it exists in the parent activity
        hideViewCartSheetIfPresent();

        // Load cart items locally
        loadCartItems();
        
        // Show the cheaper cart suggestion immediately
        showCheaperCartSuggestion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
        
        // Always show the cheaper cart suggestion in onResume
        showCheaperCartSuggestion();
    }
    
    /**
     * Force the cheaper cart suggestion to be visible with sample data
     */
    private void showCheaperCartSuggestion() {
        // Make sure the card is visible
        if (cheaperCartCard != null) {
            cheaperCartCard.setVisibility(View.VISIBLE);
            
            // Set sample data if not already set
            if (suggestedSupermarketName != null) {
                suggestedSupermarketName.setText("Carrefour");
            }
            
            if (suggestedCartTotal != null) {
                suggestedCartTotal.setText("$" + String.format("%.2f", subtotal * 0.85 + 2.99));
            }
            
            if (savingsText != null) {
                double savings = (subtotal + deliveryFee) - (subtotal * 0.85 + 2.99);
                savingsText.setText("$" + String.format("%.2f", savings));
            }
            
            // Create sample cart items if needed
            if (suggestedCartItems == null || suggestedCartItems.isEmpty()) {
                List<CartItem> currentItems = CartManager.getInstance().getCartItems();
                suggestedCartItems = new ArrayList<>();
                
                // Create a fake supermarket
                suggestedSupermarketId = "carrefour";
                Supermarket carrefour = new Supermarket();
                carrefour.setId(suggestedSupermarketId);
                carrefour.setName("Carrefour");
                
                // Add to map if not already there
                if (!supermarketMap.containsKey(suggestedSupermarketId)) {
                    supermarketMap.put(suggestedSupermarketId, carrefour);
                }
                
                // Create cheaper items
                for (CartItem item : currentItems) {
                    CartItem cheaperItem = new CartItem(
                            item.getId(),
                            item.getProductId(),
                            suggestedSupermarketId,
                            item.getQuantity(),
                            item.getPrice() * 0.85,
                            item.getProductName(),
                            item.getProductImage()
                    );
                    suggestedCartItems.add(cheaperItem);
                }
            }
        }
    }

    private void loadCartItems() {
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        updateUI(cartItems);
        
        // Only find cheaper cart if we have items
        if (!cartItems.isEmpty()) {
            createCheaperCartSuggestion(cartItems);
        } else {
            // Hide cheaper cart suggestion if cart is empty
            cheaperCartCard.setVisibility(View.GONE);
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

        // Calculate delivery fee (this is a simple example, you might want to implement more complex logic)
        deliveryFee = subtotal > 0 ? 2.99 : 0.0;

        // Update price displays
        subtotalText.setText(String.format("$%.2f", subtotal));
        deliveryFeeText.setText(String.format("$%.2f", deliveryFee));
        totalText.setText(String.format("$%.2f", subtotal + deliveryFee));
    }

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        // Validate the new quantity to ensure it's valid
        if (newQuantity <= 0) {
            // If quantity is zero or negative, remove the item
            CartManager.getInstance().removeItem(item);
        } else {
            // Otherwise update the quantity
            CartManager.getInstance().updateItemQuantity(item, newQuantity);
        }
        
        // Reload cart items to reflect changes
        loadCartItems();
    }

    @Override
    public void onRemoveClicked(CartItem item) {
        CartManager.getInstance().removeItem(item);
        loadCartItems();
    }
    
    private void showTrackingDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this)
            .setTitle("Track Order")
            .setMessage("Your order is being prepared and will be delivered soon!")
            .setIcon(R.drawable.ic_location)
            .setPositiveButton("OK", null);
        
        builder.show();
    }
    
    /**
     * Hide the view cart sheet from MainActivity if it exists
     * This prevents it from overlapping with the checkout button
     */
    private void hideViewCartSheetIfPresent() {
        // Try to find the view cart sheet in the parent activity
        View viewCartSheet = getWindow().getDecorView().findViewById(R.id.viewCartSheet);
        if (viewCartSheet != null) {
            viewCartSheet.setVisibility(View.GONE);
        }
    }

    private void proceedToCheckout() {
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        if (cartItems.isEmpty()) {
            return;
        }

        // Check if we have a supermarket ID in the cart items
        String currentSupermarketId = "default";
        if (!cartItems.isEmpty() && cartItems.get(0).getSupermarketId() != null) {
            currentSupermarketId = cartItems.get(0).getSupermarketId();
        }

        // Show the cart comparison dialog
        showCartComparisonDialog(cartItems, currentSupermarketId);
    }

    private void showCartComparisonDialog(List<CartItem> cartItems, String currentSupermarketId) {
        // Check if there are other supermarkets to compare with
        SupermarketRepo supermarketRepo = new SupermarketRepo();
        supermarketRepo.getSupermarkets(new SupermarketRepo.SupermarketCallback() {
            @Override
            public void onSuccess(List<Supermarket> supermarkets) {
                if (supermarkets.size() <= 1) {
                    // If there's only one supermarket, proceed directly to checkout
                    startCheckoutActivity(cartItems);
                    return;
                }

                // Show the comparison dialog
                FragmentManager fragmentManager = getSupportFragmentManager();
                CartComparisonDialog dialog = CartComparisonDialog.newInstance(cartItems, currentSupermarketId);
                dialog.show(fragmentManager, "CartComparisonDialog");
            }

            @Override
            public void onError(String errorMessage) {
                // If there's an error, proceed directly to checkout
                startCheckoutActivity(cartItems);
            }
        });
    }

    private void startCheckoutActivity(List<CartItem> cartItems) {
        // Start CheckoutActivity with cart items and totals
        Intent intent = new Intent(this, CheckoutActivity.class);
        intent.putParcelableArrayListExtra("cart_items", new ArrayList<>(cartItems));
        intent.putExtra("subtotal", subtotal);
        intent.putExtra("delivery_fee", deliveryFee);
        startActivity(intent);
    }

    @Override
    public void onKeepCurrentCart() {
        // User chose to keep the current cart
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        startCheckoutActivity(cartItems);
    }

    @Override
    public void onSwitchToSuggestedCart(String supermarketId, List<CartItem> updatedCartItems) {
        // User chose to switch to the suggested cart
        // Update the cart with the new items
        CartManager.getInstance().clearCart();
        for (CartItem item : updatedCartItems) {
            CartManager.getInstance().addItem(item);
        }
        
        // Show a confirmation message
        Snackbar.make(findViewById(android.R.id.content), 
                "Cart updated with items from new supermarket", 
                Snackbar.LENGTH_SHORT).show();
        
        // Refresh the cart display
        loadCartItems();
        
        // Proceed to checkout
        startCheckoutActivity(updatedCartItems);
    }
    
    /**
     * Create a cheaper cart suggestion with all items from a single supermarket
     */
    private void createCheaperCartSuggestion(List<CartItem> currentCartItems) {
        // Get current supermarket ID
        String currentSupermarketId = "default";
        if (!currentCartItems.isEmpty() && currentCartItems.get(0).getSupermarketId() != null) {
            currentSupermarketId = currentCartItems.get(0).getSupermarketId();
        }
        
        // Calculate current total
        double currentTotal = subtotal + deliveryFee;
        
        // Create a cheaper cart with the same items but from a different supermarket
        List<CartItem> cheaperCartItems = new ArrayList<>();
        double cheaperTotal = 0.0;
        
        // Use Carrefour as the suggested supermarket (always cheaper for demo)
        String suggestedSuperId = "carrefour";
        Supermarket suggestedSupermarket = new Supermarket();
        suggestedSupermarket.setId(suggestedSuperId);
        suggestedSupermarket.setName("Carrefour");
        supermarketMap.put(suggestedSuperId, suggestedSupermarket);
        
        // Create cheaper items for each current item (all from the same supermarket)
        for (CartItem item : currentCartItems) {
            // Apply a 15% discount for the suggested supermarket
            double cheaperPrice = item.getPrice() * 0.85;
            
            // Create updated cart item with new price
            CartItem cheaperItem = new CartItem(
                    item.getId(),
                    item.getProductId(),
                    suggestedSuperId,  // All items from the same supermarket
                    item.getQuantity(),
                    cheaperPrice,
                    item.getProductName(),
                    item.getProductImage()
            );
            
            cheaperCartItems.add(cheaperItem);
            cheaperTotal += cheaperPrice * item.getQuantity();
        }
        
        // Add delivery fee
        double deliveryFee = 2.99;
        cheaperTotal += deliveryFee;
        
        // Save the suggested cart data
        suggestedSupermarketId = suggestedSuperId;
        suggestedCartItems = cheaperCartItems;
        suggestedCartTotalAmount = cheaperTotal;
        
        // Update UI
        suggestedSupermarketName.setText(suggestedSupermarket.getName());
        suggestedCartTotal.setText(String.format("$%.2f", cheaperTotal));
        savingsText.setText(String.format("$%.2f", currentTotal - cheaperTotal));
        
        // Make sure the card is visible
        cheaperCartCard.setVisibility(View.VISIBLE);
        
        // Debug logging
        System.out.println("DEBUG: Cheaper cart card should be visible now");
        System.out.println("DEBUG: Supermarket: " + suggestedSupermarket.getName());
        System.out.println("DEBUG: Total: " + cheaperTotal);
        System.out.println("DEBUG: Savings: " + (currentTotal - cheaperTotal));
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
        Snackbar.make(findViewById(android.R.id.content),
                "Cart updated with items from " + supermarketMap.get(suggestedSupermarketId).getName(),
                Snackbar.LENGTH_SHORT).show();

        // Hide the suggestion card
        cheaperCartCard.setVisibility(View.GONE);

        // Refresh the cart display
        loadCartItems();
    }

    /**
     * Get the price of a product for a specific supermarket
     * This is a simplified implementation - in a real app, you would query a database
     */
    private double getProductPriceForSupermarket(String productId, String supermarketId) {
        // Get the current cart items
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();

        // Find the product in the current cart
        for (CartItem item : cartItems) {
            if (item.getProductId().equals(productId)) {
                // Get the current price
                double currentPrice = item.getPrice();

                // Apply a price variation based on supermarket
                // This is just for demonstration - in a real app, you would get actual prices
                double variation = 1.0;
                if (supermarketId.equals("spinneys")) {
                    variation = 1.2; // 20% more expensive
                } else if (supermarketId.equals("carrefour")) {
                    variation = 0.9; // 10% cheaper
                } else if (supermarketId.equals("charcutier")) {
                    variation = 1.1; // 10% more expensive
                } else {
                    variation = 0.95; // 5% cheaper by default
                }

                return currentPrice * variation;
            }
        }

        // Product not found
        return 0.0;
    }
}
    
