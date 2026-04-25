package com.example.civireports;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.civireports.models.LoginResponse;
import com.example.civireports.network.ApiService;
import com.example.civireports.network.RetrofitClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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

        SharedPreferences authPrefs = getSharedPreferences("auth", MODE_PRIVATE);
        if (authPrefs.contains("token") && !authPrefs.getString("token", "").isEmpty()) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login_page);

        EditText emailInput = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordInput = findViewById(R.id.editTextTextPassword);
        ImageView showPasswordBtn = findViewById(R.id.show_password_button);

        setupHintBehavior(emailInput);
        setupHintBehavior(passwordInput);

        showPasswordBtn.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                showPasswordBtn.setImageResource(android.R.drawable.ic_menu_view);
                showPasswordBtn.setAlpha(0.5f);
            } else {
                passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                showPasswordBtn.setImageResource(android.R.drawable.ic_menu_view);
                showPasswordBtn.setAlpha(1.0f);
            }
            isPasswordVisible = !isPasswordVisible;
            passwordInput.setSelection(passwordInput.getText().length());
        });

        Button loginButton = findViewById(R.id.login_button);
        // loginButton.setOnClickListener(v -> handleLogin(loginButton, emailInput, passwordInput));
        loginButton.setOnClickListener(v -> {
           //Save a dummy token for "Stay Logged In" feature in demo mode
            getSharedPreferences("auth", MODE_PRIVATE)
                    .edit()
                    .putString("token", "dummy_token_for_demo")
                   .apply();

           Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
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

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setEnabled(false);

        ApiService api = RetrofitClient.getApiService(MainActivity.this);
        api.login(email, password).enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                loginButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    int userId = response.body().getUserId();

                    getSharedPreferences("auth", MODE_PRIVATE)
                            .edit()
                            .putString("token", token)
                            .putString("access_token", token)
                            .putInt("user_id", userId)
                            .apply();

                    Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    fetchUserProfile(token);
                } else {
                    Toast.makeText(MainActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                loginButton.setEnabled(true);
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserProfile(String token) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8000/auth/me")
                .get()
                .addHeader("Authorization", "Bearer " + token)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                    finish();
                });
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        org.json.JSONObject json = new org.json.JSONObject(body);

                        String fullName = json.optString("full_name", "");
                        String email = json.optString("email", "");
                        String photoUrl = json.optString("profile_photo_path", "")
                                .replace("127.0.0.1", "10.0.2.2");

                        getSharedPreferences("UserProfile", MODE_PRIVATE)
                                .edit()
                                .putString("full_name", fullName)
                                .putString("email", email)
                                .putString("profile_photo_path", photoUrl)
                                .apply();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(() -> {
                    startActivity(new Intent(MainActivity.this, DashboardActivity.class));
                    finish();
                });
            }
        });
    }
}