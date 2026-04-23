package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class ComplaintStatusUpdate {

    @SerializedName("status")
    private String status;

    @SerializedName("rejection_reason")
    private String feedback;

    public ComplaintStatusUpdate(String status, String feedback) {
        this.status   = status;
        this.feedback = feedback;
    }

    public ComplaintStatusUpdate(String status) {
        this.status   = status;
        this.feedback = null;
    }
}