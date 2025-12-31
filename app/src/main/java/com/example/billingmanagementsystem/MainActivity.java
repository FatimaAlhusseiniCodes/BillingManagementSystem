package com.example.billingmanagementsystem;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize API Client
        ApiClient.init(this);

        // Find views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);  // ← ADD THIS LINE
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Setup toolbar
        //setSupportActionBar(toolbar);

        // Get NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Setup AppBarConfiguration with top-level destinations
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,              // Home
                R.id.invoiceFragment,           // Invoices
                R.id.salesReceiptFragment,      // Sales Receipts
                R.id.paymentsReceivedFragment,  // Payments
                R.id.incomesFragment,           // Incomes
                R.id.expensesFragment,          // Expenses
                R.id.profitFragment,            // Profit
                R.id.settingsFragment           // Settings
        )
                .setOpenableLayout(drawer)
                .build();

        // Setup toolbar with navigation
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        // Setup navigation view (drawer)
        NavigationUI.setupWithNavController(navigationView, navController);

        // Setup bottom navigation
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}