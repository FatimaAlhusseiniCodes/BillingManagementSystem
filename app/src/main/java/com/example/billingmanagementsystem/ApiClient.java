package com.example.billingmanagementsystem;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * ApiClient - Central HTTP client for all API requests
 * Uses Volley library for networking
 */
public class ApiClient {

    private static final String TAG = "ApiClient";
    private static RequestQueue requestQueue;

    // ==================== INITIALIZATION ====================

    /**
     * Initialize Volley RequestQueue (call once in Application or MainActivity)
     */
    public static void init(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    /**
     * Get the RequestQueue instance
     */
    private static RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            init(context);
        }
        return requestQueue;
    }

    // ==================== CALLBACK INTERFACES ====================

    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    public interface ApiStringCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    // ==================== GENERIC REQUEST METHODS ====================

    /**
     * Generic GET request
     */
    public static void get(Context context, String url, final ApiCallback callback) {
        Log.d(TAG, "GET: " + url);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String errorMsg = parseError(error);
                    Log.e(TAG, "Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
        );

        getRequestQueue(context).add(request);
    }

    /**
     * Generic POST request
     */
    public static void post(Context context, String url, JSONObject params, final ApiCallback callback) {
        Log.d(TAG, "POST: " + url);
        Log.d(TAG, "Params: " + params.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                params,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String errorMsg = parseError(error);
                    Log.e(TAG, "Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
        );

        getRequestQueue(context).add(request);
    }

    /**
     * Generic PUT request
     */
    public static void put(Context context, String url, JSONObject params, final ApiCallback callback) {
        Log.d(TAG, "PUT: " + url);
        Log.d(TAG, "Params: " + params.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                params,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String errorMsg = parseError(error);
                    Log.e(TAG, "Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
        );

        getRequestQueue(context).add(request);
    }

    /**
     * Generic DELETE request
     */
    public static void delete(Context context, String url, JSONObject params, final ApiCallback callback) {
        Log.d(TAG, "DELETE: " + url);
        Log.d(TAG, "Params: " + params.toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                params,
                response -> {
                    Log.d(TAG, "Response: " + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String errorMsg = parseError(error);
                    Log.e(TAG, "Error: " + errorMsg);
                    callback.onError(errorMsg);
                }
        );

        getRequestQueue(context).add(request);
    }

    // ==================== SPECIFIC API CALLS ====================

    // ==================== PARTNERS API ====================

    /**
     * Get all partners
     */
    public static void getPartners(Context context, String type, ApiCallback callback) {
        String url = type != null && !type.isEmpty()
                ? ApiConfig.buildUrl(ApiConfig.READ_PARTNERS, "type", type)
                : ApiConfig.READ_PARTNERS;
        get(context, url, callback);
    }

    /**
     * Get single partner
     */
    public static void getPartner(Context context, int partnerId, ApiCallback callback) {
        String url = ApiConfig.buildUrl(ApiConfig.READ_PARTNER_SINGLE, "partner_id", String.valueOf(partnerId));
        get(context, url, callback);
    }

    /**
     * Create partner
     */
    public static void createPartner(Context context, String name, String contactName,
                                     String email, String phone, String type, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("name", name);
            params.put("contact_name", contactName);
            params.put("email", email);
            params.put("phone", phone);
            params.put("type", type);

            post(context, ApiConfig.CREATE_PARTNER, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    /**
     * Update partner
     */
    public static void updatePartner(Context context, int partnerId, JSONObject updates, ApiCallback callback) {
        try {
            updates.put("partner_id", partnerId);
            put(context, ApiConfig.UPDATE_PARTNER, updates, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    /**
     * Delete partner
     */
    public static void deletePartner(Context context, int partnerId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("partner_id", partnerId);
            delete(context, ApiConfig.DELETE_PARTNER, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // ==================== INCOMES API ====================

    /**
     * Get all incomes (invoiced + manual)
     */
    public static void getIncomes(Context context, int userId, String filter, ApiCallback callback) {
        String url;
        if (filter != null && !filter.isEmpty()) {
            url = ApiConfig.buildUrl(ApiConfig.READ_ALL_INCOMES, "user_id", String.valueOf(userId), "filter", filter);
        } else {
            url = ApiConfig.buildUrl(ApiConfig.READ_ALL_INCOMES, "user_id", String.valueOf(userId));
        }
        get(context, url, callback);
    }

    /**
     * Create manual income
     */
    public static void createManualIncome(Context context, int userId, int categoryId,
                                          double amount, String date, String description, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);
            params.put("category_id", categoryId);
            params.put("amount", amount);
            params.put("date", date);
            params.put("description", description);

            post(context, ApiConfig.CREATE_MANUAL_INCOME, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    /**
     * Get manual incomes only
     */
    public static void getManualIncomes(Context context, int userId, ApiCallback callback) {
        String url = ApiConfig.buildUrl(ApiConfig.READ_MANUAL_INCOMES, "user_id", String.valueOf(userId));
        get(context, url, callback);
    }

    // ==================== EXPENSES API ====================

    /**
     * Get all expenses (invoiced + manual)
     */
    public static void getExpenses(Context context, int userId, String filter, ApiCallback callback) {
        String url;
        if (filter != null && !filter.isEmpty()) {
            url = ApiConfig.buildUrl(ApiConfig.READ_ALL_EXPENSES, "user_id", String.valueOf(userId), "filter", filter);
        } else {
            url = ApiConfig.buildUrl(ApiConfig.READ_ALL_EXPENSES, "user_id", String.valueOf(userId));
        }
        get(context, url, callback);
    }

    /**
     * Create manual expense
     */
    public static void createManualExpense(Context context, int userId, int categoryId,
                                           double amount, String date, String description, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);
            params.put("category_id", categoryId);
            params.put("amount", amount);
            params.put("date", date);
            params.put("description", description);

            post(context, ApiConfig.CREATE_MANUAL_EXPENSE, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    /**
     * Get manual expenses only
     */
    public static void getManualExpenses(Context context, int userId, ApiCallback callback) {
        String url = ApiConfig.buildUrl(ApiConfig.READ_MANUAL_EXPENSES, "user_id", String.valueOf(userId));
        get(context, url, callback);
    }

    // ==================== INVOICES API ====================

    /**
     * Get all invoices
     */
    public static void getInvoices(Context context, int userId, String type, String status, ApiCallback callback) {
        StringBuilder urlBuilder = new StringBuilder(ApiConfig.READ_INVOICES);
        urlBuilder.append("?user_id=").append(userId);

        if (type != null && !type.isEmpty()) {
            urlBuilder.append("&type=").append(type);
        }
        if (status != null && !status.isEmpty()) {
            urlBuilder.append("&status=").append(status);
        }

        get(context, urlBuilder.toString(), callback);
    }

    /**
     * Get single invoice
     */
    public static void getInvoice(Context context, int invoiceId, ApiCallback callback) {
        String url = ApiConfig.buildUrl(ApiConfig.READ_INVOICE_SINGLE, "invoice_id", String.valueOf(invoiceId));
        get(context, url, callback);
    }

    /**
     * Create invoice
     */
    public static void createInvoice(Context context, JSONObject invoiceData, ApiCallback callback) {
        post(context, ApiConfig.CREATE_INVOICE, invoiceData, callback);
    }

    /**
     * Mark invoice as paid
     */
    public static void markInvoicePaid(Context context, int invoiceId, String paymentMethod, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("invoice_id", invoiceId);
            params.put("payment_method", paymentMethod);

            post(context, ApiConfig.MARK_INVOICE_PAID, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // ==================== PROFITS API ====================

    /**
     * Calculate profit for a period
     */
    public static void calculateProfit(Context context, int userId, String startDate, String endDate, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);
            params.put("period_start", startDate);
            params.put("period_end", endDate);

            post(context, ApiConfig.CALCULATE_PROFIT, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    /**
     * Get profit records
     */
    public static void getProfits(Context context, int userId, String startDate, String endDate, ApiCallback callback) {
        StringBuilder urlBuilder = new StringBuilder(ApiConfig.READ_PROFITS);
        urlBuilder.append("?user_id=").append(userId);

        if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
            urlBuilder.append("&period_start=").append(startDate);
            urlBuilder.append("&period_end=").append(endDate);
        }

        get(context, urlBuilder.toString(), callback);
    }

    // ==================== USER/AUTH API ====================

    /**
     * Login
     */
    public static void login(Context context, String email, String password, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);

            post(context, ApiConfig.LOGIN, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    /**
     * Logout
     */
    public static void logout(Context context, int userId, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);

            post(context, ApiConfig.LOGOUT, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    /**
     * Register
     */
    public static void register(Context context, String email, String password, String businessName,
                                int taxPercentage, String currency, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("email", email);
            params.put("password", password);
            params.put("business_name", businessName);
            params.put("tax_percentage", taxPercentage);
            params.put("base_currency", currency);

            post(context, ApiConfig.REGISTER, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // ==================== CATEGORIES API ====================

    /**
     * Get all categories
     */
    public static void getCategories(Context context, String type, ApiCallback callback) {
        String url = type != null && !type.isEmpty()
                ? ApiConfig.buildUrl(ApiConfig.READ_CATEGORIES, "type", type)
                : ApiConfig.READ_CATEGORIES;
        get(context, url, callback);
    }

    /**
     * Create category
     */
    public static void createCategory(Context context, String name, String type, ApiCallback callback) {
        try {
            JSONObject params = new JSONObject();
            params.put("name", name);
            params.put("type", type);

            post(context, ApiConfig.CREATE_CATEGORY, params, callback);
        } catch (JSONException e) {
            callback.onError("Failed to create request: " + e.getMessage());
        }
    }

    // ==================== HELPER METHODS ====================

    /**
     * Parse Volley error into readable message
     */
    private static String parseError(VolleyError error) {
        if (error.networkResponse != null) {
            int statusCode = error.networkResponse.statusCode;
            String data = new String(error.networkResponse.data);

            try {
                JSONObject errorJson = new JSONObject(data);
                if (errorJson.has("message")) {
                    return errorJson.getString("message");
                }
            } catch (JSONException e) {
                // Not JSON, return raw data
            }

            return "HTTP " + statusCode + ": " + data;
        }

        if (error.getMessage() != null) {
            return error.getMessage();
        }

        return "Network error occurred";
    }

    /**
     * Cancel all pending requests
     */
    public static void cancelAll(Context context) {
        if (requestQueue != null) {
            requestQueue.cancelAll(context);
        }
    }
}