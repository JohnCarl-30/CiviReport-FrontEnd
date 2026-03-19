package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.civireports.models.ForgotPasswordRequest;
import com.example.civireports.models.MessageResponse;
import com.example.civireports.models.VerifyOtpRequest;
import com.example.civireports.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyCodeActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private TextView tvResendCode, tvTryAnotherWay, tvVerifyInstructions;
    private Button btnVerify;
    private String email;
    private void clearOtpFields() {
        etOtp1.setText("");
        etOtp2.setText("");
        etOtp3.setText("");
        etOtp4.setText("");
        etOtp1.requestFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code_email);

        btnBack = findViewById(R.id.btnBack);
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        tvResendCode = findViewById(R.id.tvResendCode);
        tvTryAnotherWay = findViewById(R.id.tvTryAnotherWay);
        tvVerifyInstructions = findViewById(R.id.tvVerifyInstructions);
        btnVerify = findViewById(R.id.btnVerify);

        email = getIntent().getStringExtra("IDENTIFIER");
        if (email != null && !email.isEmpty()) {
            String sourceString = "We sent a 4-digit confirmation code to your email<br/><b>" + email + "</b>";
            tvVerifyInstructions.setText(Html.fromHtml(sourceString));
        }

        setupOtpInputs();

        btnBack.setOnClickListener(v -> finish());

        btnVerify.setOnClickListener(v -> {
            String otp = etOtp1.getText().toString() + etOtp2.getText().toString()
                    + etOtp3.getText().toString() + etOtp4.getText().toString();

            if (otp.length() < 4) {
                Toast.makeText(this, "Please enter the 4-digit code", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Verify OTP with backend first
            btnVerify.setEnabled(false); // prevent double-tap

            VerifyOtpRequest request = new VerifyOtpRequest(email, otp);
            RetrofitClient.getApiService().verifyOtp(request).enqueue(new Callback<MessageResponse>() {
                @Override
                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                    btnVerify.setEnabled(true);

                    android.util.Log.d("OTP_DEBUG", "Response code: " + response.code());
                    android.util.Log.d("OTP_DEBUG", "isSuccessful: " + response.isSuccessful());
                    if (response.isSuccessful()) {
                        Intent intent = new Intent(VerifyCodeActivity.this, ChangePasswordActivity.class);
                        intent.putExtra("IDENTIFIER", email);
                        intent.putExtra("OTP", otp);
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerifyCodeActivity.this, "Invalid or expired OTP. Try again.", Toast.LENGTH_SHORT).show();
                        clearOtpFields();
                    }
                }

                @Override
                public void onFailure(Call<MessageResponse> call, Throwable t) {
                    btnVerify.setEnabled(true);
                    Toast.makeText(VerifyCodeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        tvResendCode.setOnClickListener(v -> resendOtp());

        tvTryAnotherWay.setOnClickListener(v -> {
            Intent intent = new Intent(VerifyCodeActivity.this, VerifyMobileActivity.class);
            intent.putExtra("IDENTIFIER", email);
            startActivity(intent);
            finish();
        });
    }

    private void resendOtp() {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);

        RetrofitClient.getApiService().forgotPassword(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(VerifyCodeActivity.this, "OTP resent to " + email, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyCodeActivity.this, "Failed to resend OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(VerifyCodeActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupOtpInputs() {
        etOtp1.addTextChangedListener(new OtpTextWatcher(etOtp1, etOtp2));
        etOtp2.addTextChangedListener(new OtpTextWatcher(etOtp2, etOtp3));
        etOtp3.addTextChangedListener(new OtpTextWatcher(etOtp3, etOtp4));
        etOtp4.addTextChangedListener(new OtpTextWatcher(etOtp4, null));
    }

    private static class OtpTextWatcher implements TextWatcher {
        private final View currentView;
        private final View nextView;

        public OtpTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }
}