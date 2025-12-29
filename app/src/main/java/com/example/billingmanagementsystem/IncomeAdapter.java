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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Income RecyclerView
 * Displays both invoiced and manual income entries
 */
public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<Income> incomeList = new ArrayList<>();
    private OnIncomeClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
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
        this.incomeList = incomes;
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
            // Type Badge
            tvType.setText(income.getType().toUpperCase());
            if (income.isInvoiced()) {
                tvType.setBackgroundColor(0xFF4CAF50); // Green for invoiced
            } else {
                tvType.setBackgroundColor(0xFF2196F3); // Blue for manual
            }

            // Invoice Number (only for invoiced income)
            if (income.isInvoiced() && income.getInvoiceNumber() != null) {
                tvInvoiceNumber.setVisibility(View.VISIBLE);
                tvInvoiceNumber.setText(income.getInvoiceNumber());
            } else {
                tvInvoiceNumber.setVisibility(View.GONE);
            }

            // Source/Partner Name
            tvNameOrSource.setText(income.getSource());

            // Amount (green color for income)
            tvAmount.setText("ALL " + decimalFormat.format(income.getAmount()));
            tvAmount.setTextColor(0xFF4CAF50); // Green

            // Product/Service
            if (income.getProduct() != null && !income.getProduct().isEmpty()) {
                tvProduct.setText(income.getProduct());
                tvProduct.setVisibility(View.VISIBLE);
            } else {
                tvProduct.setVisibility(View.GONE);
            }

            // Date
            Date date = new Date(income.getDateMillis());
            tvDate.setText(dateFormat.format(date));

            // Contact Info (only for manual entries)
            if (income.isManual()) {
                boolean hasContactInfo = false;

                // Phone
                if (income.getPhone() != null && !income.getPhone().isEmpty()) {
                    layoutPhone.setVisibility(View.VISIBLE);
                    tvPhone.setText(income.getPhone());
                    hasContactInfo = true;
                } else {
                    layoutPhone.setVisibility(View.GONE);
                }

                // Email
                if (income.getEmail() != null && !income.getEmail().isEmpty()) {
                    layoutEmail.setVisibility(View.VISIBLE);
                    tvEmail.setText(income.getEmail());
                    hasContactInfo = true;
                } else {
                    layoutEmail.setVisibility(View.GONE);
                }

                layoutContactInfo.setVisibility(hasContactInfo ? View.VISIBLE : View.GONE);
            } else {
                layoutContactInfo.setVisibility(View.GONE);
            }

            // Notes (optional)
            if (income.getNotes() != null && !income.getNotes().isEmpty()) {
                tvNotes.setVisibility(View.VISIBLE);
                tvNotes.setText("Note: " + income.getNotes());
            } else {
                tvNotes.setVisibility(View.GONE);
            }

            // Timestamp
            if (income.getTimestamp() != null) {
                try {
                    long timestamp = Long.parseLong(income.getTimestamp());
                    Date timestampDate = new Date(timestamp);
                    tvTimestamp.setText("Added: " + timestampFormat.format(timestampDate));
                } catch (NumberFormatException e) {
                    tvTimestamp.setText("Added: " + income.getTimestamp());
                }
            } else {
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