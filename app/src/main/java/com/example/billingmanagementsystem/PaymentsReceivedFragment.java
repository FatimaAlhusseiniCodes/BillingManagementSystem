package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.billingmanagementsystem.databinding.FragmentPaymentsReceivedBinding;

import java.util.ArrayList;
import java.util.List;

public class PaymentsReceivedFragment extends Fragment {
    private PaymentAdapter adapter;
    private PaymentViewModel paymentViewModel;
    private FragmentPaymentsReceivedBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPaymentsReceivedBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        paymentViewModel = new ViewModelProvider(requireActivity()).get(PaymentViewModel.class);

        adapter = new PaymentAdapter(new ArrayList<>());
        binding.recyclerViewPayments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPayments.setAdapter(adapter);
        paymentViewModel.getPayments().observe(getViewLifecycleOwner(), newList -> {
            adapter.setList(newList);

            binding.fabAddPayment.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_paymentsReceivedFragment_to_recordPaymentFragment);
            });

            binding.toolbarReceived.inflateMenu(R.menu.menu_payments_received);

            MenuItem searchItem = binding.toolbarReceived.getMenu().findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    List<payment> filteredList = new ArrayList<>();

                    List<payment> currentPayments = paymentViewModel.getPayments().getValue();

                    if (currentPayments != null) {
                        for (payment p : currentPayments) {
                            if (p.getCustomerName().toLowerCase().contains(newText.toLowerCase())) {
                                filteredList.add(p);
                            }
                        }
                    }

                    adapter.setList(filteredList);
                    return true;
                }

            });

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}