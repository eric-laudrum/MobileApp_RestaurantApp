package com.gb.finddining.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class AuthManager {

    private static final String PREFS = "auth_prefs";
    private static final String KEY_USERNAME = "user_username";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_REMEMBER = "remember_me";

    public static boolean createUser(Context context, String username, String password) {
        if (context == null || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String existingUsername = prefs.getString(KEY_USERNAME, null);
        if (existingUsername != null && existingUsername.equalsIgnoreCase(username)) {
            return false; // already registered
        }
        prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_LOGGED_IN, true)
                .putBoolean(KEY_REMEMBER, true)
                .apply();
        return true;
    }

    public static boolean login(Context context, String username, String password, boolean rememberMe) {
        if (context == null || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        }
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String storedUsername = prefs.getString(KEY_USERNAME, null);
        String storedPassword = prefs.getString(KEY_PASSWORD, null);
        boolean success = username.equalsIgnoreCase(storedUsername) && password.equals(storedPassword);
        if (success) {
            prefs.edit()
                    .putString(KEY_USERNAME, username) // refresh stored user
                    .putString(KEY_PASSWORD, password)
                    .putBoolean(KEY_LOGGED_IN, true)
                    .putBoolean(KEY_REMEMBER, rememberMe)
                    .apply();
        }
        return success;
    }

    public static void logout(Context context) {
        if (context == null) return;
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .putBoolean(KEY_REMEMBER, false)
                .apply();
    }

    public static boolean isLoggedIn(Context context) {
        if (context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean remember = prefs.getBoolean(KEY_REMEMBER, false);
        boolean logged = prefs.getBoolean(KEY_LOGGED_IN, false);
        return remember && logged;
    }

    public static String getUsername(Context context) {
        if (context == null) return null;
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_USERNAME, null);
    }

    public static String getPassword(Context context) {
        if (context == null) return null;
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY_PASSWORD, null);
    }

    public static boolean shouldRemember(Context context) {
        if (context == null) return false;
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(KEY_REMEMBER, false);
    }
}
