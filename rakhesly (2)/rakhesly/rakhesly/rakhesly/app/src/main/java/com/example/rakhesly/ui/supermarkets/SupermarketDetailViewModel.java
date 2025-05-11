package com.example.rakhesly.ui.supermarkets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Category;
import com.example.rakhesly.data.model.Product;
import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.data.repo.ProductRepo;
import com.example.rakhesly.data.repo.SupermarketRepo;

import java.util.ArrayList;
import java.util.List;

public class SupermarketDetailViewModel extends ViewModel {
    private final SupermarketRepo supermarketRepo;
    private final MutableLiveData<Supermarket> supermarket = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> popularProducts = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public SupermarketDetailViewModel() {
        this.supermarketRepo = new SupermarketRepo();
    }

    public void loadSupermarket(String supermarketId) {
        // First check if the supermarket ID is valid
        if (supermarketId == null || supermarketId.isEmpty()) {
            error.postValue("Invalid supermarket ID");
            supermarket.postValue(null);
            return;
        }

        supermarketRepo.getSupermarket(supermarketId, new SupermarketRepo.SupermarketCallback() {
            @Override
            public void onSuccess(List<Supermarket> result) {
                if (result != null && !result.isEmpty()) {
                    Supermarket foundSupermarket = result.get(0);
                    // Make sure the supermarket has a valid ID
                    if (foundSupermarket.getId() != null && !foundSupermarket.getId().isEmpty()) {
                        supermarket.postValue(foundSupermarket);
                    } else {
                        error.postValue("Invalid supermarket data");
                        supermarket.postValue(null);
                    }
                } else {
                    error.postValue("Supermarket not found: " + supermarketId);
                    supermarket.postValue(null);
                }
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue("Error loading supermarket: " + errorMessage);
                supermarket.postValue(null);
            }
        });
    }

    public void loadCategories(String supermarketId) {
        List<Category> categoryList = new ArrayList<>();

        // Using categories with their corresponding images from drawable with correct case sensitivity
        Category fruitsVegetables = new Category("1", "Fruits & Vegetables", R.drawable.fruitsvegetables);
        fruitsVegetables.setSupermarketId(supermarketId);
        categoryList.add(fruitsVegetables);

        Category dairyEggs = new Category("2", "Dairy & Eggs", R.drawable.dairyeggs);
        dairyEggs.setSupermarketId(supermarketId);
        categoryList.add(dairyEggs);

        // Using ic_meat.xml for meat & poultry
        Category meatPoultry = new Category("3", "Meat & Poultry", R.drawable.meatpoultry);
        meatPoultry.setSupermarketId(supermarketId);
        categoryList.add(meatPoultry);

        Category bakery = new Category("4", "Bakery", R.drawable.bakeryy);
        bakery.setSupermarketId(supermarketId);
        categoryList.add(bakery);

        Category beverages = new Category("5", "Beverages", R.drawable.beveragess);
        beverages.setSupermarketId(supermarketId);
        categoryList.add(beverages);

        Category snacks = new Category("6", "Snacks", R.drawable.snackss);
        snacks.setSupermarketId(supermarketId);
        categoryList.add(snacks);

        Category canned= new Category("7", "Canned",R.drawable.canned);
        canned.setSupermarketId(supermarketId);
        categoryList.add(canned);

        Category grains=new Category("8","Grains",R.drawable.grains);
        grains.setSupermarketId(supermarketId);
        categoryList.add(grains);

        Category spices=new Category("9","Spices",R.drawable.spices);
        spices.setSupermarketId(supermarketId);
        categoryList.add(spices);

        Category sweets=new Category("10","Sweets",R.drawable.sweets);
        sweets.setSupermarketId(supermarketId);
        categoryList.add(sweets);

        Category nuts=new Category("11","Nuts",R.drawable.nuts);
        nuts.setSupermarketId(supermarketId);
        categoryList.add(nuts);

        categories.postValue(categoryList);
    }

    public void loadPopularProducts(String supermarketId) {
        // Get products from ProductRepo
        ProductRepo productRepo = new ProductRepo();
        productRepo.getProductsBySupermarket(supermarketId)
            .addOnSuccessListener(products -> {
                if (products != null && !products.isEmpty()) {
                    popularProducts.postValue(products);
                } else {
                    error.postValue("No products found for this supermarket");
                    popularProducts.postValue(new ArrayList<>());
                }
            })
            .addOnFailureListener(e -> {
                error.postValue("Error loading products: " + e.getMessage());
                popularProducts.postValue(new ArrayList<>());
            });
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Supermarket> getSupermarket() {
        return supermarket;
    }

    public LiveData<List<Product>> getPopularProducts() {
        return popularProducts;
    }

    public LiveData<String> getError() {
        return error;
    }
}
