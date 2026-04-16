package com.example.civireports.models;

public class EditProfileRequest {
    private String full_name;
    private String email;
    private String contact_num;
    private String address;

    public EditProfileRequest(String full_name, String email, String contact_num, String address) {
        this.full_name = full_name;
        this.email = email;
        this.contact_num = contact_num;
        this.address = address;
    }
}