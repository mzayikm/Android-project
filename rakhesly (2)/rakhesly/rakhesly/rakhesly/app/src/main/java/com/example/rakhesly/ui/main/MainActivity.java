// MainActivity.java
package com.example.rakhesly.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.rakhesly.R;
import com.example.rakhesly.data.SessionManager;
import com.example.rakhesly.ui.auth.LoginActivity;
import com.example.rakhesly.data.repo.CartManager;
import com.example.rakhesly.data.model.CartItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import android.view.View;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private NavController navController;
    private View viewCartSheet;
    private TextView textCartItemCount;
    private TextView textCartTotal;
    private boolean isViewCartSheetVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize SessionManager
        sessionManager = new SessionManager(this);
        
        // Check if user is logged in, if not, redirect to LoginActivity
        sessionManager.checkLogin(); // This will sync Firebase and SharedPreferences state
        if (!sessionManager.isLoggedIn()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2) Grab NavController from the NavHostFragment
        NavHostFragment navHost =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
        assert navHost != null;
        navController = navHost.getNavController();

        // 3) Bottom nav
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // 4) Define top‑level destinations for the Up button behavior
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.supermarketListFragment,
                R.id.cartFragment,
                R.id.ordersFragment,
                R.id.profileFragment
        ).build();

        // 5) Hook up ActionBar and BottomNav to the NavController
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Setup view cart sheet
        setupViewCartSheet();
        
        // Register cart change listener
        CartManager.getInstance().setOnCartChangedListener(this::updateCartUI);
    }

    private void setupViewCartSheet() {
        viewCartSheet = findViewById(R.id.viewCartSheet);
        textCartItemCount = viewCartSheet.findViewById(R.id.textCartItemCount);
        textCartTotal = viewCartSheet.findViewById(R.id.textCartTotal);
        MaterialButton buttonViewCart = viewCartSheet.findViewById(R.id.buttonViewCart);
        
        buttonViewCart.setOnClickListener(v -> {
            navController.navigate(R.id.cartFragment);
            // Hide the cart sheet when navigating to cart
            hideViewCartSheet();
        });
        
        // Set up a destination change listener to hide the cart sheet when on cart fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.cartFragment) {
                // Hide the view cart sheet when on the cart fragment
                hideViewCartSheet();
            }
        });
        
        // Initial update
        updateCartUI();
    }
    
    private void updateCartUI() {
        List<CartItem> cartItems = CartManager.getInstance().getCartItems();
        if (cartItems.isEmpty()) {
            hideViewCartSheet();
            return;
        }
        
        // Update item count
        int itemCount = 0;
        double total = 0;
        for (CartItem item : cartItems) {
            itemCount += item.getQuantity();
            total += item.getPrice() * item.getQuantity();
        }
        
        textCartItemCount.setText(itemCount + (itemCount == 1 ? " Item" : " Items"));
        textCartTotal.setText(String.format("Total: $%.2f", total));
        
        // Only show the cart sheet if we're not on the cart fragment
        if (navController.getCurrentDestination() != null && 
            navController.getCurrentDestination().getId() != R.id.cartFragment) {
            showViewCartSheet();
        }
    }
    
    private void showViewCartSheet() {
        if (!isViewCartSheetVisible) {
            viewCartSheet.setVisibility(View.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            viewCartSheet.startAnimation(slideUp);
            isViewCartSheetVisible = true;
        }
    }
    
    private void hideViewCartSheet() {
        if (isViewCartSheetVisible) {
            Animation slideDown = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
            slideDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    viewCartSheet.setVisibility(View.GONE);
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            viewCartSheet.startAnimation(slideDown);
            isViewCartSheetVisible = false;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            sessionManager.logoutUser();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
