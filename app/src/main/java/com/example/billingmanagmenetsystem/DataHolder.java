package com.example.billingmanagmenetsystem;

import java.util.ArrayList;

public class DataHolder {
    private static DataHolder instance;

    private ArrayList<Income> incomeList;
    private ArrayList<Expense> expenseList;

    private DataHolder() {
        incomeList = new ArrayList<>();
        expenseList = new ArrayList<>();
    }

    public static DataHolder getInstance() {
        if (instance == null) {
            instance = new DataHolder();
        }
        return instance;
    }

    // Income methods
    public void addIncome(Income income) {
        incomeList.add(income);
    }

    public ArrayList<Income> getIncomeList() {
        return incomeList;
    }

    // Expense methods
    public void addExpense(Expense expense) {
        expenseList.add(expense);
    }

    public ArrayList<Expense> getExpenseList() {
        return expenseList;
    }

    // Optional: clear all data
    public void clearAllData() {
        incomeList.clear();
        expenseList.clear();
    }
}

