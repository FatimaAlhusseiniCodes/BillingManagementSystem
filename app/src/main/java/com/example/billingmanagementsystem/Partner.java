package com.example.billingmanagementsystem;

/**
 * Partner Model Class
 * Represents a customer or supplier in the billing system
 */
public class Partner {

    // Basic information
    private String id;
    private String partnerNumber;  // Auto-incremented (e.g., "PART-0001")
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String country;

    // Partner type
    private String type;  // "Customer", "Supplier", or "Both"

    // Additional business info
    private String companyName;
    private String taxNumber;
    private String notes;

    // Tracking
    private long dateCreatedMillis;
    private long dateModifiedMillis;

    // Statistics (calculated from invoices)
    private int totalInvoices;
    private double totalAmountOwed;  // For suppliers (we owe them)
    private double totalAmountDue;   // For customers (they owe us)

    /**
     * Full constructor
     */
    public Partner(String id, String partnerNumber, String firstName, String lastName,
                   String email, String phone, String address, String city, String country,
                   String type, String companyName, String taxNumber, String notes,
                   long dateCreatedMillis, long dateModifiedMillis,
                   int totalInvoices, double totalAmountOwed, double totalAmountDue) {
        this.id = id;
        this.partnerNumber = partnerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.country = country;
        this.type = type;
        this.companyName = companyName;
        this.taxNumber = taxNumber;
        this.notes = notes;
        this.dateCreatedMillis = dateCreatedMillis;
        this.dateModifiedMillis = dateModifiedMillis;
        this.totalInvoices = totalInvoices;
        this.totalAmountOwed = totalAmountOwed;
        this.totalAmountDue = totalAmountDue;
    }

    /**
     * Simplified constructor for new partners
     */
    public Partner(String partnerNumber, String firstName, String lastName, String type) {
        this.partnerNumber = partnerNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.dateCreatedMillis = System.currentTimeMillis();
        this.dateModifiedMillis = System.currentTimeMillis();
        this.totalInvoices = 0;
        this.totalAmountOwed = 0.0;
        this.totalAmountDue = 0.0;
    }

    // ==================== GETTERS ====================

    public String getId() { return id; }
    public String getPartnerNumber() { return partnerNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public String getType() { return type; }
    public String getCompanyName() { return companyName; }
    public String getTaxNumber() { return taxNumber; }
    public String getNotes() { return notes; }
    public long getDateCreatedMillis() { return dateCreatedMillis; }
    public long getDateModifiedMillis() { return dateModifiedMillis; }
    public int getTotalInvoices() { return totalInvoices; }
    public double getTotalAmountOwed() { return totalAmountOwed; }
    public double getTotalAmountDue() { return totalAmountDue; }

    // ==================== SETTERS ====================

    public void setId(String id) { this.id = id; }
    public void setPartnerNumber(String partnerNumber) { this.partnerNumber = partnerNumber; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setCountry(String country) { this.country = country; }
    public void setType(String type) { this.type = type; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDateCreatedMillis(long dateCreatedMillis) { this.dateCreatedMillis = dateCreatedMillis; }
    public void setDateModifiedMillis(long dateModifiedMillis) { this.dateModifiedMillis = dateModifiedMillis; }
    public void setTotalInvoices(int totalInvoices) { this.totalInvoices = totalInvoices; }
    public void setTotalAmountOwed(double totalAmountOwed) { this.totalAmountOwed = totalAmountOwed; }
    public void setTotalAmountDue(double totalAmountDue) { this.totalAmountDue = totalAmountDue; }

    // ==================== HELPER METHODS ====================

    /**
     * Get full name
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        }
        return "Unknown";
    }

    /**
     * Get display name (company name if available, otherwise full name)
     */
    public String getDisplayName() {
        if (companyName != null && !companyName.trim().isEmpty()) {
            return companyName;
        }
        return getFullName();
    }

    /**
     * Check if partner is a customer
     */
    public boolean isCustomer() {
        return "Customer".equalsIgnoreCase(type) || "Both".equalsIgnoreCase(type);
    }

    /**
     * Check if partner is a supplier
     */
    public boolean isSupplier() {
        return "Supplier".equalsIgnoreCase(type) || "Both".equalsIgnoreCase(type);
    }

    /**
     * Check if partner is both customer and supplier
     */
    public boolean isBoth() {
        return "Both".equalsIgnoreCase(type);
    }

    /**
     * Get type badge color
     */
    public int getTypeColor() {
        if (isCustomer() && isSupplier()) {
            return 0xFFFF9800; // Orange for Both
        } else if (isCustomer()) {
            return 0xFF4CAF50; // Green for Customer
        } else {
            return 0xFF2196F3; // Blue for Supplier
        }
    }
}