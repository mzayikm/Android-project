package com.example.rakhesly.ui.supermarket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Supermarket;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SupermarketListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SupermarketAdapter adapter;
    private ProgressBar loadingProgressBar;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supermarket_list);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        emptyView = findViewById(R.id.emptyView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SupermarketAdapter(new ArrayList<>(), this::onSupermarketClick);
        recyclerView.setAdapter(adapter);

        // Setup SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this::loadSupermarkets);

        // Load supermarkets
        loadSupermarkets();
    }

    private void loadSupermarkets() {
        showLoading(true);
        
        db.collection("supermarkets")
            .get()
            .addOnCompleteListener(task -> {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);

                if (task.isSuccessful()) {
                    List<Supermarket> supermarkets = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Supermarket supermarket = document.toObject(Supermarket.class);
                        supermarkets.add(supermarket);
                    }
                    updateUI(supermarkets);
                } else {
                    showError("Error loading supermarkets: " + task.getException().getMessage());
                }
            });
    }

    private void updateUI(List<Supermarket> supermarkets) {
        if (supermarkets.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            adapter.updateSupermarkets(supermarkets);
        }
    }

    private void showLoading(boolean show) {
        loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG).show();
    }

    private void onSupermarketClick(Supermarket supermarket) {
        Intent intent = new Intent(this, SupermarketDetailActivity.class);
        intent.putExtra("supermarket_id", supermarket.getId());
        startActivity(intent);
    }
} 