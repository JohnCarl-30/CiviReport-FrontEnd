package com.example.civireports.models;

public class ComplaintRequest {
    private String user_id;
    private  String complaint_type;
    private String complaint_subtype;
    private String additional_notes;
    private  String complaint_location;

    public ComplaintRequest (String user_id, String complaint_type, String complaint_subtype,
                             String additional_notes, String complaint_location){

        this.user_id = user_id;
        this.complaint_type = complaint_type;
        this.complaint_subtype = complaint_subtype;
        this.additional_notes = additional_notes;
        this.complaint_location = complaint_location;
    }

}
