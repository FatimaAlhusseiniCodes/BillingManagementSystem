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

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddInvoiceFragment extends Fragment {

    // UI Components
    private RadioGroup radioGroupInvoiceType;
    private MaterialRadioButton radioSales;
    private MaterialRadioButton radioPurchase;
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
    private int currentInvoiceNumber = 1;

    // Partner data from API
    private List<String> partnerNames = new ArrayList<>();
    private List<Integer> partnerIds = new ArrayList<>();
    private ArrayAdapter<String> partnerAdapter;
    private int selectedPartnerId = -1;

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

        // Setup dropdowns
        setupPartnerDropdown();
        setupStatusDropdown();

        // Setup listeners
        setupInvoiceTypeListener();
        setupDatePickers();
        setupAutoCalculation();
        setupDefaultValues();
        setupClickListeners();

        // Load partners from API (Initial call)
        loadPartnersFromApi();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh partners list when returning (e.g., after adding a new partner)
        loadPartnersFromApi();
    }

    private void initializeViews(View view) {
        radioGroupInvoiceType = view.findViewById(R.id.radioGroupInvoiceType);
        radioSales = view.findViewById(R.id.radioSales);
        radioPurchase = view.findViewById(R.id.radioPurchase);
        editTextInvoiceNumber = view.findViewById(R.id.editTextInvoiceNumber);
        autoCompletePartner = view.findViewById(R.id.autoCompletePartner);
        partnerInputLayout = view.findViewById(R.id.partnerInputLayout);
        editTextInvoiceDate = view.findViewById(R.id.editTextInvoiceDate);
        editTextDueDate = view.findViewById(R.id.editTextDueDate);
        autoCompleteStatus = view.findViewById(R.id.autoCompleteStatus);
        editTextRate = view.findViewById(R.id.editTextRate);
        editTextQuantity = view.findViewById(R.id.editTextQuantity);
        textViewTotalAmount = view.findViewById(R.id.textViewTotalAmount);
        editTextNotes = view.findViewById(R.id.editTextNotes);
        buttonSaveInvoice = view.findViewById(R.id.buttonSaveInvoice);
    }

    private void setupPartnerDropdown() {
        // Use standard dropdown layout
        partnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                partnerNames
        );
        autoCompletePartner.setAdapter(partnerAdapter);

        // Critical for Material AutoComplete: show list even if text is empty
        autoCompletePartner.setThreshold(0);

        // Handle partner selection
        autoCompletePartner.setOnItemClickListener((parent, view, position, id) -> {
            // Get selected name to find correct ID from global list
            String selectedName = (String) parent.getItemAtPosition(position);
            int index = partnerNames.indexOf(selectedName);
            if (index != -1 && index < partnerIds.size()) {
                selectedPartnerId = partnerIds.get(index);
                android.util.Log.d("PARTNER_DEBUG", "Selected: " + selectedName + " ID: " + selectedPartnerId);
            }
        });

        // Force dropdown to show when clicked
        autoCompletePartner.setOnClickListener(v -> autoCompletePartner.showDropDown());
    }

    private void setupInvoiceTypeListener() {
        radioGroupInvoiceType.setOnCheckedChangeListener((group, checkedId) -> {
            autoCompletePartner.setText("", false);
            selectedPartnerId = -1;
            loadPartnersFromApi();
        });
    }

     private void loadPartnersFromApi() {
        if (!isAdded()) return;

        // 1. Capture the type immediately to avoid radio button state changes during the async call
        final String partnerType = (radioSales != null && radioSales.isChecked()) ? "Customer" : "Supplier";

        android.util.Log.d("PARTNER_DEBUG", "Request started for: " + partnerType);

        ApiClient.getPartners(requireContext(), partnerType, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if (!isAdded()) return;

                try {
                    // 2. Local lists to prevent partial UI updates
                    List<String> tempNames = new ArrayList<>();
                    List<Integer> tempIds = new ArrayList<>();

                    if (response.has("data") && !response.isNull("data")) {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            int id = obj.optInt("partner_id", obj.optInt("PartnerID", -1));
                            String name = obj.optString("name", obj.optString("Name", ""));

                            if (id != -1 && !name.isEmpty()) {
                                tempIds.add(id);
                                tempNames.add(name);
                            }
                        }
                    }

                    // 3. Perform all UI updates together on the Main Thread
                    requireActivity().runOnUiThread(() -> {
                        partnerNames.clear();
                        partnerNames.addAll(tempNames);
                        partnerIds.clear();
                        partnerIds.addAll(tempIds);

                        // Re-initialize adapter to clear internal filters
                        partnerAdapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_dropdown_item_1line,
                                partnerNames
                        );
                        autoCompletePartner.setAdapter(partnerAdapter);

                        android.util.Log.d("PARTNER_DEBUG", "UI Sync Complete. Count: " + partnerNames.size());

                        // Toast check MUST be inside runOnUiThread and AFTER addAll
                        if (partnerNames.isEmpty()) {
                            Toast.makeText(requireContext(), "No " + partnerType + "s found in database", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    android.util.Log.e("PARTNER_DEBUG", "Error parsing: " + e.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "API Error: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void setupStatusDropdown() {
        String[] statuses = {"Paid", "Unpaid", "Partially Paid"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, statuses);
        autoCompleteStatus.setAdapter(adapter);
        autoCompleteStatus.setText("Unpaid", false);
    }

    private void setupDatePickers() {
        editTextInvoiceDate.setOnClickListener(v -> showDatePicker(invoiceCalendar, editTextInvoiceDate));
        editTextDueDate.setOnClickListener(v -> showDatePicker(dueCalendar, editTextDueDate));
    }

    private void showDatePicker(Calendar cal, TextInputEditText targetField) {
        new DatePickerDialog(requireContext(), (view, year, month, day) -> {
            cal.set(year, month, day);
            targetField.setText(dateFormatter.format(cal.getTime()));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void setupAutoCalculation() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int b, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int b, int c, int a) {}
            @Override public void afterTextChanged(Editable s) { calculateTotalAmount(); }
        };
        editTextRate.addTextChangedListener(watcher);
        editTextQuantity.addTextChangedListener(watcher);
    }

    private void calculateTotalAmount() {
        try {
            double rate = Double.parseDouble(editTextRate.getText().toString().trim());
            double qty = Double.parseDouble(editTextQuantity.getText().toString().trim());
            textViewTotalAmount.setText("$" + decimalFormat.format(rate * qty));
        } catch (Exception e) {
            textViewTotalAmount.setText("$0.00");
        }
    }

    private void setupDefaultValues() {
        editTextInvoiceNumber.setText(generateInvoiceNumber());
        editTextInvoiceDate.setText(dateFormatter.format(invoiceCalendar.getTime()));
        editTextDueDate.setText(dateFormatter.format(dueCalendar.getTime()));
    }

    private String generateInvoiceNumber() {
        return String.format(Locale.getDefault(), "INV-%04d", currentInvoiceNumber);
    }

    private void setupClickListeners() {
        if (partnerInputLayout != null) {
            partnerInputLayout.setEndIconOnClickListener(v ->
                    NavHostFragment.findNavController(this).navigate(R.id.addPartnerFragment)
            );
        }
        buttonSaveInvoice.setOnClickListener(v -> saveInvoice());
    }

    private void saveInvoice() {
        // Validate inputs
        if (!validateInputs()) {
            return;
        }

        // Disable button to prevent double-click
        buttonSaveInvoice.setEnabled(false);
        buttonSaveInvoice.setText("Saving...");

        // Collect data (fixed - was reading from wrong fields!)
        String invoiceType = radioGroupInvoiceType.getCheckedRadioButtonId() == R.id.radioSales
                ? "Sales" : "Purchase";
        String invoiceNumber = editTextInvoiceNumber.getText() != null ?
                editTextInvoiceNumber.getText().toString().trim() : "";
        String partner = autoCompletePartner.getText().toString().trim();
        String invoiceDate = editTextInvoiceDate.getText() != null ?
                editTextInvoiceDate.getText().toString().trim() : "";
        String dueDate = editTextDueDate.getText() != null ?
                editTextDueDate.getText().toString().trim() : "";
        String status = autoCompleteStatus.getText().toString().trim();
        String rateStr = editTextRate.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();
        String notes = editTextNotes.getText() != null ?
                editTextNotes.getText().toString().trim() : "";

        double rate = Double.parseDouble(rateStr);
        double quantity = Double.parseDouble(quantityStr);
        double totalAmount = rate * quantity;

        // Get user ID from session
        SessionManager session = new SessionManager(requireContext());
        int userId = session.getUserId();

        // Convert date format from dd/MM/yyyy to yyyy-MM-dd for API
        String apiInvoiceDate = convertDateFormat(invoiceDate);
        String apiDueDate = convertDateFormat(dueDate);

        // Build invoice data JSON
        try {
            JSONObject invoiceData = new JSONObject();
            invoiceData.put("user_id", userId);
            invoiceData.put("partner_id", selectedPartnerId);
            invoiceData.put("invoice_number", invoiceNumber);
            invoiceData.put("type", invoiceType);
            invoiceData.put("invoice_date", apiInvoiceDate);
            invoiceData.put("due_date", apiDueDate);
            invoiceData.put("status", status);
            invoiceData.put("subtotal", totalAmount);
            invoiceData.put("tax_amount", 0); // You can calculate tax if needed
            invoiceData.put("total_amount", totalAmount);
            invoiceData.put("notes", notes);

            // Add line item
            JSONArray items = new JSONArray();
            JSONObject item = new JSONObject();
            item.put("description", "Item");
            item.put("quantity", quantity);
            item.put("unit_price", rate);
            item.put("total", totalAmount);
            items.put(item);
            invoiceData.put("items", items);

            // DEBUG: Log what we're sending
            android.util.Log.d("INVOICE_DEBUG", "=== SAVING INVOICE ===");
            android.util.Log.d("INVOICE_DEBUG", "User ID: " + userId);
            android.util.Log.d("INVOICE_DEBUG", "Partner ID: " + selectedPartnerId);
            android.util.Log.d("INVOICE_DEBUG", "Invoice Number: " + invoiceNumber);
            android.util.Log.d("INVOICE_DEBUG", "Type: " + invoiceType);
            android.util.Log.d("INVOICE_DEBUG", "Invoice Date: " + apiInvoiceDate);
            android.util.Log.d("INVOICE_DEBUG", "Due Date: " + apiDueDate);
            android.util.Log.d("INVOICE_DEBUG", "Status: " + status);
            android.util.Log.d("INVOICE_DEBUG", "Total: " + totalAmount);
            android.util.Log.d("INVOICE_DEBUG", "JSON: " + invoiceData.toString());

            // Call API to create invoice
            ApiClient.createInvoice(requireContext(), invoiceData, new ApiClient.ApiCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    android.util.Log.d("INVOICE_DEBUG", "API Response: " + response.toString());
                    if (!isAdded()) return;

                    buttonSaveInvoice.setEnabled(true);
                    buttonSaveInvoice.setText("Save Invoice");

                    boolean success = response.optBoolean("success", false);
                    String message = response.optString("message", "Invoice created");

                    if (success) {
                        Toast.makeText(requireContext(),
                                "✓ Invoice saved successfully!",
                                Toast.LENGTH_SHORT).show();

                        // Navigate back to invoice list
                        NavHostFragment.findNavController(AddInvoiceFragment.this).navigateUp();
                    } else {
                        Toast.makeText(requireContext(),
                                "⚠ " + message,
                                Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onError(String error) {
                    android.util.Log.e("INVOICE_DEBUG", "API Error: " + error);

                    if (!isAdded()) return;

                    buttonSaveInvoice.setEnabled(true);
                    buttonSaveInvoice.setText("Save Invoice");

                    Toast.makeText(requireContext(),
                            "⚠ Error: " + error,
                            Toast.LENGTH_LONG).show();
                }
            });

        } catch (JSONException e) {
            buttonSaveInvoice.setEnabled(true);
            buttonSaveInvoice.setText("Save Invoice");
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Helper method to convert date from dd/MM/yyyy to yyyy-MM-dd
    private String convertDateFormat(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(dateStr));
        } catch (Exception e) {
            return dateStr; // Return original if conversion fails
        }
    }

    private boolean validateInputs() {
        // Validate Partner text
        if (autoCompletePartner.getText().toString().trim().isEmpty()) {
            autoCompletePartner.setError("Partner is required");
            autoCompletePartner.requestFocus();
            return false;
        }

        // Validate Partner was selected from list (not just typed)
        if (selectedPartnerId == -1) {
            autoCompletePartner.setError("Please select a partner from the list");
            autoCompletePartner.requestFocus();
            Toast.makeText(requireContext(),
                    "Please select a partner from the dropdown",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate Rate
        if (editTextRate.getText().toString().trim().isEmpty()) {
            editTextRate.setError("Rate is required");
            editTextRate.requestFocus();
            return false;
        }

        try {
            double rate = Double.parseDouble(editTextRate.getText().toString().trim());
            if (rate <= 0) {
                editTextRate.setError("Rate must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            editTextRate.setError("Invalid rate");
            return false;
        }

        // Validate Quantity
        if (editTextQuantity.getText().toString().trim().isEmpty()) {
            editTextQuantity.setError("Quantity is required");
            editTextQuantity.requestFocus();
            return false;
        }

        try {
            double qty = Double.parseDouble(editTextQuantity.getText().toString().trim());
            if (qty <= 0) {
                editTextQuantity.setError("Quantity must be greater than 0");
                return false;
            }
        } catch (NumberFormatException e) {
            editTextQuantity.setError("Invalid quantity");
            return false;
        }

        return true;
    }
}