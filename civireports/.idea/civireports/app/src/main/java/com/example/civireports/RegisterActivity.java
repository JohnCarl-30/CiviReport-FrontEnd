package com.example.civireports;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, middleNameInput, suffixInput, emailInput, contactInput, addressInput, passwordInput, confirmPasswordInput;
    private CheckBox termsCheckbox;
    private boolean isTermsAccepted = false;
    private boolean isPrivacyAccepted = false;

    // Suffix list for validation
    private final List<String> VALID_SUFFIXES = Arrays.asList("Jr.", "Sr.", "II", "III", "IV", "V", "VI", "Jr", "Sr");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        firstNameInput = findViewById(R.id.first_name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        middleNameInput = findViewById(R.id.middle_name_input);
        suffixInput = findViewById(R.id.suffix_input);
        emailInput = findViewById(R.id.email_input);
        contactInput = findViewById(R.id.contact_input);
        addressInput = findViewById(R.id.address_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        
        // Make the main checkbox read-only so it can only be checked via the dialogs
        termsCheckbox.setClickable(false);
        
        setupTermsAndService();

        Button backToLoginButton = findViewById(R.id.back_to_login_button);
        backToLoginButton.setOnClickListener(v -> finish());

        Button registerSubmitButton = findViewById(R.id.register_submit_button);
        registerSubmitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                showRegistrationSuccessDialog();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        View firstErrorView = null;

        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String middleName = middleNameInput.getText().toString().trim();
        String suffix = suffixInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String contact = contactInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        // 1. First Name: Required, Letters only
        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            isValid = false;
            firstErrorView = firstNameInput;
        } else if (!firstName.matches("^[a-zA-Z\\s]+$")) {
            firstNameInput.setError("First name should only contain letters");
            isValid = false;
            if (firstErrorView == null) firstErrorView = firstNameInput;
        }

        // 2. Last Name: Required, Letters only
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            isValid = false;
            if (firstErrorView == null) firstErrorView = lastNameInput;
        } else if (!lastName.matches("^[a-zA-Z\\s]+$")) {
            lastNameInput.setError("Last name should only contain letters");
            isValid = false;
            if (firstErrorView == null) firstErrorView = lastNameInput;
        }

        // 3. Middle Name: Letters only (Optional)
        if (!middleName.isEmpty() && !middleName.matches("^[a-zA-Z\\s]+$")) {
            middleNameInput.setError("Middle name should only contain letters");
            isValid = false;
            if (firstErrorView == null) firstErrorView = middleNameInput;
        }

        // 4. Suffix: Optional, Must be a valid suffix
        if (!suffix.isEmpty() && !VALID_SUFFIXES.contains(suffix)) {
            suffixInput.setError("Please enter a valid suffix (e.g., Jr., Sr., II)");
            isValid = false;
            if (firstErrorView == null) firstErrorView = suffixInput;
        }

        // 5. Email: Required, Valid format
        if (email.isEmpty()) {
            emailInput.setError("Email is required");
            isValid = false;
            if (firstErrorView == null) firstErrorView = emailInput;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Please enter a valid email address");
            isValid = false;
            if (firstErrorView == null) firstErrorView = emailInput;
        }

        // 6. Contact Number: Required, 11 digits, starts with 09, numbers only
        if (contact.isEmpty()) {
            contactInput.setError("Contact number is required");
            isValid = false;
            if (firstErrorView == null) firstErrorView = contactInput;
        } else if (!contact.matches("^09\\d{9}$")) {
            contactInput.setError("Contact number must be 11 digits and start with 09");
            isValid = false;
            if (firstErrorView == null) firstErrorView = contactInput;
        }

        // 7. Address: Required, Not empty
        if (address.isEmpty()) {
            addressInput.setError("Address is required");
            isValid = false;
            if (firstErrorView == null) firstErrorView = addressInput;
        }

        // 8. Password: Required, Min 8 chars, mix of uppercase, lowercase, numbers, and symbols
        if (password.isEmpty()) {
            passwordInput.setError("Password is required");
            isValid = false;
            if (firstErrorView == null) firstErrorView = passwordInput;
        } else {
            // Updated pattern to match ChangePasswordActivity (8 chars, Upper, Lower, Number, Symbol)
            String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
            if (!password.matches(passwordPattern)) {
                passwordInput.setError("Password must be at least 8 characters, including uppercase, lowercase, number, and symbol.");
                isValid = false;
                if (firstErrorView == null) firstErrorView = passwordInput;
            }
        }

        // 9. Confirm Password: Required, Match password
        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.setError("Please confirm your password");
            isValid = false;
            if (firstErrorView == null) firstErrorView = confirmPasswordInput;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            isValid = false;
            if (firstErrorView == null) firstErrorView = confirmPasswordInput;
        }

        if (!termsCheckbox.isChecked()) {
            Toast.makeText(this, "Please read and agree to the Terms and Privacy Policy", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (firstErrorView != null) {
            firstErrorView.requestFocus();
        }

        return isValid;
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
