package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etNewPassword, etConfirmPassword;
    private Button btnChangePassword;
    private boolean isPasswordChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        // Back button listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Change Password / Log in button listener
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPasswordChanged) {
                    // Phase 1: Change Password
                    if (validatePassword()) {
                        isPasswordChanged = true;
                        Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        
                        // Change button text to "Log in"
                        btnChangePassword.setText("Log in");
                        
                        // Disable inputs to indicate completion
                        etNewPassword.setEnabled(false);
                        etConfirmPassword.setEnabled(false);
                        // Hide back button as process is finished
                        btnBack.setVisibility(View.GONE);
                    }
                } else {
                    // Phase 2: Redirect to Login (MainActivity)
                    Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
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

        // Minimum 8 characters, mix of uppercase, lowercase, numbers, and symbols
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (!newPassword.matches(passwordPattern)) {
            etNewPassword.setError("Password must be at least 8 characters, including uppercase, lowercase, number, and symbol.");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            return false;
        }

        return true;
    }
}
