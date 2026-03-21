package com.example.civireports;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class Profile extends AppCompatActivity {

    private TextView tvUserName, tvUserEmail;
    private ImageView verifiedBadge;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        verifiedBadge = findViewById(R.id.verified_badge);

        setupMenuItems();
        setupBottomNav();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Log out
        ((MaterialButton) findViewById(R.id.btnLogout)).setOnClickListener(v -> {
            // TODO: Clear session/token/preferences when backend is ready
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class); // Assuming MainActivity is Login
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh user data every time the activity comes to the foreground
        loadUserData();
    }

    private void loadUserData() {
        // Load data from SharedPreferences (Synced with EditProfileActivity)
        String name = sharedPreferences.getString("full_name", "Samantha G. Pitero");
        String email = sharedPreferences.getString("email", "lia.pitero.coi@pcu.edu.ph");
        
        tvUserName.setText(name);
        tvUserEmail.setText(email);
        
        // Example: logic for verified status
        boolean isVerified = true; 
        if (verifiedBadge != null) {
            verifiedBadge.setVisibility(isVerified ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private void setupMenuItems() {
        // Account section
        findViewById(R.id.menuEditProfile).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        findViewById(R.id.menuChangePassword).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileChangePassActivity.class)));

        findViewById(R.id.menuReportHistory).setOnClickListener(v ->
                startActivity(new Intent(this, ReportHistoryActivity.class)));

        // Support section
        findViewById(R.id.menuHelp).setOnClickListener(v ->
                startActivity(new Intent(this, HelpSupportActivity.class)));

        findViewById(R.id.menuAbout).setOnClickListener(v ->
                startActivity(new Intent(this, AboutAppActivity.class)));
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
                startActivity(new Intent(this, Notification.class));
                overridePendingTransition(0, 0);
        });

        findViewById(R.id.navProfile).setOnClickListener(v ->
                Toast.makeText(this, "You are on Profile", Toast.LENGTH_SHORT).show());
    }
}