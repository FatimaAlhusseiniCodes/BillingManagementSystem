package com.example.billingmanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private List<Invoice> invoiceList;
    private OnInvoiceClickListener listener;
    private DecimalFormat decimalFormat;

    // Interface for click events
    public interface OnInvoiceClickListener {
        void onInvoiceClick(Invoice invoice);
    }

    // Constructor
    public InvoiceAdapter(OnInvoiceClickListener listener) {
        this.invoiceList = new ArrayList<>();
        this.listener = listener;
        this.decimalFormat = new DecimalFormat("#,##0.00");
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_invoice, parent, false);
        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceViewHolder holder, int position) {
        Invoice invoice = invoiceList.get(position);
        holder.bind(invoice);
    }

    @Override
    public int getItemCount() {
        return invoiceList.size();
    }

    // Update the list
    public void setInvoices(List<Invoice> invoices) {
        this.invoiceList = invoices;
        notifyDataSetChanged();
    }

    // ViewHolder class
    class InvoiceViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewPartnerName;
        private TextView textViewTotalAmount;
        private TextView textViewInvoiceDate;
        private TextView textViewInvoiceNumber;
        private TextView textViewDueAmount;
        private TextView textViewQuantity;
        private TextView textViewOverdueStatus;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewPartnerName = itemView.findViewById(R.id.textViewPartnerName);
            textViewTotalAmount = itemView.findViewById(R.id.textViewTotalAmount);
            textViewInvoiceDate = itemView.findViewById(R.id.textViewInvoiceDate);
            textViewInvoiceNumber = itemView.findViewById(R.id.textViewInvoiceNumber);
            textViewDueAmount = itemView.findViewById(R.id.textViewDueAmount);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewOverdueStatus = itemView.findViewById(R.id.textViewOverdueStatus);

            // Set click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onInvoiceClick(invoiceList.get(position));
                }
            });
        }

        public void bind(Invoice invoice) {
            // Partner Name
            textViewPartnerName.setText(invoice.getPartnerName());

            // Total Amount (formatted with currency)
            textViewTotalAmount.setText("ALL" + decimalFormat.format(invoice.getTotalAmount()));

            // Invoice Date
            textViewInvoiceDate.setText(invoice.getInvoiceDate());

            // Invoice Number
            textViewInvoiceNumber.setText(invoice.getInvoiceNumber());

            // Due Amount
            textViewDueAmount.setText("Due: ALL" + decimalFormat.format(invoice.getDueAmount()));

            // Quantity
            textViewQuantity.setText(String.valueOf(invoice.getQuantity()));

            // Overdue Status (show only if overdue)
            if (invoice.isOverdue()) {
                textViewOverdueStatus.setVisibility(View.VISIBLE);
                textViewOverdueStatus.setText("OVERDUE BY " + invoice.getOverdueDays() + " DAYS");
            } else {
                textViewOverdueStatus.setVisibility(View.GONE);
            }
        }
    }
}