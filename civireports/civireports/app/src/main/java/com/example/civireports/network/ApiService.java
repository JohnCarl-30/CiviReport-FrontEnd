package com.example.civireports.network;
import com.example.civireports.models.LoginResponse;
import com.example.civireports.models.RegisterRequest;
import com.example.civireports.models.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import  retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("auth/login")
    Call<LoginResponse> login(
            @Field("username") String email,
            @Field("password") String password

    );

    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

}
