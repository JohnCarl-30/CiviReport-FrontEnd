package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {

    @SerializedName("complaint")
    private String complaint;

    @SerializedName("complaint_type")
    private String complaintType;

    @SerializedName("complaint_subtype")
    private String complaintSubtype;

    @SerializedName("complaint_location")
    private String complaintLocation;

    public ChatRequest(String complaint, String complaintType, String complaintSubtype, String complaintLocation) {
        this.complaint         = complaint;
        this.complaintType     = complaintType;
        this.complaintSubtype  = complaintSubtype;
        this.complaintLocation = complaintLocation;
    }
}