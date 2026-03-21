package com.example.civireports.network;

import com.example.civireports.models.ComplaintRequest;
import com.example.civireports.models.ComplaintResponse;
import com.example.civireports.models.ForgotPasswordRequest;
import com.example.civireports.models.LoginRequest;
import com.example.civireports.models.LoginResponse;
import com.example.civireports.models.MessageResponse;
import com.example.civireports.models.RegisterRequest;
import com.example.civireports.models.RegisterResponse;
import com.example.civireports.models.ResetPasswordRequest;
import com.example.civireports.models.VerifyOtpRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> login(
            @Field("username") String email,  // "username" — required ng OAuth2
            @Field("password") String password
    );

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    @POST("auth/forgot-password")
    Call<MessageResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth/reset-password")
    Call<MessageResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("auth/verify-otp") // adjust to your actual route
    Call<MessageResponse> verifyOtp(@Body VerifyOtpRequest request);

    @Multipart
    @POST("complaints/complaint-form")
    Call<ComplaintResponse> submitComplaint(
                                            @Part("complaint_type") RequestBody complaintType,
                                            @Part("complaint_subtype") RequestBody complaintSubtype,
                                            @Part("additional_notes") RequestBody additionalNotes,
                                            @Part("complaint_location") RequestBody complaint_location,
                                            @Part List<MultipartBody.Part> files
                                            );
}