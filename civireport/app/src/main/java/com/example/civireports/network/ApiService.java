package com.example.civireports.network;

import com.example.civireports.models.AiRecommendationResponse;
import com.example.civireports.models.ChangePassRequest;
import com.example.civireports.models.ChangePassResponse;
import com.example.civireports.models.ChatRequest;
import com.example.civireports.models.ChatResponse;
import com.example.civireports.models.ComplaintRequest;
import com.example.civireports.models.ComplaintResponse;
import com.example.civireports.models.ComplaintStatusUpdate;
import com.example.civireports.models.EditProfileRequest;
import com.example.civireports.models.EditProfileResponse;
import com.example.civireports.models.ForgotPasswordRequest;
import com.example.civireports.models.LoginRequest;
import com.example.civireports.models.LoginResponse;
import com.example.civireports.models.MessageResponse;
import com.example.civireports.models.PendingComplaints;
import com.example.civireports.models.RegisterRequest;
import com.example.civireports.models.RegisterResponse;
import com.example.civireports.models.ResetPasswordRequest;
import com.example.civireports.models.UserProfileResponse;
import com.example.civireports.models.VerifyOtpRequest;
import com.example.civireports.models.UserComplaint;
import com.example.civireports.models.Announcement;
import com.example.civireports.models.EmergencyRequest;
import com.example.civireports.models.EmergencyResponse;

import retrofit2.http.GET;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("complaints/my-complaints")
    Call<List<UserComplaint>> getMyComplaints();
    @PUT("auth/profile")
    Call<EditProfileResponse> updateProfile(@Body EditProfileRequest request);

    @PUT("auth/change-password")
    Call<ChangePassResponse> changePassword(@Body ChangePassRequest request);

    @Multipart
    @PUT("auth/upload-profile-picture")
    Call<MessageResponse> uploadProfilePicture(
            @Part MultipartBody.Part file
    );

    @GET("complaints/pending")
    Call<List<PendingComplaints>> getPendingComplaints();

    @GET("auth/announcements")
    Call<List<Announcement>> getAnnouncements();

    @GET("auth/me")
    Call<UserProfileResponse> getMyProfile();

    @POST("chat")
    Call<ChatResponse> getAiRecommendation(@Body ChatRequest request);
<<<<<<< Updated upstream
=======

    @POST("emergencies")
    Call<EmergencyResponse> createEmergency(@Body EmergencyRequest request);

>>>>>>> Stashed changes
    @POST("complaints/{complaint_id}/ai-recommendation")
    Call<AiRecommendationResponse> generateAiRecommendation(
            @retrofit2.http.Path("complaint_id") int complaintId
    );

    @POST("complaints/{complaint_id}/resolve")
    Call<MessageResponse> resolveComplaint(
            @retrofit2.http.Path("complaint_id") int complaintId,
            @Body ComplaintStatusUpdate payload
    );
}