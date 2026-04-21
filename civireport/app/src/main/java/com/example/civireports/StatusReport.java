package com.example.civireports;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.civireports.models.UserComplaint;
import com.example.civireports.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StatusReport extends AppCompatActivity {

    private ScrollView scrollView;
    private View layoutNoReport;
    private View cardThankYou;
    private LinearLayout reportsList;
    private MaterialButton btnBack;

    private boolean justSubmitted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_report);

        justSubmitted = getIntent().getBooleanExtra("just_submitted", false);

        initViews();
        setupButtons();
        fetchComplaints();
    }

    private void initViews() {
        scrollView     = findViewById(R.id.scrollView);
        layoutNoReport = findViewById(R.id.layoutNoReport);
        cardThankYou   = findViewById(R.id.cardThankYou);
        reportsList    = findViewById(R.id.reportsList);
        btnBack        = findViewById(R.id.btnBack);

        cardThankYou.setVisibility(justSubmitted ? View.VISIBLE : View.GONE);
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
    }

    private void fetchComplaints() {
        scrollView.setVisibility(View.GONE);
        layoutNoReport.setVisibility(View.GONE);

        RetrofitClient.getApiService(this)
                .getMyComplaints()
                .enqueue(new Callback<List<UserComplaint>>() {
                    @Override
                    public void onResponse(Call<List<UserComplaint>> call,
                                           Response<List<UserComplaint>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<UserComplaint> complaints = response.body();
                            if (complaints.isEmpty()) {
                                showEmptyState();
                            } else {
                                showComplaints(complaints);
                            }
                        } else if (response.code() == 401) {
                            startActivity(new Intent(StatusReport.this, MainActivity.class));
                            finish();
                        } else {
                            showEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<UserComplaint>> call, Throwable t) {
                        fallbackToLocalStore();
                    }
                });
    }

    private void showComplaints(List<UserComplaint> complaints) {
        scrollView.setVisibility(View.VISIBLE);
        layoutNoReport.setVisibility(View.GONE);
        reportsList.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);
        for (UserComplaint complaint : complaints) {
            View itemView = inflater.inflate(R.layout.item_status_report_detail, reportsList, false);
            bindComplaintView(itemView, complaint);
            reportsList.addView(itemView);
        }
    }

    private void bindComplaintView(View itemView, UserComplaint complaint) {
        TextView tvQueueNumber      = itemView.findViewById(R.id.tvQueueNumber);
        TextView tvStatus           = itemView.findViewById(R.id.tvStatus);
        TextView tvComplaintType    = itemView.findViewById(R.id.tvComplaintType);
        TextView tvSpecificIssue    = itemView.findViewById(R.id.tvSpecificIssue);
        TextView tvAddress          = itemView.findViewById(R.id.tvAddress);
        TextView tvNotes            = itemView.findViewById(R.id.tvNotes);
        TextView tvDateReported     = itemView.findViewById(R.id.tvDateReported);
        TextView tvAiRecommendation = itemView.findViewById(R.id.tvAiRecommendation);
        ImageView ivUploadedFile    = itemView.findViewById(R.id.ivUploadedFile);
        TextView tvNoFile           = itemView.findViewById(R.id.tvNoFile);
        
        MaterialButton btnConfirm   = itemView.findViewById(R.id.btnConfirmFinished);
        View layoutRating           = itemView.findViewById(R.id.layoutRating);
        
        View layoutInProgressSat    = itemView.findViewById(R.id.layoutInProgressSatisfaction);
        MaterialButton btnSatYes    = itemView.findViewById(R.id.btnSatisfiedYes);
        MaterialButton btnSatNo     = itemView.findViewById(R.id.btnSatisfiedNo);
        TextInputLayout tilSatFeedback = itemView.findViewById(R.id.tilSatisfactionFeedback);
        MaterialButton btnSubmitSat = itemView.findViewById(R.id.btnSubmitSatisfaction);

        tvQueueNumber.setText(complaint.getQueueNumber());
        
        String urgency = complaint.getUrgencyLevel();
        if ("emergency".equalsIgnoreCase(urgency)) {
            tvQueueNumber.setTextColor(Color.parseColor("#E53935"));
        } else if ("priority".equalsIgnoreCase(urgency)) {
            tvQueueNumber.setTextColor(Color.parseColor("#FB8C00"));
        } else {
            tvQueueNumber.setTextColor(Color.parseColor("#43A047"));
        }

        tvComplaintType.setText(complaint.getComplaintType());
        tvSpecificIssue.setText(complaint.getComplaintSubtype());
        tvAddress.setText(complaint.getComplaintLocation());
        tvNotes.setText(complaint.getAdditionalNotes());
        tvDateReported.setText(formatDate(complaint.getComplaintDate()));
        tvAiRecommendation.setText("");

        String status = complaint.getComplaintStatus();
        tvStatus.setText(complaint.getFormattedStatus());
        setStatusStyle(tvStatus, status);

        // Logic for "In Progress" Satisfaction Question
        if ("IN PROGRESS".equalsIgnoreCase(status) || "PROCESSING".equalsIgnoreCase(status)) {
            layoutInProgressSat.setVisibility(View.VISIBLE);
        } else {
            layoutInProgressSat.setVisibility(View.GONE);
        }

        // Setup the "Confirm Finished" appearance
        if ("RESOLVED".equalsIgnoreCase(status)) {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#003EAB")));
            btnConfirm.setVisibility(View.VISIBLE);
        } else {
            btnConfirm.setEnabled(false);
            btnConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A8C2F8")));
            btnConfirm.setVisibility(View.GONE);
        }

        btnSatYes.setOnClickListener(v -> {
            tilSatFeedback.setVisibility(View.GONE);
            btnSubmitSat.setVisibility(View.GONE);
            Toast.makeText(this, "Glad to hear you are satisfied!", Toast.LENGTH_SHORT).show();
            layoutInProgressSat.setVisibility(View.GONE);
            if (!btnConfirm.isEnabled()) {
                btnConfirm.setVisibility(View.VISIBLE);
            }
        });

        btnSatNo.setOnClickListener(v -> {
            tilSatFeedback.setVisibility(View.VISIBLE);
            btnSubmitSat.setVisibility(View.VISIBLE);
        });

        btnSubmitSat.setOnClickListener(v -> {
            Toast.makeText(this, "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
            layoutInProgressSat.setVisibility(View.GONE);
            if (!btnConfirm.isEnabled()) {
                btnConfirm.setVisibility(View.VISIBLE);
            }
        });

        btnConfirm.setOnClickListener(v -> {
            btnConfirm.setVisibility(View.GONE);
            layoutRating.setVisibility(View.VISIBLE);
        });

        String imageUrl = complaint.getFirstImageUrl("http://10.0.2.2:8000");
        if (imageUrl != null) {
            ivUploadedFile.setVisibility(View.VISIBLE);
            tvNoFile.setVisibility(View.GONE);
            Glide.with(this)
                    .load(imageUrl)
                    .centerCrop()
                    .into(ivUploadedFile);
        } else {
            ivUploadedFile.setVisibility(View.GONE);
            tvNoFile.setVisibility(View.VISIBLE);
        }
    }

    private void fallbackToLocalStore() {
        ReportDataStore store = ReportDataStore.getInstance();
        if (store.hasReports()) {
            scrollView.setVisibility(View.VISIBLE);
            layoutNoReport.setVisibility(View.GONE);
            displayLocalReports(store.getAllReports());
        } else {
            showEmptyState();
        }
    }

    private void displayLocalReports(List<ReportDataStore.ReportItem> reports) {
        reportsList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (ReportDataStore.ReportItem report : reports) {
            View itemView = inflater.inflate(R.layout.item_status_report_detail, reportsList, false);

            TextView tvQueueNumber = itemView.findViewById(R.id.tvQueueNumber);
            tvQueueNumber.setText(report.getQueueNumber());

            String priority = report.getPriority();
            if ("Emergency".equalsIgnoreCase(priority)) {
                tvQueueNumber.setTextColor(Color.parseColor("#E53935"));
            } else if ("Priority".equalsIgnoreCase(priority)) {
                tvQueueNumber.setTextColor(Color.parseColor("#FB8C00"));
            } else {
                tvQueueNumber.setTextColor(Color.parseColor("#43A047"));
            }

            ((TextView) itemView.findViewById(R.id.tvComplaintType)).setText(report.getComplaintType());
            ((TextView) itemView.findViewById(R.id.tvSpecificIssue)).setText(report.getSpecificIssue());
            ((TextView) itemView.findViewById(R.id.tvAddress)).setText(report.getAddress());
            ((TextView) itemView.findViewById(R.id.tvNotes)).setText(report.getNotes());
            ((TextView) itemView.findViewById(R.id.tvDateReported)).setText(report.getDate());
            ((TextView) itemView.findViewById(R.id.tvAiRecommendation)).setText("");

            TextView tvStatus = itemView.findViewById(R.id.tvStatus);
            String status = report.getStatus();
            tvStatus.setText(status);
            setStatusStyle(tvStatus, status);

            MaterialButton btnConfirm = itemView.findViewById(R.id.btnConfirmFinished);
            View layoutRating         = itemView.findViewById(R.id.layoutRating);
            
            View layoutInProgressSat    = itemView.findViewById(R.id.layoutInProgressSatisfaction);
            MaterialButton btnSatYes    = itemView.findViewById(R.id.btnSatisfiedYes);
            MaterialButton btnSatNo     = itemView.findViewById(R.id.btnSatisfiedNo);
            TextInputLayout tilSatFeedback = itemView.findViewById(R.id.tilSatisfactionFeedback);
            MaterialButton btnSubmitSat = itemView.findViewById(R.id.btnSubmitSatisfaction);

            // Logic for "In Progress" Satisfaction Question (Local Data)
            if ("IN PROGRESS".equalsIgnoreCase(status) || "PROCESSING".equalsIgnoreCase(status)) {
                layoutInProgressSat.setVisibility(View.VISIBLE);
            } else {
                layoutInProgressSat.setVisibility(View.GONE);
            }

            // Setup the "Confirm Finished" appearance
            if ("RESOLVED".equalsIgnoreCase(status)) {
                btnConfirm.setEnabled(true);
                btnConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#003EAB")));
                btnConfirm.setVisibility(View.VISIBLE);
            } else {
                btnConfirm.setEnabled(false);
                btnConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A8C2F8")));
                btnConfirm.setVisibility(View.GONE); 
            }

            btnSatYes.setOnClickListener(v -> {
                tilSatFeedback.setVisibility(View.GONE);
                btnSubmitSat.setVisibility(View.GONE);
                Toast.makeText(this, "Glad to hear you are satisfied!", Toast.LENGTH_SHORT).show();
                layoutInProgressSat.setVisibility(View.GONE);
                if (!btnConfirm.isEnabled()) {
                    btnConfirm.setVisibility(View.VISIBLE);
                }
            });

            btnSatNo.setOnClickListener(v -> {
                tilSatFeedback.setVisibility(View.VISIBLE);
                btnSubmitSat.setVisibility(View.VISIBLE);
            });

            btnSubmitSat.setOnClickListener(v -> {
                Toast.makeText(this, "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
                layoutInProgressSat.setVisibility(View.GONE);
                if (!btnConfirm.isEnabled()) {
                    btnConfirm.setVisibility(View.VISIBLE);
                }
            });

            btnConfirm.setOnClickListener(v -> {
                btnConfirm.setVisibility(View.GONE);
                layoutRating.setVisibility(View.VISIBLE);
            });

            ImageView ivUploadedFile = itemView.findViewById(R.id.ivUploadedFile);
            TextView tvNoFile        = itemView.findViewById(R.id.tvNoFile);
            if (report.getImageUri() != null) {
                ivUploadedFile.setImageURI(report.getImageUri());
                ivUploadedFile.setVisibility(View.VISIBLE);
                tvNoFile.setVisibility(View.GONE);
            } else {
                ivUploadedFile.setVisibility(View.GONE);
                tvNoFile.setVisibility(View.VISIBLE);
            }

            reportsList.addView(itemView);
        }
    }

    private void showEmptyState() {
        scrollView.setVisibility(View.GONE);
        layoutNoReport.setVisibility(View.VISIBLE);
    }

    private void setStatusStyle(TextView tv, String status) {
        if (status == null) { tv.setBackgroundResource(R.drawable.bg_status_pending); return; }
        switch (status.toUpperCase()) {
            case "REJECT":
            case "REJECTED":
                tv.setBackgroundResource(R.drawable.bg_status_rejected); break;
            case "RESOLVED":
            case "APPROVED": case "SOLVED": case "FINISHED": case "COMPLETED":
                tv.setBackgroundResource(R.drawable.bg_status_approve);  break;
            default:
                tv.setBackgroundResource(R.drawable.bg_status_pending);  break;
        }
    }

    private String formatDate(String rawDate) {
        if (rawDate == null) return "—";
        try {
            java.text.SimpleDateFormat input  = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.text.SimpleDateFormat output = new java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault());
            return output.format(input.parse(rawDate));
        } catch (Exception e) {
            return rawDate;
        }
    }
}