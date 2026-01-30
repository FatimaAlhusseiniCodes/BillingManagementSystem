package com.example.billingmanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Invoice RecyclerView
 * Handles displaying invoices and "Mark as Paid" button
 */
public class Invoiceadapter extends RecyclerView.Adapter<Invoiceadapter.InvoiceViewHolder> {

    private List<Invoice> invoiceList = new ArrayList<>();
    private OnInvoiceClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

    public Invoiceadapter(OnInvoiceClickListener listener) {
        this.listener = listener;
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

    public void setInvoices(List<Invoice> invoices) {
        this.invoiceList = invoices;
        notifyDataSetChanged();
    }

    // ==================== ViewHolder ====================

    class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView textViewPartnerName;
        TextView textViewTotalAmount;
        TextView textViewInvoiceDate;
        TextView textViewInvoiceNumber;
        TextView textViewDueAmount;
        TextView textViewQuantity;
        TextView textViewOverdueStatus;
        TextView textViewStatus;
        TextView textViewType;
        Button buttonMarkPaid;

        public InvoiceViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewPartnerName = itemView.findViewById(R.id.textViewPartnerName);
            textViewTotalAmount = itemView.findViewById(R.id.textViewTotalAmount);
            textViewInvoiceDate = itemView.findViewById(R.id.textViewInvoiceDate);
            textViewInvoiceNumber = itemView.findViewById(R.id.textViewInvoiceNumber);
            textViewDueAmount = itemView.findViewById(R.id.textViewDueAmount);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            textViewOverdueStatus = itemView.findViewById(R.id.textViewOverdueStatus);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewType = itemView.findViewById(R.id.textViewType);
            buttonMarkPaid = itemView.findViewById(R.id.buttonMarkPaid);
        }

        public void bind(Invoice invoice) {
            // Partner Name
            textViewPartnerName.setText(invoice.getPartnerName());

            // Total Amount
            textViewTotalAmount.setText("ALL " + decimalFormat.format(invoice.getTotal()));

            // Invoice Date
            Date invoiceDate = new Date(invoice.getIssueDateMillis());
            textViewInvoiceDate.setText(dateFormat.format(invoiceDate));

            // Invoice Number
            textViewInvoiceNumber.setText(invoice.getInvoiceNumber());

            // Due Amount
            textViewDueAmount.setText("Due: ALL " + decimalFormat.format(invoice.getAmountDue()));

            // Quantity
            textViewQuantity.setText(String.valueOf((int) invoice.getQuantity()));

            // Status Badge
            textViewStatus.setText(invoice.getStatus());
            setStatusColor(textViewStatus, invoice.getStatus());

            // Type Badge
            textViewType.setText(invoice.getType());
            setTypeColor(textViewType, invoice.getType());

            // Overdue Status
            if (invoice.isOverdue()) {
                textViewOverdueStatus.setVisibility(View.VISIBLE);
                textViewOverdueStatus.setText("OVERDUE BY " + invoice.getOverdueDays() + " DAYS");
                textViewOverdueStatus.setTextColor(0xFFD32F2F); // Red
            } else {
                textViewOverdueStatus.setVisibility(View.GONE);
            }

            // Mark as Paid Button (only show for unpaid invoices)
            if (invoice.isUnpaid()) {
                buttonMarkPaid.setVisibility(View.VISIBLE);
                buttonMarkPaid.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onMarkAsPaidClick(invoice);
                    }
                });
            } else {
                buttonMarkPaid.setVisibility(View.GONE);
            }

            // Item Click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInvoiceClick(invoice);
                }
            });
        }

        private void setStatusColor(TextView textView, String status) {
            int backgroundColor;
            int textColor = 0xFFFFFFFF; // White

            switch (status) {
                case "Paid":
                    backgroundColor = 0xFF4CAF50; // Green
                    break;
                case "Unpaid":
                    backgroundColor = 0xFFF44336; // Red
                    break;
                case "Partially Paid":
                    backgroundColor = 0xFFFF9800; // Orange
                    break;
                default:
                    backgroundColor = 0xFF757575; // Gray
                    break;
            }

            textView.setBackgroundColor(backgroundColor);
            textView.setTextColor(textColor);
        }

        private void setTypeColor(TextView textView, String type) {
            int backgroundColor;
            int textColor = 0xFFFFFFFF; // White

            if ("Sales".equals(type)) {
                backgroundColor = 0xFF2196F3; // Blue
            } else {
                backgroundColor = 0xFF9C27B0; // Purple
            }

            textView.setBackgroundColor(backgroundColor);
            textView.setTextColor(textColor);
        }
    }

    // ==================== Interface for Click Listeners ====================

    public interface OnInvoiceClickListener {
        void onInvoiceClick(Invoice invoice);
        void onMarkAsPaidClick(Invoice invoice);
    }
}