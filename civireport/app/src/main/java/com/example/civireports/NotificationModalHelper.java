package com.example.civireports;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.civireports.models.NotificationItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class NotificationModalHelper {

    private static final String PREFS_NAME = "notif_prefs";
    private static final String PREFS_KEY_ITEMS = "items";

    private NotificationModalHelper() {
    }

    public static void show(AppCompatActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_notification_modal, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView modalNotifCount = dialogView.findViewById(R.id.tv_notif_count);
        LinearLayout modalNotifListContainer = dialogView.findViewById(R.id.notif_list_container);
        TextView modalEmptyNotif = dialogView.findViewById(R.id.tv_empty_notif);
        ImageView btnClose = dialogView.findViewById(R.id.btn_close_notif);

        populateNotifications(activity, inflater, modalNotifCount, modalNotifListContainer, modalEmptyNotif);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private static void populateNotifications(AppCompatActivity activity,
                                              LayoutInflater inflater,
                                              TextView modalNotifCount,
                                              LinearLayout modalNotifListContainer,
                                              TextView modalEmptyNotif) {
        List<NotificationItem> notifications = loadPersistedNotifications(activity);

        modalNotifCount.setVisibility(notifications.isEmpty() ? View.GONE : View.VISIBLE);
        modalNotifCount.setText(notifications.isEmpty() ? "0" : notifications.size() + " new");

        modalNotifListContainer.removeAllViews();
        if (notifications.isEmpty()) {
            modalEmptyNotif.setText("No in-progress or approved complaint updates yet");
            modalEmptyNotif.setVisibility(View.VISIBLE);
            modalNotifListContainer.addView(modalEmptyNotif);
            return;
        }

        modalEmptyNotif.setVisibility(View.GONE);
        for (NotificationItem item : notifications) {
            View itemView = inflater.inflate(R.layout.item_notification, modalNotifListContainer, false);
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

    private static List<NotificationItem> loadPersistedNotifications(AppCompatActivity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, AppCompatActivity.MODE_PRIVATE);
        String raw = prefs.getString(PREFS_KEY_ITEMS, "[]");
        List<NotificationItem> notifications = new ArrayList<>();

        try {
            JSONArray items = new JSONArray(raw);
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                int complaintId = obj.optInt("complaintId", -1);
                String status = obj.optString("status", "updated");
                String complaintType = obj.optString("complaintType", "Complaint");
                long receivedAtMillis = obj.optLong("receivedAtMillis", System.currentTimeMillis());
                if (complaintId > 0) {
                    NotificationItem item = new NotificationItem(complaintId, status, complaintType, receivedAtMillis);
                    if (item.shouldShowInModal()) {
                        notifications.add(item);
                    }
                }
            }
        } catch (Exception e) {
            // If persisted data is corrupt, fall back to an empty modal state.
            notifications.clear();
        }

        return notifications;
    }
}