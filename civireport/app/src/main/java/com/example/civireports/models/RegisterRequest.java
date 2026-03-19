package com.example.civireports.models;

public class RegisterRequest {
    private String suffix;
    private String first_name;
    private String middle_name;
    private String last_name;
    private String email;
    private String contact_num;
    private String address;
    private String password;
    private String confirm_password;

    public RegisterRequest(String suffix, String first_name, String middle_name,
                           String last_name, String email, String contact_num,
                           String address, String password, String confirm_password) {
        this.suffix = suffix;
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.email = email;
        this.contact_num = contact_num;
        this.address = address;
        this.password = password;
        this.confirm_password = confirm_password;
    }
}