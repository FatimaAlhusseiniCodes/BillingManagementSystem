package com.example.billingmanagementsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddIncomeFragment extends Fragment {

    private TextInputEditText editTextSource;
    private TextInputEditText editTextAmount;
    private TextInputEditText editTextDate;
    private TextInputEditText editTextProduct;
    private TextInputEditText editTextPhone;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextNotes;
    private MaterialButton buttonSaveIncome;
    private MaterialButton buttonCancel;

    private Calendar selectedDate;
    private SimpleDateFormat dateFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        selectedDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        initializeViews(view);
        setupListeners();
        updateDateField();
    }

    private void initializeViews(View view) {
        editTextSource = view.findViewById(R.id.editTextSourceName);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextProduct = view.findViewById(R.id.editTextProduct);
        editTextPhone = view.findViewById(R.id.editTextPhone);
        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        buttonSaveIncome = view.findViewById(R.id.buttonSaveIncome);
        buttonCancel = view.findViewById(R.id.buttonCancel);
    }


    private void setupListeners() {
        if (editTextDate != null) {
            editTextDate.setOnClickListener(v -> showDatePicker());
        }

        if (buttonCancel != null) {
            buttonCancel.setOnClickListener(v -> {
                if (hasFormData()) {
                    showCancelConfirmationDialog();
                } else {
                    navigateBack();
                }
            });
        }

        if (buttonSaveIncome != null) {
            buttonSaveIncome.setOnClickListener(v -> saveIncome());
        }
    }

    private void showDatePicker() {
        if (getContext() == null) return;

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(Calendar.YEAR, year);
                    selectedDate.set(Calendar.MONTH, month);
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateField();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateDateField() {
        if (editTextDate != null) {
            editTextDate.setText(dateFormat.format(selectedDate.getTime()));
        }
    }

    private void saveIncome() {
        if (!validateFields()) {
            return;
        }

        String source = getTextSafely(editTextSource);
        String amountStr = getTextSafely(editTextAmount);
        String product = getTextSafely(editTextProduct);

        double amount = 0;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager session = new SessionManager(getContext());
        int userId = session.getUserId();

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String apiDate = apiDateFormat.format(selectedDate.getTime());

        String description = source + (product.isEmpty() ? "" : " - " + product);
        int categoryId = 1;

        buttonSaveIncome.setEnabled(false);
        buttonSaveIncome.setText("Saving...");

        ApiClient.createManualIncome(getContext(), userId, categoryId, amount, apiDate, description,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Snackbar.make(requireView(),
                                                "✓ Income added successfully!",
                                                Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(0xFF4CAF50)
                                        .show();

                                navigateBack();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        buttonSaveIncome.setEnabled(true);
                        buttonSaveIncome.setText("Save Income");
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                        buttonSaveIncome.setEnabled(true);
                        buttonSaveIncome.setText("Save Income");
                    }
                }
        );
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (editTextSource != null) editTextSource.setError(null);
        if (editTextAmount != null) editTextAmount.setError(null);

        String source = getTextSafely(editTextSource);
        if (source.isEmpty()) {
            if (editTextSource != null) {
                editTextSource.setError("Income source is required");
                editTextSource.requestFocus();
            }
            isValid = false;
        }

        String amountStr = getTextSafely(editTextAmount);
        if (amountStr.isEmpty()) {
            if (editTextAmount != null) {
                editTextAmount.setError("Amount is required");
                if (isValid) editTextAmount.requestFocus();
            }
            isValid = false;
        } else {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount <= 0) {
                    if (editTextAmount != null) {
                        editTextAmount.setError("Amount must be greater than 0");
                        if (isValid) editTextAmount.requestFocus();
                    }
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                if (editTextAmount != null) {
                    editTextAmount.setError("Invalid amount");
                    if (isValid) editTextAmount.requestFocus();
                }
                isValid = false;
            }
        }

        String email = getTextSafely(editTextEmail);
        if (!email.isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (editTextEmail != null) {
                editTextEmail.setError("Invalid email format");
                if (isValid) editTextEmail.requestFocus();
            }
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(getContext(),
                    "⚠️ Please fill in all required fields correctly",
                    Toast.LENGTH_LONG).show();
        }

        return isValid;
    }

    private void navigateBack() {
        try {
            NavHostFragment.findNavController(AddIncomeFragment.this).navigateUp();
        } catch (Exception e) {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    private String getTextSafely(TextInputEditText editText) {
        if (editText == null || editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private boolean hasFormData() {
        String source = getTextSafely(editTextSource);
        String amount = getTextSafely(editTextAmount);
        String product = getTextSafely(editTextProduct);

        return !source.isEmpty() || !amount.isEmpty() || !product.isEmpty();
    }

    private void showCancelConfirmationDialog() {
        if (getContext() == null) return;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) -> {
                    navigateBack();
                })
                .setNegativeButton("Keep Editing", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}