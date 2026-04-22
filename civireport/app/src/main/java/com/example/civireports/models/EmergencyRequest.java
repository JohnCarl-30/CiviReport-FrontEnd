package com.example.civireports.models;

public class EmergencyRequest {
    private final String location;
    private final String notes;
    private final String status;

    public EmergencyRequest(String location, String notes, String status) {
        this.location = location;
        this.notes = notes;
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }

    public String getStatus() {
        return status;
    }
}