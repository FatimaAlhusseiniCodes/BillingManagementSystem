package com.example.billingmanagementsystem;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for Partner RecyclerView
 * Handles displaying partners (customers and suppliers)
 */
public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.PartnerViewHolder> {

    private List<Partner> partnerList = new ArrayList<>();
    private OnPartnerClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    public PartnerAdapter(OnPartnerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public PartnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_partner, parent, false);
        return new PartnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartnerViewHolder holder, int position) {
        Partner partner = partnerList.get(position);
        holder.bind(partner);
    }

    @Override
    public int getItemCount() {
        return partnerList.size();
    }

    public void setPartners(List<Partner> partners) {
        this.partnerList = partners;
        notifyDataSetChanged();
    }

    // ==================== ViewHolder ====================

    class PartnerViewHolder extends RecyclerView.ViewHolder {

        TextView textViewInitial;
        TextView textViewPartnerName;
        TextView textViewPartnerNumber;
        TextView textViewType;
        TextView textViewEmail;
        TextView textViewPhone;
        TextView textViewCompanyName;
        TextView textViewInvoiceCount;
        TextView textViewTotalAmount;
        TextView textViewAmountLabel;
        LinearLayout layoutCompany;
        LinearLayout layoutEmail;
        LinearLayout layoutPhone;

        public PartnerViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewInitial = itemView.findViewById(R.id.textViewInitial);
            textViewPartnerName = itemView.findViewById(R.id.textViewPartnerName);
            textViewPartnerNumber = itemView.findViewById(R.id.textViewPartnerNumber);
            textViewType = itemView.findViewById(R.id.textViewType);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewCompanyName = itemView.findViewById(R.id.textViewCompanyName);
            textViewInvoiceCount = itemView.findViewById(R.id.textViewInvoiceCount);
            textViewTotalAmount = itemView.findViewById(R.id.textViewTotalAmount);
            textViewAmountLabel = itemView.findViewById(R.id.textViewAmountLabel);
            layoutCompany = itemView.findViewById(R.id.layoutCompany);
            layoutEmail = itemView.findViewById(R.id.layoutEmail);
            layoutPhone = itemView.findViewById(R.id.layoutPhone);
        }

        public void bind(Partner partner) {
            // Initial (first letter of name)
            String displayName = partner.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                textViewInitial.setText(displayName.substring(0, 1).toUpperCase());
            } else {
                textViewInitial.setText("?");
            }

            // Partner Name
            textViewPartnerName.setText(displayName);

            // Partner Number
            textViewPartnerNumber.setText(partner.getPartnerNumber());

            // Type Badge
            textViewType.setText(partner.getType());
            setTypeColor(textViewType, partner.getType());

            // Email
            if (partner.getEmail() != null && !partner.getEmail().isEmpty()) {
                textViewEmail.setText(partner.getEmail());
                layoutEmail.setVisibility(View.VISIBLE);
            } else {
                layoutEmail.setVisibility(View.GONE);
            }

            // Phone
            if (partner.getPhone() != null && !partner.getPhone().isEmpty()) {
                textViewPhone.setText(partner.getPhone());
                layoutPhone.setVisibility(View.VISIBLE);
            } else {
                layoutPhone.setVisibility(View.GONE);
            }

            // Company Name
            if (partner.getCompanyName() != null && !partner.getCompanyName().isEmpty()) {
                textViewCompanyName.setText(partner.getCompanyName());
                layoutCompany.setVisibility(View.VISIBLE);
            } else {
                layoutCompany.setVisibility(View.GONE);
            }

            // Invoice Count
            textViewInvoiceCount.setText(String.valueOf(partner.getTotalInvoices()));

            // Total Amount
            double totalAmount;
            String amountLabel;

            if (partner.isCustomer() && partner.isSupplier()) {
                // Both - show net amount
                totalAmount = partner.getTotalAmountDue() - partner.getTotalAmountOwed();
                amountLabel = totalAmount >= 0 ? "We Receive" : "We Pay";
                totalAmount = Math.abs(totalAmount);
            } else if (partner.isCustomer()) {
                // Customer - they owe us
                totalAmount = partner.getTotalAmountDue();
                amountLabel = "Due to Us";
            } else {
                // Supplier - we owe them
                totalAmount = partner.getTotalAmountOwed();
                amountLabel = "We Owe";
            }

            textViewTotalAmount.setText("ALL " + decimalFormat.format(totalAmount));
            textViewAmountLabel.setText(amountLabel);

            // Color based on amount type
            if (partner.isCustomer() && !partner.isSupplier()) {
                textViewTotalAmount.setTextColor(0xFF4CAF50); // Green for money coming in
            } else if (partner.isSupplier() && !partner.isCustomer()) {
                textViewTotalAmount.setTextColor(0xFFF44336); // Red for money going out
            } else {
                textViewTotalAmount.setTextColor(0xFFFF9800); // Orange for both
            }

            // Item Click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPartnerClick(partner);
                }
            });
        }

        private void setTypeColor(TextView textView, String type) {
            int backgroundColor;
            int textColor = 0xFFFFFFFF; // White

            if ("Customer".equalsIgnoreCase(type)) {
                backgroundColor = 0xFF4CAF50; // Green
            } else if ("Supplier".equalsIgnoreCase(type)) {
                backgroundColor = 0xFF2196F3; // Blue
            } else {
                backgroundColor = 0xFFFF9800; // Orange for Both
            }

            textView.setBackgroundColor(backgroundColor);
            textView.setTextColor(textColor);
        }
    }

    // ==================== Interface for Click Listeners ====================

    public interface OnPartnerClickListener {
        void onPartnerClick(Partner partner);
    }
}