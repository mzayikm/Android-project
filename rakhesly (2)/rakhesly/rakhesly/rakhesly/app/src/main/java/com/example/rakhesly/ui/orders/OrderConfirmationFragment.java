package com.example.rakhesly.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.rakhesly.R;
import com.google.android.material.button.MaterialButton;

public class OrderConfirmationFragment extends Fragment {

    private MaterialButton trackOrderButton;
    private MaterialButton returnToHomeButton;

    public OrderConfirmationFragment() {
        // Required empty public constructor
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the confirmation layout
        return inflater.inflate(R.layout.fragment_order_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize buttons
        trackOrderButton = view.findViewById(R.id.trackOrderButton);
        returnToHomeButton = view.findViewById(R.id.returnToHomeButton);

        // Set up click listeners
        trackOrderButton.setOnClickListener(v -> navigateToTracking());
        returnToHomeButton.setOnClickListener(v -> navigateToHome());
    }

    private void navigateToTracking() {
        // Navigate to the tracking fragment
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.trackingFragment);
    }

    private void navigateToHome() {
        // Navigate to the home/supermarket list fragment
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.supermarketListFragment);
    }
}
