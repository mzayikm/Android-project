package com.example.rakhesly.data.repo;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Supermarket;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SupermarketRepo {
    private final FirebaseFirestore db;

    public SupermarketRepo() {
        db = FirebaseFirestore.getInstance();
    }

    // Initialize sample supermarkets
    public void initializeSampleSupermarkets() {
        // Add Spinneys
        Supermarket spinneys = new Supermarket();
        spinneys.setId("spinneys");
        spinneys.setName("Spinneys Lebanon");
        spinneys.setDescription("Premium supermarket chain offering high-quality local and imported products");
        spinneys.setAddress("Dbayeh Highway, Lebanon");
        spinneys.setRating(4.5);
        spinneys.setBaseDeliveryFee(15000.0); // 15,000 LBP
        spinneys.setMinimumOrderAmount(200000.0);   // 200,000 LBP
        spinneys.setLocation(new LatLng(33.8938, 35.5018));
        addSampleSupermarket(spinneys);

        // Add Carrefour
        Supermarket carrefour = new Supermarket();
        carrefour.setId("carrefour");
        carrefour.setName("Carrefour");
        carrefour.setDescription("International hypermarket chain with competitive prices");
        carrefour.setAddress("City Mall, Dora, Lebanon");
        carrefour.setRating(4.2);
        carrefour.setBaseDeliveryFee(12000.0); // 12,000 LBP
        carrefour.setMinimumOrderAmount(150000.0);   // 150,000 LBP
        carrefour.setLocation(new LatLng(33.8897, 35.5325));
        addSampleSupermarket(carrefour);

        // Add Le Charcutier
        Supermarket charcutier = new Supermarket();
        charcutier.setId("charcutier");
        charcutier.setName("Le Charcutier Aoun");
        charcutier.setDescription("Premium local supermarket known for quality meats and imported goods");
        charcutier.setAddress("Antelias, Lebanon");
        charcutier.setRating(4.4);
        charcutier.setBaseDeliveryFee(20000.0); // 20,000 LBP
        charcutier.setMinimumOrderAmount(250000.0);   // 250,000 LBP
        charcutier.setLocation(new LatLng(33.9142, 35.5969));
        addSampleSupermarket(charcutier);
    }

    private void addSampleSupermarket(Supermarket supermarket) {
        db.collection("supermarkets").document(supermarket.getId())
            .set(supermarket)
            .addOnSuccessListener(aVoid -> System.out.println("Supermarket added: " + supermarket.getName()))
            .addOnFailureListener(e -> System.out.println("Error adding supermarket: " + e.getMessage()));
    }

    // Get all supermarkets
    public Task<List<Supermarket>> getAllSupermarkets() {
        return db.collection("supermarkets")
                .get()
                .continueWith(task -> {
                    List<Supermarket> supermarkets = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : task.getResult()) {
                            supermarkets.add(doc.toObject(Supermarket.class));
                        }
                    }
                    return supermarkets;
                });
    }

    // Get nearby supermarkets within a radius (in km)
    public Task<List<Supermarket>> getNearbySupermarkets(LatLng location, double radiusKm) {
        return db.collection("supermarkets")
                .get()
                .continueWith(task -> {
                    List<Supermarket> nearbyMarkets = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : task.getResult()) {
                            Supermarket supermarket = doc.toObject(Supermarket.class);
                            double distance = calculateDistance(location, supermarket.getLocation());
                            if (distance <= radiusKm) {
                                nearbyMarkets.add(supermarket);
                            }
                        }
                    }
                    return nearbyMarkets;
                });
    }

    // Get supermarkets by rating (descending)
    public Task<List<Supermarket>> getSupermarketsByRating() {
        return db.collection("supermarkets")
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .continueWith(task -> {
                    List<Supermarket> supermarkets = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : task.getResult()) {
                            supermarkets.add(doc.toObject(Supermarket.class));
                        }
                    }
                    return supermarkets;
                });
    }

    // Get a single supermarket by ID
    public void getSupermarket(String supermarketId, SupermarketCallback supermarketCallback) {
        if (supermarketId == null || supermarketId.isEmpty()) {
            supermarketCallback.onError("Invalid supermarket ID");
            return;
        }

        getSupermarkets(new SupermarketCallback() {
            @Override
            public void onSuccess(List<Supermarket> supermarkets) {
                boolean found = false;
                for (Supermarket supermarket : supermarkets) {
                    if (supermarket != null && 
                        supermarket.getId() != null && 
                        supermarket.getId().trim().equalsIgnoreCase(supermarketId.trim())) {
                        List<Supermarket> result = new ArrayList<>();
                        result.add(supermarket);
                        supermarketCallback.onSuccess(result);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    supermarketCallback.onError("Supermarket not found with ID: " + supermarketId);
                }
            }

            @Override
            public void onError(String errorMessage) {
                supermarketCallback.onError("Error fetching supermarket: " + errorMessage);
            }
        });
    }

    // Calculate distance between two points using Haversine formula
    private double calculateDistance(LatLng point1, LatLng point2) {
        final int R = 6371; // Earth's radius in kilometers

        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double lon1 = Math.toRadians(point1.longitude);
        double lon2 = Math.toRadians(point2.longitude);

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(dLon/2) * Math.sin(dLon/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    public void getSupermarkets(SupermarketRepo.SupermarketCallback supermarketCallback) {
        try {
            List<Supermarket> supermarkets = new ArrayList<>();

            // Add Spinneys
            Supermarket spinneys = new Supermarket();
            spinneys.setId("spinneys");
            spinneys.setName("Spinneys");
            spinneys.setLocation(new LatLng(33.8938, 35.5018)); // Beirut location
            spinneys.setRating(4.7);
            spinneys.setAddress("Hamra, Lebanon");
            spinneys.setWebsite("https://www.spinneyslebanon.com");
            spinneys.setBaseDeliveryFee(2.99);
            spinneys.setPricePerKm(0.5);
            spinneys.setPrepTime(20); // Large supermarket, more prep time
            spinneys.setOpen(true);
            spinneys.setImageUrl("spinneys");
            spinneys.setBannerImageUrl("spinneys_store");
            List<String> spinneysHours = new ArrayList<>();
            spinneysHours.add("8:00 AM - 10:00 PM");
            spinneys.setOperatingHours(spinneysHours);
            supermarkets.add(spinneys);

            // Add Carrefour
            Supermarket carrefour = new Supermarket();
            carrefour.setId("carrefour");
            carrefour.setName("Carrefour");
            carrefour.setLocation(new LatLng(33.8892, 35.4952));
            carrefour.setRating(4.5);
            carrefour.setAddress("City Mall, Lebanon");
            carrefour.setWebsite("https://www.carrefourlebanon.com");
            carrefour.setBaseDeliveryFee(3.99);
            carrefour.setPricePerKm(0.6);
            carrefour.setPrepTime(25); // Largest supermarket, most prep time
            carrefour.setOpen(true);
            carrefour.setImageUrl("carrefour");
            carrefour.setBannerImageUrl("carrefour_store");
            List<String> carrefourHours = new ArrayList<>();
            carrefourHours.add("10:00 AM - 10:00 PM");
            carrefour.setOperatingHours(carrefourHours);
            supermarkets.add(carrefour);

            // Add Le Charcutier
            Supermarket leCharcutier = new Supermarket();
            leCharcutier.setId("lecharcutier");
            leCharcutier.setName("Le Charcutier");
            leCharcutier.setLocation(new LatLng(33.8933, 35.5122));
            leCharcutier.setRating(4.8);
            leCharcutier.setAddress("Achrafieh, Beirut");
            leCharcutier.setWebsite("https://www.lecharcutier.com");
            leCharcutier.setBaseDeliveryFee(4.99);
            leCharcutier.setPricePerKm(0.7);
            leCharcutier.setPrepTime(15); // Premium boutique, fast prep
            leCharcutier.setOpen(true);
            leCharcutier.setImageUrl("le_charcutier");
            leCharcutier.setBannerImageUrl("charcetieur_store");
            List<String> leCharcutierHours = new ArrayList<>();
            leCharcutierHours.add("8:00 AM - 10:00 PM");
            leCharcutier.setOperatingHours(leCharcutierHours);
            supermarkets.add(leCharcutier);

            // Add Fahed Supermarket
            Supermarket fahed = new Supermarket();
            fahed.setId("fahed");
            fahed.setName("Fahed Supermarket");
            fahed.setLocation(new LatLng(33.8941, 35.5028));
            fahed.setRating(4.3);
            fahed.setAddress("Zalka, Lebanon");
            fahed.setWebsite("https://www.fahed.com");
            fahed.setBaseDeliveryFee(3.49);
            fahed.setPricePerKm(0.55);
            fahed.setPrepTime(18); // Medium-sized, moderate prep time
            fahed.setOpen(true);
            fahed.setImageUrl("fahed_supermarket");
            fahed.setBannerImageUrl("fahed_store");
            List<String> fahedHours = new ArrayList<>();
            fahedHours.add("Open 24/7");
            fahed.setOperatingHours(fahedHours);
            supermarkets.add(fahed);

            // Add Happy
            Supermarket happy = new Supermarket();
            happy.setId("happy");
            happy.setName("Happy");
            happy.setLocation(new LatLng(33.8896, 35.4789));
            happy.setRating(4.5);
            happy.setAddress("Centro Mall, Lebanon");
            happy.setWebsite("https://www.happy.com.lb");
            happy.setBaseDeliveryFee(3.49);
            happy.setPricePerKm(0.55);
            happy.setPrepTime(17); // Medium-sized, standard prep
            happy.setOpen(true);
            happy.setImageUrl("happy");
            happy.setBannerImageUrl("happy_store");
            List<String> happyHours = new ArrayList<>();
            happyHours.add("9:00 AM - 10:00 PM");
            happy.setOperatingHours(happyHours);
            supermarkets.add(happy);

            // Add Box For Less
            Supermarket boxForLess = new Supermarket();
            boxForLess.setId("boxforless");
            boxForLess.setName("Box For Less");
            boxForLess.setLocation(new LatLng(33.8754, 35.5085));
            boxForLess.setRating(4.5);
            boxForLess.setAddress("Jounieh Highway, Lebanon");
            boxForLess.setWebsite("https://www.boxforless.com.lb");
            boxForLess.setBaseDeliveryFee(2.99);
            boxForLess.setPricePerKm(0.45);
            boxForLess.setPrepTime(18); // Medium-sized, efficient prep
            boxForLess.setOpen(true);
            boxForLess.setImageUrl("box_for_less");
            boxForLess.setBannerImageUrl("boxforless_store");
            List<String> boxForLessHours = new ArrayList<>();
            boxForLessHours.add("Open 24/7");
            boxForLess.setOperatingHours(boxForLessHours);
            supermarkets.add(boxForLess);

            // Add Fakhani
            Supermarket fakhani = new Supermarket();
            fakhani.setId("fakhani");
            fakhani.setName("Fakhani");
            fakhani.setLocation(new LatLng(33.9043, 35.4858));
            fakhani.setRating(4.6);
            fakhani.setAddress("Hamra, Lebanon");
            fakhani.setWebsite("https://www.fakhani.com.lb");
            fakhani.setBaseDeliveryFee(3.49);
            fakhani.setPricePerKm(0.55);
            fakhani.setPrepTime(16); // Medium-sized, efficient prep
            fakhani.setOpen(true);
            fakhani.setImageUrl("fakhani");
            fakhani.setBannerImageUrl("fakhani_store");
            List<String> fakhaniHours = new ArrayList<>();
            fakhaniHours.add("6:00 AM - 12:00 AM");
            fakhani.setOperatingHours(fakhaniHours);
            supermarkets.add(fakhani);

            // Add Faddoul
            Supermarket faddoul = new Supermarket();
            faddoul.setId("faddoul");
            faddoul.setName("Faddoul");
            faddoul.setLocation(new LatLng(33.8898, 35.4789));
            faddoul.setRating(4.4);
            faddoul.setAddress("Sarba Highway, Jounieh");
            faddoul.setWebsite("https://www.faddoul.com.lb");
            faddoul.setBaseDeliveryFee(3.49);
            faddoul.setPricePerKm(0.55);
            faddoul.setPrepTime(17); // Medium-sized store
            faddoul.setOpen(true);
            faddoul.setImageUrl("faddoul");
            faddoul.setBannerImageUrl("faddoul_store");
            List<String> faddoulHours = new ArrayList<>();
            faddoulHours.add("Open 24/7");
            faddoul.setOperatingHours(faddoulHours);
            supermarkets.add(faddoul);

            supermarketCallback.onSuccess(supermarkets);
        } catch (Exception e) {
            supermarketCallback.onError("Error loading supermarkets: " + e.getMessage());
        }
    }

    public void searchSupermarkets(String query, SupermarketRepo.SupermarketCallback supermarketCallback) {
        getSupermarkets(new SupermarketCallback() {
            @Override
            public void onSuccess(List<Supermarket> result) {
                List<Supermarket> filteredList = new ArrayList<>();
                String lowercaseQuery = query.toLowerCase();

                for (Supermarket supermarket : result) {
                    if (supermarket.getName().toLowerCase().contains(lowercaseQuery) ||
                        supermarket.getAddress().toLowerCase().contains(lowercaseQuery)) {
                        filteredList.add(supermarket);
                    }
                }

                supermarketCallback.onSuccess(filteredList);
            }

            @Override
            public void onError(String errorMessage) {
                supermarketCallback.onError(errorMessage);
            }
        });
    }

    public abstract static class SupermarketCallback {
        public abstract void onSuccess(List<Supermarket> result);

        public abstract void onError(String errorMessage);
    }
}