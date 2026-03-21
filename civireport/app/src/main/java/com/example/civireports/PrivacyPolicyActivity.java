package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));
        findViewById(R.id.navHotlines).setOnClickListener(v ->
                startActivity(new Intent(this, hotlines.class)));
        findViewById(R.id.navNotification).setOnClickListener(v ->
                startActivity(new Intent(this, Notification.class)));
        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));
    }
}