package com.example.civireports;

import android.app.AlertDialog;
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
            Toast.makeText(this, "Opening: File a Report or Complaint", Toast.LENGTH_SHORT).show();
        });

        // Check Report Status
        btnCheckStatus.setOnClickListener(v -> {
            Toast.makeText(this, "Opening: Check Report Status", Toast.LENGTH_SHORT).show();
        });

        // Emergency Alert — custom SwipeButton
        swipeEmergency.setOnSwipeCompleteListener(() -> showEmergencyConfirmDialog());

        // Bottom Navigation
        navHome.setOnClickListener(v ->
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show());

        navHotlines.setOnClickListener(v -> {
            Toast.makeText(this, "Hotlines", Toast.LENGTH_SHORT).show();
        });

        navNotification.setOnClickListener(v -> {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
        });

        navProfile.setOnClickListener(v -> {
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
     */
    private void sendEmergencyAlert() {
        Toast.makeText(this, "Emergency alert sent to Barangay officials!", Toast.LENGTH_LONG).show();

        // Refresh emergency count after sending
        int current = Integer.parseInt(tvEmergencyCount.getText().toString());
        tvEmergencyCount.setText(String.format("%03d", current + 1));
    }
}
