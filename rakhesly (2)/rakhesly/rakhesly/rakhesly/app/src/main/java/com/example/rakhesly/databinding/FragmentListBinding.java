package com.example.rakhesly.databinding;

import android.app.Notification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.constraintlayout.utils.widget.MotionLabel;
import androidx.recyclerview.widget.RecyclerView;

public class FragmentListBinding {
    public RecyclerView recyclerViewSupermarkets;
    public SearchView searchView;
    public Notification.Builder progressBar;
    public Notification.Builder textViewNoResults;
    public MotionLabel textViewError;

    public static FragmentListBinding inflate(LayoutInflater inflater, ViewGroup container, boolean b) {
        return null;
    }

    public View getRoot() {
        return null;
    }
}
