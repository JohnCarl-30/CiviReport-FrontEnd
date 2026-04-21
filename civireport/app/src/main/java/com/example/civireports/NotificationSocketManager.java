package com.example.civireports;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.civireports.models.NotificationItem;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class NotificationSocketManager {

    private static final String TAG = "NotifSocket";
    private static final long RECONNECT_DELAY_MS = 3000L;
    private static final String PREFS_NAME = "notif_prefs";
    private static final String PREFS_KEY_ITEMS = "items";

    private WebSocket webSocket;
    private final OkHttpClient wsClient = new OkHttpClient();
    private final List<NotificationItem> notifList = new ArrayList<>();
    private final Activity activity;
    private final OnNotificationReceivedListener listener;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private boolean shouldReconnect = false;
    private boolean isConnected = false;
    private int currentUserId = -1;

    public interface OnNotificationReceivedListener {
        void onNotificationAdded(NotificationItem item, List<NotificationItem> notifications);

        default void onSocketStateChanged(String state) {}
    }

    public NotificationSocketManager(Activity activity, OnNotificationReceivedListener listener) {
        this.activity = activity;
        this.listener = listener;
        loadPersistedNotifications();
    }

    public void connectNotifSocket(int userId) {
        if (isConnected || webSocket != null) {
            Log.d(TAG, "connectNotifSocket skipped: already connected/connecting");
            return;
        }

        currentUserId = userId;
        shouldReconnect = true;

        // Android emulator -> use 10.0.2.2 instead of localhost
        String wsUrl = "ws://10.0.2.2:8000/complaints/ws/status-updates?user_id=" + userId;
        Log.d(TAG, "Connecting websocket: " + wsUrl);

        Request request = new Request.Builder().url(wsUrl).build();
        webSocket = wsClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                isConnected = true;
                postState("OPEN");
                Log.d(TAG, "onOpen code=" + response.code());
                // optional keepalive text because backend loop waits for receive_text()
                webSocket.send("hello");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "onMessage: " + text);
                try {
                    JSONObject obj = new JSONObject(text);
                    int complaintId = obj.optInt("complaint_id", -1);
                    String status = obj.optString("status", "updated");
                    String type = obj.optString("complaint_type", "Complaint");

                    NotificationItem item = new NotificationItem(complaintId, status, type);

                    activity.runOnUiThread(() -> {
                        addOrUpdateNotification(item);
                        persistNotifications();
                        if (listener != null) {
                            listener.onNotificationAdded(item, new ArrayList<>(notifList));
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse websocket message", e);
                }
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.w(TAG, "onClosing code=" + code + " reason=" + reason);
                postState("CLOSING");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.w(TAG, "onClosed code=" + code + " reason=" + reason);
                isConnected = false;
                NotificationSocketManager.this.webSocket = null;
                postState("CLOSED");
                tryReconnect();
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                isConnected = false;
                NotificationSocketManager.this.webSocket = null;
                postState("FAILED");
                String code = response != null ? String.valueOf(response.code()) : "none";
                Log.e(TAG, "onFailure responseCode=" + code, t);
                tryReconnect();
            }
        });
    }

    public void closeNotifSocket() {
        shouldReconnect = false;
        if (webSocket != null) {
            webSocket.close(1000, "UI closed");
            webSocket = null;
        }
        isConnected = false;
    }

    public List<NotificationItem> getNotifList() {
        return new ArrayList<>(notifList);
    }

    private void addOrUpdateNotification(NotificationItem incoming) {
        for (int i = 0; i < notifList.size(); i++) {
            NotificationItem existing = notifList.get(i);
            if (existing.complaintId == incoming.complaintId) {
                notifList.remove(i);
                notifList.add(0, incoming);
                return;
            }
        }
        notifList.add(0, incoming);
    }

    private void persistNotifications() {
        JSONArray items = new JSONArray();
        for (NotificationItem item : notifList) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("complaintId", item.complaintId);
                obj.put("status", item.status);
                obj.put("complaintType", item.complaintType);
                obj.put("receivedAtMillis", item.receivedAtMillis);
                items.put(obj);
            } catch (Exception e) {
                Log.e(TAG, "Failed to serialize notification item", e);
            }
        }

        activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)
                .edit()
                .putString(PREFS_KEY_ITEMS, items.toString())
                .apply();
    }

    private void loadPersistedNotifications() {
        String raw = activity.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)
                .getString(PREFS_KEY_ITEMS, "[]");
        notifList.clear();

        try {
            JSONArray items = new JSONArray(raw);
            for (int i = 0; i < items.length(); i++) {
                JSONObject obj = items.getJSONObject(i);
                int complaintId = obj.optInt("complaintId", -1);
                String status = obj.optString("status", "updated");
                String complaintType = obj.optString("complaintType", "Complaint");
                long receivedAtMillis = obj.optLong("receivedAtMillis", System.currentTimeMillis());
                if (complaintId > 0) {
                    notifList.add(new NotificationItem(complaintId, status, complaintType, receivedAtMillis));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse persisted notifications", e);
        }
    }

    private void tryReconnect() {
        if (!shouldReconnect || currentUserId <= 0) {
            return;
        }

        mainHandler.postDelayed(() -> {
            if (webSocket == null && !isConnected) {
                Log.d(TAG, "Attempting websocket reconnect...");
                connectNotifSocket(currentUserId);
            }
        }, RECONNECT_DELAY_MS);
    }

    private void postState(String state) {
        activity.runOnUiThread(() -> {
            if (listener != null) {
                listener.onSocketStateChanged(state);
            }
        });
    }
}
