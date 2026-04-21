package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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
                R.drawable.ic_feature_report,
                "Issue Reporting with Photos",
                "Submit issue reports with photo and video documentation");

        setFeature(R.id.feature2,
                R.drawable.ic_feature_status,
                "Real-Time status tracking",
                "Pending, in-progress, resolved");

        setFeature(R.id.feature3,
                R.drawable.ic_feature_ai,
                "AI-powered guidance",
                "Safety tips and troubleshooting");

        setFeature(R.id.feature4,
                R.drawable.ic_feature_notif,
                "Push notifications",
                "Updates you on your report status");

        setFeature(R.id.feature5,
                R.drawable.ic_feature_call,
                "Hotlines at your fingertips",
                "Reach emergency services in one tap");
    }

    private void setFeature(int viewId, int iconRes, String title, String desc) {
        android.view.View row = findViewById(viewId);
        if (row != null) {
            ImageView iconView = row.findViewById(R.id.featureIcon);
            TextView titleView = row.findViewById(R.id.featureTitle);
            TextView descView = row.findViewById(R.id.featureDesc);

            if (iconView != null) iconView.setImageResource(iconRes);
            if (titleView != null) titleView.setText(title);
            if (descView != null) descView.setText(desc);
        }
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));
        findViewById(R.id.navHotlines).setOnClickListener(v ->
                startActivity(new Intent(this, hotlines.class)));
        findViewById(R.id.navNotification).setOnClickListener(v ->
                startActivity(new Intent(this, AnnouncementActivity.class)));
        findViewById(R.id.navProfile).setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));
    }
}
