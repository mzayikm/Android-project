package com.example.rakhesly.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SessionManager {
    private static final String PREF_NAME = "RakheslySession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_EMAIL = "userEmail";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(FirebaseUser user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, user.getUid());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.commit();
    }

    public boolean isLoggedIn() {
        // First check SharedPreferences
        boolean isLoggedInPref = pref.getBoolean(KEY_IS_LOGGED_IN, false);
        
        // Then verify with Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        
        // If there's a mismatch, update SharedPreferences
        if (isLoggedInPref && currentUser == null) {
            logoutUser();
            return false;
        } else if (!isLoggedInPref && currentUser != null) {
            createLoginSession(currentUser);
            return true;
        }
        
        return isLoggedInPref;
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    public void checkLogin() {
        // Check if user is already logged in with Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        
        // Get current state from SharedPreferences
        boolean isLoggedInPref = pref.getBoolean(KEY_IS_LOGGED_IN, false);
        String savedUserId = getUserId();
        
        if (currentUser == null && isLoggedInPref) {
            // User is logged out of Firebase but still in SharedPreferences
            logoutUser();
        } else if (currentUser != null) {
            // User is logged in to Firebase
            if (!isLoggedInPref || !currentUser.getUid().equals(savedUserId)) {
                // Update session if not in sync
                createLoginSession(currentUser);
            }
        }
    }

    public void logoutUser() {
        // Clear all data from SharedPreferences
        editor.clear();
        editor.commit();

        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut();
    }
}
