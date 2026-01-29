package com.example.billingmanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Income RecyclerView
 * FIXED VERSION - Compatible with legacy Income class
 */
public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<Income> incomeList = new ArrayList<>();
    private OnIncomeClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

    public IncomeAdapter(OnIncomeClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        Income income = incomeList.get(position);
        holder.bind(income);
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public void setIncomes(List<Income> incomes) {
        this.incomeList = incomes != null ? incomes : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ==================== ViewHolder ====================

    class IncomeViewHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        TextView tvInvoiceNumber;
        TextView tvNameOrSource;
        TextView tvAmount;
        TextView tvProduct;
        TextView tvDate;
        TextView tvPhone;
        TextView tvEmail;
        TextView tvNotes;
        TextView tvTimestamp;
        LinearLayout layoutContactInfo;
        LinearLayout layoutPhone;
        LinearLayout layoutEmail;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tvType);
            tvInvoiceNumber = itemView.findViewById(R.id.tvInvoiceNumber);
            tvNameOrSource = itemView.findViewById(R.id.tvNameOrSource);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            layoutContactInfo = itemView.findViewById(R.id.layoutContactInfo);
            layoutPhone = itemView.findViewById(R.id.layoutPhone);
            layoutEmail = itemView.findViewById(R.id.layoutEmail);
        }

        public void bind(Income income) {
            if (income == null) return;

            // Type Badge
            String type = income.getType() != null ? income.getType() : "Manual";
            tvType.setText(type.toUpperCase());

            // Color based on type
            if ("Invoiced".equalsIgnoreCase(type)) {
                tvType.setBackgroundColor(0xFF4CAF50); // Green for invoiced
            } else {
                tvType.setBackgroundColor(0xFF2196F3); // Blue for manual
            }

            // Invoice Number (only for invoiced income)
            // Legacy Income doesn't have invoiceNumber, so hide it
            if (tvInvoiceNumber != null) {
                tvInvoiceNumber.setVisibility(View.GONE);
            }

            // Source/Customer Name
            String source = income.getNameOrSource();
            if (tvNameOrSource != null && source != null) {
                tvNameOrSource.setText(source);
            }

            // Amount (green color for income)
            String amountStr = income.getAmount();
            if (tvAmount != null && amountStr != null) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    tvAmount.setText("ALL " + decimalFormat.format(amount));
                } catch (NumberFormatException e) {
                    tvAmount.setText("ALL " + amountStr);
                }
                tvAmount.setTextColor(0xFF4CAF50); // Green
            }

            // Product/Service
            String product = income.getProduct();
            if (tvProduct != null) {
                if (product != null && !product.isEmpty()) {
                    tvProduct.setText(product);
                    tvProduct.setVisibility(View.VISIBLE);
                } else {
                    tvProduct.setVisibility(View.GONE);
                }
            }

            // Date
            String date = income.getDate();
            if (tvDate != null && date != null) {
                tvDate.setText(date);
            }

            // Contact Info (for manual entries)
            boolean hasContactInfo = false;
            String phone = income.getPhone();
            String email = income.getEmail();

            // Phone
            if (layoutPhone != null) {
                if (phone != null && !phone.isEmpty()) {
                    layoutPhone.setVisibility(View.VISIBLE);
                    if (tvPhone != null) {
                        tvPhone.setText(phone);
                    }
                    hasContactInfo = true;
                } else {
                    layoutPhone.setVisibility(View.GONE);
                }
            }

            // Email
            if (layoutEmail != null) {
                if (email != null && !email.isEmpty()) {
                    layoutEmail.setVisibility(View.VISIBLE);
                    if (tvEmail != null) {
                        tvEmail.setText(email);
                    }
                    hasContactInfo = true;
                } else {
                    layoutEmail.setVisibility(View.GONE);
                }
            }

            // Show/hide contact info container
            if (layoutContactInfo != null) {
                layoutContactInfo.setVisibility(hasContactInfo ? View.VISIBLE : View.GONE);
            }

            // Notes (optional)
            String notes = income.getNotes();
            if (tvNotes != null) {
                if (notes != null && !notes.isEmpty()) {
                    tvNotes.setVisibility(View.VISIBLE);
                    tvNotes.setText("Note: " + notes);
                } else {
                    tvNotes.setVisibility(View.GONE);
                }
            }

            // Timestamp
            String timestamp = income.getTimestamp();
            if (tvTimestamp != null && timestamp != null) {
                try {
                    long timestampLong = Long.parseLong(timestamp);
                    java.util.Date timestampDate = new java.util.Date(timestampLong);
                    tvTimestamp.setText("Added: " + timestampFormat.format(timestampDate));
                    tvTimestamp.setVisibility(View.VISIBLE);
                } catch (NumberFormatException e) {
                    tvTimestamp.setText("Added: " + timestamp);
                    tvTimestamp.setVisibility(View.VISIBLE);
                }
            } else if (tvTimestamp != null) {
                tvTimestamp.setVisibility(View.GONE);
            }

            // Item Click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIncomeClick(income);
                }
            });
        }
    }

    // ==================== Interface for Click Listeners ====================

    public interface OnIncomeClickListener {
        void onIncomeClick(Income income);
    }
}