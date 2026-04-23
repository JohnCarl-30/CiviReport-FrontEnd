package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class RatingRequest {

    @SerializedName("service_rating")
    private int serviceRating;

    public RatingRequest(int serviceRating) {
        this.serviceRating = serviceRating;
    }
}