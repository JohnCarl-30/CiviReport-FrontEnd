package com.example.civireports.models;

import com.google.gson.annotations.SerializedName;

public class UserProfileResponse {

    @SerializedName("user_id")
    private int userId;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("email")
    private String email;

    @SerializedName("contact_num")
    private String contactNum;

    @SerializedName("address")
    private String address;

    public int getUserId()       { return userId; }
    public String getFullName()  { return fullName; }
    public String getEmail()     { return email; }
    public String getContactNum(){ return contactNum; }
    public String getAddress()   { return address; }
}