package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class RatingRequest {

    @SerializedName("service_rating")
    private int serviceRating;

    @SerializedName("service_comment")
    private String serviceComment;

    public RatingRequest(int serviceRating, String serviceComment) {
        this.serviceRating = serviceRating;
        this.serviceComment = serviceComment;
    }

    public RatingRequest(int serviceRating) {
        this.serviceRating = serviceRating;
        this.serviceComment = null;
    }
}