package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class DeleteAccountResponse {
    @SerializedName("message")
    private String message;

    public String getMessage() { return message; }
}