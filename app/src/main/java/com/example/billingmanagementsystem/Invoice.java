package com.example.billingmanagementsystem;

/**
 * Invoice Model Class
 * Represents an invoice in the billing system
 */
public class Invoice {

    // Basic invoice information
    private String id;
    private String invoiceNumber;
    private String partnerName;
    private long partnerId;

    // Dates (stored as milliseconds)
    private long issueDateMillis;
    private long dueDateMillis;

    // Financial details
    private double subtotal;
    private double tax;
    private double total;
    private double amountPaid;
    private double amountDue;

    // Invoice metadata
    private String status;  // "Paid", "Unpaid", "Partially Paid", "Overdue"
    private String type;    // "Sales" or "Purchase"

    // Line items info
    private double quantity;
    private double rate;

    // Overdue tracking
    private boolean isOverdue;
    private int overdueDays;

    // Notes
    private String notes;

    /**
     * Full constructor
     */
    public Invoice(String id, String invoiceNumber, String partnerName, long partnerId,
                   long issueDateMillis, long dueDateMillis,
                   double subtotal, double tax, double total,
                   double amountPaid, double amountDue,
                   String status, String type,
                   double quantity, double rate,
                   boolean isOverdue, int overdueDays,
                   String notes) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.partnerName = partnerName;
        this.partnerId = partnerId;
        this.issueDateMillis = issueDateMillis;
        this.dueDateMillis = dueDateMillis;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.amountPaid = amountPaid;
        this.amountDue = amountDue;
        this.status = status;
        this.type = type;
        this.quantity = quantity;
        this.rate = rate;
        this.isOverdue = isOverdue;
        this.overdueDays = overdueDays;
        this.notes = notes;
    }

    /**
     * Simplified constructor for new invoices
     */
    public Invoice(String invoiceNumber, String partnerName, String type) {
        this.invoiceNumber = invoiceNumber;
        this.partnerName = partnerName;
        this.type = type;
        this.status = "Unpaid";
        this.amountPaid = 0.0;
        this.isOverdue = false;
        this.overdueDays = 0;
    }

    // ==================== GETTERS ====================

    public String getId() { return id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public String getPartnerName() { return partnerName; }
    public long getPartnerId() { return partnerId; }
    public long getIssueDateMillis() { return issueDateMillis; }
    public long getDueDateMillis() { return dueDateMillis; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public double getAmountPaid() { return amountPaid; }
    public double getAmountDue() { return amountDue; }
    public String getStatus() { return status; }
    public String getType() { return type; }
    public double getQuantity() { return quantity; }
    public double getRate() { return rate; }
    public boolean isOverdue() { return isOverdue; }
    public int getOverdueDays() { return overdueDays; }
    public String getNotes() { return notes; }

    // ==================== SETTERS ====================

    public void setId(String id) { this.id = id; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public void setPartnerName(String partnerName) { this.partnerName = partnerName; }
    public void setPartnerId(long partnerId) { this.partnerId = partnerId; }
    public void setIssueDateMillis(long issueDateMillis) { this.issueDateMillis = issueDateMillis; }
    public void setDueDateMillis(long dueDateMillis) { this.dueDateMillis = dueDateMillis; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public void setTax(double tax) { this.tax = tax; }
    public void setTotal(double total) { this.total = total; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
    public void setAmountDue(double amountDue) { this.amountDue = amountDue; }
    public void setStatus(String status) { this.status = status; }
    public void setType(String type) { this.type = type; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setRate(double rate) { this.rate = rate; }
    public void setOverdue(boolean overdue) { isOverdue = overdue; }
    public void setOverdueDays(int overdueDays) { this.overdueDays = overdueDays; }
    public void setNotes(String notes) { this.notes = notes; }

    // ==================== HELPER METHODS ====================

    /**
     * Check if this is a sales invoice
     */
    public boolean isSalesInvoice() {
        return "Sales".equalsIgnoreCase(type);
    }

    /**
     * Check if this is a purchase invoice
     */
    public boolean isPurchaseInvoice() {
        return "Purchase".equalsIgnoreCase(type);
    }

    /**
     * Check if invoice is paid
     */
    public boolean isPaid() {
        return "Paid".equalsIgnoreCase(status);
    }

    /**
     * Check if invoice is unpaid
     */
    public boolean isUnpaid() {
        return "Unpaid".equalsIgnoreCase(status) || "Partially Paid".equalsIgnoreCase(status);
    }

    /**
     * Calculate total amount
     */
    public void calculateTotal() {
        this.subtotal = this.quantity * this.rate;
        this.total = this.subtotal + this.tax;
        this.amountDue = this.total - this.amountPaid;
    }

    /**
     * Mark invoice as paid
     */
    public void markAsPaid() {
        this.status = "Paid";
        this.amountPaid = this.total;
        this.amountDue = 0.0;
    }
}