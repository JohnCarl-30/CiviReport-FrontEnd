package com.example.civireports;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HelpSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Tap email → open mail app
        findViewById(R.id.btnEmail).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:civireport@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Help & Support");
            try {
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show();
            }
        });

        // Tap Rate our Service → open Google Forms link
        findViewById(R.id.btnRateService).setOnClickListener(v -> {
            String url = "https://forms.gle/example"; // Replace with your actual Google Form URL
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        });

        setupBottomNav();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
                startActivity(new Intent(this, DashboardActivity.class));
                overridePendingTransition(0, 0);
        });
        findViewById(R.id.navHotlines).setOnClickListener(v -> {
                startActivity(new Intent(this, hotlines.class));
                overridePendingTransition(0, 0);
        });
        findViewById(R.id.navNotification).setOnClickListener(v -> {
                startActivity(new Intent(this, AnnouncementActivity.class));
                overridePendingTransition(0, 0);
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
                startActivity(new Intent(this, Profile.class));
                overridePendingTransition(0, 0);
        });
    }
}
