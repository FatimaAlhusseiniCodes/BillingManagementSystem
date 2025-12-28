package com.example.billingmanagementsystem; // change if your package is different

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    // Upcoming payments views
    private TextView tvUpcomingSupplierName;
    private TextView tvUpcomingSupplierEmail;
    private TextView tvUpcomingTotal;
    private TextView tvUpcomingOverdue;

    // Profit views (5 months)
    private TextView[] tvMonthName = new TextView[5];
    private TextView[] tvMonthProfit = new TextView[5];
    private TextView[] tvMonthIncome = new TextView[5];
    private TextView[] tvMonthExpenses = new TextView[5];
    private View[] layoutMonthRow = new View[5];

    // Simple model for upcoming payment (no database)
    private static class UpcomingPayment {
        String supplierName;
        String supplierEmail;
        double totalAmount;
        long dueDateMillis;

        UpcomingPayment(String supplierName, String supplierEmail,
                        double totalAmount, long dueDateMillis) {
            this.supplierName = supplierName;
            this.supplierEmail = supplierEmail;
            this.totalAmount = totalAmount;
            this.dueDateMillis = dueDateMillis;
        }
    }

    // Simple model for monthly profit (no database)
    private static class MonthProfit {
        String monthName;
        double income;
        double expenses;

        MonthProfit(String monthName, double income, double expenses) {
            this.monthName = monthName;
            this.income = income;
            this.expenses = expenses;
        }
    }

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
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Buttons navigation
        view.findViewById(R.id.btnNewInvoice).setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_home_to_newInvoice));

        view.findViewById(R.id.btnNewPartner).setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_home_to_newPartner));

        view.findViewById(R.id.btnExpenses).setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_home_to_expenses));

        view.findViewById(R.id.btnIncome).setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_home_to_income));

        // Upcoming Payments views
        tvUpcomingSupplierName = view.findViewById(R.id.tvUpcomingSupplierName);
        tvUpcomingSupplierEmail = view.findViewById(R.id.tvUpcomingSupplierEmail);
        tvUpcomingTotal = view.findViewById(R.id.tvUpcomingTotal);
        tvUpcomingOverdue = view.findViewById(R.id.tvUpcomingOverdue);

        // Profit rows (layouts)
        layoutMonthRow[0] = view.findViewById(R.id.layoutMonth1);
        layoutMonthRow[1] = view.findViewById(R.id.layoutMonth2);
        layoutMonthRow[2] = view.findViewById(R.id.layoutMonth3);
        layoutMonthRow[3] = view.findViewById(R.id.layoutMonth4);
        layoutMonthRow[4] = view.findViewById(R.id.layoutMonth5);

        // Profit text views
        tvMonthName[0] = view.findViewById(R.id.tvMonth1Name);
        tvMonthName[1] = view.findViewById(R.id.tvMonth2Name);
        tvMonthName[2] = view.findViewById(R.id.tvMonth3Name);
        tvMonthName[3] = view.findViewById(R.id.tvMonth4Name);
        tvMonthName[4] = view.findViewById(R.id.tvMonth5Name);

        tvMonthProfit[0] = view.findViewById(R.id.tvMonth1Profit);
        tvMonthProfit[1] = view.findViewById(R.id.tvMonth2Profit);
        tvMonthProfit[2] = view.findViewById(R.id.tvMonth3Profit);
        tvMonthProfit[3] = view.findViewById(R.id.tvMonth4Profit);
        tvMonthProfit[4] = view.findViewById(R.id.tvMonth5Profit);

        tvMonthIncome[0] = view.findViewById(R.id.tvMonth1Income);
        tvMonthIncome[1] = view.findViewById(R.id.tvMonth2Income);
        tvMonthIncome[2] = view.findViewById(R.id.tvMonth3Income);
        tvMonthIncome[3] = view.findViewById(R.id.tvMonth4Income);
        tvMonthIncome[4] = view.findViewById(R.id.tvMonth5Income);

        tvMonthExpenses[0] = view.findViewById(R.id.tvMonth1Expenses);
        tvMonthExpenses[1] = view.findViewById(R.id.tvMonth2Expenses);
        tvMonthExpenses[2] = view.findViewById(R.id.tvMonth3Expenses);
        tvMonthExpenses[3] = view.findViewById(R.id.tvMonth4Expenses);
        tvMonthExpenses[4] = view.findViewById(R.id.tvMonth5Expenses);

        // Fill sections
        showUpcomingPayments();
        showProfitsLastFiveMonths();
    }

    // ---------- UPCOMING PAYMENTS (in-memory, nearest due date first) ----------
    private void showUpcomingPayments() {
        List<UpcomingPayment> payments = new ArrayList<>();

        // Example data: you can change these
        payments.add(new UpcomingPayment(
                "ABC Supplies",
                "abc@supplies.com",
                1500.00,
                getMillis(2025, 1, 10)
        ));

        payments.add(new UpcomingPayment(
                "XYZ Trading",
                "xyz@trading.com",
                800.00,
                getMillis(2025, 2, 5)
        ));

        payments.add(new UpcomingPayment(
                "Global Partners",
                "info@globalpartners.com",
                2300.50,
                getMillis(2025, 3, 1)
        ));

        Collections.sort(payments, new Comparator<UpcomingPayment>() {
            @Override
            public int compare(UpcomingPayment o1, UpcomingPayment o2) {
                return Long.compare(o1.dueDateMillis, o2.dueDateMillis);
            }
        });

        if (payments.isEmpty()) {
            tvUpcomingSupplierName.setText("No upcoming payments");
            tvUpcomingSupplierEmail.setText("");
            tvUpcomingTotal.setText("");
            tvUpcomingOverdue.setText("");
            return;
        }

        UpcomingPayment first = payments.get(0);

        tvUpcomingSupplierName.setText(first.supplierName);
        tvUpcomingSupplierEmail.setText(first.supplierEmail);
        tvUpcomingTotal.setText(
                "Total: " + String.format(Locale.getDefault(), "ALL %.2f", first.totalAmount)
        );

        Date date = new Date(first.dueDateMillis);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = sdf.format(date);
        tvUpcomingOverdue.setText("Overdue: " + dateStr);
    }

    // ---------- PROFIT (last 5 months, nearest to farthest) ----------
    private void showProfitsLastFiveMonths() {
        // Start from the nearest closed month: previous month
        Calendar now = Calendar.getInstance();
        Calendar base = (Calendar) now.clone();
        base.add(Calendar.MONTH, -1);

        SimpleDateFormat monthFormat =
                new SimpleDateFormat("MMMM yyyy", Locale.getDefault());

        // Always show 5 rows (nearest to farthest)
        for (int i = 0; i < 5; i++) {
            layoutMonthRow[i].setVisibility(View.VISIBLE);

            Calendar mCal = (Calendar) base.clone();
            mCal.add(Calendar.MONTH, -i);
            String monthName = monthFormat.format(mCal.getTime());

            // Example values – change these if you want
            // Here: we simulate that nearer months are more recent data
            double income = 2000 + (4 - i) * 150;     // total income
            double expenses = 1500 + (4 - i) * 120;   // total expenses
            double profit = income - expenses;

            String sign = profit >= 0 ? "+" : "-";
            double absProfit = Math.abs(profit);

            tvMonthName[i].setText(monthName);

            // Profit with sign and absolute amount
            tvMonthProfit[i].setText(
                    sign + " " + String.format(Locale.getDefault(), "ALL %.2f", absProfit)
            );

            tvMonthIncome[i].setText(
                    "Total income: " +
                            String.format(Locale.getDefault(), "ALL %.2f", income)
            );

            tvMonthExpenses[i].setText(
                    "Total expenses: " +
                            String.format(Locale.getDefault(), "ALL %.2f", expenses)
            );
        }
    }

    // Helper: build millis for a specific date
    // month: 1 = January ... 12 = December
    private long getMillis(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1); // Calendar months are 0-based
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }
}
