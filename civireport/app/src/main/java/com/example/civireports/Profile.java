package com.example.civireports;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private TextView tvUserName, tvUserEmail;
    private ImageView verifiedBadge, profileImage;
    private SharedPreferences sharedPreferences;
    private CircleImageView dialogProfilePreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Initialize views
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        verifiedBadge = findViewById(R.id.verified_badge);
        profileImage = findViewById(R.id.profile_image);

        profileImage.setOnClickListener(v -> showProfilePreviewDialog());

        setupMenuItems();
        setupBottomNav();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Log out
        ((MaterialButton) findViewById(R.id.btnLogout)).setOnClickListener(v -> {
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void showProfilePreviewDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_profile_preview);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.7f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        dialogProfilePreview = dialog.findViewById(R.id.preview_profile_image);
        FloatingActionButton btnEdit = dialog.findViewById(R.id.btn_edit_profile_pic);

        String imageUriStr = sharedPreferences.getString("profile_image_uri", "");
        if (!imageUriStr.isEmpty()) {
            try {
                dialogProfilePreview.setImageURI(Uri.parse(imageUriStr));
            } catch (Exception e) {
                dialogProfilePreview.setImageResource(R.drawable.ic_profile);
            }
        } else {
            dialogProfilePreview.setImageResource(R.drawable.ic_profile);
        }

        btnEdit.setOnClickListener(v -> {
            if (checkStoragePermission()) {
                openGallery();
            } else {
                requestStoragePermission();
            }
        });

        dialog.show();
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied to access gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            
            // Take persistable permission
            try {
                final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(imageUri, takeFlags);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            sharedPreferences.edit().putString("profile_image_uri", imageUri.toString()).apply();
            
            if (dialogProfilePreview != null) {
                dialogProfilePreview.setImageURI(imageUri);
            }
            profileImage.setImageURI(imageUri);
            
            Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserData();
    }

    private void loadUserData() {
        String name = sharedPreferences.getString("full_name", "Juan Dela Cruz");
        String email = sharedPreferences.getString("email", "JuanDC@gmail.com");
        String imageUriStr = sharedPreferences.getString("profile_image_uri", "");
        
        tvUserName.setText(name);
        tvUserEmail.setText(email);
        
        if (!imageUriStr.isEmpty()) {
            try {
                profileImage.setImageURI(Uri.parse(imageUriStr));
            } catch (Exception e) {
                profileImage.setImageResource(R.drawable.ic_profile);
            }
        } else {
            profileImage.setImageResource(R.drawable.ic_profile);
        }
        
        boolean isVerified = true; 
        if (verifiedBadge != null) {
            verifiedBadge.setVisibility(isVerified ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }

    private void setupMenuItems() {
        findViewById(R.id.menuEditProfile).setOnClickListener(v ->
                startActivity(new Intent(this, EditProfileActivity.class)));

        findViewById(R.id.menuChangePassword).setOnClickListener(v ->
                startActivity(new Intent(this, ProfileChangePassActivity.class)));

        findViewById(R.id.menuReportHistory).setOnClickListener(v ->
                startActivity(new Intent(this, ReportHistoryActivity.class)));

        findViewById(R.id.menuHelp).setOnClickListener(v ->
                startActivity(new Intent(this, HelpSupportActivity.class)));

        findViewById(R.id.menuAbout).setOnClickListener(v ->
                startActivity(new Intent(this, AboutAppActivity.class)));
    }

    private void setupBottomNav() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
                startActivity(new Intent(this, DashboardActivity.class));
                overridePendingTransition(0, 0);
        });

        findViewById(R.id.navHotlines).setOnClickListener(v -> {
                startActivity(new Intent(this, hotlines.class));
                overridePendingTransition(0, 0);
        });

        findViewById(R.id.navNotification).setOnClickListener(v -> {
                startActivity(new Intent(this, Notification.class));
                overridePendingTransition(0, 0);
        });

        findViewById(R.id.navProfile).setOnClickListener(v ->
                Toast.makeText(this, "You are on Profile", Toast.LENGTH_SHORT).show());
    }
}
