package com.example.weighttracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.weighttracker.databinding.ActivityGoalWeightBinding;

public class GoalWeightActivity extends AppCompatActivity {

    private ActivityGoalWeightBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    private final ActivityResultLauncher<String> smsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                String message = isGranted
                        ? "SMS permission granted. Alerts can be sent when you reach your goal."
                        : "SMS permission denied. The app will continue to work without SMS alerts.";
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                binding.switchSmsEnabled.setChecked(isGranted && binding.switchSmsEnabled.isChecked());
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGoalWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        loadSavedGoal();

        binding.btnAllowSms.setOnClickListener(v -> requestSmsPermission());

        binding.btnSaveGoal.setOnClickListener(v -> saveGoal());

        binding.btnCancelGoal.setOnClickListener(v -> finish());
    }

    private void loadSavedGoal() {
        Cursor cursor = databaseHelper.getGoalForUser(sessionManager.getUserId());
        if (cursor.moveToFirst()) {
            binding.editGoalWeight.setText(String.valueOf(cursor.getDouble(0)));
            binding.editPhoneNumber.setText(cursor.getString(1));
            binding.switchSmsEnabled.setChecked(cursor.getInt(2) == 1);
        }
        cursor.close();
    }

    private void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission already granted.", Toast.LENGTH_SHORT).show();
        } else {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void saveGoal() {
        String goalText = getSafeText(binding.editGoalWeight.getText() == null ? null : binding.editGoalWeight.getText().toString());
        String phoneNumber = getSafeText(binding.editPhoneNumber.getText() == null ? null : binding.editPhoneNumber.getText().toString());
        boolean smsEnabled = binding.switchSmsEnabled.isChecked();

        if (TextUtils.isEmpty(goalText)) {
            Toast.makeText(this, "Enter a goal weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        double goalWeight;
        try {
            goalWeight = Double.parseDouble(goalText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid goal weight.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (smsEnabled && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please grant SMS permission before turning on SMS alerts.", Toast.LENGTH_LONG).show();
            return;
        }

        databaseHelper.saveGoal(sessionManager.getUserId(), goalWeight, phoneNumber, smsEnabled);
        Toast.makeText(this, "Goal weight saved.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private String getSafeText(String value) {
        return value == null ? "" : value.trim();
    }
}
