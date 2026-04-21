package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class AiRecommendationResponse {

    @SerializedName("ai_detected_category")
    private String aiDetectedCategory;

    @SerializedName("ai_urgency")
    private String aiUrgency;

    @SerializedName("ai_recommended_office")
    private String aiRecommendedOffice;

    @SerializedName("ai_reply_bullets")
    private String aiReplyBullets;

    @SerializedName("ai_suggested_actions")
    private String aiSuggestedActions;

    @SerializedName("ai_message")
    private String aiMessage;

    public String getAiDetectedCategory()  { return aiDetectedCategory; }
    public String getAiUrgency()           { return aiUrgency; }
    public String getAiRecommendedOffice() { return aiRecommendedOffice; }
    public String getAiReplyBullets()      { return aiReplyBullets; }
    public String getAiSuggestedActions()  { return aiSuggestedActions; }
    public String getAiMessage()           { return aiMessage; }
}