package com.example.billingmanagementsystem; // ⚠️ change to your package

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ExpensesFragment extends Fragment {

    private TextInputEditText etTitle, etAmount, etDate, etNotes;
    private Spinner spCategory;
    private Button btnSave, btnShowTable;
    private RecyclerView rvExpenses;

    private ArrayList<Expense> expenseList = new ArrayList<>();
    private ExpenseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        // Bind views
        etTitle = view.findViewById(R.id.etTitle);
        etAmount = view.findViewById(R.id.etAmount);
        etDate = view.findViewById(R.id.etDate);
        etNotes = view.findViewById(R.id.etNotes);
        spCategory = view.findViewById(R.id.spCategory);
        btnSave = view.findViewById(R.id.btnSave);
        btnShowTable = view.findViewById(R.id.btnShowTable);
        rvExpenses = view.findViewById(R.id.rvExpenses);

        setupCategorySpinner();
        setupDatePicker();
        setupRecyclerView();
        setupSaveButton();
        setupShowTableButton();

        return view;
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (view, y, m, d) -> etDate.setText(d + "/" + (m + 1) + "/" + y),
                    year, month, day
            );
            dialog.show();
        });
    }

    private void setupRecyclerView() {
        adapter = new ExpenseAdapter(expenseList);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenses.setAdapter(adapter);
        rvExpenses.setVisibility(View.GONE); // hidden initially
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> saveExpense());
    }

    private void setupShowTableButton() {
        btnShowTable.setOnClickListener(v -> {
            if (rvExpenses.getVisibility() == View.GONE) {
                rvExpenses.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Expenses Table shown", Toast.LENGTH_SHORT).show();
            } else {
                rvExpenses.setVisibility(View.GONE);
            }
        });
    }

    private void saveExpense() {
        String title = etTitle.getText().toString().trim();
        String amount = etAmount.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        if (title.isEmpty() || amount.isEmpty() || date.isEmpty() || category.equals("Select Category")) {
            Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current timestamp
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        // Add to list
        Expense expense = new Expense(title, amount, category, date, notes, timestamp);
        DataHolder.getInstance().addExpense(expense);

        adapter.notifyDataSetChanged();

        // Show Toast
        Toast.makeText(getContext(), "Expense saved successfully", Toast.LENGTH_SHORT).show();

        // Clear fields
        etTitle.setText("");
        etAmount.setText("");
        etDate.setText("");
        etNotes.setText("");
        spCategory.setSelection(0);
    }
}


