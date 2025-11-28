package com.gb.finddining.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

public class AuthManager {

    private static final String PREFS = "auth_prefs";
    private static final String KEY_USERNAME = "user_username";
    private static final String KEY_PASSWORD = "user_password";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_REMEMBER = "remember_me";

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static boolean hasCredentials(SharedPreferences prefs) {
        return !TextUtils.isEmpty(prefs.getString(KEY_USERNAME, null))
                && !TextUtils.isEmpty(prefs.getString(KEY_PASSWORD, null));
    }

    public static boolean createUser(Context context, String username, String password) {
        if (context == null || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            return false;
        }
        SharedPreferences prefs = prefs(context);
        String existingUsername = prefs.getString(KEY_USERNAME, null);
        if (existingUsername != null && existingUsername.equalsIgnoreCase(username)) {
            return false;
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
        SharedPreferences prefs = prefs(context);
        String storedUsername = prefs.getString(KEY_USERNAME, null);
        String storedPassword = prefs.getString(KEY_PASSWORD, null);

        boolean success = username.equalsIgnoreCase(storedUsername) && password.equals(storedPassword);
        if (success) {
            Log.i("", "login rememberMe: "+rememberMe);
            prefs.edit()
                    .putString(KEY_USERNAME, username)
                    .putString(KEY_PASSWORD, password)
                    .putBoolean(KEY_LOGGED_IN, true)
                    .putBoolean(KEY_REMEMBER, rememberMe)
                    .apply();
        }
        return success;
    }

    public static void logout(Context context) {
        if (context == null) return;
        prefs(context)
                .edit()
                .putBoolean(KEY_LOGGED_IN, false)
                .putBoolean(KEY_REMEMBER, false)
                .apply();
    }

    public static boolean isLoggedIn(Context context) {
        if (context == null) return false;
        SharedPreferences prefs = prefs(context);
        boolean remember = prefs.getBoolean(KEY_REMEMBER, false);
        boolean logged = prefs.getBoolean(KEY_LOGGED_IN, false);
        boolean hasCreds = hasCredentials(prefs);

        return remember && (logged || hasCreds);
    }

    public static String getUsername(Context context) {
        if (context == null) return null;
        return prefs(context)
                .getString(KEY_USERNAME, null);
    }

    public static String getPassword(Context context) {
        if (context == null) return null;
        return prefs(context)
                .getString(KEY_PASSWORD, null);
    }

    public static boolean shouldRemember(Context context) {
        Log.i("AuthManager", "context: "+context);

        if (context == null) return false;

        SharedPreferences prefs = prefs(context);
        boolean hasCreds = hasCredentials(prefs);
        boolean remember = prefs.getBoolean(KEY_REMEMBER, hasCreds);
        if (hasCreds && !remember) {
            prefs.edit().putBoolean(KEY_REMEMBER, true).apply();
            remember = true;
        }
        Log.i("AuthManager", "shouldRemember: "+remember+" hasCreds: "+hasCreds);
        return remember;
    }
}
