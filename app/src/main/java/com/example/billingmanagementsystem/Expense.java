package com.example.billingmanagementsystem;

public class Expense {
    private String title;
    private String amount;
    private String category;
    private String date;
    private String notes;
    private String timestamp;

    public Expense(String title, String amount, String category, String date, String notes, String timestamp) {
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public String getTitle() { return title; }
    public String getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getNotes() { return notes; }
    public String getTimestamp() { return timestamp; }
}

