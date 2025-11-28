package com.gb.finddining.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.gb.finddining.NavigationHost;
import com.gb.finddining.R;
import com.gb.finddining.auth.AuthManager;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText username = view.findViewById(R.id.input_username);
        EditText password = view.findViewById(R.id.input_password);
        Button signIn = view.findViewById(R.id.button_sign_in);
        CheckBox rememberMe = view.findViewById(R.id.checkbox_remember);
        View createAccount = view.findViewById(R.id.link_create_account);

        boolean remember = AuthManager.shouldRemember(requireContext());
        rememberMe.setChecked(remember);
        String savedUser = AuthManager.getUsername(requireContext());
        String savedPass = AuthManager.getPassword(requireContext());
        if (savedUser != null) {
            username.setText(savedUser);
        }
        if (remember && savedPass != null) {
            password.setText(savedPass);
        }

        View.OnClickListener proceed = v -> {
            String usernameText = username.getText() != null ? username.getText().toString().trim() : "";
            String passwordText = password.getText() != null ? password.getText().toString() : "";
            if (TextUtils.isEmpty(usernameText) || TextUtils.isEmpty(passwordText)) {
                Toast.makeText(requireContext(), "Username and password required", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = AuthManager.login(requireContext(), usernameText, passwordText, rememberMe.isChecked());
            if (!success) {
                Toast.makeText(requireContext(), "Invalid credentials. Create an account first.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (getActivity() instanceof NavigationHost) {
                ((NavigationHost) getActivity()).navigateToHome();
            }
        };

        signIn.setOnClickListener(proceed);
        createAccount.setOnClickListener(v -> {
            if (getActivity() instanceof NavigationHost) {
                ((NavigationHost) getActivity()).navigateToSignUp();
            }
        });
    }
}
