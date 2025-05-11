package com.example.rakhesly.utils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class LocationUtils {
    private static PlacesClient placesClient;

    public static void initialize(Context context, String apiKey) {
        if (!Places.isInitialized()) {
            Places.initialize(context.getApplicationContext(), apiKey);
        }
        placesClient = Places.createClient(context);
    }

    public static CompletableFuture<LatLng> geocodeAddress(String address) {
        CompletableFuture<LatLng> future = new CompletableFuture<>();

        // First, find place predictions for the address
        FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                .setQuery(address)
                .build();

        placesClient.findAutocompletePredictions(predictionsRequest)
                .addOnSuccessListener(response -> {
                    if (!response.getAutocompletePredictions().isEmpty()) {
                        AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
                        
                        // Then fetch the place details for the first prediction
                        FetchPlaceRequest placeRequest = FetchPlaceRequest.builder(
                                prediction.getPlaceId(),
                                Arrays.asList(Place.Field.LAT_LNG))
                                .build();

                        placesClient.fetchPlace(placeRequest)
                                .addOnSuccessListener(placeResponse -> {
                                    Place place = placeResponse.getPlace();
                                    if (place.getLatLng() != null) {
                                        future.complete(place.getLatLng());
                                    } else {
                                        future.completeExceptionally(
                                            new Exception("Location not found"));
                                    }
                                })
                                .addOnFailureListener(future::completeExceptionally);
                    } else {
                        future.completeExceptionally(new Exception("Address not found"));
                    }
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }

    public static CompletableFuture<String> reverseGeocode(LatLng location) {
        CompletableFuture<String> future = new CompletableFuture<>();

        // Create a place request for the given coordinates
        String placeId = String.format("%.8f,%.8f", location.latitude, location.longitude);
        FetchPlaceRequest request = FetchPlaceRequest.builder(
                placeId,
                Arrays.asList(Place.Field.ADDRESS))
                .build();

        placesClient.fetchPlace(request)
                .addOnSuccessListener(response -> {
                    Place place = response.getPlace();
                    if (place.getAddress() != null) {
                        future.complete(place.getAddress());
                    } else {
                        future.completeExceptionally(new Exception("Address not found"));
                    }
                })
                .addOnFailureListener(future::completeExceptionally);

        return future;
    }
} 