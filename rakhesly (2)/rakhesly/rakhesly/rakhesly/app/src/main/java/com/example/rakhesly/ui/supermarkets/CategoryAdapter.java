package com.example.rakhesly.ui.supermarkets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.Category;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {
    
    private OnCategoryClickListener listener;
    
    public CategoryAdapter() {
        super(new DiffUtil.ItemCallback<Category>() {
            @Override
            public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getId().equals(newItem.getId());
            }
            
            @Override
            public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
                return oldItem.getName().equals(newItem.getName()) && 
                       oldItem.getImageResource() == newItem.getImageResource();
            }
        });
    }
    
    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = getItem(position);
        holder.bind(category, listener);
    }
    
    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryImage;
        private final TextView categoryName;
        
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.imageViewCategory);
            categoryName = itemView.findViewById(R.id.textViewCategoryName);
        }
        
        public void bind(Category category, OnCategoryClickListener listener) {
            categoryName.setText(category.getName());
            
            // Set the category image
            if (category.getImageResource() != 0) {
                categoryImage.setImageResource(category.getImageResource());
            } else {
                // Set a default image if no specific image is provided
                categoryImage.setImageResource(R.drawable.ic_category_default);
            }
            
            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
    
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}
