package com.example.civireports;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class StatusReport extends AppCompatActivity {

    private ScrollView scrollView;
    private View layoutNoReport;
    private View cardThankYou;
    private LinearLayout reportsList;
    private MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_report);

        initViews();

        ReportDataStore store = ReportDataStore.getInstance();
        if (store.hasReports()) {
            scrollView.setVisibility(View.VISIBLE);
            layoutNoReport.setVisibility(View.GONE);
            
            // Show "Thank You" banner only if we just came from submitting a report
            cardThankYou.setVisibility(View.VISIBLE);

            displayAllReports(store.getAllReports());
        } else {
            scrollView.setVisibility(View.GONE);
            layoutNoReport.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        });
    }

    private void initViews() {
        scrollView     = findViewById(R.id.scrollView);
        layoutNoReport = findViewById(R.id.layoutNoReport);
        cardThankYou   = findViewById(R.id.cardThankYou);
        reportsList    = findViewById(R.id.reportsList);
        btnBack        = findViewById(R.id.btnBack);
    }

    private void displayAllReports(List<ReportDataStore.ReportItem> reports) {
        reportsList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (ReportDataStore.ReportItem report : reports) {
            View itemView = inflater.inflate(R.layout.item_status_report_detail, reportsList, false);

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

            tvQueueNumber.setText(report.getQueueNumber());
            tvComplaintType.setText(report.getComplaintType());
            tvSpecificIssue.setText(report.getSpecificIssue());
            tvAddress.setText(report.getAddress());
            tvNotes.setText(report.getNotes());
            tvDateReported.setText(report.getDate());
            
            // Leave AI recommendation blank as requested for future database implementation
            tvAiRecommendation.setText("");
            
            // Set Status Style
            tvStatus.setText(report.getStatus());
            setStatusStyle(tvStatus, report.getStatus());

            // Handle Image
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

    private void setStatusStyle(TextView tvStatus, String status) {
        switch (status.toUpperCase()) {
            case "REJECTED":
                tvStatus.setBackgroundResource(R.drawable.bg_status_rejected);
                break;
            case "APPROVED":
            case "SOLVED":
            case "FINISHED":
                tvStatus.setBackgroundResource(R.drawable.bg_status_approve);
                break;
            default:
                tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
                break;
        }
    }
}