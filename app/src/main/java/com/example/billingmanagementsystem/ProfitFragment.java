package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfitFragment extends Fragment {

    public ProfitFragment() {
        super(R.layout.fragment_profit);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView incomeTv = view.findViewById(R.id.tvTotalIncome);
        TextView expenseTv = view.findViewById(R.id.tvTotalExpenses);
        TextView grossTv = view.findViewById(R.id.tvGrossProfit);
        TextView netTv = view.findViewById(R.id.tvNetProfit);

        double income = 5000;
        double expenses = 2000;
        double tax = 10;

        double gross = income - expenses;
        double net = ProfitCalculator.calculateNetProfit(income, expenses, tax);

        incomeTv.setText("Total Income: " + income);
        expenseTv.setText("Total Expenses: " + expenses);
        grossTv.setText("Gross Profit: " + gross);
        netTv.setText("Net Profit: " + net);
    }
}
