package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etIdentifier;
    private TextView tvResend;
    private Button btnSendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        etIdentifier = findViewById(R.id.etIdentifier);
        tvResend = findViewById(R.id.tvResend);
        btnSendOTP = findViewById(R.id.btnSendOTP);

        // Back button listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close activity and go back
            }
        });

        // Send OTP button listener
        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identifier = etIdentifier.getText().toString().trim();
                
                if (identifier.isEmpty()) {
                    etIdentifier.setError("Please enter your email or mobile number");
                    etIdentifier.requestFocus();
                    return;
                }

                // Check for valid email
                boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(identifier).matches();
                // Check for valid PH mobile number (11 digits, starts with 09)
                boolean isValidMobile = identifier.matches("^09\\d{9}$");

                if (isValidEmail) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
                    intent.putExtra("IDENTIFIER", identifier);
                    startActivity(intent);
                } else if (isValidMobile) {
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyMobileActivity.class);
                    intent.putExtra("IDENTIFIER", identifier);
                    startActivity(intent);
                } else {
                    // Provide specific error messages based on input type
                    if (identifier.matches("\\d+")) {
                        etIdentifier.setError("Mobile number must consist of 11 digits and start with 09");
                    } else {
                        etIdentifier.setError("Please enter a valid email address (e.g. name@email.com)");
                    }
                    etIdentifier.requestFocus();
                }
            }
        });

        // Resend text listener
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ForgotPasswordActivity.this, "Resending code...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
