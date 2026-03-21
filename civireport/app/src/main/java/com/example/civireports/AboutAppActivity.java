package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutAppActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupFeatures();

        findViewById(R.id.btnPrivacyPolicy).setOnClickListener(v ->
                startActivity(new Intent(this, PrivacyPolicyActivity.class)));

        findViewById(R.id.btnTerms).setOnClickListener(v ->
                startActivity(new Intent(this, TermsServicesActivity.class)));

        setupBottomNav();
    }

    private void setupFeatures() {
        setFeature(R.id.feature1,
                android.R.drawable.ic_menu_report_image,
                "Issue Reporting with Photos",
                "Submit issue reports with photo and video documentation");

        setFeature(R.id.feature2,
                android.R.drawable.ic_menu_recent_history,
                "Real-Time status tracking",
                "Pending, in-progress, resolved");

        setFeature(R.id.feature3,
                android.R.drawable.ic_menu_help,
                "AI-powered guidance",
                "Safety tips and troubleshooting");

        setFeature(R.id.feature4,
                android.R.drawable.ic_dialog_info,
                "Push notifications",
                "Updates you on your report status");

        setFeature(R.id.feature5,
                android.R.drawable.ic_menu_call,
                "Hotlines at your fingertips",
                "Reach emergency services in one tap");
    }

    private void setFeature(int viewId, int iconRes, String title, String desc) {
        android.view.View row = findViewById(viewId);
        ((ImageView) row.findViewById(R.id.featureIcon)).setImageResource(iconRes);
        ((TextView)  row.findViewById(R.id.featureTitle)).setText(title);
        ((TextView)  row.findViewById(R.id.featureDesc)).setText(desc);
    }

    private void setupBottomNav() {
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