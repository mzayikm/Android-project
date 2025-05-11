package com.example.rakhesly;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.google.firebase.FirebaseApp;
import com.example.rakhesly.data.repo.ProductRepo;
import com.example.rakhesly.data.repo.SupermarketRepo;

public class RakheslyApp extends Application {
    private ProductRepo productRepo;
    private SupermarketRepo supermarketRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        
        // Initialize repositories
        productRepo = new ProductRepo();
        supermarketRepo = new SupermarketRepo();
        
        // Add sample data with a slight delay to ensure Firebase is ready
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // First add supermarkets
            supermarketRepo.initializeSampleSupermarkets();
            
            // Then add products (after a short delay to ensure supermarkets are created)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                productRepo.initializeSampleProducts();
            }, 2000); // 2 second delay
        }, 1000); // 1 second delay
    }
}