package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.billingmanagementsystem.databinding.FragmentCustomerDetailsBinding;

public class CustomerDetailsFragment extends Fragment {

    private FragmentCustomerDetailsBinding binding;
    private PaymentViewModel paymentViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCustomerDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbarDetails.setNavigationOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);

        binding.toolbarDetails.inflateMenu(R.menu.menu_record);
        binding.toolbarDetails.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save) {
                String name = binding.etDetailsName.getText().toString();
                String amount = binding.etDetailsAmount.getText().toString();
                String date = binding.etDetailsDate.getText().toString();

                if (TextUtils.isEmpty(amount)) {
                    binding.etDetailsAmount.setError("Please enter valid amount");
                } else {
                    payment p = new payment(name, "LBP " + amount, date, "Success");
                    paymentViewModel.addPayment(p);

                    NavHostFragment.findNavController(this).navigateUp();
                }
                return true;
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}