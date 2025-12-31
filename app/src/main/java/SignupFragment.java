package com.example.billingmanagementsystem;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupFragment extends Fragment {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextBusinessName;
    private Button buttonSignup;
    private TextView textViewLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        editTextConfirmPassword = view.findViewById(R.id.editTextConfirmPassword);
        editTextBusinessName = view.findViewById(R.id.editTextBusinessName);
        buttonSignup = view.findViewById(R.id.buttonSignup);
        textViewLogin = view.findViewById(R.id.textViewLogin);

        buttonSignup.setOnClickListener(v -> signup());

        textViewLogin.setOnClickListener(v -> {
            NavHostFragment.findNavController(SignupFragment.this).navigateUp();
        });
    }

    private void signup() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String businessName = editTextBusinessName.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmPassword.setError("Confirm password is required");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(businessName)) {
            editTextBusinessName.setError("Business name is required");
            editTextBusinessName.requestFocus();
            return;
        }

        buttonSignup.setEnabled(false);
        buttonSignup.setText("Creating account...");

        ApiClient.register(getContext(), email, password, businessName, 15, "ALL",
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Snackbar.make(requireView(),
                                                "✓ Account created! Please login.",
                                                Snackbar.LENGTH_LONG)
                                        .setBackgroundTint(0xFF4CAF50)
                                        .show();

                                requireView().postDelayed(() -> {
                                    NavHostFragment.findNavController(SignupFragment.this).navigateUp();
                                }, 1500);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        buttonSignup.setEnabled(true);
                        buttonSignup.setText("Sign Up");
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Signup Failed: " + error,
                                Toast.LENGTH_LONG).show();
                        buttonSignup.setEnabled(true);
                        buttonSignup.setText("Sign Up");
                    }
                }
        );
    }
}