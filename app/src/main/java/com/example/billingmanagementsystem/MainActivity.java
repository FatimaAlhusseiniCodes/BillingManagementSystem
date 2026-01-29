package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.View;

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

        // Find views
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Setup toolbar
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        // Get NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment == null) {
            return;
        }

        navController = navHostFragment.getNavController();

        // Setup AppBarConfiguration - use only fragments that EXIST in your nav_graph
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment,
                R.id.invoicesFragment,
                R.id.partnersFragment,
                R.id.incomesFragment,
                R.id.expensesFragment,
                R.id.settingsFragment
        )
                .setOpenableLayout(drawer)
                .build();

        if (toolbar != null) {
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        }

        // Setup navigation view (drawer)
        if (navigationView != null) {
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // Setup bottom navigation
        if (bottomNav != null) {
            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}