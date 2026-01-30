package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCustomerDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);

        if (getArguments() != null) {
            String name = getArguments().getString("customerName");
            binding.etDetailsName.setText(name);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.menu_record, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            savePaymentData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void savePaymentData() {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}