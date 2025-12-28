package com.example.billingmanagementsystem;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Step 1: Find all views (Lecture 17, Slide 5)
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Step 2: Get NavController (Lecture 16, Slide 7)
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Step 3: Define top-level destinations (Lecture 16, Slide 13)
        // These screens show hamburger icon (☰) instead of back arrow (←)
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,              // Home
                R.id.invoiceFragment,            // Invoices
                R.id.salesReceiptFragment,       // Sales
                R.id.paymentsReceivedFragment)   // Payments
                .setOpenableLayout(drawer)       // Enable drawer (Lecture 17, Slide 12)
                .build();

        // Step 4: Setup toolbar with NavController (Lecture 16, Slide 13)
        NavigationUI.setupWithNavController(toolbar, navController, mAppBarConfiguration);

        // Step 5: Setup NavigationView with NavController (Lecture 17, Slide 13)
        NavigationUI.setupWithNavController(navigationView, navController);

        // Step 6: Setup BottomNavigationView with NavController (Lecture 17, Slide 5)
        NavigationUI.setupWithNavController(bottomNav, navController);
    }

    // Handle Up/Back button (Lecture 16, Slide 14)
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}