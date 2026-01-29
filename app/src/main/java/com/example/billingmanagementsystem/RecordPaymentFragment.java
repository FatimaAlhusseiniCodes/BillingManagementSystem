package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import com.example.billingmanagementsystem.databinding.FragmentRecordPaymentBinding;

public class RecordPaymentFragment extends Fragment {

    private FragmentRecordPaymentBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentRecordPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        binding.btnAdd.setOnClickListener(v -> {
            NavHostFragment.findNavController(RecordPaymentFragment.this)
                    .navigate(R.id.action_recordPaymentFragment_to_newCustomerFragment);
        });

        binding.etCustomer.setOnEditorActionListener((v, actionId, event) -> {
            String name = binding.etCustomer.getText().toString();

            if (!name.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("customerName", name);

                Navigation.findNavController(view).navigate(R.id.action_recordPayment_to_customerDetails, bundle);
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            leaveDialog dialog = new leaveDialog();
            dialog.show(getParentFragmentManager(), "default");

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}