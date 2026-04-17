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

import com.example.civireports.models.EditProfileRequest;
import com.example.civireports.models.EditProfileResponse;
import com.example.civireports.network.ApiService;
import com.example.civireports.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                // Change placeholder for Full Name when focused
                if (editText.getId() == R.id.etFullName) {
                    editText.setHint("Last Name, First Name, Middle Initial, Suffix");
                }
                editText.setTextColor(Color.parseColor("#1B2F5B"));
            } else {
                // Restore default placeholder when focus is lost
                if (editText.getId() == R.id.etFullName) {
                    editText.setHint("Juan Dela Cruz");
                }
            }
        });
    }

    private void loadCurrentData() {
        String name = sharedPreferences.getString("full_name", "");
        String email = sharedPreferences.getString("email", "");
        String contact = sharedPreferences.getString("contact", "");
        String address = sharedPreferences.getString("address", "");
        String gender = sharedPreferences.getString("gender", "Not Specified");

        // Use empty string if no data is found so hint can be shown
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

        EditProfileRequest request = new EditProfileRequest(
                name,
                email,
                contact,
                address
        );

        ApiService apiService = RetrofitClient.getApiService(this);

        Call<EditProfileResponse> call = apiService.updateProfile(request);

        call.enqueue(new Callback<EditProfileResponse>() {
            @Override
            public void onResponse(Call<EditProfileResponse> call, Response<EditProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {


                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("full_name", name);
                    editor.putString("email", email);
                    editor.putString("contact", contact);
                    editor.putString("address", address);
                    editor.apply();

                    Toast.makeText(EditProfileActivity.this,
                            "Profile updated successfully!",
                            Toast.LENGTH_SHORT).show();

                    finish();

                } else {
                    Toast.makeText(EditProfileActivity.this,
                            "Update failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EditProfileResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
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
