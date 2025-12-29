package com.example.billingmanagementsystem;

/**
 * Expense Model Class
 * Represents expense transactions from purchase invoices and manual entries
 */
public class Expense {

    private String id;
    private String type; // "Invoiced", "Manual", or "Unpaid"
    private String title; // Description or invoice number
    private String supplierName; // Supplier for invoiced expenses
    private String invoiceNumber; // If from purchase invoice
    private double amount;
    private long dateMillis;
    private String category;
    private String notes;
    private String timestamp;
    private boolean isPaid; // Payment status for invoiced expenses

    /**
     * Constructor for Invoiced Expense (from Purchase Invoice - Paid)
     */
    public Expense(String id, String invoiceNumber, String supplierName,
                   double amount, long dateMillis, String category, boolean isPaid) {
        this.id = id;
        this.type = isPaid ? "Invoiced" : "Unpaid";
        this.invoiceNumber = invoiceNumber;
        this.supplierName = supplierName;
        this.title = "Invoice: " + invoiceNumber;
        this.amount = amount;
        this.dateMillis = dateMillis;
        this.category = category;
        this.isPaid = isPaid;
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    /**
     * Constructor for Manual Expense
     */
    public Expense(String id, String title, double amount, long dateMillis,
                   String category, String notes) {
        this.id = id;
        this.type = "Manual";
        this.title = title;
        this.amount = amount;
        this.dateMillis = dateMillis;
        this.category = category;
        this.notes = notes;
        this.isPaid = true; // Manual expenses are considered paid
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }

    /**
     * Legacy constructor for backward compatibility
     */
    public Expense(String title, String amount, String category,
                   String date, String notes, String timestamp) {
        this.title = title;
        try {
            this.amount = Double.parseDouble(amount);
        } catch (NumberFormatException e) {
            this.amount = 0.0;
        }
        this.category = category;
        this.notes = notes;
        this.timestamp = timestamp;
        this.type = "Manual";
        this.isPaid = true;
        this.dateMillis = System.currentTimeMillis();
    }

    // ==================== GETTERS ====================

    public String getId() { return id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getSupplierName() { return supplierName; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public double getAmount() { return amount; }
    public long getDateMillis() { return dateMillis; }
    public String getCategory() { return category; }
    public String getNotes() { return notes; }
    public String getTimestamp() { return timestamp; }
    public boolean isPaid() { return isPaid; }

    // ==================== SETTERS ====================

    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setTitle(String title) { this.title = title; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setDateMillis(long dateMillis) { this.dateMillis = dateMillis; }
    public void setCategory(String category) { this.category = category; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setPaid(boolean paid) {
        isPaid = paid;
        this.type = paid ? "Invoiced" : "Unpaid";
    }

    // ==================== HELPER METHODS ====================

    public boolean isInvoiced() {
        return "Invoiced".equals(type);
    }

    public boolean isManual() {
        return "Manual".equals(type);
    }

    public boolean isUnpaid() {
        return "Unpaid".equals(type);
    }

    /**
     * Mark expense as paid (for unpaid purchase invoices)
     */
    public void markAsPaid() {
        this.isPaid = true;
        this.type = "Invoiced";
    }
}