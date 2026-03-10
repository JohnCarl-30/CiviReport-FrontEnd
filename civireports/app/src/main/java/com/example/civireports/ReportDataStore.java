package com.example.civireports;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple singleton class to temporarily store multiple report data 
 * across different screens while the backend is not yet connected.
 */
public class ReportDataStore {
    private static ReportDataStore instance;
    private final List<ReportItem> reports = new ArrayList<>();

    private ReportDataStore() {}

    public static synchronized ReportDataStore getInstance() {
        if (instance == null) {
            instance = new ReportDataStore();
        }
        return instance;
    }

    public void addReport(ReportItem report) {
        reports.add(0, report); // Add new reports to the top
    }

    public List<ReportItem> getAllReports() {
        return reports;
    }

    public boolean hasReports() {
        return !reports.isEmpty();
    }

    /**
     * Data model for a single report item.
     */
    public static class ReportItem {
        private final String queueNumber;
        private final String status;
        private final String complaintType;
        private final String specificIssue;
        private final String address;
        private final String notes;
        private final String date;
        private final Uri imageUri;

        public ReportItem(String queueNumber, String status, String complaintType, 
                          String specificIssue, String address, String notes, String date, Uri imageUri) {
            this.queueNumber = queueNumber;
            this.status = status;
            this.complaintType = complaintType;
            this.specificIssue = specificIssue;
            this.address = address;
            this.notes = notes;
            this.date = date;
            this.imageUri = imageUri;
        }

        public String getQueueNumber() { return queueNumber; }
        public String getStatus() { return status; }
        public String getComplaintType() { return complaintType; }
        public String getSpecificIssue() { return specificIssue; }
        public String getAddress() { return address; }
        public String getNotes() { return notes; }
        public String getDate() { return date; }
        public Uri getImageUri() { return imageUri; }
    }
}