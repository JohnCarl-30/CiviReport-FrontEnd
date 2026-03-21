package com.example.civireports;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ReportHistoryActivity extends AppCompatActivity {

    private LinearLayout reportItemsContainer;
    private TextView tvEmptyState;

    // ── Holds submitted reports — TODO: replace with real API data ────────────
    // When backend is ready, fetch reports from API and call loadReports(list)
    private final List<ReportItem> reports = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_history);

        reportItemsContainer = findViewById(R.id.reportItemsContainer);
        tvEmptyState         = findViewById(R.id.tvEmptyState);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        setupBottomNav();

        // TODO: Replace with real API call when backend is ready
        // For now loads whatever is in the reports list (empty by default)
        // To test with dummy data, add items like:
        // reports.add(new ReportItem("Queue #001", "Noise / Disturbance", "Loud Music", "02/19/2026", "PENDING"));
        loadReports();
    }

    // ── Renders report cards or shows empty state ─────────────────────────────
    private void loadReports() {
        if (reports.isEmpty()) {
            tvEmptyState.setVisibility(View.GONE);
            reportItemsContainer.setVisibility(View.GONE);
            return;
        }

        tvEmptyState.setVisibility(View.GONE);
        reportItemsContainer.setVisibility(View.VISIBLE);
        reportItemsContainer.removeAllViews();

        for (int i = 0; i < reports.size(); i++) {
            reportItemsContainer.addView(buildReportRow(reports.get(i)));
            // Divider between rows
            if (i < reports.size() - 1) {
                View divider = new View(this);
                LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
                dp.setMargins(0, dpToPx(8), 0, dpToPx(8));
                divider.setLayoutParams(dp);
                divider.setBackgroundColor(0xFFF0F0F0);
                reportItemsContainer.addView(divider);
            }
        }
    }

    // ── Build a single report row ─────────────────────────────────────────────
    private View buildReportRow(ReportItem r) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Left: report details
        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        left.setLayoutParams(leftParams);

        TextView queue = new TextView(this);
        queue.setText(r.queueNumber);
        queue.setTextColor(0xFF1B2F5B);
        queue.setTextSize(14);
        queue.setTypeface(null, Typeface.BOLD);

        TextView type = new TextView(this);
        type.setText(r.complaintType + " • " + r.specificIssue);
        type.setTextColor(0xFF555555);
        type.setTextSize(12);

        TextView date = new TextView(this);
        date.setText(r.dateReported);
        date.setTextColor(0xFF999999);
        date.setTextSize(11);

        left.addView(queue);
        left.addView(type);
        left.addView(date);

        // Right: status badge
        TextView status = new TextView(this);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        statusParams.setMargins(dpToPx(8), 0, 0, 0);
        status.setLayoutParams(statusParams);
        status.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        status.setText(r.status);
        status.setTextColor(0xFFFFFFFF);
        status.setTextSize(11);
        status.setTypeface(null, Typeface.BOLD);

        // Set badge color based on status
        switch (r.status.toUpperCase()) {
            case "REJECTED":
                status.setBackgroundResource(R.drawable.bg_status_rejected);
                break;
            case "APPROVED":
            case "SOLVED":
            case "FINISHED":
                status.setBackgroundResource(R.drawable.bg_status_approve);
                break;
            default: // PENDING
                status.setBackgroundResource(R.drawable.bg_status_pending);
                break;
        }

        row.addView(left);
        row.addView(status);
        return row;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    // ── Simple report data model ──────────────────────────────────────────────
    static class ReportItem {
        String queueNumber, complaintType, specificIssue, dateReported, status;
        ReportItem(String q, String ct, String si, String d, String s) {
            queueNumber = q; complaintType = ct; specificIssue = si;
            dateReported = d; status = s;
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));
        findViewById(R.id.navHotlines).setOnClickListener(v ->
                startActivity(new Intent(this, hotlines.class)));
        findViewById(R.id.navNotification).setOnClickListener(v ->
                startActivity(new Intent(this, Notification.class)));
        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));
    }
}