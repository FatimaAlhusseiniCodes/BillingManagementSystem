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
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Fragment to display and manage incomes
 * Shows incomes from paid Sales invoices and manual entries
 * 3 tabs: All, Invoiced, Manual
 */
public class IncomesFragment extends Fragment implements IncomeAdapter.OnIncomeClickListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddIncome;
    private LinearLayout layoutEmptyState;

    private IncomeAdapter adapter;
    private List<Income> allIncomes;
    private String currentFilter = "All"; // Default tab

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_incomes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rv_incomes);
        fabAddIncome = view.findViewById(R.id.fab_add_income);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        // Setup RecyclerView
        setupRecyclerView();

        // Load incomes
        loadIncomes();

        // Setup tabs
        setupTabs();

        // Setup FAB
        setupFAB();
    }

    private void setupRecyclerView() {
        adapter = new IncomeAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadIncomes() {
        // Combine incomes from:
        // 1. Paid Sales Invoices (automatic)
        // 2. Manual Income Entries (user-added)

        allIncomes = new ArrayList<>();

        // TODO: Get paid sales invoices from database
        // SELECT * FROM invoices WHERE type='Sales' AND status='Paid'
        allIncomes.addAll(getIncomesFromPaidInvoices());

        // TODO: Get manual income entries from database
        // SELECT * FROM manual_incomes
        allIncomes.addAll(getManualIncomes());

        // Filter and display
        filterIncomes(currentFilter);
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
                }
                filterIncomes(currentFilter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterIncomes(String filter) {
        List<Income> filteredList = new ArrayList<>();

        for (Income income : allIncomes) {
            boolean matches = false;

            switch (filter) {
                case "All":
                    // Show everything
                    matches = true;
                    break;

                case "Invoiced":
                    // Show only incomes from paid invoices
                    matches = income.isInvoiced();
                    break;

                case "Manual":
                    // Show only manual income entries
                    matches = income.isManual();
                    break;
            }

            if (matches) {
                filteredList.add(income);
            }
        }

        // Update adapter
        adapter.setIncomes(filteredList);

        // Show/hide empty state
        if (filteredList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupFAB() {
        fabAddIncome.setOnClickListener(v -> {
            // Navigate to Add Manual Income screen
            // TODO: Create AddManualIncomeFragment
            NavHostFragment.findNavController(IncomesFragment.this)
                    .navigate(R.id.addManualIncomeFragment);
        });
    }

    // ==================== OnIncomeClickListener Implementation ====================

    @Override
    public void onIncomeClick(Income income) {
        // TODO: Show income details or navigate to related invoice
        Toast.makeText(requireContext(),
                "Income: " + income.getSource(),
                Toast.LENGTH_SHORT).show();
    }

    // ==================== Data Loading Methods ====================

    /**
     * Get incomes from PAID Sales invoices
     * This runs automatically when an invoice is marked as paid
     */
    private List<Income> getIncomesFromPaidInvoices() {
        List<Income> invoicedIncomes = new ArrayList<>();

        // TODO: Replace with database query
        // SELECT i.id, i.invoice_number, p.name, i.total, i.issue_date, i.product
        // FROM invoices i
        // JOIN partners p ON i.partner_id = p.id
        // WHERE i.type = 'Sales' AND i.status = 'Paid'

        // Sample data (remove when connecting to database)
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.DECEMBER, 15);

        invoicedIncomes.add(new Income(
                "1",
                "INV-0001",
                "ABC Company",
                1500.00,
                cal.getTimeInMillis(),
                "Web Development Services"
        ));

        cal.set(2024, Calendar.DECEMBER, 20);
        invoicedIncomes.add(new Income(
                "2",
                "INV-0003",
                "Tech Corp",
                3200.50,
                cal.getTimeInMillis(),
                "Consulting Services"
        ));

        return invoicedIncomes;
    }

    /**
     * Get manual income entries (user-added)
     */
    private List<Income> getManualIncomes() {
        List<Income> manualIncomes = new ArrayList<>();

        // TODO: Replace with database query
        // SELECT * FROM manual_incomes

        // Sample data from DataHolder (if exists)
        if (DataHolder.getInstance() != null &&
                DataHolder.getInstance().getIncomeList() != null) {

            // Convert old Income objects to new format
            for (Income oldIncome : DataHolder.getInstance().getIncomeList()) {
                manualIncomes.add(oldIncome);
            }
        }

        // Sample manual income
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.DECEMBER, 25);

        manualIncomes.add(new Income(
                "3",
                "Direct Payment - John Doe",
                500.00,
                cal.getTimeInMillis(),
                "Freelance Work",
                "+1234567890",
                "john@example.com",
                "Quick cash payment"
        ));

        return manualIncomes;
    }

    /**
     * Refresh incomes (call this when a new invoice is paid)
     */
    public void refreshIncomes() {
        loadIncomes();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh when returning to screen
        refreshIncomes();
    }
}