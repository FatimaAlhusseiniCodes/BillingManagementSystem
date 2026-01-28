package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.billingmanagementsystem.databinding.FragmentPaymentReceiptBinding;


public class PaymentReceiptFragment extends Fragment {

    private FragmentPaymentReceiptBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPaymentReceiptBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            binding.tvCustomerNameReceipt.setText(getArguments().getString("custName"));
            binding.tvAmountValue.setText(getArguments().getString("custAmount"));
            binding.tvReceiptDate.setText("Payment Date: " + getArguments().getString("custDate"));
        }

        binding.toolbarReceipt.setNavigationOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}