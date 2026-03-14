package com.example.civireports.models;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("message")
    private String message;


    public String getAccessToken(){return accessToken;}
    public String getTokenType(){return tokenType;}
    public String getMessage(){return message;}

}
