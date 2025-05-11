// SupermarketViewModel.java
package com.example.rakhesly.ui.supermarkets;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.rakhesly.data.model.Supermarket;
import com.example.rakhesly.data.repo.SupermarketRepo;

import java.util.List;

public class SupermarketViewModel extends ViewModel {

    private SupermarketRepo  repository;
    private MutableLiveData<List<Supermarket>> supermarkets = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public SupermarketViewModel() {
        repository = new SupermarketRepo();
    }

    public LiveData<List<Supermarket>> getSupermarkets() {
        return supermarkets;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void loadSupermarkets() {
        isLoading.setValue(true);
        error.setValue(null);

        repository.getSupermarkets(new SupermarketRepo.SupermarketCallback() {
            @Override
            public void onSuccess(List<Supermarket> result) {
                supermarkets.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
                isLoading.postValue(false);
            }
        });
    }

    public void searchSupermarkets(String query) {
        isLoading.setValue(true);
        error.setValue(null);

        repository.searchSupermarkets(query, new SupermarketRepo.SupermarketCallback() {
            @Override
            public void onSuccess(List<Supermarket> result) {
                supermarkets.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
                isLoading.postValue(false);
            }
        });
    }
}