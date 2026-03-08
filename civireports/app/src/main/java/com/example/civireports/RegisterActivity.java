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

import com.example.civireports.models.RegisterResponse;
import com.example.civireports.models.RegisterRequest;
import com.example.civireports.network.ApiService;
import com.example.civireports.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private CheckBox termsCheckbox;
    private boolean isTermsAccepted = false;
    private boolean isPrivacyAccepted = false;

    //Input fieldsss NYAHAHA
    private EditText firstNameInput, lastNameInput, middleNameInput;
    private EditText emailInput, contactInput, addressInput;
    private EditText passwordInput, confirmPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Initialize natin here yung fields nyahahaha
        firstNameInput = findViewById(R.id.first_name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        middleNameInput = findViewById(R.id.middle_name_input);
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
        registerSubmitButton.setOnClickListener(v -> handleRegister());
    }

    private void handleRegister(){
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String middleName = middleNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String contact = contactInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();


        //TO CHECK IF LAHAT NG FIELD IS NOT EMPTY
        if (firstName.isEmpty() || lastName.isEmpty() || middleName.isEmpty()|| email.isEmpty()||contact.isEmpty()||address.isEmpty()||password.isEmpty()||confirmPassword.isEmpty()){
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //TO CHECK IF TERMS AY ACCEPTED
        if (!termsCheckbox.isChecked()){
            Toast.makeText(this, "Please accept the Term and Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        // dito naman yung request object natinn nyahahaha
        RegisterRequest request = new RegisterRequest(
                firstName,middleName,lastName,email,contact,address,password,confirmPassword
        );

        //CALL NA NATIN YUNG API
        ApiService apiService = RetrofitClient.getApiService();
        Call<RegisterResponse> call = apiService.register(request);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    //PAG SUCCESS
                    showRegistrationSuccessDialog();
                }else {

                    //PAG may error galing server
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegisterActivity.this,
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
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
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_terms, null);
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
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_privacy_policy, null);
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
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_registration_success, null);

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
