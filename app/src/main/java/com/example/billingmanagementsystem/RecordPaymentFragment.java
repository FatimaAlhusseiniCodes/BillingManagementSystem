package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class RecordPaymentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {

        View view = inflater.inflate(
                R.layout.fragment_record_payment,
                container,
                false
        );

        setHasOptionsMenu(true);

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = view.findViewById(R.id.toolbar);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            ConfirmBackDialog dialog = new ConfirmBackDialog();
            dialog.show(getChildFragmentManager(), "confirmBack");
        });
    }

    @Override
    public void onCreateOptionsMenu(
            @NonNull Menu menu,
            @NonNull MenuInflater inflater
    ) {
        inflater.inflate(R.menu.menu_save, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_save) {
            Toast.makeText(
                    requireContext(),
                    "Saved successfully",
                    Toast.LENGTH_SHORT
            ).show();
            NavHostFragment.findNavController(this)
                    .navigate(R.id.paymentsReceivedFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

