package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class EmergencyResponse {

    @SerializedName("emergency_id")
    private int emergencyId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("location")
    private String location;

    @SerializedName("status")
    private String status;

    @SerializedName("notes")
    private String notes;

    @SerializedName("message")
    private String message;

    public int getEmergencyId() {
        return emergencyId;
    }

    public int getUserId() {
        return userId;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public String getMessage() {
        return message;
    }
}