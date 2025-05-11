package com.example.rakhesly.ui.supermarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Product;
import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.ui.cart.CartActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SupermarketDetailActivity extends AppCompatActivity {
    private String supermarketId;
    private FirebaseFirestore db;
    private ProductAdapter productAdapter;

    private ImageView supermarketImage;
    private TextView ratingText;
    private TextView statusText;
    private TextView addressText;
    private TextView deliveryFeeText;
    private RecyclerView productsRecyclerView;
    private View loadingView;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarket_detail);

        supermarketId = getIntent().getStringExtra("supermarket_id");
        if (supermarketId == null) {
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        supermarketImage = findViewById(R.id.supermarketImage);
        ratingText = findViewById(R.id.ratingText);
        statusText = findViewById(R.id.statusText);
        addressText = findViewById(R.id.addressText);
        deliveryFeeText = findViewById(R.id.deliveryFeeText);
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        loadingView = findViewById(R.id.loadingView);
        emptyView = findViewById(R.id.emptyView);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        productsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new ProductAdapter(new ArrayList<>(), this::onProductClick);
        productsRecyclerView.setAdapter(productAdapter);

        loadSupermarketDetails();
        loadProducts();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_supermarket_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cart) {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSupermarketDetails() {
        db.collection("supermarkets")
                .document(supermarketId)
                .get()
                .addOnSuccessListener(document -> {
                    Supermarket supermarket = document.toObject(Supermarket.class);
                    if (supermarket != null) {
                        updateSupermarketUI(supermarket);
                    }
                })
                .addOnFailureListener(e ->
                        showError("Error loading supermarket details: " + e.getMessage()));
    }

    private void loadProducts() {
        showLoading(true);
        db.collection("products")
                .whereEqualTo("supermarketId", supermarketId)
                .get()
                .addOnSuccessListener(documents -> {
                    List<Product> products = new ArrayList<>();
                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : documents) {
                        Product product = doc.toObject(Product.class);
                        if (product.isAvailable() && product.getStockQuantity() > 0) {
                            products.add(product);
                        }
                    }
                    updateProductsUI(products);
                    showLoading(false);
                })
                .addOnFailureListener(e -> {
                    showError("Error loading products: " + e.getMessage());
                    showLoading(false);
                });
    }

    private void updateSupermarketUI(Supermarket supermarket) {
        getSupportActionBar().setTitle(supermarket.getName());

        // Load supermarket image from drawable based on name
        String imageName = supermarket.getName().toLowerCase().replace(" ", "_");
        int imageResId = getResources().getIdentifier(imageName, "drawable", getPackageName());

        if (imageResId != 0) {
            supermarketImage.setImageResource(imageResId);
        } else {
            supermarketImage.setImageDrawable(null);
        }

        ratingText.setText(String.format("%.1f★", supermarket.getRating()));
        statusText.setText(supermarket.isOpen() ? "Open" : "Closed");
        statusText.setTextColor(getColor(supermarket.isOpen() ? R.color.green : R.color.red));
        addressText.setText(supermarket.getAddress());
        deliveryFeeText.setText(String.format("Delivery from $%.2f", supermarket.getBaseDeliveryFee()));
    }

    private void updateProductsUI(List<Product> products) {
        if (products.isEmpty()) {
            productsRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            productsRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            productAdapter.updateProducts(products);
        }
    }

    private void showLoading(boolean show) {
        loadingView.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            productsRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        Snackbar.make(productsRecyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void onProductClick(Product product) {
        showAddToCartDialog(product);
    }

    private void showAddToCartDialog(Product product) {
        AddToCartDialog dialog = AddToCartDialog.newInstance(product);
        dialog.setOnAddToCartListener((selectedProduct, quantity) -> {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            CartItem cartItem = new CartItem(selectedProduct, quantity);

            db.collection("carts")
                    .document(userId)
                    .collection("items")
                    .document(selectedProduct.getId())
                    .set(cartItem)
                    .addOnSuccessListener(aVoid -> {
                        String message = String.format("Added %d x %s to cart", quantity, selectedProduct.getName());
                        Snackbar.make(productsRecyclerView, message, Snackbar.LENGTH_SHORT)
                                .setAction("View Cart", v -> {
                                    Intent intent = new Intent(this, CartActivity.class);
                                    startActivity(intent);
                                })
                                .show();
                    })
                    .addOnFailureListener(e ->
                            showError("Error adding to cart: " + e.getMessage()));
        });
        dialog.show(getSupportFragmentManager(), "add_to_cart");
    }
}