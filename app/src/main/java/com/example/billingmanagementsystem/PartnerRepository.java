package com.example.billingmanagementsystem;

import java.util.ArrayList;
import java.util.List;

public class PartnerRepository {

    /**
     * Get all partners who have Sales invoices (Customers)
     */
    public List<InvoiceViewModel.Partner> getCustomers() {
        List<InvoiceViewModel.Partner> customers = new ArrayList<>();

        // TODO: Database query
        // SELECT DISTINCT p.id, p.name
        // FROM partners p
        // JOIN invoices i ON p.id = i.partner_id
        // WHERE i.type = 'Sales'

        return customers;
    }

    /**
     * Get all partners who have Purchase invoices (Suppliers)
     */
    public List<InvoiceViewModel.Partner> getSuppliers() {
        List<InvoiceViewModel.Partner> suppliers = new ArrayList<>();

        // TODO: Database query
        // SELECT DISTINCT p.id, p.name
        // FROM partners p
        // JOIN invoices i ON p.id = i.partner_id
        // WHERE i.type = 'Purchase'

        return suppliers;
    }
}
