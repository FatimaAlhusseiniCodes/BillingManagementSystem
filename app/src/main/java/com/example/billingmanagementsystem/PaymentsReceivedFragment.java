package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import java.util.Collections;


public class PaymentsReceivedFragment extends Fragment {
    ArrayList<String> paymentsList;
    PaymentsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_payments_received,
                container,
                false
        );

        Button btnRecordPayment = view.findViewById(R.id.btnRecordPayment);

        btnRecordPayment.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_payments_to_record)
        );

        return view;
    }

    public void sortList() {
        Collections.sort(paymentsList);
        adapter.notifyDataSetChanged();
    }

}



