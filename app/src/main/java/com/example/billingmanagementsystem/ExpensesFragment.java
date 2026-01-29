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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExpensesFragment extends Fragment implements ExpenseAdapter.OnExpenseClickListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddExpense;
    private LinearLayout layoutEmptyState;

    private ExpenseAdapter adapter;
    private List<Expense> allExpenses;
    private List<Expense> filteredExpenses;

    private String currentFilter = "All";
    private String currentSortBy = "date";
    private String currentSearchQuery = "";

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
        return inflater.inflate(R.layout.fragment_expenses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupToolbar();
        setupRecyclerView();
        setupTabs();
        setupFAB();
        loadExpenses();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (drawerLayout != null && drawerToggle != null) {
            drawerLayout.removeDrawerListener(drawerToggle);
        }
    }

    private void initializeViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tabLayout = view.findViewById(R.id.tabLayout);
        recyclerView = view.findViewById(R.id.rv_expenses);
        fabAddExpense = view.findViewById(R.id.fab_add_expense);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
    }

    private void setupToolbar() {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        if (getActivity() != null) {
            drawerLayout = getActivity().findViewById(R.id.drawer_layout);

            if (drawerLayout != null) {
                drawerToggle = new ActionBarDrawerToggle(
                        getActivity(),
                        drawerLayout,
                        toolbar,
                        R.string.navigation_drawer_open,
                        R.string.navigation_drawer_close
                );

                drawerLayout.addDrawerListener(drawerToggle);
                drawerToggle.syncState();

                if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
                }
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
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
                loadExpenses();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFAB() {
        fabAddExpense.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Add Expense (Coming soon)", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_expenses, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();

            if (searchView != null) {
                searchView.setQueryHint("Search expenses...");
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
        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        if (id == R.id.sort_by_date) {
            currentSortBy = "date";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "✓ Sorted by Date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort_by_amount) {
            currentSortBy = "amount";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "✓ Sorted by Amount", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_refresh) {
            loadExpenses();
            Toast.makeText(getContext(), "✓ Refreshing...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadExpenses() {
        SessionManager session = new SessionManager(getContext());
        int userId = session.getUserId();

        String apiFilter;
        switch (currentFilter) {
            case "Invoiced": apiFilter = "invoiced"; break;
            case "Manual": apiFilter = "manual"; break;
            case "Unpaid": apiFilter = "unpaid"; break;
            default: apiFilter = "all"; break;
        }

        ApiClient.getExpenses(getContext(), userId, apiFilter, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isAdded()) return;

                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        allExpenses = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);

                            String type = item.optString("type", "Manual");
                            String source = item.optString("source", "");
                            String invoiceNumber = item.optString("invoice_number", "");
                            double amount = item.optDouble("amount", 0.0);
                            String apiDate = item.optString("date", "");
                            boolean isPaid = item.optBoolean("is_paid", true);

                            long millis = System.currentTimeMillis();
                            if (!apiDate.isEmpty()) {
                                try {
                                    java.text.SimpleDateFormat apiFormat =
                                            new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                                    java.util.Date parsed = apiFormat.parse(apiDate);
                                    if (parsed != null) {
                                        millis = parsed.getTime();
                                    }
                                } catch (java.text.ParseException ignored) { }
                            }

                            Expense expense;
                            if (type.equalsIgnoreCase("Invoiced")) {
                                expense = new Expense(
                                        String.valueOf(item.optInt("id")),
                                        invoiceNumber,
                                        source,
                                        amount,
                                        millis,
                                        "Purchase",
                                        isPaid
                                );
                            } else {
                                expense = new Expense(
                                        String.valueOf(item.optInt("id")),
                                        source,
                                        amount,
                                        millis,
                                        "Manual",
                                        ""
                                );
                            }

                            allExpenses.add(expense);
                        }

                        filteredExpenses = new ArrayList<>();
                        applyFiltersAndSort();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFiltersAndSort() {
        if (allExpenses == null) allExpenses = new ArrayList<>();
        if (filteredExpenses == null) filteredExpenses = new ArrayList<>();

        filterExpensesByTab();

        if (!currentSearchQuery.isEmpty()) {
            applySearch();
        }

        sortExpenses(currentSortBy);
    }

    private void filterExpensesByTab() {
        filteredExpenses.clear();

        for (Expense expense : allExpenses) {
            boolean matches = false;

            switch (currentFilter) {
                case "All": matches = true; break;
                case "Invoiced": matches = expense.isInvoiced(); break;
                case "Manual": matches = expense.isManual(); break;
                case "Unpaid": matches = expense.isUnpaid(); break;
            }

            if (matches) {
                filteredExpenses.add(expense);
            }
        }
    }

    private void applySearch() {
        List<Expense> searchResults = new ArrayList<>();
        String query = currentSearchQuery.toLowerCase();

        for (Expense expense : filteredExpenses) {
            boolean matchesTitle = expense.getTitle() != null &&
                    expense.getTitle().toLowerCase().contains(query);
            boolean matchesSupplier = expense.getSupplierName() != null &&
                    expense.getSupplierName().toLowerCase().contains(query);
            boolean matchesInvoice = expense.getInvoiceNumber() != null &&
                    expense.getInvoiceNumber().toLowerCase().contains(query);
            boolean matchesCategory = expense.getCategory() != null &&
                    expense.getCategory().toLowerCase().contains(query);

            if (matchesTitle || matchesSupplier || matchesInvoice || matchesCategory) {
                searchResults.add(expense);
            }
        }

        filteredExpenses = searchResults;
    }

    private void performSearch(String query) {
        currentSearchQuery = query;
        applyFiltersAndSort();
    }

    private void sortExpenses(String sortBy) {
        switch (sortBy) {
            case "date":
                Collections.sort(filteredExpenses, new Comparator<Expense>() {
                    @Override
                    public int compare(Expense e1, Expense e2) {
                        return Long.compare(e2.getDateMillis(), e1.getDateMillis());
                    }
                });
                break;

            case "amount":
                Collections.sort(filteredExpenses, new Comparator<Expense>() {
                    @Override
                    public int compare(Expense e1, Expense e2) {
                        return Double.compare(e2.getAmount(), e1.getAmount());
                    }
                });
                break;
        }

        updateUI();
    }

    private void updateUI() {
        adapter.setExpenses(filteredExpenses);

        if (filteredExpenses.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onExpenseClick(Expense expense) {
        Toast.makeText(requireContext(),
                "Expense: " + expense.getTitle(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMarkAsPaidClick(Expense expense) {
        expense.markAsPaid();
        applyFiltersAndSort();

        Snackbar.make(requireView(),
                        "✓ Expense marked as paid!",
                        Snackbar.LENGTH_SHORT)
                .setBackgroundTint(0xFF4CAF50)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadExpenses();
    }
}