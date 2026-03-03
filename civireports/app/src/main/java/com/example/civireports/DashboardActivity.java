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
    private LinearLayout btnEmergencyAlert;

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
        btnEmergencyAlert = findViewById(R.id.btnEmergencyAlert);

        navHome         = findViewById(R.id.navHome);
        navHotlines     = findViewById(R.id.navHotlines);
        navNotification = findViewById(R.id.navNotification);
        navProfile      = findViewById(R.id.navProfile);
    }

    /**
     * Load or refresh dashboard counts.
     * Replace this with a real API/database call as needed.
     */
    private void loadDashboardData() {
        tvReportCount.setText("004");
        tvEmergencyCount.setText("001");
    }

    private void setupClickListeners() {

        // File a Report
        btnFileReport.setOnClickListener(v -> {
            // TODO: Replace with your actual FileReportActivity
            Toast.makeText(this, "Opening: File a Report or Complaint", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, FileReportActivity.class));
        });

        // Check Report Status
        btnCheckStatus.setOnClickListener(v -> {
            // TODO: Replace with your actual ReportStatusActivity
            Toast.makeText(this, "Opening: Check Report Status", Toast.LENGTH_SHORT).show();
            // startActivity(new Intent(this, ReportStatusActivity.class));
        });

        // Emergency Alert — confirm before sending
        btnEmergencyAlert.setOnClickListener(v -> showEmergencyConfirmDialog());

        // Bottom Navigation
        navHome.setOnClickListener(v ->
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show());

        navHotlines.setOnClickListener(v -> {
            // TODO: startActivity(new Intent(this, HotlinesActivity.class));
            Toast.makeText(this, "Hotlines", Toast.LENGTH_SHORT).show();
        });

        navNotification.setOnClickListener(v -> {
            // TODO: startActivity(new Intent(this, NotificationActivity.class));
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
        });

        navProfile.setOnClickListener(v -> {
            // TODO: startActivity(new Intent(this, ProfileActivity.class));
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Shows a confirmation dialog before sending the emergency alert.
     */
    private void showEmergencyConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("⚠ Send Emergency Alert?")
                .setMessage("This will immediately notify Barangay officials. Are you sure?")
                .setPositiveButton("Yes, Send Now", (dialog, which) -> sendEmergencyAlert())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Handles the actual emergency alert logic.
     * Plug in your API call, SMS, or push notification here.
     */
    private void sendEmergencyAlert() {
        // TODO: Implement real emergency alert (API call, SMS, FCM push, etc.)
        Toast.makeText(this, "Emergency alert sent to Barangay officials!", Toast.LENGTH_LONG).show();

        // Refresh emergency count after sending
        int current = Integer.parseInt(tvEmergencyCount.getText().toString());
        tvEmergencyCount.setText(String.format("%03d", current + 1));
    }
}
