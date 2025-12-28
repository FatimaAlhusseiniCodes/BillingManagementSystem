package com.example.billingmanagementsystem;

public class Invoice {
    private String id;
    private String invoiceNumber;
    private String partnerName;
    private String invoiceDate;
    private String dueDate;
    private double totalAmount;
    private double dueAmount;
    private int quantity;
    private String status; // "Paid", "Unpaid", "Partially Paid"
    private boolean isOverdue;
    private int overdueDays;

    // Constructor
    public Invoice(String id, String invoiceNumber, String partnerName,
                   String invoiceDate, String dueDate, double totalAmount,
                   double dueAmount, int quantity, String status,
                   boolean isOverdue, int overdueDays) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.partnerName = partnerName;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.dueAmount = dueAmount;
        this.quantity = quantity;
        this.status = status;
        this.isOverdue = isOverdue;
        this.overdueDays = overdueDays;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(int overdueDays) {
        this.overdueDays = overdueDays;
    }
}