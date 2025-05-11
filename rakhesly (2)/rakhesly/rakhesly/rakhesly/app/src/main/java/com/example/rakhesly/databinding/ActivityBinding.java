package com.example.rakhesly.databinding;

import android.view.LayoutInflater;
import android.view.View;

import com.example.rakhesly.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityBinding {
    public BottomNavigationView bottomNavigation;
    private View rootView;

    public ActivityBinding(View rootView) {
        this.rootView = rootView;
        this.bottomNavigation = (BottomNavigationView) rootView.findViewById(R.id.bottom_navigation);
    }

    public static ActivityBinding inflate(LayoutInflater layoutInflater) {
        View root = layoutInflater.inflate(R.layout.activity_main, null);
        return new ActivityBinding(root);
    }

    public View getRoot() {
        return rootView;
    }
}
