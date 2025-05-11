package com.example.rakhesly.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.utils.widget.MotionLabel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rakhesly.R;
import com.google.android.material.appbar.MaterialToolbar;

public class FragmentSupermarketBinding {
    public final ImageView supermarketImage;
    public final MaterialToolbar toolbar;
    public final TextView ratingText;
    public final TextView statusText;
    public final TextView addressText;
    public final TextView deliveryFeeText;
    public final RecyclerView productsRecyclerView;
    public final ProgressBar loadingView;
    public final TextView emptyView;
    private final View rootView;
    public View buttonViewAll;
    public MotionLabel textViewSupermarketName;
    public MotionLabel textViewDeliveryTime;
    public MotionLabel textViewDeliveryFee;
    public MotionLabel textViewMinOrder;
    public MotionLabel textViewRating;
    public MotionLabel textViewAddress;
    public MotionLabel textViewOpeningHours;
    public ImageView imageViewBanner;
    public MotionLabel textViewStatus;

    private FragmentSupermarketBinding(View rootView) {
        this.rootView = rootView;
        this.supermarketImage = rootView.findViewById(R.id.supermarketImage);
        this.toolbar = rootView.findViewById(R.id.toolbar);
        this.ratingText = rootView.findViewById(R.id.ratingText);
        this.statusText = rootView.findViewById(R.id.statusText);
        this.addressText = rootView.findViewById(R.id.addressText);
        this.deliveryFeeText = rootView.findViewById(R.id.deliveryFeeText);
        this.productsRecyclerView = rootView.findViewById(R.id.productsRecyclerView);
        this.loadingView = rootView.findViewById(R.id.loadingView);
        this.emptyView = rootView.findViewById(R.id.emptyView);
    }

    public static FragmentSupermarketBinding inflate(LayoutInflater inflater, ViewGroup container, boolean attachToParent) {
        View root = inflater.inflate(R.layout.activity_supermarket_detail, container, attachToParent);
        return new FragmentSupermarketBinding(root);
    }

    public View getRoot() {
        return rootView;
    }
}
