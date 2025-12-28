package com.example.billingmanagementsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ProfitFragment extends Fragment {

    private TextInputEditText etStartDate, etEndDate;
    private Button btnFilter;
    private TextView tvTotalIncome, tvTotalExpenses, tvTotalProfit;
    private RecyclerView rvIncomeProfit, rvExpenseProfit;

    private ArrayList<Income> incomeList = DataHolder.getInstance().getIncomeList();
    private ArrayList<Expense> expenseList = DataHolder.getInstance().getExpenseList();

    private IncomeAdapter incomeAdapter;
    private ExpenseAdapter expenseAdapter;

    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profit, container, false);

        etStartDate = view.findViewById(R.id.etStartDate);
        etEndDate = view.findViewById(R.id.etEndDate);
        btnFilter = view.findViewById(R.id.btnFilter);

        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpenses = view.findViewById(R.id.tvTotalExpenses);
        tvTotalProfit = view.findViewById(R.id.tvTotalProfit);

        rvIncomeProfit = view.findViewById(R.id.rvIncomeProfit);
        rvExpenseProfit = view.findViewById(R.id.rvExpenseProfit);

        setupDatePickers();
        setupRecyclerViews();
        btnFilter.setOnClickListener(v -> filterByDate());

        return view;
    }

    private void setupDatePickers() {
        etStartDate.setOnClickListener(v -> showDatePicker(etStartDate));
        etEndDate.setOnClickListener(v -> showDatePicker(etEndDate));
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

    private void setupRecyclerViews() {
        incomeAdapter = new IncomeAdapter(new ArrayList<>());
        rvIncomeProfit.setLayoutManager(new LinearLayoutManager(getContext()));
        rvIncomeProfit.setAdapter(incomeAdapter);

        expenseAdapter = new ExpenseAdapter(new ArrayList<>());
        rvExpenseProfit.setLayoutManager(new LinearLayoutManager(getContext()));
        rvExpenseProfit.setAdapter(expenseAdapter);
    }

    private void filterByDate() {
        String start = etStartDate.getText().toString();
        String end = etEndDate.getText().toString();

        if (start.isEmpty() || end.isEmpty()) {
            Toast.makeText(getContext(), "Please select both dates", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date startDate = sdf.parse(start);
            Date endDate = sdf.parse(end);

            if (startDate.after(endDate)) {
                Toast.makeText(getContext(), "Start date must be before end date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Filter incomes
            ArrayList<Income> filteredIncomes = new ArrayList<>();
            double totalIncome = 0;
            for (Income income : incomeList) {
                Date incomeDate = sdf.parse(income.getDate());
                if (!incomeDate.before(startDate) && !incomeDate.after(endDate)) {
                    filteredIncomes.add(income);
                    totalIncome += Double.parseDouble(income.getAmount());
                }
            }

            // Filter expenses
            ArrayList<Expense> filteredExpenses = new ArrayList<>();
            double totalExpenses = 0;
            for (Expense expense : expenseList) {
                Date expenseDate = sdf.parse(expense.getDate());
                if (!expenseDate.before(startDate) && !expenseDate.after(endDate)) {
                    filteredExpenses.add(expense);
                    totalExpenses += Double.parseDouble(expense.getAmount());
                }
            }

            double profit = totalIncome - totalExpenses;

            // Update totals
            tvTotalIncome.setText("Total Income: $" + String.format("%.2f", totalIncome));
            tvTotalExpenses.setText("Total Expenses: $" + String.format("%.2f", totalExpenses));
            tvTotalProfit.setText("Total Profit: $" + String.format("%.2f", profit));

            // Update RecyclerViews
            incomeAdapter = new IncomeAdapter(filteredIncomes);
            rvIncomeProfit.setAdapter(incomeAdapter);

            expenseAdapter = new ExpenseAdapter(filteredExpenses);
            rvExpenseProfit.setAdapter(expenseAdapter);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
        }
    }
}

