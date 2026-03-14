package com.example.civireports.models;
import com.google.gson.annotations.SerializedName;
public class RegisterRequest {
    @SerializedName("first_name")
    private String first_name;

    @SerializedName("middle_name")
    private String middle_name;

    @SerializedName("last_name")
    private String last_name;

    @SerializedName("suffix")
    private String suffix;

    @SerializedName("email")
    private String email;

    @SerializedName("contact_num")
    private String contact_num;

    @SerializedName("address")
    private String address;

    @SerializedName("password")
    private String password;

    @SerializedName("confirm_password")
    private String confirm_password;

    public RegisterRequest(String first_name, String middle_name, String last_name,String suffix, String email, String contact_num, String address, String password, String confirm_password){
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.suffix = suffix;
        this.email = email;
        this.contact_num = contact_num;
        this.address = address;
        this.password = password;
        this.confirm_password = confirm_password;
    }
}
