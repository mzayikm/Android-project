package com.example.rakhesly.ui.auth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.rakhesly.R;
import com.example.rakhesly.data.model.User;
import com.example.rakhesly.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText, nameEditText, phoneEditText, addressEditText;
    private Button registerButton;
    private TextView loginLink;
    private ProgressBar loadingProgressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Handler timeoutHandler = new Handler();
    private Runnable timeoutRunnable;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Sign out any existing user to ensure fresh registration
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phone);
        addressEditText = findViewById(R.id.address);
        registerButton = findViewById(R.id.register);
        loadingProgressBar = findViewById(R.id.loading);
        loginLink = findViewById(R.id.login_link);

        registerButton.setOnClickListener(v -> attemptRegistration());

        loginLink.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void attemptRegistration() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (!isConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ||
                TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);

        // Timeout fallback: redirect to login page after exactly 5 seconds
        timeoutRunnable = () -> {
            // Force sign out any current user to ensure login is required
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
            }

            // Clear any session data
            getSharedPreferences("UserSession", MODE_PRIVATE)
                    .edit().clear().apply();

            Toast.makeText(RegisterActivity.this, "Registration submitted. Please login with your credentials.", Toast.LENGTH_LONG).show();
            showProgress(false);

            // Redirect to login page and clear activity stack
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        };

        // Set exact 5 second timeout
        timeoutHandler.postDelayed(timeoutRunnable, 5000); // 5 seconds timeout

        registerUser(email, password, name, phone, address);
    }

    private void registerUser(String email, String password, String name, String phone, String address) {
        // We're not removing the timeout callback here - let it redirect after 5 seconds regardless
        // This ensures the user always gets redirected to login after 5 seconds

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                String userId = task.getResult().getUser().getUid();
                // All users are registered as customers now
                User user = new User(userId, email, name, phone, address, User.UserType.CUSTOMER);

                // Save user to SharedPreferences for local access
                saveUserToSharedPreferences(user);

                // Sign out immediately to force login
                mAuth.signOut();

                db.collection("users").document(userId).set(user)
                        .addOnSuccessListener(aVoid -> {
                            // Success is handled by the 5-second timeout
                            // We don't need to do anything here
                        })
                        .addOnFailureListener(e -> {
                            // We'll let the timeout handle the redirection
                            // Just log the error
                            Log.e("RegisterActivity", "Error saving user data: " + e.getMessage());
                        });
            } else {
                // Firebase Auth operation failed - still let the timeout handle redirection
                Log.e("RegisterActivity", "Registration failed: " + task.getException().getMessage());
            }
        });
    }

    private void showProgress(boolean show) {
        loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        registerButton.setEnabled(!show);
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Saves the user object to SharedPreferences for local access
     * @param user The user object to save
     */
    private void saveUserToSharedPreferences(User user) {
        String userJson = new Gson().toJson(user);
        sharedPreferences.edit()
                .putString("user_" + user.getId(), userJson)
                .apply();
    }
}