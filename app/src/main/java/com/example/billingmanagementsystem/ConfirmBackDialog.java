package com.example.billingmanagementsystem;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmBackDialog extends DialogFragment {

    public interface ConfirmBackListener {
        void onConfirmBack();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle("Discard Changes?")
                .setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
                .setPositiveButton("Discard", (dialog, which) -> {
                    if (getParentFragment() instanceof ConfirmBackListener) {
                        ((ConfirmBackListener) getParentFragment()).onConfirmBack();
                    }
                    dismiss();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dismiss())
                .create();
    }
}