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

        loginButton.setOnClickListener(v -> {
            String user = username.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)) {
                Toast.makeText(requireContext(),
                        "Enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user.equals("user") && pass.equals("1234")) {
                Toast.makeText(requireContext(),
                        "Login Successful", Toast.LENGTH_SHORT).show();

                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_login_to_home);
            } else {
                Toast.makeText(requireContext(),
                        "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });

        signupText.setOnClickListener(v ->
                Toast.makeText(requireContext(),
                        "Sign up not implemented yet", Toast.LENGTH_SHORT).show());
    }
}
