package com.example.billingmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {

    private TextView textViewProfileInitial;
    private TextView textViewUserName;
    private TextView textViewUserEmail;
    private TextView textViewUserCompany;
    private TextView textViewAppVersion;
    private TextView textViewBuildDate;
    private MaterialButton buttonEditProfile;
    private MaterialButton buttonLogout;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        loadUserProfile();
        setupListeners();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (drawerLayout != null && drawerToggle != null) {
            drawerLayout.removeDrawerListener(drawerToggle);
        }
    }

    private void initializeViews(View view) {
        textViewProfileInitial = view.findViewById(R.id.textViewProfileInitial);
        textViewUserName = view.findViewById(R.id.textViewUserName);
        textViewUserEmail = view.findViewById(R.id.textViewUserEmail);
        textViewUserCompany = view.findViewById(R.id.textViewUserCompany);
        textViewAppVersion = view.findViewById(R.id.textViewAppVersion);
        textViewBuildDate = view.findViewById(R.id.textViewBuildDate);
        buttonEditProfile = view.findViewById(R.id.buttonEditProfile);
        buttonLogout = view.findViewById(R.id.buttonLogout);
    }



    private void setupListeners() {
        buttonEditProfile.setOnClickListener(v -> {
            // Navigate to Edit Profile
            NavHostFragment.findNavController(SettingsFragment.this)
                    .navigate(R.id.action_settings_to_editProfile);
        });

        buttonLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void loadUserProfile() {
        SessionManager session = new SessionManager(getContext());

        String email = session.getEmail();
        String businessName = session.getBusinessName();

        if (email != null && !email.isEmpty()) {
            textViewProfileInitial.setText(email.substring(0, 1).toUpperCase());
            textViewUserEmail.setText(email);

            String userName = email.split("@")[0];
            textViewUserName.setText(userName);
        } else {
            textViewProfileInitial.setText("?");
            textViewUserName.setText("User");
            textViewUserEmail.setText("user@example.com");
        }

        if (businessName != null && !businessName.isEmpty()) {
            textViewUserCompany.setText(businessName);
        } else {
            textViewUserCompany.setText("My Business");
        }

        try {
            String versionName = requireContext().getPackageManager()
                    .getPackageInfo(requireContext().getPackageName(), 0).versionName;
            textViewAppVersion.setText("v" + versionName);
        } catch (Exception e) {
            textViewAppVersion.setText("v1.0.0");
        }

        textViewBuildDate.setText("January 2026");
    }

    private void showLogoutConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?\n\nYour session will be cleared.")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Logout", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void performLogout() {
        SessionManager session = new SessionManager(getContext());
        int userId = session.getUserId();

        buttonLogout.setEnabled(false);
        buttonLogout.setText("Logging out...");

        ApiClient.logout(getContext(), userId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        session.logout();

                        Snackbar.make(requireView(),
                                        "✓ Logged out successfully!",
                                        Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(0xFF4CAF50)
                                .show();

                        requireView().postDelayed(() -> {
                            goToLogin();
                        }, 1000);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

                buttonLogout.setEnabled(true);
                buttonLogout.setText("Logout");
            }

            @Override
            public void onError(String error) {
                // Still logout locally even if API fails
                session.logout();
                Toast.makeText(getContext(), "Logged out locally", Toast.LENGTH_SHORT).show();
                goToLogin();
            }
        });
    }

    // NEW METHOD: Navigate to LoginActivity
    private void goToLogin() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}