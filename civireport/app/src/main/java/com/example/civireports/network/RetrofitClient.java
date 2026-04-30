package com.example.civireports.network;

import android.content.Context;
import com.example.civireports.BuildConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = BuildConfig.BASE_URL;

    public static ApiService getApiService(Context context) {

        // Lenient Gson
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        // Logging para makita ang actual server response sa Logcat
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String token = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
                            .getString("token", "");
                    Request request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiService.class);
    }
}