package com.example.rakhesly.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rakhesly.R;
import com.example.rakhesly.data.SessionManager;
import com.example.rakhesly.ui.auth.LoginActivity;
import com.example.rakhesly.ui.auth.RegisterActivity;
import com.example.rakhesly.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_TIME = 1000; // 1 second
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize session manager
        sessionManager = new SessionManager(this);

        // Use a handler to delay just slightly for the splash screen
        new Handler().postDelayed(() -> {
            // Check if user is already logged in
            if (sessionManager.isLoggedIn()) {
                // User is logged in, go directly to MainActivity
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                // User is not logged in, go to RegisterActivity
                startActivity(new Intent(SplashActivity.this, RegisterActivity.class));
            }

            // Close this activity
            finish();
        }, SPLASH_DISPLAY_TIME);
    }
}