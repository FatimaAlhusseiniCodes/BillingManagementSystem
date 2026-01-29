package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

/**
 * AddPartnerFragment - form to add new partners (customers/suppliers)
 * Integrated with backend:
 * POST /api/partners/create.php via ApiClient.createPartner(...)
 */
public class AddPartnerFragment extends Fragment {

    // UI Components
    private RadioGroup radioGroupPartnerType;
    private MaterialRadioButton radioCustomer, radioSupplier, radioBoth;

    private TextInputEditText editTextPartnerNumber;
    private TextInputEditText editTextFirstName;
    private TextInputEditText editTextLastName;
    private TextInputEditText editTextCompanyName;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPhone;
    private TextInputEditText editTextAddress;
    private TextInputEditText editTextCity;
    private TextInputEditText editTextCountry;
    private TextInputEditText editTextTaxNumber;
    private TextInputEditText editTextNotes;

    private MaterialButton buttonSavePartner;
    private MaterialButton buttonCancel;

    private String selectedPartnerType = "Customer"; // Default

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_partner, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        initializeViews(view);

        // Setup listeners
        setupListeners();

        // Generate partner number
        generatePartnerNumber();
    }

    private void initializeViews(View view) {
        radioGroupPartnerType = view.findViewById(R.id.radioGroupPartnerType);
        radioCustomer = view.findViewById(R.id.radioCustomer);
        radioSupplier = view.findViewById(R.id.radioSupplier);
        radioBoth = view.findViewById(R.id.radioBoth);

        editTextPartnerNumber = view.findViewById(R.id.editTextPartnerNumber);
        editTextFirstName = view.findViewById(R.id.editTextFirstName);
        editTextLastName = view.findViewById(R.id.editTextLastName);
        editTextCompanyName = view.findViewById(R.id.editTextCompanyName);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextAddress = view.findViewById(R.id.editTextAddress);
        editTextCity = view.findViewById(R.id.editTextCity);
        editTextCountry = view.findViewById(R.id.editTextCountry);
        editTextTaxNumber = view.findViewById(R.id.editTextTaxNumber);
        editTextNotes = view.findViewById(R.id.editTextNotes);

        buttonSavePartner = view.findViewById(R.id.buttonSavePartner);
        buttonCancel = view.findViewById(R.id.buttonCancel);
    }

    private void setupListeners() {
        // Partner type selection
        radioGroupPartnerType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCustomer) {
                selectedPartnerType = "Customer";
            } else if (checkedId == R.id.radioSupplier) {
                selectedPartnerType = "Supplier";
            } else if (checkedId == R.id.radioBoth) {
                selectedPartnerType = "Both";
            }
        });

        // Cancel button
        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(v -> {
                if (hasFormData()) {
                    showCancelConfirmationDialog();
                } else {
                    NavHostFragment.findNavController(AddPartnerFragment.this).navigateUp();
                }
            });
        }

        // Save button
        buttonSavePartner.setOnClickListener(v -> savePartner());
    }

    private void generatePartnerNumber() {
        // Placeholder, UI-only. Backend uses its own IDs.
        int nextNumber = 7;
        String partnerNumber = String.format("PART-%04d", nextNumber);
        editTextPartnerNumber.setText(partnerNumber);
    }

    private void savePartner() {
        // Validate required fields
        if (!validateFields()) {
            return;
        }

        // Get values
        String partnerNumber = editTextPartnerNumber.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String companyName = editTextCompanyName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String country = editTextCountry.getText().toString().trim();
        String taxNumber = editTextTaxNumber.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        String displayName = companyName.isEmpty()
                ? (firstName + " " + lastName).trim()
                : companyName;

        String contactName = (firstName + " " + lastName).trim();

        // Disable button while sending
        buttonSavePartner.setEnabled(false);
        buttonSavePartner.setAlpha(0.6f);

        // Call ApiClient.createPartner with 7 arguments:
        // (Context, name, contact_name, email, phone, type, ApiCallback)
        ApiClient.createPartner(
                requireContext(),
                displayName,
                contactName,
                email,
                phone,
                selectedPartnerType,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        // Re-enable button
                        buttonSavePartner.setEnabled(true);
                        buttonSavePartner.setAlpha(1.0f);

                        if (!isAdded()) return;

                        boolean success = response.optBoolean("success", false);
                        String message = response.optString(
                                "message",
                                "Partner \"" + displayName + "\" added successfully!"
                        );

                        if (success) {
                            int partnerId = response.optInt("partner_id", -1);

                            Snackbar.make(requireView(),
                                            "✓ " + message,
                                            Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(0xFF4CAF50)
                                    .show();

                            // Optional: local Partner object
                            Partner newPartner = new Partner(
                                    partnerId == -1 ? null : String.valueOf(partnerId),
                                    partnerNumber,
                                    firstName,
                                    lastName,
                                    email,
                                    phone,
                                    address,
                                    city,
                                    country,
                                    selectedPartnerType,
                                    companyName,
                                    taxNumber,
                                    notes,
                                    System.currentTimeMillis(),
                                    System.currentTimeMillis(),
                                    0,
                                    0.0,
                                    0.0
                            );

                            // Navigate back
                            NavHostFragment.findNavController(AddPartnerFragment.this).navigateUp();

                        } else {
                            Snackbar.make(requireView(),
                                            "⚠ " + message,
                                            Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(0xFFF57C00)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        // Re-enable button
                        buttonSavePartner.setEnabled(true);
                        buttonSavePartner.setAlpha(1.0f);

                        if (!isAdded()) return;

                        Snackbar.make(requireView(),
                                        "⚠ Error: " + error,
                                        Snackbar.LENGTH_LONG)
                                .setBackgroundTint(0xFFD32F2F)
                                .show();
                    }
                }
        );
    }

    private boolean validateFields() {
        boolean isValid = true;

        editTextFirstName.setError(null);
        editTextLastName.setError(null);
        editTextEmail.setError(null);
        editTextPhone.setError(null);

        String firstName = editTextFirstName.getText().toString().trim();
        if (firstName.isEmpty()) {
            editTextFirstName.setError("First name is required");
            editTextFirstName.requestFocus();
            isValid = false;
        }

        String lastName = editTextLastName.getText().toString().trim();
        if (lastName.isEmpty()) {
            editTextLastName.setError("Last name is required");
            if (isValid) editTextLastName.requestFocus();
            isValid = false;
        }

        String email = editTextEmail.getText().toString().trim();
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            if (isValid) editTextEmail.requestFocus();
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
            if (isValid) editTextEmail.requestFocus();
            isValid = false;
        }

        String phone = editTextPhone.getText().toString().trim();
        if (phone.isEmpty()) {
            editTextPhone.setError("Phone number is required");
            if (isValid) editTextPhone.requestFocus();
            isValid = false;
        } else if (phone.length() < 8) {
            editTextPhone.setError("Phone number is too short");
            if (isValid) editTextPhone.requestFocus();
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(getContext(),
                    "⚠️ Please fill in all required fields correctly",
                    Toast.LENGTH_LONG).show();
        }

        return isValid;
    }

    private boolean hasFormData() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String company = editTextCompanyName.getText().toString().trim();

        return !firstName.isEmpty() || !lastName.isEmpty() || !email.isEmpty() ||
                !phone.isEmpty() || !company.isEmpty();
    }

    private void showCancelConfirmationDialog() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) ->
                        NavHostFragment.findNavController(AddPartnerFragment.this).navigateUp())
                .setNegativeButton("Keep Editing", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
