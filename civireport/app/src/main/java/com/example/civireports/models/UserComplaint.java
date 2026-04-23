package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserComplaint {

    @SerializedName("complaint_id")
    private int complaintId;

    @SerializedName("complaint_type")
    private String complaintType;

    @SerializedName("complaint_subtype")
    private String complaintSubtype;

    @SerializedName("additional_notes")
    private String additionalNotes;

    @SerializedName("complaint_location")
    private String complaintLocation;

    @SerializedName("complaint_status")
    private String complaintStatus;

    @SerializedName("urgency_level")
    private String urgencyLevel;

    @SerializedName("complaint_date")
    private String complaintDate;

    @SerializedName("resolved_notes")
    private String adminNotes;

    @SerializedName("rejection_reason")
    private String rejectionReason;

    @SerializedName("media")
    private List<Media> media;

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

    @SerializedName("service_rating")
    private Integer service_rating;

    // Getters
    public int    getComplaintId()       { return complaintId; }
    public String getComplaintType()     { return complaintType; }
    public String getComplaintSubtype()  { return complaintSubtype; }
    public String getAdditionalNotes()   { return additionalNotes; }
    public String getComplaintLocation() { return complaintLocation; }
    public String getComplaintStatus()   { return complaintStatus; }
    public String getUrgencyLevel()      { return urgencyLevel != null ? urgencyLevel : "nominal"; }
    public String getComplaintDate()     { return complaintDate; }
    public String getAdminNotes()        { return adminNotes; }
    public String getRejectionReason()   { return rejectionReason; }
    public List<Media> getMedia()        { return media; }

    public String getAiDetectedCategory()  { return aiDetectedCategory; }
    public String getAiUrgency()           { return aiUrgency; }
    public String getAiRecommendedOffice() { return aiRecommendedOffice; }
    public String getAiReplyBullets()      { return aiReplyBullets; }
    public String getAiSuggestedActions()  { return aiSuggestedActions; }
    public String getAiMessage()           { return aiMessage; }
    public Integer getServiceRating()           { return service_rating; }

    // Display helpers
    public String getQueueNumber() {
        return "#" + String.format("%03d", complaintId);
    }

    public String getFormattedStatus() {
        if (complaintStatus == null) return "PENDING";
        String status = complaintStatus.toUpperCase();
        switch (status) {
            case "PROCESSING":
                return "IN PROGRESS";
            case "COMPLETED":
            case "SOLVED":
            case "FINISHED":
                return "RESOLVED";
            case "REJECTED":
                return "REJECTED";
            default:
                return status;
        }
    }

    public String getFirstImageUrl(String baseUrl) {
        if (media == null || media.isEmpty()) return null;
        for (Media m : media) {
            if ("image".equals(m.getMediaType())) {
                return baseUrl + m.getFilePath();
            }
        }
        return null;
    }

    public String getResolutionImageUrl(String baseUrl) {
        if (media == null || media.size() < 2) return null;
        return baseUrl + media.get(media.size() - 1).getFilePath();
    }

    // Media nested class
    public static class Media {
        @SerializedName("file_path")
        private String filePath;

        @SerializedName("media_type")
        private String mediaType;

        public String getFilePath()  { return filePath; }
        public String getMediaType() { return mediaType; }
    }
}