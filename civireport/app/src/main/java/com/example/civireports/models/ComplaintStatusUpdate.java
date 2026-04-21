package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class ComplaintStatusUpdate {

    @SerializedName("action")
    private String action;

    @SerializedName("feedback")
    private String feedback;

    public ComplaintStatusUpdate(String action, String feedback) {
        this.action   = action;
        this.feedback = feedback;
    }

    public ComplaintStatusUpdate(String action) {
        this.action   = action;
        this.feedback = null;
    }
}