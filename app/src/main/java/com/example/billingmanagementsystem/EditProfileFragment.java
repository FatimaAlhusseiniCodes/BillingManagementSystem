package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileFragment extends Fragment {

    // Views
    private TextView textViewAvatarInitial;
    private TextView textViewCurrentEmail;
    private TextInputEditText editTextBusinessName;
    private TextInputEditText editTextTaxPercentage;
    private MaterialAutoCompleteTextView autoCompleteCurrency;
    private TextInputEditText editTextCurrentPassword;
    private TextInputEditText editTextNewPassword;
    private TextInputEditText editTextConfirmPassword;
    private MaterialButton buttonCancel;
    private MaterialButton buttonSaveProfile;

    // Data
    private SessionManager sessionManager;

    // Currency options
    private final String[] currencies = {
            "ALL - Albanian Lek",
            "USD - US Dollar",
            "EUR - Euro",
            "GBP - British Pound",
            "CHF - Swiss Franc"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        initializeViews(view);
        setupCurrencyDropdown();
        loadCurrentProfile();
        setupListeners();
    }

    private void initializeViews(View view) {
        textViewAvatarInitial = view.findViewById(R.id.textViewAvatarInitial);
        textViewCurrentEmail = view.findViewById(R.id.textViewCurrentEmail);
        editTextBusinessName = view.findViewById(R.id.editTextBusinessName);
        editTextTaxPercentage = view.findViewById(R.id.editTextTaxPercentage);
        autoCompleteCurrency = view.findViewById(R.id.autoCompleteCurrency);
        editTextCurrentPassword = view.findViewById(R.id.editTextCurrentPassword);
        editTextNewPassword = view.findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        buttonSaveProfile = view.findViewById(R.id.buttonSaveProfile);
    }

    private void setupCurrencyDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                currencies
        );
        autoCompleteCurrency.setAdapter(adapter);
    }

    private void loadCurrentProfile() {
        // Get current values from session
        String email = sessionManager.getEmail();
        String businessName = sessionManager.getBusinessName();

        // Set avatar initial
        if (textViewAvatarInitial != null && email != null && !email.isEmpty()) {
            textViewAvatarInitial.setText(email.substring(0, 1).toUpperCase());
        }

        if (textViewCurrentEmail != null && email != null) {
            textViewCurrentEmail.setText(email);
        }

        // Set business name
        if (editTextBusinessName != null && businessName != null) {
            editTextBusinessName.setText(businessName);
        }

        // Set default tax (you may need to adjust this based on your SessionManager)
        if (editTextTaxPercentage != null) {
            editTextTaxPercentage.setText("20"); // Default value
        }

        // Set default currency
        if (autoCompleteCurrency != null) {
            autoCompleteCurrency.setText(currencies[0], false);
        }
    }

    private void setupListeners() {
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(v -> {
                NavHostFragment.findNavController(this).navigateUp();
            });
        }

        if (buttonSaveProfile != null) {
            buttonSaveProfile.setOnClickListener(v -> saveProfile());
        }
    }

     private void saveProfile() {
        // Get values - declare as final for use in inner class
        final String businessName;
        final String taxStr;
        final String currencyFull;
        final String currentPassword;
        final String newPassword;
        final String confirmPassword;

        if (editTextBusinessName != null && editTextBusinessName.getText() != null) {
            businessName = editTextBusinessName.getText().toString().trim();
        } else {
            businessName = "";
        }

        if (editTextTaxPercentage != null && editTextTaxPercentage.getText() != null) {
            taxStr = editTextTaxPercentage.getText().toString().trim();
        } else {
            taxStr = "";
        }

        if (autoCompleteCurrency != null && autoCompleteCurrency.getText() != null) {
            currencyFull = autoCompleteCurrency.getText().toString().trim();
        } else {
            currencyFull = "";
        }

        if (editTextCurrentPassword != null && editTextCurrentPassword.getText() != null) {
            currentPassword = editTextCurrentPassword.getText().toString();
        } else {
            currentPassword = "";
        }

        if (editTextNewPassword != null && editTextNewPassword.getText() != null) {
            newPassword = editTextNewPassword.getText().toString();
        } else {
            newPassword = "";
        }

        if (editTextConfirmPassword != null && editTextConfirmPassword.getText() != null) {
            confirmPassword = editTextConfirmPassword.getText().toString();
        } else {
            confirmPassword = "";
        }

        // Validate business name
        if (businessName.isEmpty()) {
            if (editTextBusinessName != null) {
                editTextBusinessName.setError("Business name is required");
                editTextBusinessName.requestFocus();
            }
            return;
        }

        // Validate tax percentage
        int taxPercentage = 0;
        if (!taxStr.isEmpty()) {
            try {
                taxPercentage = Integer.parseInt(taxStr);
                if (taxPercentage < 0 || taxPercentage > 100) {
                    if (editTextTaxPercentage != null) {
                        editTextTaxPercentage.setError("Tax must be between 0 and 100");
                        editTextTaxPercentage.requestFocus();
                    }
                    return;
                }
            } catch (NumberFormatException e) {
                if (editTextTaxPercentage != null) {
                    editTextTaxPercentage.setError("Invalid tax percentage");
                    editTextTaxPercentage.requestFocus();
                }
                return;
            }
        }

        // Extract currency code
        final String currency;
        if (!currencyFull.isEmpty() && currencyFull.length() >= 3) {
            currency = currencyFull.substring(0, 3);
        } else {
            currency = "ALL";
        }

        // Validate password change
        final boolean changingPassword = !newPassword.isEmpty();
        if (changingPassword) {
            if (currentPassword.isEmpty()) {
                if (editTextCurrentPassword != null) {
                    editTextCurrentPassword.setError("Current password required");
                    editTextCurrentPassword.requestFocus();
                }
                return;
            }
            if (newPassword.length() < 6) {
                if (editTextNewPassword != null) {
                    editTextNewPassword.setError("Minimum 6 characters");
                    editTextNewPassword.requestFocus();
                }
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                if (editTextConfirmPassword != null) {
                    editTextConfirmPassword.setError("Passwords don't match");
                    editTextConfirmPassword.requestFocus();
                }
                return;
            }
        }

        // Disable button
        if (buttonSaveProfile != null) {
            buttonSaveProfile.setEnabled(false);
            buttonSaveProfile.setText("Saving...");
        }

        // Get user ID
        int userId = sessionManager.getUserId();

        // Build request
        try {
            JSONObject params = new JSONObject();
            params.put("user_id", userId);
            params.put("business_name", businessName);
            params.put("tax_percentage", taxPercentage);
            params.put("base_currency", currency);

            if (changingPassword) {
                params.put("password", newPassword);
            }

            // Call API
            ApiClient.put(requireContext(), ApiConfig.UPDATE_USER, params, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (!isAdded()) return;

                    if (buttonSaveProfile != null) {
                        buttonSaveProfile.setEnabled(true);
                        buttonSaveProfile.setText("Save Changes");
                    }

                    boolean success = response.optBoolean("success", false);
                    String message = response.optString("message", "Profile updated");

                    if (success) {
                        // Update session - businessName is now final so this works!
                        sessionManager.setBusinessName(businessName);

                        Snackbar.make(requireView(),
                                        "✓ " + message,
                                        Snackbar.LENGTH_SHORT)
                                .setBackgroundTint(0xFF4CAF50)
                                .show();

                        // Navigate back
                        requireView().postDelayed(() -> {
                            if (isAdded()) {
                                NavHostFragment.findNavController(EditProfileFragment.this)
                                        .navigateUp();
                            }
                        }, 1000);

                    } else {
                        Snackbar.make(requireView(), "⚠ " + message, Snackbar.LENGTH_LONG)
                                .setBackgroundTint(0xFFF57C00)
                                .show();
                    }
                }

                @Override
                public void onError(String error) {
                    if (!isAdded()) return;

                    if (buttonSaveProfile != null) {
                        buttonSaveProfile.setEnabled(true);
                        buttonSaveProfile.setText("Save Changes");
                    }

                    Snackbar.make(requireView(), "⚠ Error: " + error, Snackbar.LENGTH_LONG)
                            .setBackgroundTint(0xFFD32F2F)
                            .show();
                }
            });

        } catch (JSONException e) {
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            if (buttonSaveProfile != null) {
                buttonSaveProfile.setEnabled(true);
                buttonSaveProfile.setText("Save Changes");
            }
        }
    }
}