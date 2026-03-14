package com.example.civireports;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
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

import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, middleNameInput, suffixInput, emailInput, contactInput, passwordInput, confirmPasswordInput;
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

        View mainLayout = findViewById(R.id.register_main);
        LinearLayout formContainer = null; // We'll find this inside ScrollView
        
        // Find the inner LinearLayout of the ScrollView to adjust its padding
        ScrollView scrollView = null;
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

        final LinearLayout finalFormContainer = formContainer;

        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            
            // System bars padding
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            
            // Dynamic padding based on keyboard (IME) visibility
            if (finalFormContainer != null) {
                int bottomPadding;
                if (ime.bottom > 0) {
                    // Keyboard is visible, add 130dp allowance
                    float density = getResources().getDisplayMetrics().density;
                    bottomPadding = (int) (130 * density);
                } else {
                    // Keyboard is hidden, back to normal (30dp)
                    float density = getResources().getDisplayMetrics().density;
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
        middleNameInput = findViewById(R.id.middle_name_input);
        suffixInput = findViewById(R.id.suffix_input);
        emailInput = findViewById(R.id.email_input);
        contactInput = findViewById(R.id.contact_input);
        passwordInput = findViewById(R.id.password_input);
        confirmPasswordInput = findViewById(R.id.confirm_password_input);
        termsCheckbox = findViewById(R.id.terms_checkbox);
        
        // Main checkbox is read-only so it can only be checked via the dialogs
        if (termsCheckbox != null) {
            termsCheckbox.setClickable(false);
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
        if (termsCheckbox != null && !termsCheckbox.isChecked()) { Toast.makeText(this, "Please read and agree to the Terms and Privacy Policy", Toast.LENGTH_SHORT).show(); isValid = false; }

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
    }

    private void updateMainCheckbox() {
        if (termsCheckbox != null) {
            termsCheckbox.setChecked(isTermsAccepted && isPrivacyAccepted);
        }
    }

    private void showTermsDialog() {
        // Implementation remains same
    }

    private void showPrivacyPolicyDialog() {
        // Implementation remains same
    }

    private void showRegistrationSuccessDialog() {
        // Implementation remains same
    }
}
