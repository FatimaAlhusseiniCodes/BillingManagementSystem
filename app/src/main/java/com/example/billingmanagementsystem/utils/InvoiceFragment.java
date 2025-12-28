package com.example.billingmanagementsystem.utils;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoices")
public class Invoice {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String invoiceNumber;
    public long partnerId;
    public long issueDateMillis;
    public long dueDateMillis;

    public double subtotal;
    public double tax;
    public double total;

    public String status;   // DRAFT, SENT, PAID, OVERDUE
    public String type;     // e.g. "SALE" or "PURCHASE"

    public Invoice(String invoiceNumber,
                   long partnerId,
                   long issueDateMillis,
                   long dueDateMillis,
                   double subtotal,
                   double tax,
                   double total,
                   String status,
                   String type) {
        this.invoiceNumber = invoiceNumber;
        this.partnerId = partnerId;
        this.issueDateMillis = issueDateMillis;
        this.dueDateMillis = dueDateMillis;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.status = status;
        this.type = type;
    }
}
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "invoices")
public class Invoice {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String invoiceNumber;
    public long partnerId;
    public long issueDateMillis;
    public long dueDateMillis;

    public double subtotal;
    public double tax;
    public double total;

    public String status;   // DRAFT, SENT, PAID, OVERDUE
    public String type;     // e.g. "SALE" or "PURCHASE"

    public Invoice(String invoiceNumber,
                   long partnerId,
                   long issueDateMillis,
                   long dueDateMillis,
                   double subtotal,
                   double tax,
                   double total,
                   String status,
                   String type) {
        this.invoiceNumber = invoiceNumber;
        this.partnerId = partnerId;
        this.issueDateMillis = issueDateMillis;
        this.dueDateMillis = dueDateMillis;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.status = status;
        this.type = type;
    }
}

