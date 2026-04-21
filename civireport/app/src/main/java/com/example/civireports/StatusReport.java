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
import com.example.civireports.models.AiRecommendationResponse;
import com.example.civireports.models.ComplaintStatusUpdate;
import com.example.civireports.models.MessageResponse;
import com.example.civireports.models.UserComplaint;
import com.example.civireports.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.civireports.models.ChatRequest;
import com.example.civireports.models.ChatResponse;
import android.util.Log;

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

        View layoutInProgressSat       = itemView.findViewById(R.id.layoutInProgressSatisfaction);
        MaterialButton btnSatYes       = itemView.findViewById(R.id.btnSatisfiedYes);
        MaterialButton btnSatNo        = itemView.findViewById(R.id.btnSatisfiedNo);
        TextInputLayout tilSatFeedback = itemView.findViewById(R.id.tilSatisfactionFeedback);
        MaterialButton btnSubmitSat    = itemView.findViewById(R.id.btnSubmitSatisfaction);

        tvQueueNumber.setText(complaint.getQueueNumber());

        String urgency = complaint.getUrgencyLevel();
        if ("emergency".equalsIgnoreCase(urgency) || "critical".equalsIgnoreCase(urgency)) {
            tvQueueNumber.setTextColor(Color.parseColor("#E53935"));
        } else if ("priority".equalsIgnoreCase(urgency) || "medium".equalsIgnoreCase(urgency)) {
            tvQueueNumber.setTextColor(Color.parseColor("#FB8C00"));
        } else {
            tvQueueNumber.setTextColor(Color.parseColor("#43A047"));
        }

        tvComplaintType.setText(complaint.getComplaintType());
        tvSpecificIssue.setText(complaint.getComplaintSubtype());
        tvAddress.setText(complaint.getComplaintLocation());
        tvNotes.setText(complaint.getAdditionalNotes());
        tvDateReported.setText(formatDate(complaint.getComplaintDate()));
        fetchAiRecommendation(complaint, tvAiRecommendation);

        String status = normalizeStatus(complaint.getComplaintStatus());
        tvStatus.setText(complaint.getFormattedStatus());
        setStatusStyle(tvStatus, status);

        Log.d("STATUS_DEBUG", "RAW: " + complaint.getComplaintStatus());
        Log.d("STATUS_DEBUG", "NORMALIZED: " + status);

        // Show satisfaction section only when in_progress
        if (status.equals("in_progress") || status.equals("processing")) {
            layoutInProgressSat.setVisibility(View.VISIBLE);
        } else {
            layoutInProgressSat.setVisibility(View.GONE);
        }

        // Show confirm button only when resolved
        if (status.equals("resolved")) {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#003EAB")));
            btnConfirm.setVisibility(View.VISIBLE);
        } else {
            btnConfirm.setEnabled(false);
            btnConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A8C2F8")));
            btnConfirm.setVisibility(View.GONE);
        }

        // Approve — mark as resolved
        btnSatYes.setOnClickListener(v -> {
            RetrofitClient.getApiService(this)
                    .resolveComplaint(complaint.getComplaintId(), new ComplaintStatusUpdate("approve"))
                    .enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            Toast.makeText(StatusReport.this, "Complaint marked as resolved!", Toast.LENGTH_SHORT).show();
                            tilSatFeedback.setVisibility(View.GONE);
                            btnSubmitSat.setVisibility(View.GONE);
                            layoutInProgressSat.setVisibility(View.GONE);
                            tvStatus.setText("RESOLVED");
                            setStatusStyle(tvStatus, "resolved");
                            btnConfirm.setEnabled(true);
                            btnConfirm.setVisibility(View.VISIBLE);
                            btnConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#003EAB")));
                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            Toast.makeText(StatusReport.this, "Failed to update status.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Show feedback field
        btnSatNo.setOnClickListener(v -> {
            tilSatFeedback.setVisibility(View.VISIBLE);
            btnSubmitSat.setVisibility(View.VISIBLE);
        });

        // Not satisfied — revert to pending + save feedback
        btnSubmitSat.setOnClickListener(v -> {
            String feedback = "";
            if (tilSatFeedback.getEditText() != null) {
                feedback = tilSatFeedback.getEditText().getText().toString().trim();
            }

            if (feedback.isEmpty()) {
                tilSatFeedback.setError("Please tell admin why you are not satisfied.");
                return;
            }

            tilSatFeedback.setError(null);
            final String finalFeedback = feedback;

            RetrofitClient.getApiService(this)
                    .resolveComplaint(complaint.getComplaintId(),
                            new ComplaintStatusUpdate("not_satisfied", finalFeedback))
                    .enqueue(new Callback<MessageResponse>() {
                        @Override
                        public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                            Toast.makeText(StatusReport.this, "Feedback sent! Complaint resubmitted.", Toast.LENGTH_SHORT).show();
                            layoutInProgressSat.setVisibility(View.GONE);
                            tvStatus.setText("PENDING");
                            setStatusStyle(tvStatus, "pending");
                            tilSatFeedback.setVisibility(View.GONE);
                            btnSubmitSat.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailure(Call<MessageResponse> call, Throwable t) {
                            Toast.makeText(StatusReport.this, "Failed to send feedback.", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    private String normalizeStatus(String status) {
        if (status == null) return "";
        return status.toLowerCase().trim().replace(" ", "_");
    }

    private void fetchAiRecommendation(UserComplaint complaint, TextView tvAiRecommendation) {
        if (complaint.getAiReplyBullets() != null && !complaint.getAiReplyBullets().isEmpty()) {
            displayAiRecommendation(tvAiRecommendation,
                    complaint.getAiDetectedCategory(),
                    complaint.getAiUrgency(),
                    complaint.getAiRecommendedOffice(),
                    complaint.getAiReplyBullets(),
                    complaint.getAiSuggestedActions());
            return;
        }

        tvAiRecommendation.setText("Loading AI recommendation...");

        RetrofitClient.getApiService(this)
                .generateAiRecommendation(complaint.getComplaintId())
                .enqueue(new Callback<AiRecommendationResponse>() {
                    @Override
                    public void onResponse(Call<AiRecommendationResponse> call,
                                           Response<AiRecommendationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AiRecommendationResponse ai = response.body();
                            displayAiRecommendation(tvAiRecommendation,
                                    ai.getAiDetectedCategory(),
                                    ai.getAiUrgency(),
                                    ai.getAiRecommendedOffice(),
                                    ai.getAiReplyBullets(),
                                    ai.getAiSuggestedActions());
                        } else {
                            tvAiRecommendation.setText("AI recommendation unavailable.");
                        }
                    }

                    @Override
                    public void onFailure(Call<AiRecommendationResponse> call, Throwable t) {
                        tvAiRecommendation.setText("Could not load AI recommendation.");
                    }
                });
    }

    private void displayAiRecommendation(TextView tv, String category, String urgency,
                                         String office, String bulletsRaw, String actionsRaw) {
        StringBuilder sb = new StringBuilder();
        sb.append("📋 Category: ").append(category != null ? category : "—").append("\n");
        sb.append("⚠️ Urgency: ").append(urgency != null ? urgency : "—").append("\n");
        sb.append("🏢 Recommended Office: ").append(office != null ? office : "—").append("\n\n");

        sb.append("📌 Assessment:\n");
        if (bulletsRaw != null) {
            for (String bullet : bulletsRaw.split("\\|")) {
                sb.append("• ").append(bullet.trim()).append("\n");
            }
        }

        sb.append("\n✅ Suggested Actions:\n");
        if (actionsRaw != null) {
            for (String action : actionsRaw.split("\\|")) {
                sb.append("• ").append(action.trim()).append("\n");
            }
        }

        tv.setText(sb.toString().trim());
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

            MaterialButton btnConfirm      = itemView.findViewById(R.id.btnConfirmFinished);
            View layoutRating              = itemView.findViewById(R.id.layoutRating);
            View layoutInProgressSat       = itemView.findViewById(R.id.layoutInProgressSatisfaction);
            MaterialButton btnSatYes       = itemView.findViewById(R.id.btnSatisfiedYes);
            MaterialButton btnSatNo        = itemView.findViewById(R.id.btnSatisfiedNo);
            TextInputLayout tilSatFeedback = itemView.findViewById(R.id.tilSatisfactionFeedback);
            MaterialButton btnSubmitSat    = itemView.findViewById(R.id.btnSubmitSatisfaction);

            if ("IN PROGRESS".equalsIgnoreCase(status) || "PROCESSING".equalsIgnoreCase(status)) {
                layoutInProgressSat.setVisibility(View.VISIBLE);
            } else {
                layoutInProgressSat.setVisibility(View.GONE);
            }

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
            });

            btnSatNo.setOnClickListener(v -> {
                tilSatFeedback.setVisibility(View.VISIBLE);
                btnSubmitSat.setVisibility(View.VISIBLE);
            });

            btnSubmitSat.setOnClickListener(v -> {
                Toast.makeText(this, "Thank you for your feedback.", Toast.LENGTH_SHORT).show();
                layoutInProgressSat.setVisibility(View.GONE);
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
        switch (status.toLowerCase()) {
            case "reject":
            case "rejected":
                tv.setBackgroundResource(R.drawable.bg_status_rejected); break;
            case "resolved":
            case "approved":
            case "solved":
            case "finished":
            case "completed":
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