package com.example.rakhesly.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.rakhesly.data.model.User;
import com.example.rakhesly.databinding.FragmentProfileBinding;
import com.example.rakhesly.ui.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        if (currentUser == null) {
            navigateToLogin();
            return;
        }

        // Get user data from SharedPreferences
        User user = getUserFromSharedPreferences(currentUser.getUid());

        // Set user information
        if (user != null) {
            // Display user information from registration
            binding.profileName.setText(user.getName());
            binding.profileEmail.setText(user.getEmail());
            binding.profilePhone.setText("Phone: " + user.getPhone());
            binding.profileAddress.setText("Address: " + user.getAddress());
        } else {
            // Fallback to Firebase user information
            binding.profileName.setText(currentUser.getDisplayName());
            binding.profileEmail.setText(currentUser.getEmail());
            binding.profilePhone.setText("Phone: Not available");
            binding.profileAddress.setText("Address: Not available");
        }

        // Set click listeners
        binding.editProfileButton.setOnClickListener(v -> {
            // TODO: Implement edit profile functionality
            Toast.makeText(requireContext(), "Edit profile coming soon", Toast.LENGTH_SHORT).show();
        });

        binding.logoutButton.setOnClickListener(v -> logout());
    }

    /**
     * Retrieves the User object from SharedPreferences
     * @param userId The user ID to retrieve
     * @return User object or null if not found
     */
    private User getUserFromSharedPreferences(String userId) {
        String userJson = sharedPreferences.getString("user_" + userId, null);
        if (userJson != null) {
            return new Gson().fromJson(userJson, User.class);
        }
        return null;
    }

    private void logout() {
        firebaseAuth.signOut();
        navigateToLogin();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}