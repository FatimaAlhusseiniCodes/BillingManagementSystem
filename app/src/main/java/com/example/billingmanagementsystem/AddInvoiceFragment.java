package com.example.billingmanagementsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddInvoiceFragment extends Fragment {

    // UI Components
    private MaterialToolbar toolbar;
    private RadioGroup radioGroupInvoiceType;
    private TextInputEditText editTextInvoiceNumber;
    private MaterialAutoCompleteTextView autoCompletePartner;
    private TextInputLayout partnerInputLayout;
    private TextInputEditText editTextInvoiceDate;
    private TextInputEditText editTextDueDate;
    private MaterialAutoCompleteTextView autoCompleteStatus;
    private TextInputEditText editTextRate;
    private TextInputEditText editTextQuantity;
    private TextView textViewTotalAmount;
    private TextInputEditText editTextNotes;
    private MaterialButton buttonSaveInvoice;

    // Data variables
    private Calendar invoiceCalendar;
    private Calendar dueCalendar;
    private SimpleDateFormat dateFormatter;
    private DecimalFormat decimalFormat;
    private int currentInvoiceNumber = 1; // This would come from database in real app

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_invoice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize formatters
        dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        decimalFormat = new DecimalFormat("0.00");

        // Initialize calendars
        invoiceCalendar = Calendar.getInstance();
        dueCalendar = Calendar.getInstance();

        // Initialize views
        initializeViews(view);

        // Setup toolbar
        setupToolbar();

        // Setup dropdowns
        setupPartnerDropdown();
        setupStatusDropdown();

        // Setup date pickers
        setupDatePickers();

        // Setup auto-calculation
        setupAutoCalculation();

        // Setup default values
        setupDefaultValues();

        // Setup click listeners
        setupClickListeners();
    }

    private void initializeViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        radioGroupInvoiceType = view.findViewById(R.id.radioGroupInvoiceType);
        editTextInvoiceNumber = view.findViewById(R.id.editTextInvoiceNumber);
        autoCompletePartner = view.findViewById(R.id.autoCompletePartner);
        partnerInputLayout = view.findViewById(R.id.autoCompletePartner).getParent().getParent();
        editTextInvoiceDate = view.findViewById(R.id.editTextInvoiceDate);
        editTextDueDate = view.findViewById(R.id.editTextDueDate);
        autoCompleteStatus = view.findViewById(R.id.autoCompleteStatus);
        editTextRate = view.findViewById(R.id.editTextRate);
        editTextQuantity = view.findViewById(R.id.editTextQuantity);
        textViewTotalAmount = view.findViewById(R.id.textViewTotalAmount);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        buttonSaveInvoice = view.findViewById(R.id.buttonSaveInvoice);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            // Navigate back
            NavHostFragment.findNavController(AddInvoiceFragment.this).navigateUp();
        });
    }

    private void setupPartnerDropdown() {
        // TODO: Replace with actual data from database/API
        String[] partners = {
                "ABC Company",
                "XYZ Corporation",
                "Tech Solutions Ltd",
                "Global Trading Co",
                "Best Suppliers Inc"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                partners
        );

        autoCompletePartner.setAdapter(adapter);
    }

    private void setupStatusDropdown() {
        String[] statuses = {"Paid", "Unpaid", "Partially Paid"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                statuses
        );

        autoCompleteStatus.setAdapter(adapter);

        // Set default status to "Unpaid"
        autoCompleteStatus.setText("Unpaid", false);
    }

    private void setupDatePickers() {
        // Invoice Date Picker
        editTextInvoiceDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        invoiceCalendar.set(Calendar.YEAR, year);
                        invoiceCalendar.set(Calendar.MONTH, month);
                        invoiceCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        editTextInvoiceDate.setText(dateFormatter.format(invoiceCalendar.getTime()));
                    },
                    invoiceCalendar.get(Calendar.YEAR),
                    invoiceCalendar.get(Calendar.MONTH),
                    invoiceCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Due Date Picker
        editTextDueDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        dueCalendar.set(Calendar.YEAR, year);
                        dueCalendar.set(Calendar.MONTH, month);
                        dueCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        editTextDueDate.setText(dateFormatter.format(dueCalendar.getTime()));
                    },
                    dueCalendar.get(Calendar.YEAR),
                    dueCalendar.get(Calendar.MONTH),
                    dueCalendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupAutoCalculation() {
        TextWatcher calculationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalAmount();
            }
        };

        editTextRate.addTextChangedListener(calculationWatcher);
        editTextQuantity.addTextChangedListener(calculationWatcher);
    }

    private void calculateTotalAmount() {
        try {
            String rateStr = editTextRate.getText().toString().trim();
            String quantityStr = editTextQuantity.getText().toString().trim();

            if (!rateStr.isEmpty() && !quantityStr.isEmpty()) {
                double rate = Double.parseDouble(rateStr);
                double quantity = Double.parseDouble(quantityStr);
                double total = rate * quantity;

                textViewTotalAmount.setText("$" + decimalFormat.format(total));
            } else {
                textViewTotalAmount.setText("$0.00");
            }
        } catch (NumberFormatException e) {
            textViewTotalAmount.setText("$0.00");
        }
    }

    private void setupDefaultValues() {
        // Set auto-generated invoice number
        editTextInvoiceNumber.setText(generateInvoiceNumber());

        // Set today's date as default for invoice date
        editTextInvoiceDate.setText(dateFormatter.format(invoiceCalendar.getTime()));

        // Set today's date as default for due date
        editTextDueDate.setText(dateFormatter.format(dueCalendar.getTime()));
    }

    private String generateInvoiceNumber() {
        // TODO: This should come from database - get max invoice number + 1
        // For now, using a simple incrementing number
        return String.format(Locale.getDefault(), "INV-%04d", currentInvoiceNumber);
    }

    private void setupClickListeners() {
        // Add Partner Icon Click (the end icon)
        View.OnClickListener addPartnerListener = v -> {
            // TODO: Navigate to Add Partner screen
            // For now, just show a toast
            Toast.makeText(requireContext(),
                    "Navigate to Add Partner screen",
                    Toast.LENGTH_SHORT).show();

            // When you have the navigation action set up in nav_graph:
            // NavHostFragment.findNavController(AddInvoiceFragment.this)
            //     .navigate(R.id.action_addInvoiceFragment_to_addPartnerFragment);
        };

        // Set click listener on the end icon
        if (partnerInputLayout != null) {
            partnerInputLayout.setEndIconOnClickListener(addPartnerListener);
        }

        // Save Invoice Button
        buttonSaveInvoice.setOnClickListener(v -> saveInvoice());
    }

    private void saveInvoice() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Collect data
        String invoiceType = radioGroupInvoiceType.getCheckedRadioButtonId() == R.id.radioSales
                ? "Sales" : "Purchase";
        String invoiceNumber = editTextInvoiceNumber.getText().toString().trim();
        String partner = autoCompletePartner.getText().toString().trim();
        String invoiceDate = editTextInvoiceDate.getText().toString().trim();
        String dueDate = editTextDueDate.getText().toString().trim();
        String status = autoCompleteStatus.getText().toString().trim();
        String rateStr = editTextRate.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        double rate = Double.parseDouble(rateStr);
        double quantity = Double.parseDouble(quantityStr);
        double totalAmount = rate * quantity;

        // TODO: Save to database via ViewModel
        // For now, just show success message
        String message = String.format(Locale.getDefault(),
                "Invoice Saved!\nType: %s\nPartner: %s\nTotal: $%.2f",
                invoiceType, partner, totalAmount);

        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();

        // Navigate back to invoice list
        NavHostFragment.findNavController(AddInvoiceFragment.this).navigateUp();
    }

    private boolean validateInputs() {
        // Validate Partner
        if (autoCompletePartner.getText().toString().trim().isEmpty()) {
            autoCompletePartner.setError("Please select a partner");
            autoCompletePartner.requestFocus();
            return false;
        }

        // Validate Invoice Date
        if (editTextInvoiceDate.getText().toString().trim().isEmpty()) {
            editTextInvoiceDate.setError("Please select invoice date");
            editTextInvoiceDate.requestFocus();
            return false;
        }

        // Validate Due Date
        if (editTextDueDate.getText().toString().trim().isEmpty()) {
            editTextDueDate.setError("Please select due date");
            editTextDueDate.requestFocus();
            return false;
        }

        // Validate Status
        if (autoCompleteStatus.getText().toString().trim().isEmpty()) {
            autoCompleteStatus.setError("Please select payment status");
            autoCompleteStatus.requestFocus();
            return false;
        }

        // Validate Rate
        String rateStr = editTextRate.getText().toString().trim();
        if (rateStr.isEmpty()) {
            editTextRate.setError("Please enter rate");
            editTextRate.requestFocus();
            return false;
        }

        try {
            double rate = Double.parseDouble(rateStr);
            if (rate <= 0) {
                editTextRate.setError("Rate must be greater than 0");
                editTextRate.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editTextRate.setError("Invalid rate format");
            editTextRate.requestFocus();
            return false;
        }

        // Validate Quantity
        String quantityStr = editTextQuantity.getText().toString().trim();
        if (quantityStr.isEmpty()) {
            editTextQuantity.setError("Please enter quantity");
            editTextQuantity.requestFocus();
            return false;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            if (quantity <= 0) {
                editTextQuantity.setError("Quantity must be greater than 0");
                editTextQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editTextQuantity.setError("Invalid quantity format");
            editTextQuantity.requestFocus();
            return false;
        }

        return true;
    }
}