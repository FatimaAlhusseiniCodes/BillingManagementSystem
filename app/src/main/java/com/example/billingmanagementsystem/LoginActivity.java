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

public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginButton;
    private TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check if already logged in
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            goToMain();
            return;
        }

        // Find views
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

        // Login button click
        loginButton.setOnClickListener(v -> {
            String email = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            login(email, pass);
        });

        // Go to signup
        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, com.example.billingmanagementsystem.SignupActivity.class);
            startActivity(intent);
        });
    }

    private void login(String email, String pass) {
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        ApiClient.login(this, email, pass, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject userData = response.getJSONObject("data");

                        SessionManager session = new SessionManager(LoginActivity.this);
                        session.createLoginSession(
                                userData.getInt("user_id"),
                                userData.getString("email"),
                                userData.getString("business_name"),
                                userData.getInt("tax_percentage"),
                                userData.getString("base_currency")
                        );

                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        goToMain();
                    } else {
                        String message = response.optString("message", "Login failed");
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        resetButton();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    resetButton();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginActivity.this, "Login Failed: " + error, Toast.LENGTH_LONG).show();
                resetButton();
            }
        });
    }

    private void resetButton() {
        loginButton.setEnabled(true);
        loginButton.setText("Login");
    }

    private void goToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}