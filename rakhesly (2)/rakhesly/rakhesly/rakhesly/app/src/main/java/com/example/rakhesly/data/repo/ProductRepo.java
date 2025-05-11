package com.example.rakhesly.data.repo;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Cart;
import com.example.rakhesly.data.model.CartItem;
import com.example.rakhesly.data.model.Product;
import com.example.rakhesly.data.model.Supermarket;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRepo {
    private final FirebaseFirestore db;

    public ProductRepo() {
        db = FirebaseFirestore.getInstance();
    }

    // Initialize sample Lebanese products
    public void initializeSampleProducts() {
        // Dairy Products
        Product labanAyran = new Product("dairy1", "Laban Ayran", "Fresh Lebanese yogurt drink", "Dairy",
                "", "Tanmia", "L", 1.0, createPriceMap(1.99));
        labanAyran.setImageResource(R.drawable.laban_ayran);
        addSampleProduct(labanAyran);
        
        Product labneh = new Product("dairy2", "Labneh", "Traditional strained yogurt", "Dairy",
                "", "Dairy Khoury", "kg", 0.5, createPriceMap(4.50));
        labneh.setImageResource(R.drawable.labneh);
        addSampleProduct(labneh);
        
        Product halloumi = new Product("dairy3", "Halloumi Cheese", "Traditional grilling cheese", "Dairy",
                "", "Dairy Khoury", "kg", 0.25, createPriceMap(6.99));
        halloumi.setImageResource(R.drawable.halloumi); 
        addSampleProduct(halloumi);
        
        Product yogurt = new Product("dairy4", "Greek Yogurt", "Creamy traditional yogurt", "Dairy",
                "", "Taaneyel", "kg", 0.5, createPriceMap(3.25));
        yogurt.setImageResource(R.drawable.yogurt); 
        addSampleProduct(yogurt);
        
        // Bakery Products
        Product kaak = new Product("bakery1", "Kaak", "Lebanese street bread with sesame", "Bakery",
                "", "Lebanese Bakery", "piece", 0.1, createPriceMap(0.50));
        kaak.setImageResource(R.drawable.kaak);
        addSampleProduct(kaak);
        
        Product manoushe = new Product("bakery2", "Man'oushe Zaatar", "Traditional thyme flatbread", "Bakery",
                "", "Lebanese Bakery", "piece", 0.15, createPriceMap(0.99));
        manoushe.setImageResource(R.drawable.mankoushi_zaatar);
        addSampleProduct(manoushe);
        
        Product pita = new Product("bakery3", "Pita Bread", "Soft pocket bread", "Bakery",
                "", "Lebanese Bakery", "pack", 0.2, createPriceMap(1.25));
        pita.setImageResource(R.drawable.pita); 
        addSampleProduct(pita);
        
        Product croissant = new Product("bakery4", "Zaatar Croissant", "Flaky pastry with zaatar", "Bakery",
                "", "Lebanese Bakery", "piece", 0.1, createPriceMap(1.50));
        croissant.setImageResource(R.drawable.croissant); 
        addSampleProduct(croissant);
        
        // Snacks
        Product bonjus = new Product("snacks1", "Bonjus", "Popular fruit nectar drink", "Beverages",
                "", "Bonjus", "pack", 0.25, createPriceMap(2.75));
        bonjus.setImageResource(R.drawable.bonjus);
        addSampleProduct(bonjus);
        
        Product masterChips = new Product("snacks2", "Master Chips", "Local potato chips", "Snacks",
                "", "Master", "pack", 0.15, createPriceMap(1.25));
        masterChips.setImageResource(R.drawable.master_chips);
        addSampleProduct(masterChips);
        
        Product fantaOrange = new Product("beverages1", "Fanta Orange", "Carbonated orange drink", "Beverages",
                "", "Coca-Cola", "bottle", 0.33, createPriceMap(0.99));
        fantaOrange.setImageResource(R.drawable.fanta); 
        addSampleProduct(fantaOrange);
        
        Product cocaCola = new Product("beverages2", "Coca-Cola", "Classic cola drink", "Beverages",
                "", "Coca-Cola", "bottle", 0.33, createPriceMap(0.99));
        cocaCola.setImageResource(R.drawable.coca_cola); 
        addSampleProduct(cocaCola);
        
        // Fruits & Vegetables
        Product cucumber = new Product("produce1", "Lebanese Cucumber", "Fresh local cucumber", "Produce",
                "", "Local Farms", "kg", 1.0, createPriceMap(2.25));
        cucumber.setImageResource(R.drawable.cucumber);
        addSampleProduct(cucumber);
        
        Product potatoes = new Product("produce2", "Bekaa Potatoes", "Premium potatoes from Bekaa Valley", "Produce",
                "", "Bekaa Farms", "kg", 1.0, createPriceMap(1.75));
        potatoes.setImageResource(R.drawable.potato);
        addSampleProduct(potatoes);
        
        Product tomatoes = new Product("produce3", "Vine Tomatoes", "Fresh local tomatoes", "Produce",
                "", "Local Farms", "kg", 1.0, createPriceMap(2.50));
        tomatoes.setImageResource(R.drawable.tomato); 
        addSampleProduct(tomatoes);
        
        Product lettuce = new Product("produce4", "Romaine Lettuce", "Crisp green lettuce", "Produce",
                "", "Bekaa Farms", "piece", 0.5, createPriceMap(1.25));
        lettuce.setImageResource(R.drawable.lettuce); 
        addSampleProduct(lettuce);
        
        // Meat & Poultry
        Product chicken = new Product("meat1", "Fresh Local Chicken", "Whole fresh chicken", "Meat",
                "", "Hawa Chicken", "kg", 1.0, createPriceMap(8.50));
        chicken.setImageResource(R.drawable.chicken);
        addSampleProduct(chicken);
        
        Product lambKofta = new Product("meat2", "Lamb Kofta", "Fresh ground lamb meat", "Meat",
                "", "Local Butcher", "kg", 1.0, createPriceMap(19.99));
        lambKofta.setImageResource(R.drawable.lamb_kafta);
        addSampleProduct(lambKofta);
        
        Product beefSteak = new Product("meat3", "Beef Steak", "Premium cut beef steak", "Meat",
                "", "Local Butcher", "kg", 0.5, createPriceMap(24.99));
        beefSteak.setImageResource(R.drawable.beef_steak); 
        addSampleProduct(beefSteak);
        
        Product chickenBreast = new Product("meat4", "Turkey Thighs", "Turkey Thighs", "Meat",
                "", "Hawa Chicken", "kg", 0.5, createPriceMap(9.99));
        chickenBreast.setImageResource(R.drawable.turkey);
        addSampleProduct(chickenBreast);
        
        // Canned & Preserved Foods
        Product hummus = new Product("canned1", "Hummus", "Ready-to-eat chickpea dip", "Canned",
                "", "Al Wadi", "jar", 0.25, createPriceMap(3.50));
        hummus.setImageResource(R.drawable.hummus); 
        addSampleProduct(hummus);
        
        Product tahini = new Product("canned2", "Tahini", "Sesame paste", "Canned",
                "", "Al Wadi", "jar", 0.5, createPriceMap(4.99));
        tahini.setImageResource(R.drawable.tahini); 
        addSampleProduct(tahini);
        
        // Grains & Legumes
        Product bulgur = new Product("grains1", "Fine Bulgur", "Cracked wheat for tabbouleh", "Grains",
                "", "Lebanese Mills", "kg", 1.0, createPriceMap(2.99));
        bulgur.setImageResource(R.drawable.bulgur); 
        addSampleProduct(bulgur);
        
        Product lentils = new Product("grains2", "Red Lentils", "Dried lentils for mujadara", "Grains",
                "", "Lebanese Mills", "kg", 1.0, createPriceMap(3.25));
        lentils.setImageResource(R.drawable.lentils); 
        addSampleProduct(lentils);
        
        // Spices & Condiments
        Product zaatar = new Product("spices1", "Zaatar Mix", "Traditional Lebanese herb blend", "Spices",
                "", "Al Wadi", "pack", 0.25, createPriceMap(4.50));
        zaatar.setImageResource(R.drawable.zaatar); 
        addSampleProduct(zaatar);
        
        Product sumac = new Product("spices2", "Sumac", "Tangy red spice", "Spices",
                "", "Al Wadi", "pack", 0.1, createPriceMap(3.75));
        sumac.setImageResource(R.drawable.sumac); 
        addSampleProduct(sumac);
        
        // Sweets & Desserts
        Product baklava = new Product("sweets1", "Baklava", "Traditional pastry with nuts and syrup", "Sweets",
                "", "Lebanese Sweets", "kg", 0.5, createPriceMap(18.99));
        baklava.setImageResource(R.drawable.baklava); 
        addSampleProduct(baklava);
        
        Product maamoul = new Product("sweets2", "Maamoul", "Date-filled cookies", "Sweets",
                "", "Lebanese Sweets", "kg", 0.5, createPriceMap(12.50));
        maamoul.setImageResource(R.drawable.maamoul); 
        addSampleProduct(maamoul);
        
        // Nuts & Dried Fruits
        Product pistachios = new Product("nuts1", "Pistachios", "Roasted and salted pistachios", "Nuts",
                "", "Lebanese Farms", "kg", 0.25, createPriceMap(15.99));
        pistachios.setImageResource(R.drawable.pistachios); 
        addSampleProduct(pistachios);
        
        Product driedApricots = new Product("nuts2", "Dried Apricots", "Sweet dried apricots", "Nuts",
                "", "Lebanese Farms", "kg", 0.25, createPriceMap(7.99));
        driedApricots.setImageResource(R.drawable.dried_apricots); 
        addSampleProduct(driedApricots);
    }

    private Map<String, Double> createPriceMap(double basePrice) {
        Map<String, Double> prices = new HashMap<>();
        // Add slight variations for different supermarkets
        prices.put("spinneys", basePrice * 1.2);  // Spinneys tends to be more expensive
        prices.put("carrefour", basePrice * 1.0); // Carrefour as baseline
        prices.put("lecharcutier", basePrice * 1.1); // Le Charcutier
        // Add more supermarkets
        prices.put("fahed", basePrice * 0.95); // Fahed
        prices.put("happy", basePrice * 1.05); // Happy
        prices.put("boxforless", basePrice * 0.9); // Box For Less
        prices.put("fakhani", basePrice * 1.0); // Fakhani
        prices.put("faddoul", basePrice * 1.05); // Faddoul
        return prices;
    }

    private void addSampleProduct(Product product) {
        db.collection("products").document(product.getId())
            .set(product)
            .addOnSuccessListener(aVoid -> System.out.println("Product added: " + product.getName()))
            .addOnFailureListener(e -> System.out.println("Error adding product: " + e.getMessage()));
    }

    // Get products by supermarket
    public Task<List<Product>> getProductsBySupermarket(String supermarketId) {
        // For testing purposes, return sample products directly instead of querying Firebase
        // This ensures products will show up even if Firebase initialization is delayed
        return Tasks.forResult(getSampleProductsForSupermarket(supermarketId));
        
        /* Commented out Firebase query for now to ensure products display
        return db.collection("products")
                .get()
                .continueWith(task -> {
                    List<Product> products = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : task.getResult()) {
                            Product product = doc.toObject(Product.class);
                            // Only include products that have a price for this supermarket
                            if (product.getSupermarketPrices() != null && product.getSupermarketPrices().containsKey(supermarketId)) {
                                products.add(product);
                            }
                        }
                    }
                    return products;
                });
        */
    }

    // Compare cart prices across supermarkets
    public Task<Map<String, Double>> compareCartPrices(Cart cart) {
        return db.collection("supermarkets")
                .get()
                .continueWith(task -> {
                    Map<String, Double> supermarketTotals = new HashMap<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : task.getResult()) {
                            Supermarket supermarket = doc.toObject(Supermarket.class);
                            double total = calculateCartTotal(cart.getItems(), supermarket.getId());
                            if (total > 0) {
                                supermarketTotals.put(supermarket.getId(), total);
                            }
                        }
                    }
                    return supermarketTotals;
                });
    }

    // Calculate cart total for a specific supermarket
    private double calculateCartTotal(List<CartItem> items, String supermarketId) {
        double total = 0;
        for (CartItem item : items) {
            Product product = getProduct(item.getProductId());
            if (product != null) {
                double price = product.getPriceForSupermarket(supermarketId);
                if (price > 0) {
                    total += price * item.getQuantity();
                } else {
                    // If any item is not available in this supermarket, return 0
                    return 0;
                }
            }
        }
        return total;
    }

    // Get a single product by ID
    private Product getProduct(String productId) {
        try {
            return db.collection("products")
                    .document(productId)
                    .get()
                    .continueWith(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            return task.getResult().toObject(Product.class);
                        }
                        return null;
                    })
                    .getResult();
        } catch (Exception e) {
            return null;
        }
    }

    // Search products across all supermarkets
    public Task<List<Product>> searchProducts(String query) {
        return db.collection("products")
                .orderBy("name")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .continueWith(task -> {
                    List<Product> products = new ArrayList<>();
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (com.google.firebase.firestore.QueryDocumentSnapshot doc : task.getResult()) {
                            products.add(doc.toObject(Product.class));
                        }
                    }
                    return products;
                });
    }

    // Add a new product
    public Task<Void> addProduct(Product product) {
        return db.collection("products").document(product.getId()).set(product);
    }

    // Add multiple products
    public void addSampleProducts() {
        // Lebanese Products
        addProduct(new Product(
            "zaatar1",
            "Lebanese Zaatar",
            "Premium quality Lebanese zaatar mix with sumac and sesame seeds",
            "Spices",
            "https://example.com/zaatar.jpg",
            "Adonis",
            "pack",
            250.0,
            createPrices(15000.0, 16500.0, 14500.0)
        ));

        addProduct(new Product(
            "labneh1",
            "Labneh Baladi",
            "Fresh strained yogurt cheese",
            "Dairy",
            "https://example.com/labneh.jpg",
            "Dairy Khoury",
            "kg",
            500.0,
            createPrices(45000.0, 48000.0, 43000.0)
        ));

        addProduct(new Product(
            "tahini1",
            "Al-Wadi Tahini",
            "Pure sesame paste",
            "Condiments",
            "https://example.com/tahini.jpg",
            "Al-Wadi",
            "jar",
            454.0,
            createPrices(38000.0, 40000.0, 37500.0)
        ));

        // International Products with Lebanese Pricing
        addProduct(new Product(
            "nutella1",
            "Nutella",
            "Hazelnut spread with cocoa",
            "Spreads",
            "https://example.com/nutella.jpg",
            "Ferrero",
            "jar",
            400.0,
            createPrices(89000.0, 92000.0, 88000.0)
        ));

        addProduct(new Product(
            "chips1",
            "Master Chips - Zaatar & Time",
            "Lebanese-style potato chips with zaatar seasoning",
            "Snacks",
            "https://example.com/chips.jpg",
            "Master",
            "pack",
            150.0,
            createPrices(12000.0, 13000.0, 11500.0)
        ));
    }

    private Map<String, Double> createPrices(double price1, double price2, double price3) {
        Map<String, Double> prices = new HashMap<>();
        prices.put("spinneys", price1);  // Spinneys
        prices.put("carrefour", price2); // Carrefour
        prices.put("lecharcutier", price3); // Le Charcutier
        return prices;
    }
    
    // Helper method to get sample products for a specific supermarket
    private List<Product> getSampleProductsForSupermarket(String supermarketId) {
        List<Product> allProducts = new ArrayList<>();
        
        // Dairy Products
        Product labanAyran = new Product("dairy1", "Laban Ayran", "Fresh Lebanese yogurt drink", "Dairy",
                "", "Tanmia", "L", 1.0, createPriceMap(1.99));
        labanAyran.setImageResource(R.drawable.laban_ayran);
        allProducts.add(labanAyran);
        
        Product labneh = new Product("dairy2", "Labneh", "Traditional strained yogurt", "Dairy",
                "", "Dairy Khoury", "kg", 0.5, createPriceMap(4.50));
        labneh.setImageResource(R.drawable.labneh);
        allProducts.add(labneh);
        
        // Bakery Products
        Product kaak = new Product("bakery1", "Kaak", "Lebanese street bread with sesame", "Bakery",
                "", "Lebanese Bakery", "piece", 0.1, createPriceMap(0.50));
        kaak.setImageResource(R.drawable.kaak);
        allProducts.add(kaak);
        
        Product manoushe = new Product("bakery2", "Man'oushe Zaatar", "Traditional thyme flatbread", "Bakery",
                "", "Lebanese Bakery", "piece", 0.15, createPriceMap(0.99));
        manoushe.setImageResource(R.drawable.mankoushi_zaatar);
        allProducts.add(manoushe);
        
        // Snacks
        Product bonjus = new Product("snacks1", "Bonjus", "Popular fruit nectar drink", "Beverages",
                "", "Bonjus", "pack", 0.25, createPriceMap(2.75));
        bonjus.setImageResource(R.drawable.bonjus);
        allProducts.add(bonjus);
        
        Product masterChips = new Product("snacks2", "Master Chips", "Local potato chips", "Snacks",
                "", "Master", "pack", 0.15, createPriceMap(1.25));
        masterChips.setImageResource(R.drawable.master_chips);
        allProducts.add(masterChips);
        
        // Fruits & Vegetables
        Product cucumber = new Product("produce1", "Lebanese Cucumber", "Fresh local cucumber", "Produce",
                "", "Local Farms", "kg", 1.0, createPriceMap(2.25));
        cucumber.setImageResource(R.drawable.cucumber);
        allProducts.add(cucumber);
        
        Product potatoes = new Product("produce2", "Bekaa Potatoes", "Premium potatoes from Bekaa Valley", "Produce",
                "", "Bekaa Farms", "kg", 1.0, createPriceMap(1.75));
        potatoes.setImageResource(R.drawable.potato);
        allProducts.add(potatoes);
        
        // Meat & Poultry
        Product chicken = new Product("meat1", "Fresh Local Chicken", "Whole fresh chicken", "Meat",
                "", "Hawa Chicken", "kg", 1.0, createPriceMap(8.50));
        chicken.setImageResource(R.drawable.chicken);
        allProducts.add(chicken);
        
        Product lambKofta = new Product("meat2", "Lamb Kofta", "Fresh ground lamb meat", "Meat",
                "", "Local Butcher", "kg", 1.0, createPriceMap(19.99));
        lambKofta.setImageResource(R.drawable.lamb_kafta);
        allProducts.add(lambKofta);
        
        // Filter products for the specific supermarket
        List<Product> filteredProducts = new ArrayList<>();
        for (Product product : allProducts) {
            if (product.getSupermarketPrices() != null && product.getSupermarketPrices().containsKey(supermarketId)) {
                filteredProducts.add(product);
            }
        }
        
        return filteredProducts;
    }
}
