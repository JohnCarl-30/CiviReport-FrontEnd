package com.example.civireports.network;
import com.example.civireports.models.LoginRequest;
import com.example.civireports.models.LoginResponse;
import com.example.civireports.models.RegisterRequest;
import com.example.civireports.models.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import  retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("user/")
    Call<RegisterResponse> register(@Body RegisterRequest request);

}
