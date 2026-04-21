package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class PendingComplaints {

    @SerializedName("complaint_id")
    private int complaintId;

    @SerializedName("complaint_type")
    private String complaintType;

    @SerializedName("urgency_level")
    private String urgencyLevel;

    @SerializedName("complaint_date")
    private String complaintDate;

    @SerializedName("complaint_status")
    private String complaintStatus;

    public int getComplaintId() { return complaintId; }
    public String getComplaintType() { return complaintType; }
    public String getUrgencyLevel() { return urgencyLevel; }
    public String getComplaintDate() { return complaintDate; }
    public String getComplaintStatus() { return complaintStatus; }
}