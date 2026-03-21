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

import com.example.civireports.models.ForgotPasswordRequest;
import com.example.civireports.models.MessageResponse;
import com.example.civireports.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etIdentifier;
    private TextView tvResend;
    private Button btnSendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        btnBack = findViewById(R.id.btnBack);
        etIdentifier = findViewById(R.id.etIdentifier);
        tvResend = findViewById(R.id.tvResend);
        btnSendOTP = findViewById(R.id.btnSendOTP);

        setupPlaceholderBehavior(etIdentifier);

        btnBack.setOnClickListener(v -> finish());

        btnSendOTP.setOnClickListener(v -> {
            String identifier = etIdentifier.getText().toString().trim();

            if (identifier.isEmpty()) {
                etIdentifier.setError("Please enter your email or mobile number");
                etIdentifier.requestFocus();
                return;
            }

            boolean isValidEmail = Patterns.EMAIL_ADDRESS.matcher(identifier).matches();
            boolean isValidMobile = identifier.matches("^09\\d{9}$");

            if (isValidEmail) {
                sendOtp(identifier);
            } else if (isValidMobile) {
                Intent intent = new Intent(ForgotPasswordActivity.this, VerifyMobileActivity.class);
                intent.putExtra("IDENTIFIER", identifier);
                startActivity(intent);
            } else {
                if (identifier.matches("\\d+")) {
                    etIdentifier.setError("Mobile number must consist of 11 digits and start with 09");
                } else {
                    etIdentifier.setError("Please enter a valid email address (e.g. name@email.com)");
                }
                etIdentifier.requestFocus();
            }
        });

        tvResend.setOnClickListener(v -> {
            String identifier = etIdentifier.getText().toString().trim();
            if (!identifier.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(identifier).matches()) {
                sendOtp(identifier);
            } else {
                Toast.makeText(this, "Please enter a valid email first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOtp(String email) {
        btnSendOTP.setEnabled(false);

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        RetrofitClient.getApiService(this).forgotPassword(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                btnSendOTP.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "OTP sent to your email!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPasswordActivity.this, VerifyCodeActivity.class);
                    intent.putExtra("IDENTIFIER", email);
                    startActivity(intent);
                } else if (response.code() == 404) {
                    etIdentifier.setError("Email not found");
                    etIdentifier.requestFocus();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                btnSendOTP.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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