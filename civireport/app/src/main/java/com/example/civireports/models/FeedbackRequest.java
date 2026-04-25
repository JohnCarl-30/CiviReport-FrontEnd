package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class FeedbackRequest {

    @SerializedName("service_feedback")
    private String serviceFeedback;

    public FeedbackRequest(String serviceFeedback) {
        this.serviceFeedback = serviceFeedback;
    }
}