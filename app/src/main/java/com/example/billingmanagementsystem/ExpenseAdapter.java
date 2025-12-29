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

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenseList = new ArrayList<>();
    private OnExpenseClickListener listener;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

    public ExpenseAdapter(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.bind(expense);
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenseList = expenses;
        notifyDataSetChanged();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {

        TextView tvType, tvInvoiceNumber, tvTitle, tvAmount;
        TextView tvCategory, tvDate, tvNotes, tvTimestamp;
        Button buttonMarkPaid;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);

            tvType = itemView.findViewById(R.id.tvType);
            tvInvoiceNumber = itemView.findViewById(R.id.tvInvoiceNumber);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            buttonMarkPaid = itemView.findViewById(R.id.buttonMarkPaid);
        }

        public void bind(Expense expense) {
            // Type Badge
            tvType.setText(expense.getType().toUpperCase());

            if (expense.isInvoiced()) {
                tvType.setBackgroundColor(0xFFF44336); // Red (paid expense)
            } else if (expense.isUnpaid()) {
                tvType.setBackgroundColor(0xFFFF9800); // Orange (unpaid)
            } else {
                tvType.setBackgroundColor(0xFF2196F3); // Blue (manual)
            }

            // Invoice Number (for invoiced expenses)
            if (expense.getInvoiceNumber() != null) {
                tvInvoiceNumber.setVisibility(View.VISIBLE);
                tvInvoiceNumber.setText(expense.getInvoiceNumber());
            } else {
                tvInvoiceNumber.setVisibility(View.GONE);
            }

            // Title or Supplier Name
            if (expense.getSupplierName() != null) {
                tvTitle.setText(expense.getSupplierName());
            } else {
                tvTitle.setText(expense.getTitle());
            }

            // Amount (red for expenses)
            tvAmount.setText("ALL " + decimalFormat.format(expense.getAmount()));
            tvAmount.setTextColor(0xFFF44336); // Red

            // Category
            if (expense.getCategory() != null) {
                tvCategory.setText(expense.getCategory());
            }

            // Date
            Date date = new Date(expense.getDateMillis());
            tvDate.setText(dateFormat.format(date));

            // Notes
            if (expense.getNotes() != null && !expense.getNotes().isEmpty()) {
                tvNotes.setVisibility(View.VISIBLE);
                tvNotes.setText("Note: " + expense.getNotes());
            } else {
                tvNotes.setVisibility(View.GONE);
            }

            // Timestamp
            if (expense.getTimestamp() != null) {
                try {
                    long timestamp = Long.parseLong(expense.getTimestamp());
                    Date timestampDate = new Date(timestamp);
                    tvTimestamp.setText("Added: " + timestampFormat.format(timestampDate));
                } catch (NumberFormatException e) {
                    tvTimestamp.setText("Added: " + expense.getTimestamp());
                }
            }

            // Mark as Paid button (only for unpaid)
            if (expense.isUnpaid()) {
                buttonMarkPaid.setVisibility(View.VISIBLE);
                buttonMarkPaid.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onMarkAsPaidClick(expense);
                    }
                });
            } else {
                buttonMarkPaid.setVisibility(View.GONE);
            }

            // Click
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExpenseClick(expense);
                }
            });
        }
    }

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
        void onMarkAsPaidClick(Expense expense);
    }
}