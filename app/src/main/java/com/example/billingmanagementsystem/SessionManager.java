package com.example.billingmanagementsystem;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SessionManager - Handles user session and login state
 * Uses SharedPreferences to persist login data
 */
public class SessionManager {

    private static final String PREF_NAME = "BillingManagementSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_BUSINESS_NAME = "businessName";
    private static final String KEY_TAX_PERCENTAGE = "taxPercentage";
    private static final String KEY_BASE_CURRENCY = "baseCurrency";
    private static final String KEY_CURRENCY = "currency";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(int userId, String email, String businessName,
                                   int taxPercentage, String baseCurrency) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_BUSINESS_NAME, businessName);
        editor.putInt(KEY_TAX_PERCENTAGE, taxPercentage);
        editor.putString(KEY_BASE_CURRENCY, baseCurrency);
        editor.commit();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get user ID
     */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get email
     */
    public String getEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    /**
     * Get business name
     */
    public String getBusinessName() {
        return prefs.getString(KEY_BUSINESS_NAME, null);
    }

    /**
     * Get tax percentage
     */
    public int getTaxPercentage() {
        return prefs.getInt(KEY_TAX_PERCENTAGE, 0);
    }

    /**
     * Get base currency
     */
    public String getBaseCurrency() {
        return prefs.getString(KEY_BASE_CURRENCY, "USD");
    }
    // Get currency
    public String getCurrency() {
        return prefs.getString(KEY_CURRENCY, "ALL");
    }

    // Update profile
    public void updateProfile(String businessName, int taxPercentage, String currency) {
        editor.putString(KEY_BUSINESS_NAME, businessName);
        editor.putInt(KEY_TAX_PERCENTAGE, taxPercentage);
        editor.putString(KEY_CURRENCY, currency);
        editor.commit();
    }
    // Set business name (add only if missing)
    public void setBusinessName(String businessName) {
        editor.putString(KEY_BUSINESS_NAME, businessName);
        editor.commit();
    }
    /**
     * Update business name
     */
    public void updateBusinessName(String businessName) {
        editor.putString(KEY_BUSINESS_NAME, businessName);
        editor.commit();
    }

    /**
     * Logout user - clear all session data
     */
    public void logout() {
        editor.clear();
        editor.commit();
    }
}