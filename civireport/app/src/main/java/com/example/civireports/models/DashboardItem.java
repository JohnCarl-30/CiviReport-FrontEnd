package com.example.civireports.models;

public class DashboardItem {
    private final int id;
    private final String displayId;
    private final String title;
    private final String urgencyLevel; // "critical", "medium", "nominal"
    private final String type; // "complaint" or "emergency"
    private final String timeAgo;

    public DashboardItem(int id, String displayId, String title, String urgencyLevel, String type, String timeAgo) {
        this.id = id;
        this.displayId = displayId;
        this.title = title;
        this.urgencyLevel = urgencyLevel;
        this.type = type;
        this.timeAgo = timeAgo;
    }

    public int getId()             { return id; }
    public String getDisplayId()   { return displayId; }
    public String getTitle()       { return title; }
    public String getUrgencyLevel(){ return urgencyLevel; }
    public String getType()        { return type; }
    public String getTimeAgo()     { return timeAgo; }
}