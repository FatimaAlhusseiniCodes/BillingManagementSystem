package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InvoiceFragment extends Fragment implements InvoiceAdapter.OnInvoiceClickListener {
/**
 * InvoiceFragment - Complete implementation with Toolbar, Search, Sort, and Drawer
 * Displays invoices with filtering, searching, and sorting capabilities
 */

    // UI Components
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddInvoice;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBar;

    // Data & Adapter
    private InvoiceAdapter adapter;
    private List<Invoice> allInvoices = new ArrayList<>();         // FIXED: Initialize here
    private List<Invoice> filteredInvoices = new ArrayList<>();    // FIXED: Initialize here

    // State tracking
    private String currentFilter = "All"; // Default to All
    private String currentSortBy = "date";
    private String currentSearchQuery = "";

    // Drawer
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        initializeViews(view);
        setupRecyclerView();
        setupTabs();
        setupFAB();
        loadInvoices();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadInvoices(); // Refresh when returning
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (drawerLayout != null && drawerToggle != null) {
            drawerLayout.removeDrawerListener(drawerToggle);
        }
    }

    private void initializeViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rv_invoices);
        fabAddInvoice = view.findViewById(R.id.fab_add_invoice);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
       // progressBar = view.findViewById(R.id.progressBar); // Add if you have one
    }

    private void setupRecyclerView() {
        adapter = new InvoiceAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentFilter = tab.getText().toString();
                applyFiltersAndSort(); // Just filter locally, don't re-fetch
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFAB() {
        fabAddInvoice.setOnClickListener(v -> {
            NavHostFragment.findNavController(InvoiceFragment.this)
                    .navigate(R.id.action_invoices_to_addInvoice);
        });
    }

    // ==================== DATA LOADING ====================

    private void loadInvoices() {
        android.util.Log.d("INVOICES_DEBUG", "=== Loading invoices ===");

        // Show loading indicator
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // FIXED: Get user ID from SessionManager instead of hardcoded value
        SessionManager session = new SessionManager(requireContext());
        int userId = session.getUserId();

        android.util.Log.d("INVOICES_DEBUG", "User ID: " + userId);

        // Load ALL invoices, filter locally
        ApiClient.getInvoices(requireContext(), 0, null, null, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                android.util.Log.d("INVOICES_DEBUG", "API Response: " + response.toString());

                if (!isAdded()) return;

                try {
                    List<Invoice> fetchedInvoices = new ArrayList<>();

                    if (response.has("data") && !response.isNull("data")) {
                        JSONArray data = response.getJSONArray("data");
                        android.util.Log.d("INVOICES_DEBUG", "Found " + data.length() + " invoices");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);

                            // Parse invoice - handle both lowercase and uppercase field names
                            String invoiceId = obj.optString("invoice_id", obj.optString("InvoiceID", "0"));
                            String invoiceNumber = obj.optString("invoice_number", obj.optString("InvoiceNumber", ""));
                            String partnerName = obj.optString("partner_name", obj.optString("PartnerName", "Unknown"));
                            long partnerId = obj.optLong("partner_id", obj.optLong("PartnerID", 0));
                            double totalAmount = obj.optDouble("total_amount", obj.optDouble("TotalAmount", 0.0));
                            String status = obj.optString("status", obj.optString("Status", "Unpaid"));
                            String type = obj.optString("type", obj.optString("Type", "Sales"));
                            String invoiceDate = obj.optString("invoice_date", obj.optString("InvoiceDate", ""));
                            String dueDate = obj.optString("due_date", obj.optString("DueDate", ""));

                            android.util.Log.d("INVOICES_DEBUG", "Invoice: " + invoiceNumber + " | " + partnerName + " | " + totalAmount);

                            // Create Invoice object
                            Invoice invoice = new Invoice(
                                    invoiceId,
                                    invoiceNumber,
                                    partnerName,
                                    partnerId,
                                    0, 0, // dates as millis (parse if needed)
                                    totalAmount, 0, totalAmount,
                                    0, totalAmount,
                                    status,
                                    type,
                                    0, 0,
                                    false, 0,
                                    ""
                            );

                            fetchedInvoices.add(invoice);
                        }
                    } else {
                        android.util.Log.d("INVOICES_DEBUG", "No 'data' field in response");
                    }

                    // Update UI on main thread
                    requireActivity().runOnUiThread(() -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);

                        allInvoices.clear();
                        allInvoices.addAll(fetchedInvoices);

                        android.util.Log.d("INVOICES_DEBUG", "Total invoices loaded: " + allInvoices.size());

                        applyFiltersAndSort();
                    });

                } catch (Exception e) {
                    android.util.Log.e("INVOICES_DEBUG", "Parse error: " + e.getMessage());
                    e.printStackTrace();

                    requireActivity().runOnUiThread(() -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        Toast.makeText(requireContext(), "Error parsing invoices", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("INVOICES_DEBUG", "API Error: " + error);

                if (!isAdded()) return;

                requireActivity().runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(requireContext(), "Failed to load: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void applyFiltersAndSort() {
        // FIXED: Null checks
        if (allInvoices == null) {
            allInvoices = new ArrayList<>();
        }
        if (filteredInvoices == null) {
            filteredInvoices = new ArrayList<>();
        }

        filterInvoicesByTab();

        if (!currentSearchQuery.isEmpty()) {
            applySearch();
        }

        sortInvoices(currentSortBy);
    }

    private void filterInvoicesByTab() {
        filteredInvoices.clear();

        for (Invoice invoice : allInvoices) {
            boolean matches = false;

            switch (currentFilter) {
                case "Unpaid":
                    matches = "Unpaid".equals(invoice.getStatus()) ||
                            "Partially Paid".equals(invoice.getStatus());
                    break;
                case "Paid":
                    matches = "Paid".equals(invoice.getStatus());
                    break;
                case "Sales":
                    matches = "Sales".equals(invoice.getType());
                    break;
                case "Purchase":
                    matches = "Purchase".equals(invoice.getType());
                    break;
                case "All":
                default:
                    matches = true;
                    break;
            }

            if (matches) {
                filteredInvoices.add(invoice);
            }
        }

        android.util.Log.d("INVOICES_DEBUG", "Filtered to " + filteredInvoices.size() + " invoices for: " + currentFilter);
    }

    private void applySearch() {
        if (currentSearchQuery.isEmpty()) return;

        List<Invoice> searchResults = new ArrayList<>();
        String query = currentSearchQuery.toLowerCase();

        for (Invoice invoice : filteredInvoices) {
            boolean matchesInvoiceNumber = invoice.getInvoiceNumber().toLowerCase().contains(query);
            boolean matchesPartnerName = invoice.getPartnerName().toLowerCase().contains(query);
            boolean matchesAmount = String.valueOf(invoice.getTotal()).contains(query);
            boolean matchesStatus = invoice.getStatus().toLowerCase().contains(query);

            if (matchesInvoiceNumber || matchesPartnerName || matchesAmount || matchesStatus) {
                searchResults.add(invoice);
            }
        }

        filteredInvoices.clear();
        filteredInvoices.addAll(searchResults);
    }

    private void sortInvoices(String sortBy) {
        switch (sortBy) {
            case "name":
                Collections.sort(filteredInvoices, (i1, i2) ->
                        i1.getPartnerName().compareToIgnoreCase(i2.getPartnerName()));
                break;
            case "date":
                Collections.sort(filteredInvoices, (i1, i2) ->
                        Long.compare(i2.getIssueDateMillis(), i1.getIssueDateMillis()));
                break;
            case "amount":
                Collections.sort(filteredInvoices, (i1, i2) ->
                        Double.compare(i2.getTotal(), i1.getTotal()));
                break;
        }

        updateUI();
    }

    private void updateUI() {
        adapter.setInvoices(filteredInvoices);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredInvoices == null || filteredInvoices.isEmpty()) {
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
            if (recyclerView != null) recyclerView.setVisibility(View.GONE);
        } else {
            if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
        }
    }

    // ==================== OPTIONS MENU ====================

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_invoices, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) {
                searchView.setQueryHint("Search invoices...");
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
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        if (id == R.id.sort_by_name) {
            currentSortBy = "name";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "Sorted by Partner Name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort_by_date) {
            currentSortBy = "date";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "Sorted by Date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort_by_amount) {
            currentSortBy = "amount";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "Sorted by Amount", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void performSearch(String query) {
        currentSearchQuery = query;
        applyFiltersAndSort();
    }

    // ==================== INVOICE CLICK LISTENERS ====================

    @Override
    public void onInvoiceClick(Invoice invoice) {
        Toast.makeText(requireContext(), "Invoice: " + invoice.getInvoiceNumber(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkAsPaidClick(Invoice invoice) {
        invoice.markAsPaid();
        applyFiltersAndSort();
        Snackbar.make(requireView(), invoice.getInvoiceNumber() + " marked as paid!", Snackbar.LENGTH_SHORT).show();
    }
}