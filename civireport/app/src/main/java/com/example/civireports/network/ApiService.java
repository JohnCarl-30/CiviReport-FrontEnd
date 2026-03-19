package com.example.civireports.network;

import com.example.civireports.models.ForgotPasswordRequest;
import com.example.civireports.models.LoginRequest;
import com.example.civireports.models.LoginResponse;
import com.example.civireports.models.MessageResponse;
import com.example.civireports.models.RegisterRequest;
import com.example.civireports.models.RegisterResponse;
import com.example.civireports.models.ResetPasswordRequest;
import com.example.civireports.models.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("auth/forgot-password")
    Call<MessageResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/reset-password")
    Call<MessageResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("auth/verify-otp") // adjust to your actual route
    Call<MessageResponse> verifyOtp(@Body VerifyOtpRequest request);
}