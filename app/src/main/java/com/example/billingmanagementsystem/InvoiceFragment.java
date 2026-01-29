package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * InvoiceFragment - Complete implementation with Toolbar, Search, Sort, and Drawer
 * Displays invoices with filtering, searching, and sorting capabilities
 */
public class InvoiceFragment extends Fragment implements Invoiceadapter.OnInvoiceClickListener {

    // UI Components
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddInvoice;
    private LinearLayout layoutEmptyState;

    // Data & Adapter
    private Invoiceadapter adapter;
    private List<Invoice> allInvoices;
    private List<Invoice> filteredInvoices;

    // State tracking
    private String currentFilter = "Unpaid"; // Default tab
    private String currentSortBy = "date"; // Default sort
    private String currentSearchQuery = "";

    // Drawer
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    // ==================== LIFECYCLE METHODS ====================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // CRITICAL: Enable options menu for this fragment
        setHasOptionsMenu(true);
    }

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
        initializeViews(view);

        // Setup components
        setupRecyclerView();
        setupTabs();
        setupFAB();

        // Load data
        loadInvoices();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up drawer listener
        if (drawerLayout != null && drawerToggle != null) {
            drawerLayout.removeDrawerListener(drawerToggle);
        }
    }

    // ==================== INITIALIZATION ====================

    private void initializeViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rv_invoices);
        fabAddInvoice = view.findViewById(R.id.fab_add_invoice);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
    }



    private void setupRecyclerView() {
        adapter = new Invoiceadapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
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
                applyFiltersAndSort();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFAB() {
        fabAddInvoice.setOnClickListener(v -> {
            // Navigate to Add Invoice screen
            NavHostFragment.findNavController(InvoiceFragment.this)
                    .navigate(R.id.action_invoices_to_addInvoice);
        });
    }

    // ==================== DATA LOADING ====================

    private void loadInvoices() {
        // TODO: Replace with actual database/API call
        allInvoices = getSampleInvoices();
        filteredInvoices = new ArrayList<>();

        // Apply filters and sort
        applyFiltersAndSort();
    }

    private void applyFiltersAndSort() {
        // Step 1: Filter by tab
        filterInvoicesByTab();

        // Step 2: Apply search if there's a query
        if (!currentSearchQuery.isEmpty()) {
            applySearch();
        }

        // Step 3: Sort the results
        sortInvoices(currentSortBy);
    }

    private void filterInvoicesByTab() {
        filteredInvoices.clear();

        for (Invoice invoice : allInvoices) {
            boolean matches = false;

            switch (currentFilter) {
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
                filteredInvoices.add(invoice);
            }
        }
    }

    private void applySearch() {
        List<Invoice> searchResults = new ArrayList<>();
        String query = currentSearchQuery.toLowerCase();

        for (Invoice invoice : filteredInvoices) {
            // Search in multiple fields
            boolean matchesInvoiceNumber = invoice.getInvoiceNumber().toLowerCase().contains(query);
            boolean matchesPartnerName = invoice.getPartnerName().toLowerCase().contains(query);
            boolean matchesAmount = String.valueOf(invoice.getTotal()).contains(query);
            boolean matchesStatus = invoice.getStatus().toLowerCase().contains(query);

            if (matchesInvoiceNumber || matchesPartnerName || matchesAmount || matchesStatus) {
                searchResults.add(invoice);
            }
        }

        filteredInvoices = searchResults;
    }

    // ==================== OPTIONS MENU ====================

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        // Clear any existing menu items
        menu.clear();

        // Inflate the menu
        inflater.inflate(R.menu.menu_invoices, menu);

        // Setup Search
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();

            if (searchView != null) {
                searchView.setQueryHint("Search invoices...");
                searchView.setMaxWidth(Integer.MAX_VALUE);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        performSearch(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        performSearch(newText);
                        return true;
                    }
                });

                // Handle search view close
                searchView.setOnCloseListener(() -> {
                    performSearch("");
                    return false;
                });
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle drawer toggle
        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle menu item clicks
        int id = item.getItemId();

        if (id == R.id.sort_by_name) {
            currentSortBy = "name";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "Sorted by Partner Name", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.sort_by_date) {
            currentSortBy = "date";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "Sorted by Date (Newest First)", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.sort_by_amount) {
            currentSortBy = "amount";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "Sorted by Amount (Highest First)", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // ==================== SEARCH FUNCTIONALITY ====================

    private void performSearch(String query) {
        currentSearchQuery = query;
        applyFiltersAndSort();
    }

    // ==================== SORT FUNCTIONALITY ====================

    private void sortInvoices(String sortBy) {
        switch (sortBy) {
            case "name":
                Collections.sort(filteredInvoices, new Comparator<Invoice>() {
                    @Override
                    public int compare(Invoice i1, Invoice i2) {
                        return i1.getPartnerName().compareToIgnoreCase(i2.getPartnerName());
                    }
                });
                break;

            case "date":
                Collections.sort(filteredInvoices, new Comparator<Invoice>() {
                    @Override
                    public int compare(Invoice i1, Invoice i2) {
                        // Newest first
                        return Long.compare(i2.getIssueDateMillis(), i1.getIssueDateMillis());
                    }
                });
                break;

            case "amount":
                Collections.sort(filteredInvoices, new Comparator<Invoice>() {
                    @Override
                    public int compare(Invoice i1, Invoice i2) {
                        // Highest first
                        return Double.compare(i2.getTotal(), i1.getTotal());
                    }
                });
                break;
        }

        // Update UI
        updateUI();
    }

    // ==================== UI UPDATE ====================

    private void updateUI() {
        adapter.setInvoices(filteredInvoices);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredInvoices.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ==================== INVOICE CLICK LISTENERS ====================

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

        // TODO: Update in database/API

        // Refresh display
        applyFiltersAndSort();

        // Show confirmation
        Snackbar.make(requireView(),
                invoice.getInvoiceNumber() + " marked as paid!",
                Snackbar.LENGTH_SHORT).show();
    }

    // ==================== SAMPLE DATA ====================

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

        // Sample Invoice 5 - Sales, Paid
        cal.set(2024, Calendar.DECEMBER, 15);
        long issue5 = cal.getTimeInMillis();
        cal.set(2025, Calendar.JANUARY, 15);
        long due5 = cal.getTimeInMillis();

        invoices.add(new Invoice(
                "5", "INV-0005", "Green Solutions", 5,
                issue5, due5,
                1200.00, 0.00, 1200.00,
                1200.00, 0.00,
                "Paid", "Sales",
                8.0, 150.0,
                false, 0,
                ""
        ));

        // Sample Invoice 6 - Sales, Unpaid
        cal.set(2024, Calendar.DECEMBER, 28);
        long issue6 = cal.getTimeInMillis();
        cal.set(2025, Calendar.JANUARY, 28);
        long due6 = cal.getTimeInMillis();

        invoices.add(new Invoice(
                "6", "INV-0006", "Blue Ocean Inc", 6,
                issue6, due6,
                800.00, 0.00, 800.00,
                0.00, 800.00,
                "Unpaid", "Sales",
                5.0, 160.0,
                false, 0,
                ""
        ));

        return invoices;
    }
}