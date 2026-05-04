package com.example.civireports.models;

import java.util.Locale;

public class NotificationItem {
    public final String type;           // "complaint" or "announcement"
    public final int complaintId;
    public final String status;
    public final String complaintType;
    public final String title;
    public final String description;
    public final String eventDate;
    public final String venue;
    public final long receivedAtMillis;

    // Constructor for complaint notifications
    public NotificationItem(int complaintId, String status, String complaintType) {
        this("complaint", complaintId, status, complaintType, null, null, null, null, System.currentTimeMillis());
    }

    public NotificationItem(int complaintId, String status, String complaintType, long receivedAtMillis) {
        this("complaint", complaintId, status, complaintType, null, null, null, null, receivedAtMillis);
    }

    // Constructor for announcement notifications
    public NotificationItem(String title, String description, String eventDate, String venue) {
        this("announcement", -1, null, null, title, description, eventDate, venue, System.currentTimeMillis());
    }

    public NotificationItem(String type, int complaintId, String status, String complaintType,
                            String title, String description, String eventDate, String venue,
                            long receivedAtMillis) {
        this.type = type != null ? type : "complaint";
        this.complaintId = complaintId;
        this.status = status;
        this.complaintType = complaintType;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.venue = venue;
        this.receivedAtMillis = receivedAtMillis;
    }

    public boolean isAnnouncement() {
        return "announcement".equals(type);
    }

    public String getDisplayText() {
        if (isAnnouncement()) {
            return title != null ? title : "New Announcement";
        }
        return "Complaint #" + complaintId + " is now " + getStatusLabel() + " (" + complaintType + ")";
    }

    public boolean isInProgress() {
        if (isAnnouncement()) return false;
        String normalized = status == null ? "" : status.trim().toLowerCase(Locale.US);
        return normalized.equals("in_progress") || normalized.equals("in progress");
    }

    public boolean isApproved() {
        if (isAnnouncement()) return false;
        String normalized = status == null ? "" : status.trim().toLowerCase(Locale.US);
        return normalized.equals("approved");
    }

    public boolean shouldShowInModal() {
        if (isAnnouncement()) return true;
        return isInProgress() || isApproved();
    }

    public String getModalBadgeText() {
        if (isAnnouncement()) {
            return "Announcement";
        }
        if (isApproved()) {
            return "Approved";
        }
        if (isInProgress()) {
            return "In Progress";
        }
        return "Status Update";
    }

    public String getAnnouncementText() {
        if (isAnnouncement()) {
            StringBuilder sb = new StringBuilder();
            if (description != null && !description.isEmpty()) {
                sb.append(description);
            }
            if (eventDate != null && !eventDate.isEmpty()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append("Date: ").append(eventDate);
            }
            if (venue != null && !venue.isEmpty()) {
                if (sb.length() > 0) sb.append("\n");
                sb.append("Venue: ").append(venue);
            }
            return sb.length() > 0 ? sb.toString() : "New announcement posted";
        }
        if (isApproved()) {
            return "Announcement: Complaint #" + complaintId + " (" + complaintType + ") has been approved.";
        }
        if (isInProgress()) {
            return "Announcement: Complaint #" + complaintId + " (" + complaintType + ") is now in progress.";
        }
        return getDisplayText();
    }

    public String getStatusLabel() {
        if (isAnnouncement()) return "";
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
