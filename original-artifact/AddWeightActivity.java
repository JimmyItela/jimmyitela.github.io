package com.example.weighttracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.weighttracker.databinding.ActivityAddWeightBinding;

public class AddWeightActivity extends AppCompatActivity {

    private ActivityAddWeightBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private int entryId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddWeightBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        entryId = getIntent().getIntExtra("entry_id", -1);

        if (entryId != -1) {
            binding.txtAddWeightTitle.setText("Update Weight Entry");
            loadExistingEntry();
        }

        binding.btnSaveWeight.setOnClickListener(v -> saveWeight());
        binding.btnCancelWeight.setOnClickListener(v -> finish());
    }

    private void loadExistingEntry() {
        Cursor cursor = databaseHelper.getWeightById(entryId);
        if (cursor.moveToFirst()) {
            binding.editDate.setText(cursor.getString(1));
            binding.editWeight.setText(String.valueOf(cursor.getDouble(2)));
        }
        cursor.close();
    }

    private void saveWeight() {
        String weightText = getSafeText(binding.editWeight.getText() == null ? null : binding.editWeight.getText().toString());
        String dateText = getSafeText(binding.editDate.getText() == null ? null : binding.editDate.getText().toString());

        if (TextUtils.isEmpty(weightText) || TextUtils.isEmpty(dateText)) {
            Toast.makeText(this, "Enter both a weight and a date.", Toast.LENGTH_SHORT).show();
            return;
        }

        double weightValue;
        try {
            weightValue = Double.parseDouble(weightText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid weight value.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (entryId == -1) {
            long result = databaseHelper.insertWeight(sessionManager.getUserId(), weightValue, dateText);
            if (result == -1) {
                Toast.makeText(this, "Unable to save weight entry.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Weight entry saved.", Toast.LENGTH_SHORT).show();
        } else {
            int result = databaseHelper.updateWeight(entryId, weightValue, dateText);
            if (result <= 0) {
                Toast.makeText(this, "Unable to update weight entry.", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Weight entry updated.", Toast.LENGTH_SHORT).show();
        }

        checkGoalAndSendSms(weightValue);
        finish();
    }

    private void checkGoalAndSendSms(double newWeight) {
        Cursor cursor = databaseHelper.getGoalForUser(sessionManager.getUserId());
        if (!cursor.moveToFirst()) {
            cursor.close();
            return;
        }

        double goalWeight = cursor.getDouble(0);
        String phoneNumber = cursor.getString(1);
        boolean smsEnabled = cursor.getInt(2) == 1;
        cursor.close();

        if (newWeight > goalWeight) {
            return;
        }

        Toast.makeText(this, "Goal reached or beaten. Great job!", Toast.LENGTH_LONG).show();

        if (!smsEnabled) {
            Toast.makeText(this, "SMS alert not sent because SMS alerts are turned off.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Goal reached, but no phone number is saved for SMS alerts.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Goal reached, but SMS permission is not granted. The app still works without SMS.", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            SmsManager smsManager;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                smsManager = getSystemService(SmsManager.class);
            } else {
                smsManager = SmsManager.getDefault();
            }

            if (smsManager != null) {
                smsManager.sendTextMessage(phoneNumber, null,
                        "Congratulations! You reached your goal weight of " + goalWeight + " lbs.",
                        null, null);
                Toast.makeText(this, "Goal SMS sent.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS service unavailable on this device.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Unable to send SMS on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSafeText(String value) {
        return value == null ? "" : value.trim();
    }
}
