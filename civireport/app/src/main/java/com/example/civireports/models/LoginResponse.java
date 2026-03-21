package com.example.civireports.models;




public class LoginResponse {
    public String access_token;
    public String token_type;
    public String message;
    public int user_id;

    public String getAccessToken() { return access_token; }
    public String getMessage() { return message; }
    public int getUserId() { return user_id; }
}
