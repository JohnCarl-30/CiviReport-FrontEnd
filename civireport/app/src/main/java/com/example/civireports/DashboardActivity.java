package com.example.civireports;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView btnNotificationHeader;

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
        checkFirstLogin();
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
        btnNotificationHeader = findViewById(R.id.btnNotificationHeader);

        navHome         = findViewById(R.id.navHome);
        navHotlines     = findViewById(R.id.navHotlines);
        navNotification = findViewById(R.id.navNotification);
        navProfile      = findViewById(R.id.navProfile);
    }

    private void loadDashboardData() {
        ReportDataStore store = ReportDataStore.getInstance();
        //burat
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

        swipeEmergency.setOnSwipeCompleteListener(() -> {
            sendEmergencyAlert();
            // Go direct to hotlines with a flag to show the popup
            Intent intent = new Intent(this, hotlines.class);
            intent.putExtra("show_emergency_popup", true);
            startActivity(intent);
        });

        btnNotificationHeader.setOnClickListener(v -> {
            startActivity(new Intent(this, Notification.class));
        });

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
            startActivity(new Intent(this, Profile.class));
        });
    }

    private void checkFirstLogin() {
        SharedPreferences prefs = getSharedPreferences("CiviReportPrefs", MODE_PRIVATE);
        boolean isFirstLogin = prefs.getBoolean("isFirstLogin", true);

        if (isFirstLogin) {
            showWelcomeDialog();
            // Set flag to false so it doesn't show again
            prefs.edit().putBoolean("isFirstLogin", false).apply();
        }
    }

    private void showWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_welcome, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        
        // Make background transparent to show card corners
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnGetStarted = dialogView.findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void sendEmergencyAlert() {
        ReportDataStore.getInstance().incrementEmergencyCount();
        loadDashboardData(); // Update the UI immediately
        // Here you would normally add the logic to notify the admin side (API call, etc.)
        Toast.makeText(this, "Emergency alert sent to admin!", Toast.LENGTH_SHORT).show();
    }
}
