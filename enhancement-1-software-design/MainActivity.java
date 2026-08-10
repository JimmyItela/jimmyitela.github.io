package com.example.weighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.weighttracker.data.AuthResult;
import com.example.weighttracker.databinding.ActivityMainBinding;
import com.example.weighttracker.ui.ViewModelFactory;
import com.example.weighttracker.ui.login.LoginViewModel;

/**
 * Login / create-account screen. All credential handling lives in
 * {@link LoginViewModel} and the repository beneath it; this class only wires
 * views to that ViewModel.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this)).get(LoginViewModel.class);

        if (viewModel.isLoggedIn()) {
            openDashboard();
            return;
        }

        viewModel.getAuthResult().observe(this, this::handleAuthResult);

        binding.btnLogin.setOnClickListener(v ->
                viewModel.login(usernameText(), passwordText()));
        binding.btnCreateAccount.setOnClickListener(v ->
                viewModel.createAccount(usernameText(), passwordText()));
    }

    private void handleAuthResult(AuthResult result) {
        if (result == null) {
            return;
        }
        if (result.isSuccess()) {
            openDashboard();
        } else {
            Toast.makeText(this, result.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDashboard() {
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private String usernameText() {
        CharSequence value = binding.editUsername.getText();
        return value == null ? "" : value.toString().trim();
    }

    private String passwordText() {
        CharSequence value = binding.editPassword.getText();
        return value == null ? "" : value.toString();
    }
}
