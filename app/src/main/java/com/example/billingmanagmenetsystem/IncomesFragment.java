package com.example.billingmanagmenetsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class IncomesFragment extends Fragment {

    private Button btnCustomer, btnOthers, btnSaveIncome, btnShowIncomeTable;
    private LinearLayout customerFields, othersFields;
    private RecyclerView rvIncomes;
    private TextInputEditText etCustomerName, etCustomerPhone, etCustomerEmail, etProduct,
            etCustomerAmount, etCustomerDate, etCustomerNotes;
    private TextInputEditText etOtherSource, etOtherAmount, etOtherDate, etOtherNotes;

    private ArrayList<Income> incomeList = new ArrayList<>();
    private IncomeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incomes, container, false);

        // Bind Views
        btnCustomer = view.findViewById(R.id.btnCustomer);
        btnOthers = view.findViewById(R.id.btnOthers);
        btnSaveIncome = view.findViewById(R.id.btnSaveIncome);
        btnShowIncomeTable = view.findViewById(R.id.btnShowIncomeTable);
        customerFields = view.findViewById(R.id.customerFields);
        othersFields = view.findViewById(R.id.othersFields);
        rvIncomes = view.findViewById(R.id.rvIncomes);

        etCustomerName = view.findViewById(R.id.etCustomerName);
        etCustomerPhone = view.findViewById(R.id.etCustomerPhone);
        etCustomerEmail = view.findViewById(R.id.etCustomerEmail);
        etProduct = view.findViewById(R.id.etProduct);
        etCustomerAmount = view.findViewById(R.id.etCustomerAmount);
        etCustomerDate = view.findViewById(R.id.etCustomerDate);
        etCustomerNotes = view.findViewById(R.id.etCustomerNotes);

        etOtherSource = view.findViewById(R.id.etOtherSource);
        etOtherAmount = view.findViewById(R.id.etOtherAmount);
        etOtherDate = view.findViewById(R.id.etOtherDate);
        etOtherNotes = view.findViewById(R.id.etOtherNotes);

        setupToggleButtons();
        setupDatePickers();
        setupRecyclerView();
        setupSaveIncomeButton();
        setupShowIncomeTableButton();

        return view;
    }

    private void setupToggleButtons() {
        btnCustomer.setOnClickListener(v -> {
            customerFields.setVisibility(View.VISIBLE);
            othersFields.setVisibility(View.GONE);
        });

        btnOthers.setOnClickListener(v -> {
            customerFields.setVisibility(View.GONE);
            othersFields.setVisibility(View.VISIBLE);
        });
    }

    private void setupDatePickers() {
        etCustomerDate.setOnClickListener(v -> showDatePicker(etCustomerDate));
        etOtherDate.setOnClickListener(v -> showDatePicker(etOtherDate));
    }

    private void showDatePicker(TextInputEditText editText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> editText.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void setupRecyclerView() {
        adapter = new IncomeAdapter(incomeList);
        rvIncomes.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIncomes.setAdapter(adapter);
        rvIncomes.setVisibility(View.GONE);
    }

    private void setupSaveIncomeButton() {
        btnSaveIncome.setOnClickListener(v -> saveIncome());
    }

    private void setupShowIncomeTableButton() {
        btnShowIncomeTable.setOnClickListener(v -> {
            if (rvIncomes.getVisibility() == View.GONE) {
                rvIncomes.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Income Table shown", Toast.LENGTH_SHORT).show();
            } else {
                rvIncomes.setVisibility(View.GONE);
            }
        });
    }

    private void saveIncome() {
        String type, nameOrSource = "", phone = "", email = "", product = "", amount, date, notes;

        if (customerFields.getVisibility() == View.VISIBLE) {
            type = "Customer";
            nameOrSource = etCustomerName.getText().toString().trim();
            phone = etCustomerPhone.getText().toString().trim();
            email = etCustomerEmail.getText().toString().trim();
            product = etProduct.getText().toString().trim();
            amount = etCustomerAmount.getText().toString().trim();
            date = etCustomerDate.getText().toString().trim();
            notes = etCustomerNotes.getText().toString().trim();

            if (nameOrSource.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            type = "Other";
            nameOrSource = etOtherSource.getText().toString().trim();
            amount = etOtherAmount.getText().toString().trim();
            date = etOtherDate.getText().toString().trim();
            notes = etOtherNotes.getText().toString().trim();

            if (nameOrSource.isEmpty() || amount.isEmpty() || date.isEmpty()) {
                Toast.makeText(getContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Timestamp
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        Income income = new Income(type, nameOrSource, phone, email, product, amount, date, notes, timestamp);
        DataHolder.getInstance().addIncome(income);

        adapter.notifyDataSetChanged();

        Toast.makeText(getContext(), "Income saved successfully", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    private void clearFields() {
        etCustomerName.setText("");
        etCustomerPhone.setText("");
        etCustomerEmail.setText("");
        etProduct.setText("");
        etCustomerAmount.setText("");
        etCustomerDate.setText("");
        etCustomerNotes.setText("");

        etOtherSource.setText("");
        etOtherAmount.setText("");
        etOtherDate.setText("");
        etOtherNotes.setText("");
    }
}

