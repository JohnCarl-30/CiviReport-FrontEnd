package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class Profile extends AppCompatActivity {

    private LinearLayout navHome;
    private LinearLayout navHotlines;
    private LinearLayout navNotification;
    private LinearLayout navProfile;

    private TextView btnBack;
    private MaterialButton btnLogout;
    
    // Menu items
    private LinearLayout menuEditProfile;
    private LinearLayout menuSecurity;
    private LinearLayout menuLegal;
    private LinearLayout menuHelp;
    private LinearLayout menuAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        initViews();
        setupClickListeners();
        setupBottomNav();
    }

    private void initViews() {
        navHome         = findViewById(R.id.navHome);
        navHotlines     = findViewById(R.id.navHotlines);
        navNotification = findViewById(R.id.navNotification);
        navProfile      = findViewById(R.id.navProfile);
        btnLogout       = findViewById(R.id.btnLogout);
        btnBack         = findViewById(R.id.btnBack);
        
        menuEditProfile = findViewById(R.id.menuEditProfile);
        menuSecurity    = findViewById(R.id.menuSecurity);
        menuLegal       = findViewById(R.id.menuLegal);
        menuHelp        = findViewById(R.id.menuHelp);
        menuAbout       = findViewById(R.id.menuAbout);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        menuEditProfile.setOnClickListener(v -> Toast.makeText(this, "Edit Profile clicked", Toast.LENGTH_SHORT).show());
        menuSecurity.setOnClickListener(v -> Toast.makeText(this, "Security clicked", Toast.LENGTH_SHORT).show());
        menuLegal.setOnClickListener(v -> Toast.makeText(this, "Legal Information clicked", Toast.LENGTH_SHORT).show());
        menuHelp.setOnClickListener(v -> Toast.makeText(this, "Help & Support clicked", Toast.LENGTH_SHORT).show());
        menuAbout.setOnClickListener(v -> Toast.makeText(this, "About App clicked", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNav() {
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });

        navHotlines.setOnClickListener(v -> {
            startActivity(new Intent(this, hotlines.class));
            finish();
        });

        navNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, Notification.class));
            finish();
        });

        navProfile.setOnClickListener(v -> {
            // Already on this screen
        });
    }
}
