package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {

    private List<payment> paymentList;

    public PaymentAdapter(List<payment> paymentList) {
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        payment payment = paymentList.get(position);
        holder.tvName.setText(payment.getCustomerName());
        holder.tvAmount.setText(payment.getAmount());
        holder.tvDate.setText(payment.getDate());
        holder.tvStatus.setText(payment.getStatus());
        holder.itemView.setOnClickListener(v -> {

            Bundle bundle = new Bundle();
            bundle.putString("custName", paymentList.get(position).getCustomerName());
            bundle.putString("custAmount", paymentList.get(position).getAmount());
            bundle.putString("custDate", paymentList.get(position).getDate());

            Navigation.findNavController(v).navigate(R.id.action_paymentReceiptFragment_to_paymentsReceivedFragment, bundle);
        });
        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                removeItem(currentPosition);
            }
        });
    }
    @Override
    public int getItemCount() {
        return paymentList.size();
    }
    //Refresh list for Search View results
    public void setFilteredList(List<payment> filteredList) {
        this.paymentList = filteredList;
        notifyDataSetChanged();
    }
    public static class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount, tvDate, tvStatus;
        ImageView btnDelete;
        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_customer_name);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
    public void setList(List<payment> newList) {
        this.paymentList = newList;
        notifyDataSetChanged();
    }
    public void removeItem(int position) {
        paymentList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, paymentList.size());
    }

}