package com.example.civireports;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etContact, etAddress, etGender;
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
        etGender = findViewById(R.id.etGender);

        // Set up interactions: clear on focus and change color
        setupEditBehavior(etFullName);
        setupEditBehavior(etEmail);
        setupEditBehavior(etContact);
        setupEditBehavior(etAddress);

        // Load current data from SharedPreferences
        loadCurrentData();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Confirm Changes
        MaterialButton btnConfirm = findViewById(R.id.btnConfirmChanges);
        if (btnConfirm != null) {
            btnConfirm.setOnClickListener(v -> {
                saveProfileData();
            });
        }

        // Delete Account — show confirmation dialog
        TextView tvDelete = findViewById(R.id.tvDeleteAccount);
        if (tvDelete != null) {
            tvDelete.setOnClickListener(v ->
                    showDeleteAccountDialog());
        }

        setupBottomNav();
    }

    private void setupEditBehavior(EditText editText) {
        if (editText == null) return;
        
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Clear the text so the user can input whatever they want
                editText.setText("");
                // Set color to 1B2F5B when focused/typing
                editText.setTextColor(Color.parseColor("#1B2F5B"));
            }
        });
    }

    private void loadCurrentData() {
        String name = sharedPreferences.getString("full_name", "Juan Dela Cruz");
        String email = sharedPreferences.getString("email", "JuanDC@gmail.com");
        String contact = sharedPreferences.getString("contact", "09123456789");
        String address = sharedPreferences.getString("address", "Secret");
        String gender = sharedPreferences.getString("gender", "Not Specified");

        etFullName.setText(name);
        etEmail.setText(email);
        etContact.setText(contact);
        etAddress.setText(address);
        etGender.setText(gender);
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

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
        
        // Return to Profile
        finish();
    }

    private void showDeleteAccountDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_acc);

        if (dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setLayout(
                    (int)(getResources().getDisplayMetrics().widthPixels * 0.88),
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            // Raise the dialog position a little bit to the top
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            params.y = -(int)(60 * getResources().getDisplayMetrics().density); // Moves it 60dp up from the center
            window.setAttributes(params);
        }

        dialog.setCancelable(true);

        View btnBack = dialog.findViewById(R.id.btnBackDialog);
        if (btnBack != null) btnBack.setOnClickListener(v -> dialog.dismiss());

        MaterialButton btnDelete = dialog.findViewById(R.id.btnConfirmDelete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                dialog.dismiss();
                sharedPreferences.edit().clear().apply();
                Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }

        dialog.show();
    }

    private void setupBottomNav() {
        View home = findViewById(R.id.navHome);
        if (home != null) home.setOnClickListener(v -> startActivity(new Intent(this, DashboardActivity.class)));
        
        View hotlines = findViewById(R.id.navHotlines);
        if (hotlines != null) hotlines.setOnClickListener(v -> startActivity(new Intent(this, hotlines.class)));
        
        View notification = findViewById(R.id.navNotification);
        if (notification != null) notification.setOnClickListener(v -> startActivity(new Intent(this, Notification.class)));
        
        View profile = findViewById(R.id.navProfile);
        if (profile != null) profile.setOnClickListener(v -> startActivity(new Intent(this, Profile.class)));
    }
}
