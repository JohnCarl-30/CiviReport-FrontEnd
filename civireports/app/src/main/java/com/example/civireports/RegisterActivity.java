package com.example.civireports;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private CheckBox termsCheckbox;
    private boolean isTermsAccepted = false;
    private boolean isPrivacyAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.registration_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        termsCheckbox = findViewById(R.id.terms_checkbox);
        // Make the main checkbox read-only so it can only be checked via the dialogs
        termsCheckbox.setClickable(false);
        
        setupTermsAndService();

        Button backToLoginButton = findViewById(R.id.back_to_login_button);
        backToLoginButton.setOnClickListener(v -> finish());

        Button registerSubmitButton = findViewById(R.id.register_submit_button);
        registerSubmitButton.setOnClickListener(v -> showRegistrationSuccessDialog());
    }

    private void setupTermsAndService() {
        TextView termsText = findViewById(R.id.terms_text);
        String text = "I agree to the Terms and Service and Privacy Policy";
        SpannableString ss = new SpannableString(text);

        // Clickable span for Terms and Service
        ClickableSpan termsSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showTermsDialog();
            }
        };

        // Clickable span for Privacy Policy
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showPrivacyPolicyDialog();
            }
        };

        // Setup Terms and Service span
        int termsStart = text.indexOf("Terms and Service");
        int termsEnd = termsStart + "Terms and Service".length();
        ss.setSpan(termsSpan, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.BLUE), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Setup Privacy Policy span
        int privacyStart = text.indexOf("Privacy Policy");
        int privacyEnd = privacyStart + "Privacy Policy".length();
        ss.setSpan(privacySpan, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(Color.BLUE), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        termsText.setText(ss);
        termsText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void updateMainCheckbox() {
        termsCheckbox.setChecked(isTermsAccepted && isPrivacyAccepted);
    }

    private void showTermsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.terms_and_services, null);
        ScrollView scrollView = dialogView.findViewById(R.id.terms_scrollview);
        CheckBox cbAccept = dialogView.findViewById(R.id.cb_accept_terms);
        ImageView btnBack = dialogView.findViewById(R.id.btn_back);

        // Pre-check if already accepted
        cbAccept.setChecked(isTermsAccepted);
        if (isTermsAccepted) cbAccept.setVisibility(View.VISIBLE);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnBack.setOnClickListener(v -> dialog.dismiss());

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
            if (diff <= 0) {
                cbAccept.setVisibility(View.VISIBLE);
            }
        });

        cbAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isTermsAccepted = isChecked;
            updateMainCheckbox();
            if (isChecked) {
                dialogView.postDelayed(dialog::dismiss, 500);
            }
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(0.5f);
        }
    }

    private void showPrivacyPolicyDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.privacy_and_policy_dialog, null);
        ScrollView scrollView = dialogView.findViewById(R.id.privacy_scrollview);
        CheckBox cbAccept = dialogView.findViewById(R.id.cb_accept_privacy);
        ImageView btnBack = dialogView.findViewById(R.id.btn_back_privacy);

        // Pre-check if already accepted
        cbAccept.setChecked(isPrivacyAccepted);
        if (isPrivacyAccepted) cbAccept.setVisibility(View.VISIBLE);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        btnBack.setOnClickListener(v -> dialog.dismiss());

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
            if (diff <= 0) {
                cbAccept.setVisibility(View.VISIBLE);
            }
        });

        cbAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPrivacyAccepted = isChecked;
            updateMainCheckbox();
            if (isChecked) {
                dialogView.postDelayed(dialog::dismiss, 500);
            }
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(0.5f);
        }
    }

    private void showRegistrationSuccessDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.register_success_dialog, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.getWindow().setDimAmount(0.5f);
        }
    }
}
