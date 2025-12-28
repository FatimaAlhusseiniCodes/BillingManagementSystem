package com.example.billingmanagementsystem;

public class Income {
    private String type; // "Customer" or "Other"
    private String nameOrSource;
    private String phone;
    private String email;
    private String product;
    private String amount;
    private String date;
    private String notes;
    private String timestamp;

    public Income(String type, String nameOrSource, String phone, String email, String product,
                  String amount, String date, String notes, String timestamp) {
        this.type = type;
        this.nameOrSource = nameOrSource;
        this.phone = phone;
        this.email = email;
        this.product = product;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public String getNameOrSource() { return nameOrSource; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getProduct() { return product; }
    public String getAmount() { return amount; }
    public String getDate() { return date; }
    public String getNotes() { return notes; }
    public String getTimestamp() { return timestamp; }
}

