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

public class InvoiceFragment extends Fragment implements InvoiceAdapter.OnInvoiceClickListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddInvoice;
    private LinearLayout layoutEmptyState;

    private InvoiceAdapter adapter;
    private List<Invoice> allInvoices;
    private String currentFilter = "Unpaid"; // Default tab

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rv_invoices);
        fabAddInvoice = view.findViewById(R.id.fab_add_invoice);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        // Setup RecyclerView
        setupRecyclerView();

        // Load invoices
        loadInvoices();

        // Setup tabs
        setupTabs();

        // Setup FAB
        setupFAB();
    }

    private void setupRecyclerView() {
        adapter = new InvoiceAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadInvoices() {
        // TODO: Replace with actual database/API call
        allInvoices = getSampleInvoices();

        // Filter and display
        filterInvoices(currentFilter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: currentFilter = "Unpaid"; break;
                    case 1: currentFilter = "Paid"; break;
                    case 2: currentFilter = "Sales"; break;
                    case 3: currentFilter = "Purchase"; break;
                    case 4: currentFilter = "All"; break;
                }
                filterInvoices(currentFilter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterInvoices(String filter) {
        List<Invoice> filteredList = new ArrayList<>();

        for (Invoice invoice : allInvoices) {
            boolean matches = false;

            switch (filter) {
                case "Unpaid":
                    matches = invoice.getStatus().equals("Unpaid") ||
                            invoice.getStatus().equals("Partially Paid");
                    break;
                case "Paid":
                    matches = invoice.getStatus().equals("Paid");
                    break;
                case "Sales":
                    matches = invoice.getType().equals("Sales");
                    break;
                case "Purchase":
                    matches = invoice.getType().equals("Purchase");
                    break;
                case "All":
                    matches = true;
                    break;
            }

            if (matches) {
                filteredList.add(invoice);
            }
        }

        adapter.setInvoices(filteredList);

        if (filteredList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupFAB() {
        fabAddInvoice.setOnClickListener(v -> {
            NavHostFragment.findNavController(InvoiceFragment.this)
                    .navigate(R.id.addInvoiceFragment);
        });
    }

    // ==================== OnInvoiceClickListener Implementation ====================

    @Override
    public void onInvoiceClick(Invoice invoice) {
        // TODO: Navigate to invoice details or show options
        Toast.makeText(requireContext(),
                "Invoice: " + invoice.getInvoiceNumber(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkAsPaidClick(Invoice invoice) {
        // Mark invoice as paid
        invoice.markAsPaid();

        // TODO: Update in database

        // Refresh display
        filterInvoices(currentFilter);

        // Show confirmation
        Snackbar.make(requireView(),
                "Invoice marked as paid!",
                Snackbar.LENGTH_SHORT).show();
    }

    // ==================== Sample Data ====================

    private List<Invoice> getSampleInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        Calendar cal = Calendar.getInstance();

        // Sample Invoice 1 - Sales, Unpaid, Overdue
        cal.set(2024, Calendar.DECEMBER, 1);
        long issue1 = cal.getTimeInMillis();
        cal.set(2024, Calendar.DECEMBER, 15);
        long due1 = cal.getTimeInMillis();

        invoices.add(new Invoice(
                "1", "INV-0001", "ABC Company", 1,
                issue1, due1,
                1500.00, 0.00, 1500.00,
                0.00, 1500.00,
                "Unpaid", "Sales",
                10.0, 150.0,
                true, 15,
                ""
        ));

        // Sample Invoice 2 - Purchase, Paid
        cal.set(2024, Calendar.NOVEMBER, 10);
        long issue2 = cal.getTimeInMillis();
        cal.set(2024, Calendar.DECEMBER, 10);
        long due2 = cal.getTimeInMillis();

        invoices.add(new Invoice(
                "2", "INV-0002", "XYZ Suppliers", 2,
                issue2, due2,
                2500.00, 0.00, 2500.00,
                2500.00, 0.00,
                "Paid", "Purchase",
                25.0, 100.0,
                false, 0,
                ""
        ));

        // Sample Invoice 3 - Sales, Unpaid
        cal.set(2024, Calendar.DECEMBER, 20);
        long issue3 = cal.getTimeInMillis();
        cal.set(2025, Calendar.JANUARY, 20);
        long due3 = cal.getTimeInMillis();

        invoices.add(new Invoice(
                "3", "INV-0003", "Tech Corp", 3,
                issue3, due3,
                3200.50, 0.00, 3200.50,
                0.00, 3200.50,
                "Unpaid", "Sales",
                20.0, 160.025,
                false, 0,
                ""
        ));

        // Sample Invoice 4 - Purchase, Partially Paid, Overdue
        cal.set(2024, Calendar.NOVEMBER, 25);
        long issue4 = cal.getTimeInMillis();
        cal.set(2024, Calendar.DECEMBER, 25);
        long due4 = cal.getTimeInMillis();

        invoices.add(new Invoice(
                "4", "INV-0004", "Office Supplies Ltd", 4,
                issue4, due4,
                5000.00, 0.00, 5000.00,
                2500.00, 2500.00,
                "Partially Paid", "Purchase",
                50.0, 100.0,
                true, 5,
                ""
        ));

        return invoices;
    }
}