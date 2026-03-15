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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, contactInput, passwordInput, confirmPasswordInput;
    private CheckBox termsCheckbox;
    private boolean isTermsAccepted = false;
    private boolean isPrivacyAccepted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration_page);

        View mainLayout = findViewById(R.id.register_main);
        LinearLayout formContainer = null;
        
        // Find the inner LinearLayout of the ScrollView to adjust its padding
        ScrollView scrollView = null;
        if (mainLayout instanceof android.view.ViewGroup) {
            for (int i = 0; i < ((android.view.ViewGroup)mainLayout).getChildCount(); i++) {
                View child = ((android.view.ViewGroup)mainLayout).getChildAt(i);
                if (child instanceof ScrollView) {
                    scrollView = (ScrollView) child;
                    if (scrollView.getChildAt(0) instanceof LinearLayout) {
                        formContainer = (LinearLayout) scrollView.getChildAt(0);
                    }
                    break;
                }
            }
        }

        final LinearLayout finalFormContainer = formContainer;

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            
            if (finalFormContainer != null) {
                int bottomPadding;
                float density = getResources().getDisplayMetrics().density;
                if (ime.bottom > 0) {
                    bottomPadding = (int) (130 * density);
                } else {
                    bottomPadding = (int) (30 * density);
                }
                finalFormContainer.setPadding(
                    finalFormContainer.getPaddingLeft(),
                    finalFormContainer.getPaddingTop(),
                    finalFormContainer.getPaddingRight(),
                    bottomPadding
                );
            }
            
            return insets;
        });

        // Initialize views
        firstNameInput = findViewById(R.id.first_name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        emailInput = findViewById(R.id.email_input);
        contactInput = findViewById(R.id.contact_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        
        // Disable main checkbox until terms and privacy are accepted in their dialogs
        if (termsCheckbox != null) {
            termsCheckbox.setEnabled(false);
        }
        
        setupTermsAndService();

        View loginLink = findViewById(R.id.back_to_login_button);
        if (loginLink != null) {
            loginLink.setOnClickListener(v -> finish());
        }

        Button registerSubmitButton = findViewById(R.id.register_submit_button);
        if (registerSubmitButton != null) {
            registerSubmitButton.setOnClickListener(v -> {
                if (validateInputs()) {
                    showRegistrationSuccessDialog();
                }
            });
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        View firstErrorView = null;

        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String contact = contactInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        if (firstName.isEmpty()) { firstNameInput.setError("First name is required"); isValid = false; firstErrorView = firstNameInput; }
        if (lastName.isEmpty()) { lastNameInput.setError("Last name is required"); isValid = false; if (firstErrorView == null) firstErrorView = lastNameInput; }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) { emailInput.setError("Valid email is required"); isValid = false; if (firstErrorView == null) firstErrorView = emailInput; }
        if (contact.isEmpty() || !contact.matches("^09\\d{9}$")) { contactInput.setError("Valid 11-digit contact number is required"); isValid = false; if (firstErrorView == null) firstErrorView = contactInput; }
        if (password.isEmpty() || password.length() < 8) { passwordInput.setError("Password must be at least 8 characters"); isValid = false; if (firstErrorView == null) firstErrorView = passwordInput; }
        if (!password.equals(confirmPassword)) { confirmPasswordInput.setError("Passwords do not match"); isValid = false; if (firstErrorView == null) firstErrorView = confirmPasswordInput; }
        if (termsCheckbox != null && !termsCheckbox.isChecked()) { Toast.makeText(this, "Please read and agree to the Terms and Privacy Policy by clicking the links", Toast.LENGTH_SHORT).show(); isValid = false; }

        if (firstErrorView != null) {
            firstErrorView.requestFocus();
        }

        return isValid;
    }

    private void setupTermsAndService() {
        TextView termsText = findViewById(R.id.terms_text);
        if (termsText == null) return;
        
        String text = "I agree to the Terms and Service and Privacy Policy";
        SpannableString ss = new SpannableString(text);

        ClickableSpan termsSpan = new ClickableSpan() {
            @Override public void onClick(@NonNull View widget) { showTermsDialog(); }
        };
        ClickableSpan privacySpan = new ClickableSpan() {
            @Override public void onClick(@NonNull View widget) { showPrivacyPolicyDialog(); }
        };

        int termsStart = text.indexOf("Terms and Service");
        int termsEnd = termsStart + "Terms and Service".length();
        if (termsStart != -1) {
            ss.setSpan(termsSpan, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.BLUE), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int privacyStart = text.indexOf("Privacy Policy");
        int privacyEnd = privacyStart + "Privacy Policy".length();
        if (privacyStart != -1) {
            ss.setSpan(privacySpan, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(Color.BLUE), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        termsText.setText(ss);
        termsText.setMovementMethod(LinkMovementMethod.getInstance());
        termsText.setHighlightColor(Color.TRANSPARENT);
    }

    private void updateMainCheckbox() {
        if (termsCheckbox != null) {
            boolean bothAccepted = isTermsAccepted && isPrivacyAccepted;
            termsCheckbox.setEnabled(bothAccepted);
            termsCheckbox.setChecked(bothAccepted);
        }
    }

    private void showTermsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.terms_and_services, null);
        if (dialogView == null) return;

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView btnBack = dialogView.findViewById(R.id.btn_back);
        CheckBox cbAccept = dialogView.findViewById(R.id.cb_accept_terms);

        if (btnBack != null) btnBack.setOnClickListener(v -> dialog.dismiss());
        if (cbAccept != null) {
            cbAccept.setChecked(isTermsAccepted);
            cbAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isTermsAccepted = isChecked;
                updateMainCheckbox();
            });
        }

        dialog.show();
    }

    private void showPrivacyPolicyDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.privacy_and_policy_dialog, null);
        if (dialogView == null) return;

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView btnBack = dialogView.findViewById(R.id.btn_back_privacy);
        CheckBox cbAccept = dialogView.findViewById(R.id.cb_accept_privacy);

        if (btnBack != null) btnBack.setOnClickListener(v -> dialog.dismiss());
        if (cbAccept != null) {
            cbAccept.setChecked(isPrivacyAccepted);
            cbAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
                isPrivacyAccepted = isChecked;
                updateMainCheckbox();
            });
        }

        dialog.show();
    }

    private void showRegistrationSuccessDialog() {
        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_LONG).show();
    }
}
