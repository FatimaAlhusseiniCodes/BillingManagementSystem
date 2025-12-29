package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseClickListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddExpense;
    private LinearLayout layoutEmptyState;

    private ExpenseAdapter adapter;
    private List<Expense> allExpenses;
    private String currentFilter = "All"; // Default tab

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rv_expenses);
        fabAddExpense = view.findViewById(R.id.fab_add_expense);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        // Setup RecyclerView
        setupRecyclerView();

        // Load expenses
        loadExpenses();

        // Setup tabs
        setupTabs();

        // Setup FAB
        setupFAB();
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadExpenses() {
        // Combine expenses from:
        // 1. Purchase Invoices (Paid and Unpaid)
        // 2. Manual Expense Entries

        allExpenses = new ArrayList<>();

        // Get expenses from purchase invoices
        allExpenses.addAll(getExpensesFromPurchaseInvoices());

        // Get manual expenses
        allExpenses.addAll(getManualExpenses());

        // Filter and display
        filterExpenses(currentFilter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: currentFilter = "All"; break;
                    case 1: currentFilter = "Invoiced"; break;
                    case 2: currentFilter = "Manual"; break;
                    case 3: currentFilter = "Unpaid"; break;
                }
                filterExpenses(currentFilter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterExpenses(String filter) {
        List<Expense> filteredList = new ArrayList<>();

        for (Expense expense : allExpenses) {
            boolean matches = false;

            switch (filter) {
                case "All":
                    // Show everything
                    matches = true;
                    break;

                case "Invoiced":
                    // Show only PAID purchase invoices
                    matches = expense.isInvoiced();
                    break;

                case "Manual":
                    // Show only manual expenses
                    matches = expense.isManual();
                    break;

                case "Unpaid":
                    // Show only UNPAID purchase invoices
                    matches = expense.isUnpaid();
                    break;
            }

            if (matches) {
                filteredList.add(expense);
            }
        }

        adapter.setExpenses(filteredList);

        if (filteredList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupFAB() {
        fabAddExpense.setOnClickListener(v -> {
            NavHostFragment.findNavController(ExpensesFragment.this)
                    .navigate(R.id.action_expensesFragment_to_addExpenseFragment);
        });
    }

    // ==================== Listener Implementation ====================

    @Override
    public void onExpenseClick(Expense expense) {
        Toast.makeText(requireContext(),
                "Expense: " + expense.getTitle(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkAsPaidClick(Expense expense) {
        // Mark unpaid purchase invoice as paid
        expense.markAsPaid();

        // TODO: Update in database

        // Refresh display
        filterExpenses(currentFilter);

        Snackbar.make(requireView(),
                "Expense marked as paid!",
                Snackbar.LENGTH_SHORT).show();
    }

    // ==================== Data Loading ====================

    /**
     * Get expenses from Purchase invoices (both paid and unpaid)
     */
    private List<Expense> getExpensesFromPurchaseInvoices() {
        List<Expense> invoicedExpenses = new ArrayList<>();

        // TODO: Database query
        // SELECT * FROM invoices WHERE type='Purchase'

        // Sample data
        Calendar cal = Calendar.getInstance();

        // Paid purchase invoice
        cal.set(2024, Calendar.DECEMBER, 10);
        invoicedExpenses.add(new Expense(
                "1",
                "INV-0002",
                "XYZ Suppliers",
                2500.00,
                cal.getTimeInMillis(),
                "Office Supplies",
                true  // isPaid
        ));

        // Unpaid purchase invoice
        cal.set(2024, Calendar.DECEMBER, 25);
        invoicedExpenses.add(new Expense(
                "2",
                "INV-0004",
                "Office Depot",
                5000.00,
                cal.getTimeInMillis(),
                "Equipment",
                false  // isPaid = false (Unpaid)
        ));

        return invoicedExpenses;
    }

    /**
     * Get manual expenses
     */
    private List<Expense> getManualExpenses() {
        List<Expense> manualExpenses = new ArrayList<>();

        // TODO: Database query
        // SELECT * FROM manual_expenses

        // Sample data from DataHolder
        if (DataHolder.getInstance() != null &&
                DataHolder.getInstance().getExpenseList() != null) {

            for (Expense oldExpense : DataHolder.getInstance().getExpenseList()) {
                manualExpenses.add(oldExpense);
            }
        }

        // Sample manual expense
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.DECEMBER, 15);

        manualExpenses.add(new Expense(
                "3",
                "Lunch Meeting",
                50.00,
                cal.getTimeInMillis(),
                "Meals",
                "Team lunch at restaurant"
        ));

        return manualExpenses;
    }

    public void refreshExpenses() {
        loadExpenses();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshExpenses();
    }
}