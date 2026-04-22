package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class Announcement {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("venue")
    private String venue;

    @SerializedName("post_date")
    private String postDate;

    @SerializedName("event_date")
    private String eventDate;

    @SerializedName("who_will_attend")
    private String whoWillAttend;

    @SerializedName("category")
    private String category;


    public int getId()               { return id; }
    public String getTitle()         { return title; }
    public String getDescription()   { return description; }
    public String getVenue()         { return venue; }
    public String getPostDate()      { return postDate; }
    public String getEventDate()     { return eventDate; }
    public String getWhoWillAttend() { return whoWillAttend; }

    public String getCategory() { return category != null ? category : ""; }
}