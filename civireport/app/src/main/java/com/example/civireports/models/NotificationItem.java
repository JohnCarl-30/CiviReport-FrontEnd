package com.example.civireports.models;

import java.util.Locale;

public class NotificationItem {
    public final int complaintId;
    public final String status;
    public final String complaintType;
    public final long receivedAtMillis;

    public NotificationItem(int complaintId, String status, String complaintType) {
        this(complaintId, status, complaintType, System.currentTimeMillis());
    }

    public NotificationItem(int complaintId, String status, String complaintType, long receivedAtMillis) {
        this.complaintId = complaintId;
        this.status = status;
        this.complaintType = complaintType;
        this.receivedAtMillis = receivedAtMillis;
    }

    public String getDisplayText() {
        return "Complaint #" + complaintId + " is now " + getStatusLabel() + " (" + complaintType + ")";
    }

    public boolean isInProgress() {
        String normalized = status == null ? "" : status.trim().toLowerCase(Locale.US);
        return normalized.equals("in_progress") || normalized.equals("in progress");
    }

    public boolean isApproved() {
        String normalized = status == null ? "" : status.trim().toLowerCase(Locale.US);
        return normalized.equals("approved");
    }

    public boolean shouldShowInModal() {
        return isInProgress() || isApproved();
    }

    public String getModalBadgeText() {
        if (isApproved()) {
            return "Approved";
        }
        if (isInProgress()) {
            return "In Progress";
        }
        return "Status Update";
    }

    public String getAnnouncementText() {
        if (isApproved()) {
            return "Announcement: Complaint #" + complaintId + " (" + complaintType + ") has been approved.";
        }
        if (isInProgress()) {
            return "Announcement: Complaint #" + complaintId + " (" + complaintType + ") is now in progress.";
        }
        return getDisplayText();
    }

    public String getStatusLabel() {
        if (isApproved()) {
            return "Approved";
        }
        if (isInProgress()) {
            return "In Progress";
        }
        if (status == null || status.trim().isEmpty()) {
            return "Updated";
        }
        String normalized = status.replace('_', ' ').trim().toLowerCase(Locale.US);
        String[] words = normalized.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return builder.toString();
    }

    public String getRelativeTime() {
        long diffMillis = Math.max(0L, System.currentTimeMillis() - receivedAtMillis);
        long diffMinutes = diffMillis / 60000L;
        if (diffMinutes < 1L) {
            return "Just now";
        }
        if (diffMinutes < 60L) {
            return diffMinutes + " minute" + (diffMinutes == 1L ? "" : "s") + " ago";
        }
        long diffHours = diffMinutes / 60L;
        if (diffHours < 24L) {
            return diffHours + " hour" + (diffHours == 1L ? "" : "s") + " ago";
        }
        long diffDays = diffHours / 24L;
        return diffDays + " day" + (diffDays == 1L ? "" : "s") + " ago";
    }
}
