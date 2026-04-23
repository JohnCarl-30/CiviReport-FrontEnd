package com.example.civireports;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.civireports.models.UserComplaint;
import com.example.civireports.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportHistoryActivity extends AppCompatActivity {

    private LinearLayout reportItemsContainer;
    private TextView tvEmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_history);

        reportItemsContainer = findViewById(R.id.reportItemsContainer);
        tvEmptyState         = findViewById(R.id.tvEmptyState);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        setupBottomNav();

        fetchComplaints();
    }

    private void fetchComplaints() {
        RetrofitClient.getApiService(this)
                .getMyComplaints()
                .enqueue(new Callback<List<UserComplaint>>() {
                    @Override
                    public void onResponse(Call<List<UserComplaint>> call, Response<List<UserComplaint>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            loadReports(response.body());
                        } else {
                            loadReports(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserComplaint>> call, Throwable t) {
                        loadReports(new ArrayList<>());
                    }
                });
    }

    private void loadReports(List<UserComplaint> complaints) {
        if (complaints.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            reportItemsContainer.setVisibility(View.GONE);
            return;
        }

        tvEmptyState.setVisibility(View.GONE);
        reportItemsContainer.setVisibility(View.VISIBLE);
        reportItemsContainer.removeAllViews();

        for (int i = 0; i < complaints.size(); i++) {
            reportItemsContainer.addView(buildReportRow(complaints.get(i)));
            // Divider between rows
            if (i < complaints.size() - 1) {
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

    private View buildReportRow(UserComplaint r) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setPadding(0, dpToPx(8), 0, dpToPx(8));
        row.setOnClickListener(v -> {
            Intent intent = new Intent(this, StatusReport.class);
            // We could pass the specific complaint ID if needed, 
            // but StatusReport currently fetches all.
            startActivity(intent);
        });

        // Left: report details
        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        left.setLayoutParams(leftParams);

        TextView queue = new TextView(this);
        queue.setText(r.getQueueNumber());
        queue.setTextColor(0xFF1B2F5B);
        queue.setTextSize(14);
        queue.setTypeface(null, Typeface.BOLD);

        TextView type = new TextView(this);
        type.setText(r.getComplaintType() + " • " + r.getComplaintSubtype());
        type.setTextColor(0xFF555555);
        type.setTextSize(12);

        TextView date = new TextView(this);
        date.setText(r.getComplaintDate());
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
        status.setText(r.getFormattedStatus());
        status.setTextColor(0xFFFFFFFF);
        status.setTextSize(11);
        status.setTypeface(null, Typeface.BOLD);

        String statusStr = r.getComplaintStatus() != null ? r.getComplaintStatus().toUpperCase() : "PENDING";
        switch (statusStr) {
            case "REJECTED":
                status.setBackgroundResource(R.drawable.bg_status_rejected);
                break;
            case "APPROVED":
            case "SOLVED":
            case "FINISHED":
            case "RESOLVED":
                status.setBackgroundResource(R.drawable.bg_status_approve);
                break;
            default: // PENDING, PROCESSING, etc.
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

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));
        findViewById(R.id.navHotlines).setOnClickListener(v ->
                startActivity(new Intent(this, hotlines.class)));
        findViewById(R.id.navNotification).setOnClickListener(v ->
                startActivity(new Intent(this, AnnouncementActivity.class)));
        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));
    }
}
