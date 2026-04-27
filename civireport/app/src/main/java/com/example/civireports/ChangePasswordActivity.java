package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.civireports.models.MessageResponse;
import com.example.civireports.models.ResetPasswordRequest;
import com.example.civireports.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private String email, otp;
    
    private boolean isNewVisible = false;
    private boolean isConfirmVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        btnBack = findViewById(R.id.btnBack);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        email = getIntent().getStringExtra("IDENTIFIER");
        otp = getIntent().getStringExtra("OTP");

        setupPlaceholderBehavior(etNewPassword);
        setupPlaceholderBehavior(etConfirmPassword);
        setupVisibilityToggles();

        btnBack.setOnClickListener(v -> finish());

        btnChangePassword.setOnClickListener(v -> {
            if (validatePassword()) {
                handleResetPassword();
            }
        });
    }

    private void setupVisibilityToggles() {
        ImageView ivNew = findViewById(R.id.show_new_password);
        ImageView ivConfirm = findViewById(R.id.show_confirm_password);

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

    private void handleResetPassword() {
        String newPassword = etNewPassword.getText().toString();
        btnChangePassword.setEnabled(false);

        ResetPasswordRequest request = new ResetPasswordRequest(email, otp, newPassword);

        RetrofitClient.getApiService(this).resetPassword(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                btnChangePassword.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    btnChangePassword.setText("Log in");
                    etNewPassword.setEnabled(false);
                    etConfirmPassword.setEnabled(false);
                    btnBack.setVisibility(View.GONE);

                    btnChangePassword.setOnClickListener(v -> {
                        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    });
                } else if (response.code() == 400) {
                    Toast.makeText(ChangePasswordActivity.this, "Invalid or expired OTP. Please try again.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, ForgotPasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                btnChangePassword.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validatePassword() {
        String newPassword = etNewPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (newPassword.isEmpty()) {
            etNewPassword.setError("Password is required");
            return false;
        }

        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[_@$!%*?&])[A-Za-z\\d_@$!%*?&]{8,}$";
        if (!newPassword.matches(passwordPattern)) {
            etNewPassword.setError("Min 8 chars, must include uppercase, lowercase, number, and symbol");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void setupPlaceholderBehavior(EditText editText) {
        if (editText == null) return;
        final CharSequence originalHint = editText.getHint();
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) editText.setHint("");
            else editText.setHint(originalHint);
        });
    }
}