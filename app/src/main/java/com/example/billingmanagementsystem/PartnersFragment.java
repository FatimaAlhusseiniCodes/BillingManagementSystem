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

public class PartnersFragment extends Fragment implements PartnerAdapter.OnPartnerClickListener {
    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAddPartner;
    private LinearLayout layoutEmptyState;

    private PartnerAdapter adapter;
    private List<Partner> allPartners;
    private List<Partner> filteredPartners;

    private String currentFilter = "All";
    private String currentSortBy = "name";
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
        return inflater.inflate(R.layout.fragment_partners, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        setupTabs();
        setupFAB();
        loadPartners();
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
        recyclerView = view.findViewById(R.id.rv_partners);
        fabAddPartner = view.findViewById(R.id.fab_add_partner);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
    }

    private void setupRecyclerView() {
        adapter = new PartnerAdapter(this);
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
                    case 1: currentFilter = "Customer"; break;
                    case 2: currentFilter = "Supplier"; break;
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
        fabAddPartner.setOnClickListener(v -> {
            NavHostFragment.findNavController(PartnersFragment.this)
                    .navigate(R.id.action_partners_to_addPartner);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_partners, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();

            if (searchView != null) {
                searchView.setQueryHint("Search partners...");
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

        if (id == R.id.sort_by_name) {
            currentSortBy = "name";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "✓ Sorted by Name", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort_by_date) {
            currentSortBy = "date";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "✓ Sorted by Date", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort_by_invoices) {
            currentSortBy = "invoices";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "✓ Sorted by Invoices", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.sort_by_amount) {
            currentSortBy = "amount";
            applyFiltersAndSort();
            Toast.makeText(getContext(), "✓ Sorted by Amount", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_refresh) {
            loadPartners();
            Toast.makeText(getContext(), "✓ Refreshing...", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadPartners() {
        String filterType = currentFilter.equals("All") ? null : currentFilter;

        ApiClient.getPartners(getContext(), filterType, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray data = response.getJSONArray("data");
                        allPartners = new ArrayList<>();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject item = data.getJSONObject(i);

                            String partnerId = String.valueOf(item.getInt("partner_id"));
                            String name = item.getString("name");
                            String contactName = item.optString("contact_name", "");
                            String email = item.optString("email", "");
                            String phone = item.optString("phone", "");
                            String type = item.getString("type");

                            String[] nameParts = contactName.split(" ", 2);
                            String firstName = nameParts.length > 0 ? nameParts[0] : "";
                            String lastName = nameParts.length > 1 ? nameParts[1] : "";

                            long currentTime = System.currentTimeMillis();

                            Partner partner = new Partner(
                                    partnerId,
                                    "PART-" + partnerId,
                                    firstName,
                                    lastName,
                                    email,
                                    phone,
                                    "",
                                    "",
                                    "",
                                    type,
                                    name,
                                    "",
                                    "",
                                    currentTime,
                                    currentTime,
                                    0,
                                    0.0,
                                    0.0
                            );

                            allPartners.add(partner);
                        }

                        filteredPartners = new ArrayList<>();
                        applyFiltersAndSort();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFiltersAndSort() {
        filterPartnersByTab();

        if (!currentSearchQuery.isEmpty()) {
            applySearch();
        }

        sortPartners(currentSortBy);
    }

    private void filterPartnersByTab() {
        filteredPartners.clear();

        for (Partner partner : allPartners) {
            boolean matches = false;

            switch (currentFilter) {
                case "All":
                    matches = true;
                    break;
                case "Customer":
                    matches = partner.isCustomer();
                    break;
                case "Supplier":
                    matches = partner.isSupplier();
                    break;
            }

            if (matches) {
                filteredPartners.add(partner);
            }
        }
    }

    private void applySearch() {
        List<Partner> searchResults = new ArrayList<>();
        String query = currentSearchQuery.toLowerCase();

        for (Partner partner : filteredPartners) {
            boolean matchesName = partner.getFullName().toLowerCase().contains(query);
            boolean matchesCompany = partner.getCompanyName() != null &&
                    partner.getCompanyName().toLowerCase().contains(query);
            boolean matchesEmail = partner.getEmail() != null &&
                    partner.getEmail().toLowerCase().contains(query);
            boolean matchesPhone = partner.getPhone() != null &&
                    partner.getPhone().contains(query);
            boolean matchesPartnerNumber = partner.getPartnerNumber().toLowerCase().contains(query);

            if (matchesName || matchesCompany || matchesEmail || matchesPhone || matchesPartnerNumber) {
                searchResults.add(partner);
            }
        }

        filteredPartners = searchResults;
    }

    private void performSearch(String query) {
        currentSearchQuery = query;
        applyFiltersAndSort();
    }

    private void sortPartners(String sortBy) {
        switch (sortBy) {
            case "name":
                Collections.sort(filteredPartners, new Comparator<Partner>() {
                    @Override
                    public int compare(Partner p1, Partner p2) {
                        return p1.getDisplayName().compareToIgnoreCase(p2.getDisplayName());
                    }
                });
                break;

            case "date":
                Collections.sort(filteredPartners, new Comparator<Partner>() {
                    @Override
                    public int compare(Partner p1, Partner p2) {
                        return Long.compare(p2.getDateCreatedMillis(), p1.getDateCreatedMillis());
                    }
                });
                break;

            case "invoices":
                Collections.sort(filteredPartners, new Comparator<Partner>() {
                    @Override
                    public int compare(Partner p1, Partner p2) {
                        return Integer.compare(p2.getTotalInvoices(), p1.getTotalInvoices());
                    }
                });
                break;

            case "amount":
                Collections.sort(filteredPartners, new Comparator<Partner>() {
                    @Override
                    public int compare(Partner p1, Partner p2) {
                        double amount1 = p1.getTotalAmountDue() + p1.getTotalAmountOwed();
                        double amount2 = p2.getTotalAmountDue() + p2.getTotalAmountOwed();
                        return Double.compare(amount2, amount1);
                    }
                });
                break;
        }

        updateUI();
    }

    private void updateUI() {
        adapter.setPartners(filteredPartners);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (filteredPartners.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPartnerClick(Partner partner) {
        Toast.makeText(requireContext(),
                "Partner: " + partner.getDisplayName(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadPartners();
    }
}