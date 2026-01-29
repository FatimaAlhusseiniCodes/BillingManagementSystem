package com.example.billingmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextBusinessName;
    private Button buttonSignup;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Find views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextBusinessName = findViewById(R.id.editTextBusinessName);
        buttonSignup = findViewById(R.id.buttonSignup);
        textViewLogin = findViewById(R.id.textViewLogin);

        // Signup button click
        buttonSignup.setOnClickListener(v -> signup());

        // Go back to login
        textViewLogin.setOnClickListener(v -> finish());
    }

    private void signup() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String businessName = editTextBusinessName.getText().toString().trim();

        // Validation
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

        ApiClient.register(this, email, password, businessName, 15, "USD",
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(SignupActivity.this,
                                        "Account created! Please login.", Toast.LENGTH_LONG).show();

                                // Go back to login
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                String message = response.optString("message", "Signup failed");
                                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
                                resetButton();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SignupActivity.this,
                                    "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            resetButton();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(SignupActivity.this,
                                "Signup Failed: " + error, Toast.LENGTH_LONG).show();
                        resetButton();
                    }
                });
    }

    private void resetButton() {
        buttonSignup.setEnabled(true);
        buttonSignup.setText("Sign Up");

    }
    private void signup(String email, String pass, String businessName, int taxPercentage, String currency) {
        buttonSignup.setEnabled(false);
        buttonSignup.setText("Creating account...");

        // DEBUG: Show what we're sending
        Toast.makeText(this, "Connecting to: " + ApiConfig.REGISTER, Toast.LENGTH_LONG).show();

        ApiClient.register(this, email, pass, businessName, taxPercentage, currency,
                new ApiClient.ApiCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        // DEBUG: Show full response
                        Toast.makeText(SignupActivity.this, "Response: " + response.toString(), Toast.LENGTH_LONG).show();

                        // ... rest of code
                    }

                    @Override
                    public void onError(String error) {
                        // DEBUG: Show full error
                        Toast.makeText(SignupActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        resetButton();
                    }
                });
    }
}