package com.example.civireports;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class hotlines extends AppCompatActivity {

    private LinearLayout navHome;
    private LinearLayout navHotlines;
    private LinearLayout navNotification;
    private LinearLayout navProfile;

    private CardView cardEmergency, cardPNP, cardPNPText, cardBFP, cardRedCross, cardSuicide, cardNDRRMC, cardMMDA, cardDSWD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotlines_page);

        // Navigation
        navHome         = findViewById(R.id.navHome);
        navHotlines     = findViewById(R.id.navHotlines);
        navNotification = findViewById(R.id.navNotification);
        navProfile      = findViewById(R.id.navProfile);

        // Hotline Cards
        cardEmergency   = findViewById(R.id.cardEmergency);
        cardPNP         = findViewById(R.id.cardPNP);
        cardPNPText     = findViewById(R.id.cardPNPText);
        cardBFP         = findViewById(R.id.cardBFP);
        cardRedCross    = findViewById(R.id.cardRedCross);
        cardSuicide     = findViewById(R.id.cardSuicide);
        cardNDRRMC      = findViewById(R.id.cardNDRRMC);
        cardMMDA        = findViewById(R.id.cardMMDA);
        cardDSWD        = findViewById(R.id.cardDSWD);

        setupClickListeners();

        // Check if we should show the emergency popup
        if (getIntent().getBooleanExtra("show_emergency_popup", false)) {
            showEmergencyReceivedDialog();
        }
    }

    private void setupClickListeners() {
        // Bottom Navigation
        navHome.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
        });

        navHotlines.setOnClickListener(v -> {
            // Already on this screen
        });

        navNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, Notification.class));
        });

        navProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });

        // Hotline Click Actions
        cardEmergency.setOnClickListener(v -> makeCall("911"));
        cardPNP.setOnClickListener(v -> makeCall("117"));
        cardPNPText.setOnClickListener(v -> makeCall("09178475757"));
        cardBFP.setOnClickListener(v -> makeCall("024260219"));
        cardRedCross.setOnClickListener(v -> makeCall("143"));
        cardSuicide.setOnClickListener(v -> makeCall("0288937603"));
        cardNDRRMC.setOnClickListener(v -> makeCall("0289115061"));
        cardMMDA.setOnClickListener(v -> makeCall("136"));
        cardDSWD.setOnClickListener(v -> makeCall("0289318101"));
    }

    private void makeCall(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    private void showEmergencyReceivedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        
        View dialogView = inflater.inflate(R.layout.dialog_emergency_received, null); 
        
        ImageView btnClose = dialogView.findViewById(R.id.btn_close_emergency);

        AlertDialog dialog = builder.setView(dialogView).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dialog.dismiss());
        }
        
        dialog.show();
    }
}
