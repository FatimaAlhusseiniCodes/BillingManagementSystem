package com.example.billingmanagementsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<Income> incomeList;

    public IncomeAdapter(List<Income> incomeList) {
        this.incomeList = incomeList;
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
        holder.tvType.setText(income.getType());
        holder.tvNameOrSource.setText(income.getNameOrSource());
        holder.tvPhone.setText(income.getPhone());
        holder.tvEmail.setText(income.getEmail());
        holder.tvProduct.setText(income.getProduct());
        holder.tvAmount.setText("$" + income.getAmount());
        holder.tvDate.setText(income.getDate());
        holder.tvNotes.setText(income.getNotes());
        holder.tvTimestamp.setText(income.getTimestamp());
    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvNameOrSource, tvPhone, tvEmail, tvProduct, tvAmount, tvDate, tvNotes, tvTimestamp;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            tvNameOrSource = itemView.findViewById(R.id.tvNameOrSource);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}
