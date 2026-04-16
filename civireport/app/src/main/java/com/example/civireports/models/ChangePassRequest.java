package com.example.civireports.models;

public class ChangePassRequest {
    private String current_password;
    private String new_password;
    private String confirm_new_password;

    public ChangePassRequest(String current_password, String new_password, String confirm_new_password) {
        this.current_password = current_password;
        this.new_password = new_password;
        this.confirm_new_password = confirm_new_password;
    }
}
