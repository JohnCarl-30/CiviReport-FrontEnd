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

public class VerifyMobileActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private TextView tvResendCode, tvTryAnotherWay, tvVerifyInstructions;
    private Button btnVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code_mobilenumber);

        // Initialize views
        btnBack = findViewById(R.id.btnBack);
        etOtp1 = findViewById(R.id.etOtp1);
        etOtp2 = findViewById(R.id.etOtp2);
        etOtp3 = findViewById(R.id.etOtp3);
        etOtp4 = findViewById(R.id.etOtp4);
        tvResendCode = findViewById(R.id.tvResendCode);
        tvTryAnotherWay = findViewById(R.id.tvTryAnotherWay);
        tvVerifyInstructions = findViewById(R.id.tvVerifyInstructions);
        btnVerify = findViewById(R.id.btnVerify);

        // Get identifier from intent and update instructions
        String identifier = getIntent().getStringExtra("IDENTIFIER");
        if (identifier != null && !identifier.isEmpty()) {
            String sourceString = "We sent a 4-digit confirmation code to your<br/>mobile number <b>" + identifier + "</b>";
            tvVerifyInstructions.setText(Html.fromHtml(sourceString));
        }

        // Set up OTP auto-focus
        setupOtpInputs();

        // Back button listener
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Verify button listener
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = etOtp1.getText().toString() + etOtp2.getText().toString() +
                             etOtp3.getText().toString() + etOtp4.getText().toString();
                
                if (otp.length() < 4) {
                    Toast.makeText(VerifyMobileActivity.this, "Please enter the 4-digit code", Toast.LENGTH_SHORT).show();
                } else {
                    // Simulating correct OTP verification
                    Toast.makeText(VerifyMobileActivity.this, "OTP Verified", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerifyMobileActivity.this, ChangePasswordActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        tvResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerifyMobileActivity.this, "Resending code...", Toast.LENGTH_SHORT).show();
            }
        });

        tvTryAnotherWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to VerifyCodeActivity (Email)
                Intent intent = new Intent(VerifyMobileActivity.this, VerifyCodeActivity.class);
                intent.putExtra("IDENTIFIER", getIntent().getStringExtra("IDENTIFIER"));
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupOtpInputs() {
        etOtp1.addTextChangedListener(new OtpTextWatcher(etOtp1, etOtp2));
        etOtp2.addTextChangedListener(new OtpTextWatcher(etOtp2, etOtp3));
        etOtp3.addTextChangedListener(new OtpTextWatcher(etOtp3, etOtp4));
        etOtp4.addTextChangedListener(new OtpTextWatcher(etOtp4, null));
    }

    private class OtpTextWatcher implements TextWatcher {
        private View currentView;
        private View nextView;

        public OtpTextWatcher(View currentView, View nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }
}
