package com.example.civireports;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.civireports.models.Announcement;
import com.example.civireports.network.ApiService;
import com.example.civireports.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AnnouncementActivity extends AppCompatActivity {

    private LinearLayout navHome, navHotlines, navNotification, navProfile;
    private ImageView btnNotificationHeader;
    private TextView tvEventCount;
    private LinearLayout notifContainer;
    private TextView tabAll, tabCommunity, tabHealth, tabEducation, tabCulture;

    private final List<Announcement> allAnnouncements = new ArrayList<>();
    private String activeCategory = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);

        initViews();
        setupBottomNav();
        setupTabs();
        fetchAnnouncements();
    }

    private void initViews() {
        navHome         = findViewById(R.id.navHome);
        navHotlines     = findViewById(R.id.navHotlines);
        navNotification = findViewById(R.id.navNotification);
        navProfile      = findViewById(R.id.navProfile);
        btnNotificationHeader = findViewById(R.id.btnNotificationHeader);
        tvEventCount    = findViewById(R.id.tvEventCount);
        notifContainer  = findViewById(R.id.notifContainer);
        tabAll          = findViewById(R.id.tabAll);
        tabCommunity    = findViewById(R.id.tabCommunity);
        tabHealth       = findViewById(R.id.tabHealth);
        tabEducation    = findViewById(R.id.tabEducation);
        tabCulture      = findViewById(R.id.tabCulture);
    }

    private void setupTabs() {
        tabAll.setOnClickListener(v       -> setActiveTab(tabAll,       "All"));
        tabCommunity.setOnClickListener(v -> setActiveTab(tabCommunity, "community"));
        tabHealth.setOnClickListener(v    -> setActiveTab(tabHealth,    "health"));
        tabEducation.setOnClickListener(v -> setActiveTab(tabEducation, "education"));
        tabCulture.setOnClickListener(v   -> setActiveTab(tabCulture,   "culture"));

        // Set default active tab
        setActiveTab(tabAll, "All");
    }

    private void setActiveTab(TextView selected, String category) {
        activeCategory = category;

        for (TextView tab : new TextView[]{tabAll, tabCommunity, tabHealth, tabEducation, tabCulture}) {
            tab.setBackgroundResource(R.drawable.bg_tab_inactive);
            tab.setTextColor(0xFF1A3A6B);
        }
        selected.setBackgroundResource(R.drawable.bg_tab_active);
        selected.setTextColor(0xFFFFFFFF);

        showAnnouncements();
    }

    private void fetchAnnouncements() {
        ApiService api = RetrofitClient.getApiService(this);
        api.getAnnouncements().enqueue(new Callback<List<Announcement>>() {
            @Override
            public void onResponse(Call<List<Announcement>> call, Response<List<Announcement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allAnnouncements.clear();
                    allAnnouncements.addAll(response.body());
                    showAnnouncements();
                } else {
                    Toast.makeText(AnnouncementActivity.this, "Failed to load announcements", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Announcement>> call, Throwable t) {
                Toast.makeText(AnnouncementActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAnnouncements() {
        notifContainer.removeAllViews();

        List<Announcement> filtered = new ArrayList<>();
        for (Announcement a : allAnnouncements) {
            if (activeCategory.equals("All") ||
                    activeCategory.equalsIgnoreCase(a.getCategory())) {
                filtered.add(a);
            }
        }

        int count = filtered.size();
        tvEventCount.setText(count == 1 ? "Showing 1 Event" : "Showing " + count + " Events");

        for (Announcement a : filtered) {
            notifContainer.addView(buildCard(a));
        }
    }

    private View buildCard(Announcement a) {
        LinearLayout wrapper = new LinearLayout(this);
        LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        wp.setMargins(0, 0, 0, dpToPx(12));
        wrapper.setLayoutParams(wp);

        CardView card = new CardView(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        card.setRadius(dpToPx(14));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(0xFFFFFFFF);
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        int p = dpToPx(16);
        inner.setPadding(p, p, p, p);

        // Category badge
        if (a.getCategory() != null && !a.getCategory().isEmpty()) {
            TextView badge = new TextView(this);
            LinearLayout.LayoutParams badgeP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            badgeP.setMargins(0, 0, 0, dpToPx(8));
            badge.setLayoutParams(badgeP);
            badge.setBackgroundResource(R.drawable.bg_tab_inactive);
            badge.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
            badge.setText(capitalize(a.getCategory()));
            badge.setTextColor(0xFF1A3A6B);
            badge.setTextSize(11);
            inner.addView(badge);
        }

        // Title
        TextView title = new TextView(this);
        LinearLayout.LayoutParams titleP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleP.setMargins(0, 0, 0, dpToPx(6));
        title.setLayoutParams(titleP);
        title.setText(a.getTitle());
        title.setTextColor(0xFF1A1A2E);
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);

        // Description
        TextView desc = new TextView(this);
        LinearLayout.LayoutParams descP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descP.setMargins(0, 0, 0, dpToPx(10));
        desc.setLayoutParams(descP);
        desc.setText(a.getDescription());
        desc.setTextColor(0xFF555555);
        desc.setTextSize(13);
        desc.setMaxLines(3);
        desc.setEllipsize(TextUtils.TruncateAt.END);
        desc.setLineSpacing(0, 1.3f);

        // Event Date
        TextView date = new TextView(this);
        date.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        date.setText("📅  " + a.getEventDate());
        date.setTextColor(0xFF444444);
        date.setTextSize(12);

        // Venue
        TextView venue = new TextView(this);
        LinearLayout.LayoutParams venueP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        venueP.setMargins(0, dpToPx(4), 0, 0);
        venue.setLayoutParams(venueP);
        venue.setText("📍  " + a.getVenue());
        venue.setTextColor(0xFF444444);
        venue.setTextSize(12);

        inner.addView(title);
        inner.addView(desc);
        inner.addView(date);
        inner.addView(venue);
        card.addView(inner);
        wrapper.addView(card);

        card.setOnClickListener(v -> showAnnouncementModal(a));

        return wrapper;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private void showAnnouncementModal(Announcement a) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.announcement_detail);

        TextView detailTitle       = dialog.findViewById(R.id.detailTitle);
        TextView detailDescription = dialog.findViewById(R.id.detailDescription);
        TextView detailDate        = dialog.findViewById(R.id.detailDate);
        TextView detailLocation    = dialog.findViewById(R.id.detailLocation);

        detailTitle.setText(a.getTitle());
        detailDescription.setText(a.getDescription());
        detailDate.setText(a.getEventDate());
        detailLocation.setText(a.getVenue());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
            dialog.setCanceledOnTouchOutside(true);
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        }

        dialog.show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void setupBottomNav() {
        if (btnNotificationHeader != null) {
            btnNotificationHeader.setOnClickListener(v -> NotificationModalHelper.show(this));
        }

        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));
        navHotlines.setOnClickListener(v ->
                startActivity(new Intent(this, hotlines.class)));
        navNotification.setOnClickListener(v -> { });
        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));
    }
}