package com.example.civireports;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.civireports.models.EmergencyListResponse;
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

    private final List<UserComplaint> complaints = new ArrayList<>();
    private final List<EmergencyListResponse> emergencies = new ArrayList<>();
    private int callsCompleted = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_history);

        reportItemsContainer = findViewById(R.id.reportItemsContainer);
        tvEmptyState         = findViewById(R.id.tvEmptyState);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        setupBottomNav();
        fetchAll();
    }

    private void fetchAll() {
        callsCompleted = 0;
        complaints.clear();
        emergencies.clear();

        // Fetch complaints
        RetrofitClient.getApiService(this)
                .getMyComplaints()
                .enqueue(new Callback<List<UserComplaint>>() {
                    @Override
                    public void onResponse(Call<List<UserComplaint>> call, Response<List<UserComplaint>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            complaints.addAll(response.body());
                        }
                        callsCompleted++;
                        if (callsCompleted == 2) renderAll();
                    }

                    @Override
                    public void onFailure(Call<List<UserComplaint>> call, Throwable t) {
                        callsCompleted++;
                        if (callsCompleted == 2) renderAll();
                    }
                });

        // Fetch emergencies
        RetrofitClient.getApiService(this)
                .getMyEmergencies()
                .enqueue(new Callback<List<EmergencyListResponse>>() {
                    @Override
                    public void onResponse(Call<List<EmergencyListResponse>> call, Response<List<EmergencyListResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            emergencies.addAll(response.body());
                        }
                        callsCompleted++;
                        if (callsCompleted == 2) renderAll();
                    }

                    @Override
                    public void onFailure(Call<List<EmergencyListResponse>> call, Throwable t) {
                        callsCompleted++;
                        if (callsCompleted == 2) renderAll();
                    }
                });
    }

    private void renderAll() {
        if (complaints.isEmpty() && emergencies.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            reportItemsContainer.setVisibility(View.GONE);
            return;
        }

        // Combine into unified list
        List<Object> combined = new ArrayList<>();
        combined.addAll(complaints);
        combined.addAll(emergencies);

        // Sort by date — most recent first
        combined.sort((a, b) -> {
            String dateA = getDateString(a);
            String dateB = getDateString(b);
            return dateB.compareTo(dateA);
        });

        tvEmptyState.setVisibility(View.GONE);
        reportItemsContainer.setVisibility(View.VISIBLE);
        reportItemsContainer.removeAllViews();

        for (int i = 0; i < combined.size(); i++) {
            Object item = combined.get(i);
            if (item instanceof UserComplaint) {
                reportItemsContainer.addView(buildComplaintRow((UserComplaint) item));
            } else if (item instanceof EmergencyListResponse) {
                reportItemsContainer.addView(buildEmergencyRow((EmergencyListResponse) item));
            }
            if (i < combined.size() - 1) addDivider();
        }
    }

    private String getDateString(Object item) {
        if (item instanceof UserComplaint) {
            String date = ((UserComplaint) item).getComplaintDate();
            return date != null ? date : "";
        } else if (item instanceof EmergencyListResponse) {
            String date = ((EmergencyListResponse) item).getCreatedAt();
            return date != null ? date : "";
        }
        return "";
    }

    private View buildComplaintRow(UserComplaint r) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setPadding(0, dpToPx(8), 0, dpToPx(8));
        row.setOnClickListener(v ->
                startActivity(new Intent(this, StatusReport.class)));

        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        left.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView queue = new TextView(this);
        queue.setText(r.getQueueNumber());
        String urgency = r.getUrgencyLevel() != null ? r.getUrgencyLevel().toLowerCase() : "nominal";
        if (urgency.equals("emergency") || urgency.equals("critical")) {
            queue.setTextColor(Color.parseColor("#E53935"));
        } else if (urgency.equals("priority") || urgency.equals("medium")) {
            queue.setTextColor(Color.parseColor("#FB8C00"));
        } else {
            queue.setTextColor(Color.parseColor("#43A047"));
        }
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

        TextView status = buildStatusBadge(r.getFormattedStatus(), r.getComplaintStatus());

        row.addView(left);
        row.addView(status);
        return row;
    }

    private View buildEmergencyRow(EmergencyListResponse e) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setPadding(0, dpToPx(8), 0, dpToPx(8));

        LinearLayout left = new LinearLayout(this);
        left.setOrientation(LinearLayout.VERTICAL);
        left.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView queue = new TextView(this);
        queue.setText("#E" + String.format("%03d", e.getEmergencyId()));
        queue.setTextColor(Color.parseColor("#E53935"));
        queue.setTextSize(14);
        queue.setTypeface(null, Typeface.BOLD);

        TextView type = new TextView(this);
        type.setText("Emergency Alert • " + e.getLocation());
        type.setTextColor(0xFF555555);
        type.setTextSize(12);

        TextView date = new TextView(this);
        String createdAt = e.getCreatedAt() != null && e.getCreatedAt().length() >= 10
                ? e.getCreatedAt().substring(0, 10) : "";
        date.setText(createdAt);
        date.setTextColor(0xFF999999);
        date.setTextSize(11);

        left.addView(queue);
        left.addView(type);
        left.addView(date);

        TextView status = buildStatusBadge(
                e.getStatus() != null ? e.getStatus().toUpperCase() : "PENDING",
                e.getStatus()
        );

        row.addView(left);
        row.addView(status);
        return row;
    }

    private TextView buildStatusBadge(String displayText, String rawStatus) {
        TextView status = new TextView(this);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        statusParams.setMargins(dpToPx(8), 0, 0, 0);
        status.setLayoutParams(statusParams);
        status.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        status.setText(displayText);
        status.setTextColor(0xFFFFFFFF);
        status.setTextSize(11);
        status.setTypeface(null, Typeface.BOLD);

        String s = rawStatus != null ? rawStatus.toUpperCase() : "PENDING";
        switch (s) {
            case "REJECTED":
                status.setBackgroundResource(R.drawable.bg_status_rejected); break;
            case "APPROVED": case "SOLVED": case "FINISHED": case "RESOLVED":
                status.setBackgroundResource(R.drawable.bg_status_approve);  break;
            default:
                status.setBackgroundResource(R.drawable.bg_status_pending);  break;
        }
        return status;
    }

    private void addDivider() {
        View divider = new View(this);
        LinearLayout.LayoutParams dp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(1));
        dp.setMargins(0, dpToPx(8), 0, dpToPx(8));
        divider.setLayoutParams(dp);
        divider.setBackgroundColor(0xFFF0F0F0);
        reportItemsContainer.addView(divider);
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