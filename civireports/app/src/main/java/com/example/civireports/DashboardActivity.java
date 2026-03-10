package com.example.civireports;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    // Stats
    private TextView tvReportCount;
    private TextView tvEmergencyCount;

    // Action buttons
    private LinearLayout btnFileReport;
    private LinearLayout btnCheckStatus;
    private SwipeButton swipeEmergency;

    // Bottom navigation
    private LinearLayout navHome;
    private LinearLayout navHotlines;
    private LinearLayout navNotification;
    private LinearLayout navProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        loadDashboardData();
        setupClickListeners();
    }

    private void initViews() {
        tvReportCount    = findViewById(R.id.tvReportCount);
        tvEmergencyCount = findViewById(R.id.tvEmergencyCount);

        btnFileReport     = findViewById(R.id.btnFileReport);
        btnCheckStatus    = findViewById(R.id.btnCheckStatus);
        swipeEmergency    = findViewById(R.id.swipeEmergency);

        navHome         = findViewById(R.id.navHome);
        navHotlines     = findViewById(R.id.navHotlines);
        navNotification = findViewById(R.id.navNotification);
        navProfile      = findViewById(R.id.navProfile);
    }

    private void loadDashboardData() {
        tvReportCount.setText("004");
        tvEmergencyCount.setText("001");
    }

    private void setupClickListeners() {

        btnFileReport.setOnClickListener(v -> {
            startActivity(new Intent(this, Report.class));
        });

        btnCheckStatus.setOnClickListener(v -> {
            startActivity(new Intent(this, StatusReport.class));
        });

        swipeEmergency.setOnSwipeCompleteListener(() -> showEmergencyConfirmDialog());

        // Bottom Navigation
        navHome.setOnClickListener(v -> {
            // Already on home
        });

        navHotlines.setOnClickListener(v -> {
            startActivity(new Intent(this, hotlines.class));
        });

        navNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, Notification.class));
        });

        navProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        });
    }

    private void showEmergencyConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("⚠ Send Emergency Alert?")
                .setMessage("This will immediately notify Barangay officials. Are you sure?")
                .setPositiveButton("Yes, Send Now", (dialog, which) -> sendEmergencyAlert())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendEmergencyAlert() {
        Toast.makeText(this, "Emergency alert sent to Barangay officials!", Toast.LENGTH_LONG).show();

        int current = Integer.parseInt(tvEmergencyCount.getText().toString());
        tvEmergencyCount.setText(String.format("%03d", current + 1));
    }
}