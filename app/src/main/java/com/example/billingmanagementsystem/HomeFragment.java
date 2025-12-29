package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    // Header views
    private TextView tvBusinessName;
    private TextView tvWelcome;

    // Containers
    private LinearLayout layoutUpcomingPayments;
    private LinearLayout layoutProfitMonths;
    private TextView tvNoUpcomingPayments;
    private TextView tvNoProfitData;

    // Format for currency
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

    public HomeFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tvBusinessName = view.findViewById(R.id.tvBusinessName);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        layoutUpcomingPayments = view.findViewById(R.id.layoutUpcomingPayments);
        layoutProfitMonths = view.findViewById(R.id.layoutProfitMonths);
        tvNoUpcomingPayments = view.findViewById(R.id.tvNoUpcomingPayments);
        tvNoProfitData = view.findViewById(R.id.tvNoProfitData);

        // Load user data (TODO: Replace with actual data from database/preferences)
        loadUserData();

        // Setup quick action buttons
        setupQuickActions(view);

        // Load upcoming payments
        loadUpcomingPayments();

        // Load profit data
        loadProfitData();
    }

    /**
     * Load user and business information
     * TODO: Replace with actual data from SharedPreferences or Database
     */
    private void loadUserData() {
        // TODO: Get from SharedPreferences or User table
        String businessName = "My Business"; // Replace with actual business name
        String userName = "Fatima Alhusseini"; // Replace with actual user name

        tvBusinessName.setText(businessName);
        tvWelcome.setText("Welcome, " + userName);
    }

    /**
     * Setup quick action buttons navigation
     */
    private void setupQuickActions(View view) {
        // New Invoice
        view.findViewById(R.id.btnNewInvoice).setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.addInvoiceFragment);
        });

        // New Partner
        view.findViewById(R.id.btnNewPartner).setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.addPartnerFragment);
        });

        // New Expense
        view.findViewById(R.id.btnNewExpense).setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.expensesFragment);
        });

        // New Income
        view.findViewById(R.id.btnNewIncome).setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.incomesFragment);
        });
    }

    /**
     * Load upcoming payments from Purchase Invoices
     * Shows nearest 5 payments, sorted by due date (nearest first)
     */
    private void loadUpcomingPayments() {
        // TODO: Replace with actual database query
        // Query: SELECT * FROM invoices WHERE invoice_type='Purchase'
        //        AND status IN ('Unpaid', 'Partially Paid')
        //        ORDER BY due_date ASC LIMIT 5

        List<UpcomingPayment> payments = getSampleUpcomingPayments();

        // Sort by due date (nearest first)
        Collections.sort(payments, new Comparator<UpcomingPayment>() {
            @Override
            public int compare(UpcomingPayment p1, UpcomingPayment p2) {
                return p1.dueDate.compareTo(p2.dueDate);
            }
        });

        // Clear existing views
        layoutUpcomingPayments.removeAllViews();

        if (payments.isEmpty()) {
            tvNoUpcomingPayments.setVisibility(View.VISIBLE);
            return;
        }

        tvNoUpcomingPayments.setVisibility(View.GONE);

        // Take only first 5
        int count = Math.min(payments.size(), 5);
        for (int i = 0; i < count; i++) {
            UpcomingPayment payment = payments.get(i);
            View paymentView = createPaymentItemView(payment);
            layoutUpcomingPayments.addView(paymentView);

            // Add divider except for last item
            if (i < count - 1) {
                View divider = createDivider();
                layoutUpcomingPayments.addView(divider);
            }
        }
    }

    /**
     * Create a view for a single payment item
     */
    private View createPaymentItemView(UpcomingPayment payment) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_upcoming_payment, layoutUpcomingPayments, false);

        TextView tvSupplierName = view.findViewById(R.id.tvSupplierName);
        TextView tvInvoiceNumber = view.findViewById(R.id.tvInvoiceNumber);
        TextView tvAmount = view.findViewById(R.id.tvAmount);
        TextView tvDueDate = view.findViewById(R.id.tvDueDate);

        tvSupplierName.setText(payment.supplierName);
        tvInvoiceNumber.setText("Invoice: " + payment.invoiceNumber);
        tvAmount.setText("ALL " + currencyFormat.format(payment.amount));

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        tvDueDate.setText("Due: " + sdf.format(payment.dueDate));

        // Check if overdue
        if (payment.dueDate.before(new Date())) {
            tvDueDate.setTextColor(0xFFF44336); // Red color for overdue
            tvDueDate.setText("OVERDUE: " + sdf.format(payment.dueDate));
        }

        return view;
    }

    /**
     * Load profit data for last 5 months
     * Shows profits from newest to oldest
     */
    private void loadProfitData() {
        // TODO: Replace with actual database query
        // Query: SELECT month, year, SUM(income) as total_income, SUM(expenses) as total_expenses
        //        FROM monthly_summary
        //        GROUP BY year, month
        //        ORDER BY year DESC, month DESC LIMIT 5

        List<MonthlyProfit> profits = getSampleMonthlyProfits();

        // Sort by date (newest first)
        Collections.sort(profits, new Comparator<MonthlyProfit>() {
            @Override
            public int compare(MonthlyProfit p1, MonthlyProfit p2) {
                return p2.date.compareTo(p1.date); // Descending order
            }
        });

        // Clear existing views
        layoutProfitMonths.removeAllViews();

        if (profits.isEmpty()) {
            tvNoProfitData.setVisibility(View.VISIBLE);
            return;
        }

        tvNoProfitData.setVisibility(View.GONE);

        // Take only first 5
        int count = Math.min(profits.size(), 5);
        for (int i = 0; i < count; i++) {
            MonthlyProfit profit = profits.get(i);
            View profitView = createProfitItemView(profit);
            layoutProfitMonths.addView(profitView);

            // Add divider except for last item
            if (i < count - 1) {
                View divider = createDivider();
                layoutProfitMonths.addView(divider);
            }
        }
    }

    /**
     * Create a view for a single profit month item
     */
    private View createProfitItemView(MonthlyProfit profit) {
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_monthly_profit, layoutProfitMonths, false);

        TextView tvMonthName = view.findViewById(R.id.tvMonthName);
        TextView tvProfit = view.findViewById(R.id.tvProfit);
        TextView tvIncome = view.findViewById(R.id.tvIncome);
        TextView tvExpenses = view.findViewById(R.id.tvExpenses);

        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        tvMonthName.setText(monthFormat.format(profit.date));

        double profitAmount = profit.income - profit.expenses;
        String sign = profitAmount >= 0 ? "+" : "";
        tvProfit.setText(sign + " ALL " + currencyFormat.format(profitAmount));

        // Color code profit
        if (profitAmount > 0) {
            tvProfit.setTextColor(0xFF4CAF50); // Green for profit
        } else if (profitAmount < 0) {
            tvProfit.setTextColor(0xFFF44336); // Red for loss
        } else {
            tvProfit.setTextColor(0xFF757575); // Gray for zero
        }

        tvIncome.setText("Income: ALL " + currencyFormat.format(profit.income));
        tvExpenses.setText("Expenses: ALL " + currencyFormat.format(profit.expenses));

        return view;
    }

    /**
     * Create a divider view
     */
    private View createDivider() {
        View divider = new View(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                1 // 1dp height
        );
        params.setMargins(0, 12, 0, 12);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(0xFFE0E0E0); // Light gray
        return divider;
    }

    // ==================== SAMPLE DATA (Remove when connecting to database) ====================

    /**
     * Sample upcoming payments data
     * TODO: Replace with actual database query
     */
    private List<UpcomingPayment> getSampleUpcomingPayments() {
        List<UpcomingPayment> payments = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        // Payment 1 - Overdue
        cal.set(2024, Calendar.DECEMBER, 15);
        payments.add(new UpcomingPayment(
                "ABC Supplies",
                "INV-001",
                1500.00,
                cal.getTime()
        ));

        // Payment 2 - Due soon
        cal.set(2025, Calendar.JANUARY, 5);
        payments.add(new UpcomingPayment(
                "XYZ Trading",
                "INV-002",
                2300.50,
                cal.getTime()
        ));

        // Payment 3
        cal.set(2025, Calendar.JANUARY, 15);
        payments.add(new UpcomingPayment(
                "Global Partners",
                "INV-003",
                890.00,
                cal.getTime()
        ));

        // Payment 4
        cal.set(2025, Calendar.FEBRUARY, 1);
        payments.add(new UpcomingPayment(
                "Tech Solutions",
                "INV-004",
                4500.00,
                cal.getTime()
        ));

        // Payment 5
        cal.set(2025, Calendar.FEBRUARY, 10);
        payments.add(new UpcomingPayment(
                "Office Depot",
                "INV-005",
                670.25,
                cal.getTime()
        ));

        return payments;
    }

    /**
     * Sample monthly profit data
     * TODO: Replace with actual database query
     */
    private List<MonthlyProfit> getSampleMonthlyProfits() {
        List<MonthlyProfit> profits = new ArrayList<>();

        Calendar cal = Calendar.getInstance();

        // Last 5 months
        for (int i = 0; i < 5; i++) {
            cal.add(Calendar.MONTH, -1);
            Date monthDate = cal.getTime();

            // Sample data - varies per month
            double income = 5000 + (i * 500);
            double expenses = 3500 + (i * 300);

            profits.add(new MonthlyProfit(monthDate, income, expenses));
        }

        return profits;
    }

    // ==================== DATA MODELS ====================

    /**
     * Model for upcoming payment item
     */
    private static class UpcomingPayment {
        String supplierName;
        String invoiceNumber;
        double amount;
        Date dueDate;

        UpcomingPayment(String supplierName, String invoiceNumber, double amount, Date dueDate) {
            this.supplierName = supplierName;
            this.invoiceNumber = invoiceNumber;
            this.amount = amount;
            this.dueDate = dueDate;
        }
    }

    /**
     * Model for monthly profit item
     */
    private static class MonthlyProfit {
        Date date; // First day of the month
        double income;
        double expenses;

        MonthlyProfit(Date date, double income, double expenses) {
            this.date = date;
            this.income = income;
            this.expenses = expenses;
        }
    }
}