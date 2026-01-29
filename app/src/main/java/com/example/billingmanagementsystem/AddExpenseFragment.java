package com.example.billingmanagementsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Fragment for adding manual expenses
 */
public class AddExpenseFragment extends Fragment {

    private TextInputEditText editTextTitle;
    private MaterialAutoCompleteTextView autoCompleteCategory;
    private TextInputEditText editTextAmount;
    private TextInputEditText editTextDate;
    private TextInputEditText editTextNotes;
    private MaterialButton buttonSaveExpense;

    private Calendar selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    // Expense categories
    private String[] categories = {
            "Office Supplies",
            "Utilities",
            "Rent",
            "Meals & Entertainment",
            "Transportation",
            "Equipment",
            "Marketing",
            "Professional Services",
            "Insurance",
            "Maintenance",
            "Salaries",
            "Other"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        editTextTitle = view.findViewById(R.id.editTextTitle);
        autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        buttonSaveExpense = view.findViewById(R.id.buttonSaveExpense);
        // Setup category dropdown
        setupCategoryDropdown();

        // Setup date picker
        setupDatePicker();

        // Setup save button
        setupSaveButton();

        // Set today's date as default
        selectedDate = Calendar.getInstance();
        editTextDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void setupCategoryDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories
        );
        autoCompleteCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        editTextDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = selectedDate != null ? selectedDate : Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);
                    editTextDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void setupSaveButton() {
        buttonSaveExpense.setOnClickListener(v -> saveExpense());
    }

    private void saveExpense() {
        // Get input values
        String title = editTextTitle.getText() != null ?
                editTextTitle.getText().toString().trim() : "";
        String category = autoCompleteCategory.getText() != null ?
                autoCompleteCategory.getText().toString().trim() : "";
        String amountStr = editTextAmount.getText() != null ?
                editTextAmount.getText().toString().trim() : "";
        String notes = editTextNotes.getText() != null ?
                editTextNotes.getText().toString().trim() : "";

        // Validation
        if (title.isEmpty()) {
            editTextTitle.setError("Title is required");
            editTextTitle.requestFocus();
            return;
        }

        if (category.isEmpty()) {
            autoCompleteCategory.setError("Category is required");
            autoCompleteCategory.requestFocus();
            return;
        }

        if (amountStr.isEmpty()) {
            editTextAmount.setError("Amount is required");
            editTextAmount.requestFocus();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                editTextAmount.setError("Amount must be greater than 0");
                editTextAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editTextAmount.setError("Invalid amount");
            editTextAmount.requestFocus();
            return;
        }

        if (selectedDate == null) {
            Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button to prevent double-click
        buttonSaveExpense.setEnabled(false);
        buttonSaveExpense.setText("Saving...");

        // Get user ID from session
        SessionManager session = new SessionManager(requireContext());
        int userId = session.getUserId();

        // Format date for API (yyyy-MM-dd)
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateStr = apiDateFormat.format(selectedDate.getTime());

        // Get category ID
        int categoryId = getCategoryId(category);

        // Call API to save expense
        ApiClient.createManualExpense(requireContext(), userId, categoryId, amount, dateStr, title,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        if (!isAdded()) return;

                        try {
                            if (response.getBoolean("success")) {
                                Snackbar.make(requireView(),
                                                "✓ Expense saved successfully!",
                                                Snackbar.LENGTH_SHORT)
                                        .setBackgroundTint(0xFF4CAF50)
                                        .show();

                                // Navigate back after short delay
                                requireView().postDelayed(() -> {
                                    if (isAdded()) {
                                        NavHostFragment.findNavController(AddExpenseFragment.this)
                                                .navigateUp();
                                    }
                                }, 500);
                            } else {
                                String message = response.optString("message", "Failed to save expense");
                                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                                resetButton();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            resetButton();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (!isAdded()) return;
                        Toast.makeText(requireContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                        resetButton();
                    }
                });
    }

    private void resetButton() {
        if (buttonSaveExpense != null) {
            buttonSaveExpense.setEnabled(true);
            buttonSaveExpense.setText("Save Expense");
        }
    }

    // Map category name to category ID based on your Categories table
    private int getCategoryId(String categoryName) {
        switch (categoryName) {
            case "Office Supplies": return 1;
            case "Utilities": return 2;
            case "Rent": return 3;
            case "Meals & Entertainment": return 4;
            case "Transportation": return 5;
            case "Equipment": return 6;
            case "Marketing": return 7;
            case "Professional Services": return 8;
            case "Insurance": return 9;
            case "Maintenance": return 10;
            case "Salaries": return 11;
            case "Other": return 12;
            default: return 1;
        }
    }

    /**
     * Clear all fields
     */
    private void clearFields() {
        editTextTitle.setText("");
        autoCompleteCategory.setText("");
        editTextAmount.setText("");
        editTextNotes.setText("");
        selectedDate = Calendar.getInstance();
        editTextDate.setText(dateFormat.format(selectedDate.getTime()));
    }
}












