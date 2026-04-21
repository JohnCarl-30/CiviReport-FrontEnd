package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChatResponse {

    @SerializedName("detected_category")
    private String detectedCategory;

    @SerializedName("urgency")
    private String urgency;

    @SerializedName("recommended_office")
    private String recommendedOffice;

    @SerializedName("reply_bullets")
    private List<String> replyBullets;

    @SerializedName("suggested_actions")
    private List<String> suggestedActions;

    @SerializedName("message")
    private String message;

    public String getDetectedCategory()       { return detectedCategory; }
    public String getUrgency()                { return urgency; }
    public String getRecommendedOffice()      { return recommendedOffice; }
    public List<String> getReplyBullets()     { return replyBullets; }
    public List<String> getSuggestedActions() { return suggestedActions; }
    public String getMessage()                { return message; }
}