package com.example.weighttracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weighttracker.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (sessionManager.isLoggedIn()) {
            openDashboard();
            return;
        }

        binding.btnLogin.setOnClickListener(v -> loginUser());
        binding.btnCreateAccount.setOnClickListener(v -> createAccount());
    }

    private void loginUser() {
        String username = getTrimmedText(binding.editUsername.getText() == null ? null : binding.editUsername.getText().toString());
        String password = getTrimmedText(binding.editPassword.getText() == null ? null : binding.editPassword.getText().toString());

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter a username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.validateUser(username, password)) {
            sessionManager.saveUserSession(databaseHelper.getUserId(username), username);
            Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show();
            openDashboard();
        } else {
            Toast.makeText(this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount() {
        String username = getTrimmedText(binding.editUsername.getText() == null ? null : binding.editUsername.getText().toString());
        String password = getTrimmedText(binding.editPassword.getText() == null ? null : binding.editPassword.getText().toString());

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter a username and password to create an account.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.userExists(username)) {
            Toast.makeText(this, "That username already exists. Please log in.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (databaseHelper.createUser(username, password)) {
            sessionManager.saveUserSession(databaseHelper.getUserId(username), username);
            Toast.makeText(this, "Account created.", Toast.LENGTH_SHORT).show();
            openDashboard();
        } else {
            Toast.makeText(this, "Unable to create account.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openDashboard() {
        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private String getTrimmedText(String value) {
        return value == null ? "" : value.trim();
    }
}
