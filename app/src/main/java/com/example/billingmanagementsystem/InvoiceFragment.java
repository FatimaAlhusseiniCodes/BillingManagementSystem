package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceFragment extends Fragment {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddInvoice;
    private InvoiceAdapter adapter;
    private List<Invoice> allInvoices;

    // YOUR API ENDPOINT
    private static final String API_URL = "http://your-server.com/api/invoices";

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

        // Setup RecyclerView
        setupRecyclerView();

        // Load REAL data from database
        loadInvoicesFromAPI();

        // Setup tabs
        setupTabs();

        // Setup FAB click
        setupFAB();
    }

    private void setupRecyclerView() {
        adapter = new InvoiceAdapter(invoice -> {
            // Handle invoice item click
            Toast.makeText(requireContext(),
                    "Clicked: " + invoice.getInvoiceNumber(),
                    Toast.LENGTH_SHORT).show();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    private void loadInvoicesFromAPI() {
        allInvoices = new ArrayList<>();

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject json = response.getJSONObject(i);

                            // Parse REAL data from database
                            Invoice invoice = new Invoice(
                                    json.getString("id"),
                                    json.getString("invoice_number"),
                                    json.getString("partner_name"),      // ← REAL
                                    json.getString("invoice_date"),
                                    json.getString("due_date"),
                                    json.getDouble("total_amount"),      // ← REAL
                                    json.getDouble("due_amount"),
                                    json.getInt("quantity"),
                                    json.getString("status"),
                                    calculateOverdue(json.getString("due_date")),
                                    calculateOverdueDays(json.getString("due_date"))
                            );

                            allInvoices.add(invoice);
                        }

                        // Display unpaid invoices by default
                        filterInvoices("Unpaid");

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(),
                                "Error parsing data",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(requireContext(),
                            "Error loading invoices: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }

    private boolean calculateOverdue(String dueDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            Date dueDate = sdf.parse(dueDateStr);
            Date today = new Date();
            return today.after(dueDate);
        } catch (Exception e) {
            return false;
        }
    }

    private int calculateOverdueDays(String dueDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            Date dueDate = sdf.parse(dueDateStr);
            Date today = new Date();

            if (today.after(dueDate)) {
                long diff = today.getTime() - dueDate.getTime();
                return (int) (diff / (1000 * 60 * 60 * 24));
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0: // Unpaid
                        filterInvoices("Unpaid");
                        break;
                    case 1: // Paid
                        filterInvoices("Paid");
                        break;
                    case 2: // All
                        filterInvoices("All");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void filterInvoices(String filter) {
        List<Invoice> filteredList = new ArrayList<>();

        if (filter.equals("All")) {
            filteredList.addAll(allInvoices);
        } else if (filter.equals("Paid")) {
            for (Invoice invoice : allInvoices) {
                if (invoice.getStatus().equals("Paid")) {
                    filteredList.add(invoice);
                }
            }
        } else if (filter.equals("Unpaid")) {
            for (Invoice invoice : allInvoices) {
                if (invoice.getStatus().equals("Unpaid") ||
                        invoice.getStatus().equals("Partially Paid")) {
                    filteredList.add(invoice);
                }
            }
        }

        adapter.setInvoices(filteredList);
    }

    private void setupFAB() {
        fabAddInvoice.setOnClickListener(v -> {
            NavHostFragment.findNavController(InvoiceFragment.this)
                    .navigate(R.id.action_invoiceFragment_to_addInvoiceFragment);
        });
    }
}