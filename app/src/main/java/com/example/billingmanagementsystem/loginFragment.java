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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {

    private EditText username, password;
    private Button loginButton;
    private TextView signupText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.loginButton);
        signupText = view.findViewById(R.id.signupText);

        SessionManager session = new SessionManager(getContext());
        if (session.isLoggedIn()) {
            navigateToHome();
            return;
        }

        loginButton.setOnClickListener(v -> {
            String email = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                Toast.makeText(requireContext(),
                        "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            login(email, pass);
        });

        signupText.setOnClickListener(v -> {
            try {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.signupFragment);
            } catch (Exception e) {
                Toast.makeText(requireContext(),
                        "Sign up not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String email, String password) {
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        ApiClient.login(getContext(), email, password, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject userData = response.getJSONObject("data");

                        SessionManager session = new SessionManager(getContext());
                        session.createLoginSession(
                                userData.getInt("user_id"),
                                userData.getString("email"),
                                userData.getString("business_name"),
                                userData.getInt("tax_percentage"),
                                userData.getString("base_currency")
                        );

                        Toast.makeText(getContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }

                loginButton.setEnabled(true);
                loginButton.setText("Login");
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Login Failed: " + error, Toast.LENGTH_LONG).show();
                loginButton.setEnabled(true);
                loginButton.setText("Login");
            }
        });
    }

    private void navigateToHome() {
        try {
            NavHostFragment.findNavController(LoginFragment.this)
                    .navigate(R.id.action_login_to_home);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Navigation error", Toast.LENGTH_SHORT).show();
        }
    }
}