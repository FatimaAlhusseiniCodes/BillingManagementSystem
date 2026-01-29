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
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IncomesFragment extends Fragment implements IncomeAdapter.OnIncomeClickListener {

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddIncome;
    private LinearLayout layoutEmptyState;

    private IncomeAdapter adapter;
    private List<Income> allIncomes;
    private List<Income> filteredIncomes;

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
        return inflater.inflate(R.layout.fragment_incomes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupTabs();
        setupFAB();
        loadIncomes();
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
        recyclerView = view.findViewById(R.id.rv_incomes);
        fabAddIncome = view.findViewById(R.id.fab_add_income);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
    }


    private void setupRecyclerView() {
        adapter = new IncomeAdapter(this);
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
                }
                loadIncomes();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void setupFAB() {
        fabAddIncome.setOnClickListener(v -> {
            try {
                NavHostFragment.findNavController(IncomesFragment.this)
                        .navigate(R.id.action_incomes_to_addIncome);
            } catch (Exception e) {
                Toast.makeText(getContext(), "Navigation not set up", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_incomes, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();

            if (searchView != null) {
                searchView.setQueryHint("Search incomes...");
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
            loadIncomes();
            Toast.makeText(getContext(), "✓ Refreshing...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadIncomes() {
        SessionManager session = new SessionManager(getContext());
        int userId = session.getUserId();

        String apiFilter;
        switch (currentFilter) {
            case "Invoiced": apiFilter = "invoiced"; break;
            case "Manual": apiFilter = "manual"; break;
            default: apiFilter = "all"; break;
        }

        ApiClient.getIncomes(getContext(), userId, apiFilter, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isAdded()) return;

                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        allIncomes = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);

                            String type = item.optString("type", "Manual");
                            String source = item.optString("source", "");
                            String invoiceNumber = item.optString("invoice_number", "");
                            String amount = item.optString("amount", "0");
                            String apiDate = item.optString("date", "");

                            String displayDate = apiDate;
                            long millis = System.currentTimeMillis();

                            if (!apiDate.isEmpty()) {
                                try {
                                    java.text.SimpleDateFormat apiFormat =
                                            new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                                    java.util.Date parsed = apiFormat.parse(apiDate);
                                    if (parsed != null) {
                                        millis = parsed.getTime();
                                        java.text.SimpleDateFormat displayFormat =
                                                new java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault());
                                        displayDate = displayFormat.format(parsed);
                                    }
                                } catch (java.text.ParseException ignored) { }
                            }

                            String product = type.equalsIgnoreCase("Invoiced") && !invoiceNumber.isEmpty()
                                    ? "Invoice " + invoiceNumber
                                    : "";

                            Income income = new Income(
                                    type,
                                    source,
                                    "",
                                    "",
                                    product,
                                    amount,
                                    displayDate,
                                    "",
                                    String.valueOf(millis)
                            );

                            allIncomes.add(income);
                        }

                        filteredIncomes = new ArrayList<>();
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
        if (allIncomes == null) allIncomes = new ArrayList<>();
        if (filteredIncomes == null) filteredIncomes = new ArrayList<>();

        filterIncomesByTab();

        if (!currentSearchQuery.isEmpty()) {
            applySearch();
        }

        sortIncomes(currentSortBy);
    }

    private void filterIncomesByTab() {
        filteredIncomes.clear();

        for (Income income : allIncomes) {
            boolean matches = false;
            String type = income.getType();

            switch (currentFilter) {
                case "All": matches = true; break;
                case "Invoiced": matches = "Invoiced".equalsIgnoreCase(type); break;
                case "Manual": matches = !"Invoiced".equalsIgnoreCase(type); break;
            }

            if (matches) {
                filteredIncomes.add(income);
            }
        }
    }

    private void applySearch() {
        List<Income> searchResults = new ArrayList<>();
        String query = currentSearchQuery.toLowerCase();

        for (Income income : filteredIncomes) {
            String source = income.getNameOrSource();
            String product = income.getProduct();

            boolean matchesSource = source != null && source.toLowerCase().contains(query);
            boolean matchesProduct = product != null && product.toLowerCase().contains(query);

            if (matchesSource || matchesProduct) {
                searchResults.add(income);
            }
        }

        filteredIncomes = searchResults;
    }

    private void performSearch(String query) {
        currentSearchQuery = query;
        applyFiltersAndSort();
    }

    private void sortIncomes(String sortBy) {
        switch (sortBy) {
            case "date":
                Collections.sort(filteredIncomes, new Comparator<Income>() {
                    @Override
                    public int compare(Income i1, Income i2) {
                        try {
                            long t1 = Long.parseLong(i1.getTimestamp() != null ? i1.getTimestamp() : "0");
                            long t2 = Long.parseLong(i2.getTimestamp() != null ? i2.getTimestamp() : "0");
                            return Long.compare(t2, t1);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                });
                break;

            case "amount":
                Collections.sort(filteredIncomes, new Comparator<Income>() {
                    @Override
                    public int compare(Income i1, Income i2) {
                        try {
                            double a1 = Double.parseDouble(i1.getAmount() != null ? i1.getAmount() : "0");
                            double a2 = Double.parseDouble(i2.getAmount() != null ? i2.getAmount() : "0");
                            return Double.compare(a2, a1);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                });
                break;
        }

        updateUI();
    }

    private void updateUI() {
        adapter.setIncomes(filteredIncomes);

        if (filteredIncomes == null || filteredIncomes.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onIncomeClick(Income income) {
        Toast.makeText(requireContext(),
                "Income: " + income.getNameOrSource(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadIncomes();
    }
}