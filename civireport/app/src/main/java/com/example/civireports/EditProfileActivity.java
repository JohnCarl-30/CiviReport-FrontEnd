package com.example.civireports;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etContact, etAddress;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Initialize views
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etContact = findViewById(R.id.etContact);
        etAddress = findViewById(R.id.etAddress);

        // Load current data from SharedPreferences
        loadCurrentData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Confirm Changes
        ((MaterialButton) findViewById(R.id.btnConfirmChanges)).setOnClickListener(v -> {
            saveProfileData();
        });

        // Delete Account — show confirmation dialog
        ((TextView) findViewById(R.id.tvDeleteAccount)).setOnClickListener(v ->
                showDeleteAccountDialog());

        setupBottomNav();
    }

    private void loadCurrentData() {
        String name = sharedPreferences.getString("full_name", "Samantha G. Pitero");
        String email = sharedPreferences.getString("email", "Samantha@gmail.com");
        String contact = sharedPreferences.getString("contact", "09123456789");
        String address = sharedPreferences.getString("address", "Secret");

        etFullName.setText(name);
        etEmail.setText(email);
        etContact.setText(contact);
        etAddress.setText(address);
    }

    private void saveProfileData() {
        String name = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String contact = etContact.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to SharedPreferences for local sync
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("full_name", name);
        editor.putString("email", email);
        editor.putString("contact", contact);
        editor.putString("address", address);
        editor.apply();

        // TODO: Here you would also call your backend API using Retrofit to update the database
        // RetrofitClient.getApiService().updateProfile(...).enqueue(...);

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        
        // Return to Profile with result or just finish
        finish();
    }

    private void showDeleteAccountDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_acc);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.88),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.setCancelable(true);

        dialog.findViewById(R.id.btnBackDialog).setOnClickListener(v -> dialog.dismiss());

        ((MaterialButton) dialog.findViewById(R.id.btnConfirmDelete)).setOnClickListener(v -> {
            dialog.dismiss();
            
            // Clear SharedPreferences on delete
            sharedPreferences.edit().clear().apply();
            
            Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
            
            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        findViewById(R.id.navHotlines).setOnClickListener(v -> startActivity(new Intent(this, hotlines.class)));
        findViewById(R.id.navNotification).setOnClickListener(v -> startActivity(new Intent(this, Notification.class)));
        findViewById(R.id.navProfile).setOnClickListener(v -> startActivity(new Intent(this, Profile.class)));
    }
}
