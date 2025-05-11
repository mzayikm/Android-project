package com.example.rakhesly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TestProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_product);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        
        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        
        // Create and set adapter with sample products
        TestProductAdapter adapter = new TestProductAdapter(createSampleProducts());
        recyclerView.setAdapter(adapter);
    }
    
    private List<TestProduct> createSampleProducts() {
        List<TestProduct> products = new ArrayList<>();
        
        products.add(new TestProduct("Laban Ayran", "15,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Labneh", "45,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Kaak", "5,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Man'oushe Zaatar", "8,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Bonjus", "25,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Master Chips", "12,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Lebanese Cucumber", "20,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Bekaa Potatoes", "15,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Fresh Local Chicken", "75,000 LBP", R.drawable.placeholder_product));
        products.add(new TestProduct("Lamb Kofta", "180,000 LBP", R.drawable.placeholder_product));
        
        return products;
    }
    
    // Simple Product class
    static class TestProduct {
        private String name;
        private String price;
        private int imageResId;
        
        public TestProduct(String name, String price, int imageResId) {
            this.name = name;
            this.price = price;
            this.imageResId = imageResId;
        }
        
        public String getName() { return name; }
        public String getPrice() { return price; }
        public int getImageResId() { return imageResId; }
    }
    
    // Simple adapter
    static class TestProductAdapter extends RecyclerView.Adapter<TestProductAdapter.ViewHolder> {
        private List<TestProduct> products;
        
        public TestProductAdapter(List<TestProduct> products) {
            this.products = products;
        }
        
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_test_product, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TestProduct product = products.get(position);
            holder.productName.setText(product.getName());
            holder.productPrice.setText(product.getPrice());
            holder.productImage.setImageResource(product.getImageResId());
        }
        
        @Override
        public int getItemCount() {
            return products.size();
        }
        
        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView productImage;
            TextView productName;
            TextView productPrice;
            
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                productImage = itemView.findViewById(R.id.productImage);
                productName = itemView.findViewById(R.id.productName);
                productPrice = itemView.findViewById(R.id.productPrice);
            }
        }
    }
}
