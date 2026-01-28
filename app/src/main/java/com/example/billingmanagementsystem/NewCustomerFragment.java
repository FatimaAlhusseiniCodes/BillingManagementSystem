package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.billingmanagementsystem.databinding.FragmentNewCustomerBinding;


public class NewCustomerFragment extends Fragment {
    private FragmentNewCustomerBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNewCustomerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.toolbarNewCustomer.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(NewCustomerFragment.this).navigateUp();
            }
        });
        binding.toolbarNewCustomer.inflateMenu(R.menu.new_customer_menu);

        binding.toolbarNewCustomer.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_save) {
                Toast.makeText(getContext(), "Customer Saved!", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(this).navigateUp();
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