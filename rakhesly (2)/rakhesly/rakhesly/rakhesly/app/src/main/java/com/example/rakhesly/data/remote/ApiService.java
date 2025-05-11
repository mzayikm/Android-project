package com.example.rakhesly.data.remote;

import com.example.rakhesly.data.model.Product;
import com.example.rakhesly.data.model.Supermarket;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("supermarkets")
    Call<List<Supermarket>> getSupermarkets();

    @GET("supermarkets/search")
    Call<List<Supermarket>> searchSupermarkets(@Query("query") String query);

    @GET("supermarkets/{id}")
    Call<Supermarket> getSupermarketById(@Path("id") String id);

    @GET("supermarkets/{id}/products")
    Call<List<Product>> getProductsBySupermarket(@Path("id") String supermarketId);

    @GET("products/search")
    Call<List<Product>> searchProducts(
            @Query("supermarketId") String supermarketId,
            @Query("query") String query);

    @GET("products/{id}")
    Call<Product> getProductById(@Path("id") String id);
}
