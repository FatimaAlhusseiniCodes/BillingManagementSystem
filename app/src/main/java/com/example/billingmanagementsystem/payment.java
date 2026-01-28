package com.example.billingmanagementsystem;

public class payment {
    private String customerName;
    private String amount;
    private String date;
    private String status;

    public payment(String customerName, String amount, String date, String status) {
        this.customerName = customerName;
        this.amount = amount;
        this.date = date;
        this.status = status;
    }

    public String getCustomerName() { return customerName; }
    public String getAmount() { return amount; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
}