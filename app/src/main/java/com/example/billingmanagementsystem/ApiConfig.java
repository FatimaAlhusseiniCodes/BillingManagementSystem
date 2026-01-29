package com.example.billingmanagementsystem;

/**
 * API Configuration
 * Central place for all API URLs and settings
 */
public class ApiConfig {

    // ==================== BASE URL ====================

    /**
     * IMPORTANT: Change this to your actual server IP/domain
     *
     * Local Development:
     * - Use "10.0.2.2" for Android Emulator (points to host machine's localhost)
     * - Use your computer's IP (e.g., "192.168.1.100") for physical device
     *
     * Production:
     * - Use your actual domain or server IP
     */
    // For physical device:
    private static final String BASE_URL = "http://192.168.1.106/billing_api/";
    // For production: private static final String BASE_URL = "https://yourdomain.com/billing_api/";

    // ==================== USER ENDPOINTS ====================

    public static final String LOGIN = BASE_URL + "api/users/login.php";
    public static final String LOGOUT = BASE_URL + "api/users/logout.php";
    public static final String REGISTER = BASE_URL + "api/users/register.php";
    public static final String READ_USER = BASE_URL + "api/users/read.php";
    public static final String UPDATE_USER = BASE_URL + "api/users/update.php";
    public static final String DELETE_USER = BASE_URL + "api/users/delete.php";

    // ==================== INVOICE ENDPOINTS ====================

    public static final String CREATE_INVOICE = BASE_URL + "api/invoices/create.php";
    public static final String READ_INVOICES = BASE_URL + "api/invoices/read.php";
    public static final String READ_INVOICE_SINGLE = BASE_URL + "api/invoices/read_single.php";
    public static final String UPDATE_INVOICE = BASE_URL + "api/invoices/update.php";
    public static final String DELETE_INVOICE = BASE_URL + "api/invoices/delete.php";
    public static final String MARK_INVOICE_PAID = BASE_URL + "api/invoices/mark_paid.php";

    // ==================== PARTNER ENDPOINTS ====================

    public static final String CREATE_PARTNER = BASE_URL + "api/partners/create.php";
    public static final String READ_PARTNERS = BASE_URL + "api/partners/read.php";
    public static final String READ_PARTNER_SINGLE = BASE_URL + "api/partners/read_single.php";
    public static final String READ_CUSTOMERS = BASE_URL + "api/partners/read_customers.php";
    public static final String READ_SUPPLIERS = BASE_URL + "api/partners/read_suppliers.php";
    public static final String UPDATE_PARTNER = BASE_URL + "api/partners/update.php";
    public static final String DELETE_PARTNER = BASE_URL + "api/partners/delete.php";

    // ==================== PAYMENT ENDPOINTS ====================

    public static final String CREATE_PAYMENT = BASE_URL + "api/payments/create.php";
    public static final String READ_PAYMENTS = BASE_URL + "api/payments/read.php";
    public static final String READ_PAYMENT_SINGLE = BASE_URL + "api/payments/read_single.php";
    public static final String UPDATE_PAYMENT = BASE_URL + "api/payments/update.php";
    public static final String DELETE_PAYMENT = BASE_URL + "api/payments/delete.php";

    // ==================== CATEGORY ENDPOINTS ====================

    public static final String CREATE_CATEGORY = BASE_URL + "api/categories/create.php";
    public static final String READ_CATEGORIES = BASE_URL + "api/categories/read.php";
    public static final String UPDATE_CATEGORY = BASE_URL + "api/categories/update.php";
    public static final String DELETE_CATEGORY = BASE_URL + "api/categories/delete.php";

    // ==================== MANUAL INCOME ENDPOINTS ====================

    public static final String CREATE_MANUAL_INCOME = BASE_URL + "api/manual_incomes/create.php";
    public static final String READ_MANUAL_INCOMES = BASE_URL + "api/manual_incomes/read.php";
    public static final String UPDATE_MANUAL_INCOME = BASE_URL + "api/manual_incomes/update.php";
    public static final String DELETE_MANUAL_INCOME = BASE_URL + "api/manual_incomes/delete.php";

    // ==================== MANUAL EXPENSE ENDPOINTS ====================

    public static final String CREATE_MANUAL_EXPENSE = BASE_URL + "api/expenses/create.php";
    public static final String READ_MANUAL_EXPENSES = BASE_URL + "api/manual_expenses/read.php";
    public static final String UPDATE_MANUAL_EXPENSE = BASE_URL + "api/manual_expenses/update.php";
    public static final String DELETE_MANUAL_EXPENSE = BASE_URL + "api/manual_expenses/delete.php";

    // ==================== COMBINED ENDPOINTS ====================

    public static final String READ_ALL_INCOMES = BASE_URL + "api/incomes/read.php";
    public static final String READ_ALL_EXPENSES = BASE_URL + "api/expenses/read.php";

    // ==================== PROFIT ENDPOINTS ====================

    public static final String READ_PROFITS = BASE_URL + "api/profits/read.php";
    public static final String CALCULATE_PROFIT = BASE_URL + "api/profits/calculate.php";

    // ==================== REQUEST TIMEOUT ====================

    public static final int TIMEOUT_MS = 10000; // 10 seconds

    // ==================== HELPER METHODS ====================

    /**
     * Get the base URL
     */
    public static String getBaseUrl() {
        return BASE_URL;
    }

    /**
     * Build URL with query parameters
     * Example: buildUrl(READ_INVOICES, "user_id", "1", "type", "Sales")
     * Returns: http://localhost/billing_api/api/invoices/read.php?user_id=1&type=Sales
     */
    public static String buildUrl(String endpoint, String... params) {
        if (params.length == 0) {
            return endpoint;
        }

        StringBuilder url = new StringBuilder(endpoint);
        url.append("?");

        for (int i = 0; i < params.length; i += 2) {
            if (i > 0) {
                url.append("&");
            }
            url.append(params[i]).append("=").append(params[i + 1]);
        }

        return url.toString();
    }
}