package com.gb.finddining.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gb.finddining.NavigationHost;
import com.gb.finddining.R;
import com.gb.finddining.auth.AuthManager;

public class SignUpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText username = view.findViewById(R.id.input_signup_username);
        EditText password = view.findViewById(R.id.input_signup_password);
        EditText confirm = view.findViewById(R.id.input_signup_confirm);
        Button createAccount = view.findViewById(R.id.button_create_account);
        Button backToLogin = view.findViewById(R.id.button_back_to_login);

        createAccount.setOnClickListener(v -> {
            String usernameText = username.getText() != null ? username.getText().toString().trim() : "";
            String passwordText = password.getText() != null ? password.getText().toString() : "";
            String confirmText = confirm.getText() != null ? confirm.getText().toString() : "";
            if (TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(passwordText)) {
                Toast.makeText(requireContext(), "Username and password are required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!passwordText.equals(confirmText)) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean created = AuthManager.createUser(requireContext(), usernameText, passwordText);
            if (created) {
                Toast.makeText(requireContext(), "Account created. Signed in as " + usernameText, Toast.LENGTH_SHORT).show();
                if (getActivity() instanceof NavigationHost) {
                    ((NavigationHost) getActivity()).navigateToHome();
                }
            } else {
                Toast.makeText(requireContext(), "Account already exists or invalid input", Toast.LENGTH_SHORT).show();
            }
        });

        backToLogin.setOnClickListener(v -> {
            if (getActivity() instanceof NavigationHost) {
                ((NavigationHost) getActivity()).navigateToLogin();
            }
        });
    }
}
