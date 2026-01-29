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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private TextView tvBusinessName;
    private TextView tvWelcome;
    private LinearLayout layoutUpcomingPayments;
    private LinearLayout layoutProfitMonths;
    private TextView tvNoUpcomingPayments;
    private TextView tvNoProfitData;
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

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

        tvBusinessName = view.findViewById(R.id.tvBusinessName);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        layoutUpcomingPayments = view.findViewById(R.id.layoutUpcomingPayments);
        layoutProfitMonths = view.findViewById(R.id.layoutProfitMonths);
        tvNoUpcomingPayments = view.findViewById(R.id.tvNoUpcomingPayments);
        tvNoProfitData = view.findViewById(R.id.tvNoProfitData);

        loadUserData();
        setupQuickActions(view);
        loadUpcomingPayments();
        loadProfitData();
    }

    private void loadUserData() {
        SessionManager session = new SessionManager(getContext());
        String businessName = session.getBusinessName();
        String email = session.getEmail();

        if (businessName != null && !businessName.isEmpty()) {
            tvBusinessName.setText(businessName);
        } else {
            tvBusinessName.setText("My Business");
        }

        if (email != null && !email.isEmpty()) {
            String userName = email.split("@")[0];
            tvWelcome.setText("Welcome, " + userName);
        } else {
            tvWelcome.setText("Welcome");
        }
    }

    private void setupQuickActions(View view) {

        // New Invoice button
        view.findViewById(R.id.btnNewInvoice).setOnClickListener(v -> {
            // Method 1: Using action ID (recommended when action is defined)
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_to_addInvoice);
        });

        // New Partner button
        view.findViewById(R.id.btnNewPartner).setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_to_addPartner);
        });

        // New Expense button - goes to ADD EXPENSE (not expenses list!)
        view.findViewById(R.id.btnNewExpense).setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_to_addExpense);
        });

        // New Income button - goes to ADD INCOME (not incomes list!)
        view.findViewById(R.id.btnNewIncome).setOnClickListener(v -> {
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_home_to_addIncome);
        });
    }

    private void loadUpcomingPayments() {
        SessionManager session = new SessionManager(getContext());
        int userId = session.getUserId();

        ApiClient.getInvoices(getContext(), userId, "Purchase", "Unpaid", new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isAdded()) return;

                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        List<UpcomingPayment> payments = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);

                            String supplierName = item.optString("partner_name", "Unknown");
                            String invoiceNumber = item.optString("invoice_number", "");
                            double amount = item.optDouble("total_amount", 0.0);
                            String dueDateStr = item.optString("due_date", "");

                            Date dueDate = new Date();
                            if (!dueDateStr.isEmpty()) {
                                try {
                                    SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    dueDate = apiFormat.parse(dueDateStr);
                                } catch (Exception ignored) { }
                            }

                            payments.add(new UpcomingPayment(supplierName, invoiceNumber, amount, dueDate));
                        }

                        Collections.sort(payments, new Comparator<UpcomingPayment>() {
                            @Override
                            public int compare(UpcomingPayment p1, UpcomingPayment p2) {
                                return p1.dueDate.compareTo(p2.dueDate);
                            }
                        });

                        displayUpcomingPayments(payments);
                    }
                } catch (JSONException e) {
                    displayUpcomingPayments(new ArrayList<>());
                }
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                displayUpcomingPayments(new ArrayList<>());
            }
        });
    }

    private void displayUpcomingPayments(List<UpcomingPayment> payments) {
        layoutUpcomingPayments.removeAllViews();

        if (payments.isEmpty()) {
            tvNoUpcomingPayments.setVisibility(View.VISIBLE);
            return;
        }

        tvNoUpcomingPayments.setVisibility(View.GONE);

        int count = Math.min(payments.size(), 5);
        for (int i = 0; i < count; i++) {
            UpcomingPayment payment = payments.get(i);
            View paymentView = createPaymentItemView(payment);
            layoutUpcomingPayments.addView(paymentView);

            if (i < count - 1) {
                layoutUpcomingPayments.addView(createDivider());
            }
        }
    }

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

        if (payment.dueDate.before(new Date())) {
            tvDueDate.setTextColor(0xFFF44336);
            tvDueDate.setText("OVERDUE: " + sdf.format(payment.dueDate));
        }

        return view;
    }

    private void loadProfitData() {
        SessionManager session = new SessionManager(getContext());
        int userId = session.getUserId();

        final List<MonthlyProfit> profits = new ArrayList<>();
        final int[] monthsCompleted = {0};
        final int totalMonths = 5;

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (int i = 0; i < totalMonths; i++) {
            cal.add(Calendar.MONTH, -1);

            Calendar startCal = (Calendar) cal.clone();
            startCal.set(Calendar.DAY_OF_MONTH, 1);
            String startDate = apiFormat.format(startCal.getTime());

            Calendar endCal = (Calendar) cal.clone();
            endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            String endDate = apiFormat.format(endCal.getTime());

            final Date monthDate = startCal.getTime();

            ApiClient.calculateProfit(getContext(), userId, startDate, endDate, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (!isAdded()) return;

                    try {
                        if (response.getBoolean("success")) {
                            JSONObject data = response.getJSONObject("data");
                            double income = data.optDouble("total_revenue", 0.0);
                            double expenses = data.optDouble("total_expenses", 0.0);

                            profits.add(new MonthlyProfit(monthDate, income, expenses));
                        }
                    } catch (JSONException ignored) { }

                    monthsCompleted[0]++;
                    if (monthsCompleted[0] == totalMonths) {
                        displayProfitData(profits);
                    }
                }

                @Override
                public void onError(String error) {
                    if (!isAdded()) return;
                    monthsCompleted[0]++;
                    if (monthsCompleted[0] == totalMonths) {
                        displayProfitData(profits);
                    }
                }
            });
        }
    }

    private void displayProfitData(List<MonthlyProfit> profits) {
        Collections.sort(profits, new Comparator<MonthlyProfit>() {
            @Override
            public int compare(MonthlyProfit p1, MonthlyProfit p2) {
                return p2.date.compareTo(p1.date);
            }
        });

        layoutProfitMonths.removeAllViews();

        if (profits.isEmpty()) {
            tvNoProfitData.setVisibility(View.VISIBLE);
            return;
        }

        tvNoProfitData.setVisibility(View.GONE);

        int count = Math.min(profits.size(), 5);
        for (int i = 0; i < count; i++) {
            MonthlyProfit profit = profits.get(i);
            View profitView = createProfitItemView(profit);
            layoutProfitMonths.addView(profitView);

            if (i < count - 1) {
                layoutProfitMonths.addView(createDivider());
            }
        }
    }

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

        if (profitAmount > 0) {
            tvProfit.setTextColor(0xFF4CAF50);
        } else if (profitAmount < 0) {
            tvProfit.setTextColor(0xFFF44336);
        } else {
            tvProfit.setTextColor(0xFF757575);
        }

        tvIncome.setText("Income: ALL " + currencyFormat.format(profit.income));
        tvExpenses.setText("Expenses: ALL " + currencyFormat.format(profit.expenses));

        return view;
    }

    private View createDivider() {
        View divider = new View(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 1);
        params.setMargins(0, 12, 0, 12);
        divider.setLayoutParams(params);
        divider.setBackgroundColor(0xFFE0E0E0);
        return divider;
    }

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

    private static class MonthlyProfit {
        Date date;
        double income;
        double expenses;

        MonthlyProfit(Date date, double income, double expenses) {
            this.date = date;
            this.income = income;
            this.expenses = expenses;
        }
    }
}