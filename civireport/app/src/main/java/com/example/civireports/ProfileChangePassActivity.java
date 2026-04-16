package com.example.civireports;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import com.example.civireports.network.ApiService;
import com.example.civireports.network.RetrofitClient;
import com.example.civireports.models.ChangePassRequest;
import com.example.civireports.models.ChangePassResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileChangePassActivity extends AppCompatActivity {

    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnChangePassword;
    private TextView tvForgotPassword;
    
    private boolean isCurrentVisible = false;
    private boolean isNewVisible = false;
    private boolean isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_change_pass);

        // Header back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Initialize views
        etCurrentPassword = findViewById(R.id.etCurrentPassword);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePasswordSubmit);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);

        setupVisibilityToggles();

        btnChangePassword.setOnClickListener(v -> {
            if (validateInputs()) {
                changePassword();
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        setupBottomNav();
    }

    private void changePassword() {
        String current = etCurrentPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        ChangePassRequest request = new ChangePassRequest(current, newPass, confirm);

        ApiService apiService = RetrofitClient.getApiService(this);

        Call<ChangePassResponse> call = apiService.changePassword(request);

        call.enqueue(new Callback<ChangePassResponse>() {
            @Override
            public void onResponse(Call<ChangePassResponse> call, Response<ChangePassResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    showSuccessDialog();
                } else {
                    Toast.makeText(ProfileChangePassActivity.this,
                            "Failed: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePassResponse> call, Throwable t) {
                Toast.makeText(ProfileChangePassActivity.this,
                        "Error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_password_success);
        dialog.setCancelable(false);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();

        // Auto-dismiss after 2 seconds and navigate to Profile
        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            // Navigate to Profile tab
            Intent intent = new Intent(ProfileChangePassActivity.this, Profile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }, 2000);
    }

    private void setupVisibilityToggles() {
        ImageView ivCurrent = findViewById(R.id.show_current_password);
        ImageView ivNew = findViewById(R.id.show_new_password);
        ImageView ivConfirm = findViewById(R.id.show_confirm_password);

        ivCurrent.setOnClickListener(v -> {
            isCurrentVisible = !isCurrentVisible;
            togglePasswordVisibility(etCurrentPassword, ivCurrent, isCurrentVisible);
        });

        ivNew.setOnClickListener(v -> {
            isNewVisible = !isNewVisible;
            togglePasswordVisibility(etNewPassword, ivNew, isNewVisible);
        });

        ivConfirm.setOnClickListener(v -> {
            isConfirmVisible = !isConfirmVisible;
            togglePasswordVisibility(etConfirmPassword, ivConfirm, isConfirmVisible);
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageView imageView, boolean isVisible) {
        if (isVisible) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            imageView.setAlpha(1.0f);
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
            imageView.setAlpha(0.5f);
        }
        editText.setSelection(editText.getText().length());
    }

    private boolean validateInputs() {
        String current = etCurrentPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirm = etConfirmPassword.getText().toString().trim();

        if (current.isEmpty()) {
            etCurrentPassword.setError("Current password is required");
            return false;
        }
        if (newPass.isEmpty()) {
            etNewPassword.setError("New password is required");
            return false;
        }
        if (newPass.length() < 8) {
            etNewPassword.setError("Password must be at least 8 characters");
            return false;
        }
        if (!newPass.equals(confirm)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }
        return true;
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
        findViewById(R.id.navHotlines).setOnClickListener(v -> {
            startActivity(new Intent(this, hotlines.class));
            finish();
        });
        findViewById(R.id.navNotification).setOnClickListener(v -> {
            startActivity(new Intent(this, Notification.class));
            finish();
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
            finish();
        });
    }
}
