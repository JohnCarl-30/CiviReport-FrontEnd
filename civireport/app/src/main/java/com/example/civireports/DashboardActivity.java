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
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.civireports.models.NotificationItem;
import com.example.civireports.models.PendingComplaints;
import com.example.civireports.models.UserProfileResponse;
import com.example.civireports.network.ApiService;
import com.example.civireports.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private static final String TAG = "DashboardActivity";

    private TextView tvTotalComplaints;
    private TextView tvEmergencyCount;
    private TextView tvPriorityCount;
    private TextView tvNominalCount;
    private TextView tvWelcome;

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

    private NotificationSocketManager notificationSocketManager;
    private TextView modalNotifCount;
    private LinearLayout modalNotifListContainer;
    private TextView modalEmptyNotif;

    private boolean isExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initViews();
        setupClickListeners();
        checkFirstLogin();
        fetchAndSaveUserProfile();
        initNotificationSocket();
        loadDashboardData();
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
        tvWelcome = findViewById(R.id.tvWelcome);

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

    private void loadDashboardData() {
        ApiService api = RetrofitClient.getApiService(this);
        api.getPendingComplaints().enqueue(new Callback<List<PendingComplaints>>() {
            @Override
            public void onResponse(Call<List<PendingComplaints>> call, Response<List<PendingComplaints>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PendingComplaints> complaints = response.body();

                    int emergency = 0, priority = 0, nominal = 0;
                    for (PendingComplaints c : complaints) {
                        switch (c.getUrgencyLevel().toLowerCase()) {
                            case "critical": emergency++; break;
                            case "medium":   priority++;  break;
                            default:         nominal++;   break;
                        }
                    }

                    tvTotalComplaints.setText(String.valueOf(complaints.size()));
                    tvEmergencyCount.setText(emergency + " Emergency");
                    tvPriorityCount.setText(priority + " Priority");
                    tvNominalCount.setText(nominal + " Nominal");

                    populateReportsList(complaints);
                }
            }

            @Override
            public void onFailure(Call<List<PendingComplaints>> call, Throwable t) {
                Toast.makeText(DashboardActivity.this, "Failed to load complaints", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateReportsList(List<PendingComplaints> complaints) {
        reportsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(DashboardActivity.this);

        for (PendingComplaints item : complaints) {
            View itemView = inflater.inflate(R.layout.item_dashboard_report, reportsContainer, false);

            LinearLayout container = itemView.findViewById(R.id.reportItemContainer);
            TextView tvQueue = itemView.findViewById(R.id.tvQueueNumber);
            TextView tvUrgency = itemView.findViewById(R.id.tvUrgencyLabel);

            tvQueue.setText("#" + String.format("%03d", item.getComplaintId()));
            
            String urgency = item.getUrgencyLevel().toLowerCase();
            if (urgency.equals("critical")) {
                tvUrgency.setText("Emergency");
                tvUrgency.setTextColor(Color.parseColor("#E53935"));
                tvQueue.setBackgroundResource(R.drawable.bg_badge_emergency);
                container.setBackgroundResource(R.drawable.bg_report_item_emergency);
            } else if (urgency.equals("medium")) {
                tvUrgency.setText("Priority");
                tvUrgency.setTextColor(Color.parseColor("#FB8C00"));
                tvQueue.setBackgroundResource(R.drawable.bg_badge_priority);
                container.setBackgroundResource(R.drawable.bg_report_item_priority);
            } else {
                tvUrgency.setText("Nominal");
                tvUrgency.setTextColor(Color.parseColor("#43A047"));
                tvQueue.setBackgroundResource(R.drawable.bg_badge_nominal);
                container.setBackgroundResource(R.drawable.bg_report_item_nominal);
            }

            reportsContainer.addView(itemView);
        }
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
            Intent intent = new Intent(this, hotlines.class);
            intent.putExtra("show_emergency_popup", true);
            startActivity(intent);
            Toast.makeText(this, "Emergency alert sent!", Toast.LENGTH_SHORT).show();
        });

        btnNotificationHeader.setOnClickListener(v -> showNotificationModal());

        navHome.setOnClickListener(v -> { });

        navHotlines.setOnClickListener(v ->
                startActivity(new Intent(this, hotlines.class)));

        navNotification.setOnClickListener(v ->
                startActivity(new Intent(this, AnnouncementActivity.class)));

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

        modalNotifCount = dialogView.findViewById(R.id.tv_notif_count);
        modalNotifListContainer = dialogView.findViewById(R.id.notif_list_container);
        modalEmptyNotif = dialogView.findViewById(R.id.tv_empty_notif);

        ImageView btnClose = dialogView.findViewById(R.id.btn_close_notif);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setOnDismissListener(d -> {
            modalNotifCount = null;
            modalNotifListContainer = null;
            modalEmptyNotif = null;
        });

        refreshModalNotifications();

        dialog.show();
    }

    private void showNotificationDialog(NotificationItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_notification_modal, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        LinearLayout container = dialogView.findViewById(R.id.notif_list_container);
        TextView tvNotifCount = dialogView.findViewById(R.id.tv_notif_count);
        TextView tvEmptyNotif = dialogView.findViewById(R.id.tv_empty_notif);

        container.removeAllViews();
        tvEmptyNotif.setVisibility(View.GONE);
        tvNotifCount.setVisibility(View.GONE);

        View itemView = inflater.inflate(R.layout.item_notification, container, false);
        TextView tvBadge = itemView.findViewById(R.id.tvNotifBadge);
        TextView tvTitle = itemView.findViewById(R.id.tvNotifTitle);
        TextView tvDesc = itemView.findViewById(R.id.tvNotifDescription);
        TextView tvDate = itemView.findViewById(R.id.tvNotifDate);

        tvBadge.setText(item.getModalBadgeText());
        tvTitle.setText(item.getDisplayText());
        tvDesc.setVisibility(View.VISIBLE);
        tvDesc.setText(item.getAnnouncementText());
        tvDate.setText(item.getRelativeTime());

        container.addView(itemView);

        ImageView btnClose = dialogView.findViewById(R.id.btn_close_notif);
        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void initNotificationSocket() {
        notificationSocketManager = new NotificationSocketManager(this, new NotificationSocketManager.OnNotificationReceivedListener() {
            @Override
            public void onNotificationAdded(NotificationItem item, List<NotificationItem> notifications) {
                refreshModalNotifications();

                if (item.shouldShowInModal()) {
                    showNotificationDialog(item);
                }
            }

            @Override
            public void onSocketStateChanged(String state) {
                Log.d(TAG, "Notification socket state: " + state);
            }
        });

        int userId = getSharedPreferences("auth", MODE_PRIVATE).getInt("user_id", -1);
        if (userId > 0) {
            notificationSocketManager.connectNotifSocket(userId);
        } else {
            Log.w(TAG, "user_id missing in auth preferences. Websocket notifications disabled.");
        }
    }

    private void refreshModalNotifications() {
        if (modalNotifCount == null || modalNotifListContainer == null || modalEmptyNotif == null) {
            return;
        }

        List<NotificationItem> allNotifications = notificationSocketManager != null
                ? notificationSocketManager.getNotifList()
                : java.util.Collections.emptyList();

        List<NotificationItem> notifications = new ArrayList<>();
        for (NotificationItem item : allNotifications) {
            if (item.shouldShowInModal()) {
                notifications.add(item);
            }
        }

        boolean hasNewNotification = !notifications.isEmpty();
        modalNotifCount.setVisibility(hasNewNotification ? View.VISIBLE : View.GONE);
        modalNotifCount.setText(hasNewNotification ? notifications.size() + " new" : "0");

        modalNotifListContainer.removeAllViews();
        if (!hasNewNotification) {
            modalEmptyNotif.setText("No in-progress or approved complaint updates yet");
            modalEmptyNotif.setVisibility(View.VISIBLE);
            modalNotifListContainer.addView(modalEmptyNotif);
            return;
        }

        modalEmptyNotif.setVisibility(View.GONE);
        for (NotificationItem item : notifications) {
            View itemView = getLayoutInflater().inflate(R.layout.item_notification, modalNotifListContainer, false);
            TextView tvBadge = itemView.findViewById(R.id.tvNotifBadge);
            TextView tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            TextView tvDesc = itemView.findViewById(R.id.tvNotifDescription);
            TextView tvDate = itemView.findViewById(R.id.tvNotifDate);

            tvBadge.setText(item.getModalBadgeText());
            tvTitle.setText(item.getDisplayText());
            tvDesc.setVisibility(View.VISIBLE);
            tvDesc.setText(item.getAnnouncementText());
            tvDate.setText(item.getRelativeTime());
            modalNotifListContainer.addView(itemView);
        }
    }

    @Override
    protected void onDestroy() {
        if (notificationSocketManager != null) {
            notificationSocketManager.closeNotifSocket();
        }
        super.onDestroy();
    }

    private void fetchAndSaveUserProfile() {
        ApiService api = RetrofitClient.getApiService(this);
        api.getMyProfile().enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserProfileResponse profile = response.body();

                    // Save to SharedPreferences
                    getSharedPreferences("UserProfile", MODE_PRIVATE)
                            .edit()
                            .putString("full_name", profile.getFullName())
                            .putString("email", profile.getEmail())
                            .putString("contact", profile.getContactNum())
                            .putString("address", profile.getAddress())
                            .apply();


                    tvWelcome.setText("Welcome, " + profile.getFullName() + "!");
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                // fallback sa SharedPreferences kung may naka-save na
                String savedName = getSharedPreferences("UserProfile", MODE_PRIVATE)
                        .getString("full_name", "");
                if (!savedName.isEmpty() && tvWelcome != null) {
                    tvWelcome.setText("Welcome, " + savedName + "!");
                }
            }
        });
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
}