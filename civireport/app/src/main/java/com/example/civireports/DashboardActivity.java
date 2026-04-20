package com.example.civireports;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvTotalComplaints;
    private TextView tvEmergencyCount;
    private TextView tvPriorityCount;
    private TextView tvNominalCount;

    private LinearLayout btnFileReport;
    private LinearLayout btnCheckStatus;
    private SwipeButton swipeEmergency;
    private ImageView btnNotificationHeader;
    private ImageView btnExpandStats;
    private ImageView btnCollapseStats;
    private LinearLayout reportsContainer;
    private View reportsOverlapCard;
    private View statsDivider;

    private LinearLayout navHome;
    private LinearLayout navHotlines;
    private LinearLayout navNotification;
    private LinearLayout navProfile;

    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        addDummyDataIfNeeded();
        setupClickListeners();
        checkFirstLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }

    private void initViews() {
        tvTotalComplaints = findViewById(R.id.tvTotalComplaints);
        tvEmergencyCount  = findViewById(R.id.tvEmergencyCount);
        tvPriorityCount   = findViewById(R.id.tvPriorityCount);
        tvNominalCount    = findViewById(R.id.tvNominalCount);

        btnFileReport         = findViewById(R.id.btnFileReport);
        btnCheckStatus        = findViewById(R.id.btnCheckStatus);
        swipeEmergency        = findViewById(R.id.swipeEmergency);
        btnNotificationHeader = findViewById(R.id.btnNotificationHeader);
        btnExpandStats        = findViewById(R.id.btnExpandStats);
        btnCollapseStats      = findViewById(R.id.btnCollapseStats);
        reportsContainer      = findViewById(R.id.reportsContainer);
        reportsOverlapCard    = findViewById(R.id.reports_overlap_card);
        statsDivider          = findViewById(R.id.stats_divider);

        navHome         = findViewById(R.id.navHome);
        navHotlines     = findViewById(R.id.navHotlines);
        navNotification = findViewById(R.id.navNotification);
        navProfile      = findViewById(R.id.navProfile);
    }

    private void addDummyDataIfNeeded() {
        ReportDataStore store = ReportDataStore.getInstance();
        if (!store.hasReports()) {
            store.addReport(new ReportDataStore.ReportItem("#007", "Pending", "Accident", "Emergency", "Vehicle Collision", "St. Main", "Help", "1 minutes ago", null));
            store.addReport(new ReportDataStore.ReportItem("#004", "Processing", "Broken Streetlight", "Priority", "Electrical Issue", "Ave 2", "No Light", "2 minute ago", null));
            store.addReport(new ReportDataStore.ReportItem("#001", "Completed", "Garbage Collection", "Nominal", "Waste Disposal", "Rd 5", "Smelly", "32 minutes ago", null));
            store.addReport(new ReportDataStore.ReportItem("#002", "Pending", "Clogged Sewage", "Nominal", "Drainage", "Lane 1", "Stuck", "2 hours ago", null));
            store.addReport(new ReportDataStore.ReportItem("#003", "Processing", "Stray Dogs", "Nominal", "Animal Control", "St. 9", "Dangerous", "6 hours ago", null));
            store.addReport(new ReportDataStore.ReportItem("#007", "Completed", "Stray Dogs", "Nominal", "Animal Control", "Rd 2", "Done", "6 hours ago", null));
        }
    }

    private void loadDashboardData() {
        ReportDataStore store = ReportDataStore.getInstance();

        tvTotalComplaints.setText(String.valueOf(store.getTotalCount()));
        tvEmergencyCount.setText(store.getEmergencyCount() + " Emergency");
        tvPriorityCount.setText(store.getPriorityCount() + " Priority");
        tvNominalCount.setText(store.getNominalCount() + " Nominal");

        populateReportsList();
    }

    private void populateReportsList() {
        reportsContainer.removeAllViews();
        List<ReportDataStore.ReportItem> reports = new ArrayList<>(ReportDataStore.getInstance().getAllReports());
        
        // Sort reports by priority: Emergency > Priority > Nominal
        Collections.sort(reports, (r1, r2) -> {
            int p1 = getPriorityWeight(r1.getPriority());
            int p2 = getPriorityWeight(r2.getPriority());
            return Integer.compare(p1, p2);
        });

        LayoutInflater inflater = LayoutInflater.from(this);

        for (ReportDataStore.ReportItem item : reports) {
            View itemView = inflater.inflate(R.layout.item_dashboard_report, reportsContainer, false);
            
            LinearLayout container = itemView.findViewById(R.id.reportItemContainer);
            TextView tvQueue = itemView.findViewById(R.id.tvQueueNumber);
            TextView tvTitle = itemView.findViewById(R.id.tvReportTitle);
            TextView tvTime = itemView.findViewById(R.id.tvReportTime);

            tvQueue.setText(item.getQueueNumber());
            tvTitle.setText(item.getComplaintType());
            tvTime.setText(item.getDate());

            if ("Emergency".equals(item.getPriority())) {
                tvQueue.setBackgroundResource(R.drawable.bg_badge_emergency);
                container.setBackgroundResource(R.drawable.bg_report_item_emergency);
            } else if ("Priority".equals(item.getPriority())) {
                tvQueue.setBackgroundResource(R.drawable.bg_badge_priority);
                container.setBackgroundResource(R.drawable.bg_report_item_priority);
            } else {
                tvQueue.setBackgroundResource(R.drawable.bg_badge_nominal);
                container.setBackgroundResource(R.drawable.bg_report_item_nominal);
            }

            reportsContainer.addView(itemView);
        }
    }

    private int getPriorityWeight(String priority) {
        if ("Emergency".equals(priority)) return 0;
        if ("Priority".equals(priority)) return 1;
        return 2; // Nominal
    }

    private void setupClickListeners() {
        btnExpandStats.setOnClickListener(v -> {
            isExpanded = true;
            reportsOverlapCard.setVisibility(View.VISIBLE);
            if (statsDivider != null) statsDivider.setVisibility(View.VISIBLE);
            btnExpandStats.setVisibility(View.GONE);
            if (btnCollapseStats != null) btnCollapseStats.setVisibility(View.VISIBLE);
        });

        btnCollapseStats.setOnClickListener(v -> {
            isExpanded = false;
            reportsOverlapCard.setVisibility(View.GONE);
            if (statsDivider != null) statsDivider.setVisibility(View.GONE);
            btnExpandStats.setVisibility(View.VISIBLE);
            btnCollapseStats.setVisibility(View.GONE);
        });

        btnFileReport.setOnClickListener(v ->
                startActivity(new Intent(this, Report.class)));

        btnCheckStatus.setOnClickListener(v ->
                startActivity(new Intent(this, StatusReport.class)));

        swipeEmergency.setOnSwipeCompleteListener(() -> {
            sendEmergencyAlert();
            Intent intent = new Intent(this, hotlines.class);
            intent.putExtra("show_emergency_popup", true);
            startActivity(intent);
        });

        btnNotificationHeader.setOnClickListener(v -> showNotificationModal());

        navHome.setOnClickListener(v -> { });

        navHotlines.setOnClickListener(v ->
                startActivity(new Intent(this, hotlines.class)));

        navNotification.setOnClickListener(v ->
                startActivity(new Intent(this, Notification.class)));

        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));
    }

    private void showNotificationModal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_notification_modal, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView btnClose = dialogView.findViewById(R.id.btn_close_notif);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void checkFirstLogin() {
        SharedPreferences prefs = getSharedPreferences("CiviReportPrefs", MODE_PRIVATE);
        boolean isFirstLogin = prefs.getBoolean("isFirstLogin", true);

        if (isFirstLogin) {
            showWelcomeDialog();
            prefs.edit().putBoolean("isFirstLogin", false).apply();
        }
    }

    private void showWelcomeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_welcome, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnGetStarted = dialogView.findViewById(R.id.btn_get_started);
        btnGetStarted.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void sendEmergencyAlert() {
        ReportDataStore.getInstance().addReport(new ReportDataStore.ReportItem(
                "#SOS", "Pending", "Emergency Alert", "Emergency", 
                "SOS", "Location Unknown", "User swiped SOS", "Just now", null));
        loadDashboardData();
        Toast.makeText(this, "Emergency alert sent to admin!", Toast.LENGTH_SHORT).show();
    }
}
