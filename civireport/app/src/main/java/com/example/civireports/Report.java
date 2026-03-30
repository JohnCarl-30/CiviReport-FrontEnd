package com.example.civireports;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.civireports.models.ComplaintResponse;
import com.example.civireports.network.RetrofitClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Report extends AppCompatActivity {

    private Spinner spinnerCategory;
    private Spinner spinnerSpecificIssue;
    private View layoutSpinnerSpecificIssue;
    private EditText etCustomSpecificIssue;
    private CheckBox checkboxCertify;
    private Button btnCancel;
    private Button btnSubmit;
    private LinearLayout btnUploadFile;
    private TextView tvUploadStatus;
    private EditText etAddress;
    private EditText etNotes;

    private Uri selectedFileUri;

    // Gallery Launcher
    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    String fileName = getFileName(uri);
                    tvUploadStatus.setText("Selected: " + fileName);
                }
            }
    );

    // Camera Launcher
    private final ActivityResultLauncher<Void> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            bitmap -> {
                if (bitmap != null) {
                    selectedFileUri = saveBitmapToInternalStorage(bitmap);
                    tvUploadStatus.setText("Captured: Photo from Camera");
                }
            }
    );

    // Permission Launcher for the upload feature
    private final ActivityResultLauncher<String[]> uploadPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean cameraGranted = Boolean.TRUE.equals(result.get(Manifest.permission.CAMERA));
                boolean storageGranted;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    storageGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_MEDIA_IMAGES));
                } else {
                    storageGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
                }

                if (cameraGranted && storageGranted) {
                    showUploadDialog();
                } else {
                    Toast.makeText(this, "Permissions are required to upload files.", Toast.LENGTH_LONG).show();
                }
            }
    );

    private final Map<String, List<String>> issueMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_page);

        initViews();
        buildIssueMap();
        setupCategorySpinner();
        setupButtons();
        setupPlaceholderBehaviors();
    }

    private void handleComplaint(Button submit) {
        submit.setEnabled(false);

        RequestBody rbType = RequestBody.create(MediaType.parse("text/plain"), spinnerCategory.getSelectedItem().toString());
        RequestBody rbSubtype = RequestBody.create(MediaType.parse("text/plain"), spinnerSpecificIssue.getSelectedItem().toString());
        RequestBody rbNotes = RequestBody.create(MediaType.parse("text/plain"), etNotes.getText().toString().trim());
        RequestBody rbLocation = RequestBody.create(MediaType.parse("text/plain"), etAddress.getText().toString().trim());


        List<MultipartBody.Part> fileParts = new ArrayList<>();
        if (selectedFileUri != null) {
            File file = new File(selectedFileUri.getPath());
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
            fileParts.add(MultipartBody.Part.createFormData("files", file.getName(), fileBody));
        }

        RetrofitClient.getApiService(this).submitComplaint(rbType, rbSubtype, rbNotes, rbLocation, fileParts)
                .enqueue(new Callback<ComplaintResponse>() {
                    @Override
                    public void onResponse(Call<ComplaintResponse> call, Response<ComplaintResponse> response) {
                        submit.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(Report.this, "Complaint submitted!", Toast.LENGTH_SHORT).show();

                            // Save locally as well for dashboard stats
                            saveToLocalStore();

//                            startActivity(new Intent(Report.this, StatusReport.class));
//                            finish();
                            Intent intent = new Intent(Report.this, StatusReport.class);
                            intent.putExtra("just_submitted", true);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Report.this, "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ComplaintResponse> call, Throwable t) {
                        submit.setEnabled(true);
                        Toast.makeText(Report.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();

                        // Still save locally even if network fails (for testing/demo purposes)
                        saveToLocalStore();
//                        startActivity(new Intent(Report.this, StatusReport.class));
//                        finish();
                        Intent intent = new Intent(Report.this, StatusReport.class);
                        intent.putExtra("just_submitted", true);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private void saveToLocalStore() {
        String selectedCategory = spinnerCategory.getSelectedItem().toString();
        String specificIssue = spinnerSpecificIssue.getSelectedItem().toString();
        if (specificIssue.equals("Other")) {
            specificIssue = etCustomSpecificIssue.getText().toString().trim();
        }

        // Logic to determine priority based on category or issue
        String priority = "Nominal";
        if (selectedCategory.contains("Safety") || selectedCategory.contains("Health")) {
            priority = "Priority";
        }
        if (specificIssue.contains("Theft") || specificIssue.contains("Assault") || specificIssue.contains("Accident")) {
            priority = "Emergency";
        }

        String queueNum = "#" + (int)(Math.random() * 900 + 100);
        String dateStr = "Just now";

        ReportDataStore.ReportItem newItem = new ReportDataStore.ReportItem(
                queueNum,
                "Pending",
                specificIssue.isEmpty() ? selectedCategory : specificIssue,
                priority,
                specificIssue,
                etAddress.getText().toString().trim(),
                etNotes.getText().toString().trim(),
                dateStr,
                selectedFileUri
        );

        ReportDataStore.getInstance().addReport(newItem);
    }

    private void initViews() {
        spinnerCategory     = findViewById(R.id.spinnerCategory);
        spinnerSpecificIssue = findViewById(R.id.spinnerSpecificIssue);
        layoutSpinnerSpecificIssue = findViewById(R.id.layoutSpinnerSpecificIssue);
        etCustomSpecificIssue = findViewById(R.id.etCustomSpecificIssue);
        checkboxCertify     = findViewById(R.id.checkboxCertify);
        btnCancel           = findViewById(R.id.btnCancel);
        btnSubmit           = findViewById(R.id.btnSubmit);
        btnUploadFile       = findViewById(R.id.btnUploadFile);
        tvUploadStatus      = findViewById(R.id.tvUploadStatus);
        etAddress           = findViewById(R.id.etAddress);
        etNotes             = findViewById(R.id.etNotes);
    }

    private void setupPlaceholderBehaviors() {
        setupPlaceholderBehavior(etCustomSpecificIssue);
        setupPlaceholderBehavior(etAddress);
        setupPlaceholderBehavior(etNotes);
    }

    /**
     * Sets up a focus change listener to hide the placeholder when the EditText is focused.
     */
    private void setupPlaceholderBehavior(EditText editText) {
        if (editText == null) return;

        final CharSequence originalHint = editText.getHint();
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setHint("");
            } else {
                editText.setHint(originalHint);
            }
        });
    }

    private void buildIssueMap() {
        issueMap.put("Peace and Order / Safety", Arrays.asList(
                "Select Specific Issue", "Theft (Nakawan)", "Robbery (Holdap)", "Burglary",
                "Physical Assault (Bugbugan)", "Domestic Violence", "Vandalism",
                "Public Disturbance", "Suspicious Person", "Drug Related Activity", "Illegal Gambling", "Other"
        ));
        issueMap.put("Sanitation and Waste Management", Arrays.asList(
                "Select Specific Issue", "Illegal Dumping", "Uncollected Garbage",
                "Improper Waste Segregation", "Clogged Drainage", "Septic Leak / Foul Odor",
                "Open Burning of Trash", "Dead Animal Disposal", "Other"
        ));
        issueMap.put("Flooding and Drainage", Arrays.asList(
                "Select Specific Issue", "Flooded Area", "Blocked Canal", "Overflowing Drain",
                "Water Stagnation", "Erosion", "Other"
        ));
        issueMap.put("Traffic and Road Concerns", Arrays.asList(
                "Select Specific Issue", "Road Obstruction", "Illegal Parking",
                "Potholes / Damaged Road", "Broken Street Light", "Sidewalk Obstruction", "Other"
        ));
        issueMap.put("Animal-Related Concerns", Arrays.asList(
                "Select Specific Issue", "Aggressive Animal", "Animal Cruelty",
                "Pet Noise Complaint", "Improper Disposal of Animal Waste", "Other"
        ));
        issueMap.put("Community and Social Issues", Arrays.asList(
                "Select Specific Issue", "Boundary Dispute", "Family Dispute",
                "Youth Violence", "Harassment", "Substance Abuse", "Elderly Neglect", "Other"
        ));
        issueMap.put("Health & Safety", Arrays.asList(
                "Select Specific Issue", "Dengue Risk Area", "Rabies Case",
                "Unsanitary Food Vendor", "Public Health Violation", "Other"
        ));
    }

    private void setupCategorySpinner() {
        List<String> categories = new ArrayList<>();
        categories.add("Select Type of Complaint");
        categories.addAll(issueMap.keySet());

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
        updateSpecificIssueSpinner(null);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    updateSpecificIssueSpinner(null);
                } else {
                    updateSpecificIssueSpinner(categories.get(position));
                }
                etCustomSpecificIssue.setVisibility(View.GONE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void updateSpecificIssueSpinner(String category) {
        List<String> issues = new ArrayList<>();
        if (category == null || !issueMap.containsKey(category)) {
            issues.add("Select Specific Issue");
        } else {
            issues.addAll(issueMap.get(category));
        }

        ArrayAdapter<String> issueAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, issues);
        issueAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSpecificIssue.setAdapter(issueAdapter);

        spinnerSpecificIssue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedIssue = issues.get(position);
                if (selectedIssue.equals("Other")) {
                    etCustomSpecificIssue.setVisibility(View.VISIBLE);
                } else {
                    etCustomSpecificIssue.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupButtons() {
        btnCancel.setOnClickListener(v -> {
            if (hasProgress()) {
                showDiscardDialog();
            } else {
                finish();
            }
        });

        btnUploadFile.setOnClickListener(v -> {
            if (checkPermissions()) {
                showUploadDialog();
            } else {
                requestUploadPermissions();
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (spinnerCategory.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Please select a type of complaint.", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedCategory = spinnerCategory.getSelectedItem().toString();
            String specificIssue = "";

            if (spinnerSpecificIssue.getSelectedItem().toString().equals("Other")) {
                specificIssue = etCustomSpecificIssue.getText().toString().trim();
                if (specificIssue.isEmpty()) {
                    Toast.makeText(this, "Please specify your issue.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                if (spinnerSpecificIssue.getSelectedItemPosition() == 0) {
                    Toast.makeText(this, "Please select a specific issue.", Toast.LENGTH_SHORT).show();
                    return;
                }
                specificIssue = spinnerSpecificIssue.getSelectedItem().toString();
            }

            if (etAddress.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Please enter the address/location.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!checkboxCertify.isChecked()) {
                Toast.makeText(this, "Please certify that the information is true and correct.", Toast.LENGTH_SHORT).show();
                return;
            }

            handleComplaint(btnSubmit);
        });
    }

    private Uri copyFileToInternalStorage(Uri uri) {
        // If it's already an internal file URI (from camera), no need to copy
        if (uri.toString().contains(getFilesDir().toString())) {
            return uri;
        }

        try {
            InputStream is = getContentResolver().openInputStream(uri);
            if (is == null) return null;

            String fileName = "report_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }

            is.close();
            fos.flush();
            fos.close();

            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Uri saveBitmapToInternalStorage(Bitmap bitmap) {
        try {
            String fileName = "captured_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean hasProgress() {
        boolean categorySelected  = spinnerCategory.getSelectedItemPosition() > 0;
        boolean issueSelected     = spinnerSpecificIssue.getSelectedItemPosition() > 0;
        boolean customIssueFilled = etCustomSpecificIssue != null && !etCustomSpecificIssue.getText().toString().trim().isEmpty();
        boolean addressFilled     = etAddress != null && !etAddress.getText().toString().trim().isEmpty();
        boolean notesFilled       = etNotes != null && !etNotes.getText().toString().trim().isEmpty();
        boolean fileSelected      = selectedFileUri != null;

        return categorySelected || issueSelected || customIssueFilled || addressFilled || notesFilled || fileSelected;
    }

    private void showDiscardDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_discard_report);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        dialog.setCancelable(false);

        Button btnNoStay     = dialog.findViewById(R.id.btnNoStay);
        Button btnYesDiscard = dialog.findViewById(R.id.btnYesDiscard);

        btnNoStay.setOnClickListener(v -> dialog.dismiss());
        btnYesDiscard.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();
    }

    private boolean checkPermissions() {
        boolean cameraGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean storageGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storageGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            storageGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return cameraGranted && storageGranted;
    }

    private void requestUploadPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        uploadPermissionLauncher.launch(permissions.toArray(new String[0]));
    }

    private void showUploadDialog() {
        String[] options = {"Take a Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Attachment Source");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                cameraLauncher.launch(null);
            } else if (which == 1) {
                galleryLauncher.launch("image/*");
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) result = cursor.getString(index);
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) result = result.substring(cut + 1);
        }
        return result;
    }
}
