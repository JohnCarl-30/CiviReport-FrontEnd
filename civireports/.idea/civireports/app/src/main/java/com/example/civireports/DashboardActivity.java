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
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh counts whenever user returns to dashboard
        loadDashboardData();
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
        ReportDataStore store = ReportDataStore.getInstance();
        
        // Format to 3 digits like the design (001, 002, etc.)
        tvReportCount.setText(String.format("%03d", store.getReportCount()));
        tvEmergencyCount.setText(String.format("%03d", store.getEmergencyCount()));
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
        ReportDataStore.getInstance().incrementEmergencyCount();
        loadDashboardData(); // Update the UI immediately
        Toast.makeText(this, "Emergency alert sent to Barangay officials!", Toast.LENGTH_LONG).show();
    }
}