package com.example.civireports;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.civireports.models.LoginResponse;
import com.example.civireports.network.ApiService;
import com.example.civireports.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private boolean isPasswordVisible = false;

    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean allGranted = true;
                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    if (!entry.getValue()) {
                        allGranted = false;
                        break;
                    }
                }

                if (!allGranted) {
                    Toast.makeText(this, "Notifications are disabled. You might miss important updates.", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this) is removed to improve compatibility with emulator rendering
        setContentView(R.layout.activity_login_page);

        // Initialize EditTexts
        EditText emailInput = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordInput = findViewById(R.id.editTextTextPassword);
        ImageView showPasswordBtn = findViewById(R.id.show_password_button);

        setupHintBehavior(emailInput);
        setupHintBehavior(passwordInput);

        // Show/Hide Password Toggle
        showPasswordBtn.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Hide password
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordBtn.setImageResource(android.R.drawable.ic_menu_view); 
                showPasswordBtn.setAlpha(0.5f);
            } else {
                // Show password
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordBtn.setImageResource(android.R.drawable.ic_menu_view);
                showPasswordBtn.setAlpha(1.0f);
            }
            isPasswordVisible = !isPasswordVisible;
            // Move cursor to end
            passwordInput.setSelection(passwordInput.getText().length());
        });

        Button loginButton = findViewById(R.id.login_button);

        //loginButton.setOnClickListener(v -> handleLogin(loginButton, emailInput, passwordInput));
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
        });

        TextView forgotPassword = findViewById(R.id.forgot_password_textview);
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Request permissions after UI is initialized to avoid rendering hangs
        requestInitialPermissions();
    }

    private void setupHintBehavior(EditText editText) {
        if (editText == null) return;
        String originalHint = editText.getHint() != null ? editText.getHint().toString() : "";
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setHint("");
            } else {
                editText.setHint(originalHint);
            }
        });
    }

    private void requestInitialPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        if (!permissionsNeeded.isEmpty()) {
            permissionLauncher.launch(permissionsNeeded.toArray(new String[0]));
        }
    }

    private void handleLogin(Button loginButton, EditText emailInput, EditText passwordInput) {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);

        // BAGO
        ApiService api = RetrofitClient.getApiService(MainActivity.this);
        api.login(email, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loginButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    int userId = response.body().getUserId();


                    // I-save ang token sa SharedPreferences
                    getSharedPreferences("auth", MODE_PRIVATE)
                            .edit()
                            .putString("token", token)
                            .putInt("user_id", userId)
                            .apply();
                            RetrofitClient.reset();

                    Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
