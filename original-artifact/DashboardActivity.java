package com.example.weighttracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weighttracker.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);

        if (!sessionManager.isLoggedIn()) {
            returnToLogin();
            return;
        }

        binding.btnAddWeight.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddWeightActivity.class);
            startActivity(intent);
        });

        binding.btnSetGoal.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, GoalWeightActivity.class);
            startActivity(intent);
        });

        binding.btnLogout.setOnClickListener(v -> {
            sessionManager.clearSession();
            returnToLogin();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sessionManager.isLoggedIn()) {
            return;
        }
        binding.txtDashboardTitle.setText("Weight Dashboard - " + sessionManager.getUsername());
        loadGoal();
        loadWeightRows();
    }

    private void loadGoal() {
        Cursor cursor = databaseHelper.getGoalForUser(sessionManager.getUserId());
        if (cursor.moveToFirst()) {
            double goal = cursor.getDouble(0);
            boolean smsEnabled = cursor.getInt(2) == 1;
            String smsStatus = smsEnabled ? "SMS On" : "SMS Off";
            binding.txtGoalDisplay.setText("Goal Weight: " + goal + " lbs | " + smsStatus);
        } else {
            binding.txtGoalDisplay.setText("Goal Weight: not set yet");
        }
        cursor.close();
    }

    private void loadWeightRows() {
        binding.tableWeights.removeAllViews();
        addHeaderRow();

        Cursor cursor = databaseHelper.getWeightsForUser(sessionManager.getUserId());
        if (!cursor.moveToFirst()) {
            TableRow emptyRow = new TableRow(this);
            TextView emptyText = new TextView(this);
            emptyText.setPadding(8, 16, 8, 16);
            emptyText.setText("No weight entries yet. Tap Add Weight to create one.");
            emptyRow.addView(emptyText);
            binding.tableWeights.addView(emptyRow);
            cursor.close();
            return;
        }

        do {
            int entryId = cursor.getInt(0);
            String date = cursor.getString(1);
            double weight = cursor.getDouble(2);
            addWeightRow(entryId, date, weight);
        } while (cursor.moveToNext());
        cursor.close();
    }

    private void addHeaderRow() {
        TableRow headerRow = new TableRow(this);
        headerRow.addView(makeCell("Date", true));
        headerRow.addView(makeCell("Weight", true));
        headerRow.addView(makeCell("Edit", true));
        headerRow.addView(makeCell("Delete", true));
        binding.tableWeights.addView(headerRow);
    }

    private void addWeightRow(int entryId, String date, double weight) {
        TableRow row = new TableRow(this);
        row.addView(makeCell(date, false));
        row.addView(makeCell(weight + " lbs", false));

        Button editButton = new Button(this);
        editButton.setText("Edit");
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, AddWeightActivity.class);
            intent.putExtra("entry_id", entryId);
            startActivity(intent);
        });

        Button deleteButton = new Button(this);
        deleteButton.setText("Delete");
        deleteButton.setOnClickListener(v -> {
            int result = databaseHelper.deleteWeight(entryId);
            if (result > 0) {
                Toast.makeText(this, "Weight entry deleted.", Toast.LENGTH_SHORT).show();
                loadWeightRows();
            }
        });

        row.addView(editButton);
        row.addView(deleteButton);
        binding.tableWeights.addView(row);
    }

    private TextView makeCell(String text, boolean bold) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(12, 12, 12, 12);
        if (bold) {
            textView.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_TitleSmall);
        }
        return textView;
    }

    private void returnToLogin() {
        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
