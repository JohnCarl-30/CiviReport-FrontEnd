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


import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


import java.util.ArrayList;
import java.util.List;


public class Notification extends AppCompatActivity {


    private LinearLayout navHome, navHotlines, navNotification, navProfile;
    private TextView tabAll, tabCommunity, tabHealth, tabEducation, tabCulture;
    private TextView tvEventCount;
    private LinearLayout notifContainer;


    private final List<Announcement> allAnnouncements = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_page);


        initViews();
        setupBottomNav();
        buildDummyData();
        setupTabs();
        showAnnouncements("All");
    }


    private void initViews() {
        navHome         = findViewById(R.id.navHome);
        navHotlines     = findViewById(R.id.navHotlines);
        navNotification = findViewById(R.id.navNotification);
        navProfile      = findViewById(R.id.navProfile);
        tabAll          = findViewById(R.id.tabAll);
        tabCommunity    = findViewById(R.id.tabCommunity);
        tabHealth       = findViewById(R.id.tabHealth);
        tabEducation    = findViewById(R.id.tabEducation);
        tabCulture      = findViewById(R.id.tabCulture);
        tvEventCount    = findViewById(R.id.tvEventCount);
        notifContainer  = findViewById(R.id.notifContainer);
    }


    private void buildDummyData() {
        allAnnouncements.add(new Announcement("Health", "Senior Citizen Health Program",
                "Free Health Check-up and Wellness Program for our Beloved Senior Citizens. Services include free medical consultation, blood pressure monitoring, blood sugar testing, basic health assessment, distribution of vitamins and medicines, and health education on proper nutrition and wellness.",
                "Monday, March 2, 2028 | 6:00AM - 9:00AM", "2nd Floor, Barangay Hall"));


        allAnnouncements.add(new Announcement("Health", "Free Dental Mission",
                "Free dental check-up and tooth extraction for all residents. Bring your Barangay ID and health card. Services are on a first-come, first-served basis.",
                "Friday, March 7, 2028 | 8:00AM - 4:00PM", "Barangay Health Center"));


        allAnnouncements.add(new Announcement("Community", "Barangay General Assembly",
                "All residents are invited to attend the quarterly Barangay General Assembly. Agenda includes updates on barangay projects, budget transparency, and community concerns. Your voice matters!",
                "Saturday, April 5, 2028 | 9:00AM - 12:00PM", "Barangay Basketball Court"));


        allAnnouncements.add(new Announcement("Community", "Clean-Up Drive",
                "Join us for our monthly clean-up drive! Help keep our barangay clean and beautiful. Gloves and trash bags will be provided.",
                "Sunday, March 16, 2028 | 7:00AM - 10:00AM", "Barangay Covered Court"));


        allAnnouncements.add(new Announcement("Education", "Scholarship Application Open",
                "Qualified students may now apply for the Barangay Scholarship Program for SY 2028-2029. Requirements include proof of residency, grades, and financial need. Limited slots available.",
                "Deadline: May 30, 2028 | 8:00AM - 5:00PM", "Barangay Hall, Room 3"));


        allAnnouncements.add(new Announcement("Education", "Livelihood Training",
                "Free skills training on basic electronics repair, cooking, and dressmaking. Open to all residents aged 18-55.",
                "April 10-14, 2028 | 8:00AM - 5:00PM", "Barangay Livelihood Center"));


        allAnnouncements.add(new Announcement("Culture", "Barangay Fiesta Celebration",
                "Come and celebrate our annual Barangay Fiesta! Enjoy cultural shows, food stalls, games, and live performances.",
                "May 15, 2028 | 4:00PM onwards", "Barangay Plaza"));


        allAnnouncements.add(new Announcement("Culture", "Street Dance Competition",
                "Calling all dance groups! Join our annual street dance competition in celebration of Barangay Day.",
                "May 15, 2028 | 2:00PM - 6:00PM", "Main Street"));


        allAnnouncements.add(new Announcement("Community", "Feeding Program",
                "Monthly feeding program for malnourished children ages 2-6. Parents/guardians are required to accompany their children.",
                "Every Saturday | 8:00AM - 10:00AM", "Barangay Day Care Center"));
    }


    private void setupTabs() {
        tabAll.setOnClickListener(v       -> setActiveTab(tabAll,       "All"));
        tabCommunity.setOnClickListener(v -> setActiveTab(tabCommunity, "Community"));
        tabHealth.setOnClickListener(v    -> setActiveTab(tabHealth,    "Health"));
        tabEducation.setOnClickListener(v -> setActiveTab(tabEducation, "Education"));
        tabCulture.setOnClickListener(v   -> setActiveTab(tabCulture,   "Culture"));
    }


    private void setActiveTab(TextView selected, String category) {
        for (TextView tab : new TextView[]{tabAll, tabCommunity, tabHealth, tabEducation, tabCulture}) {
            tab.setBackgroundResource(R.drawable.bg_tab_inactive);
            tab.setTextColor(0xFF1A3A6B);
        }
        selected.setBackgroundResource(R.drawable.bg_tab_active);
        selected.setTextColor(0xFFFFFFFF);
        showAnnouncements(category);
    }


    private void showAnnouncements(String category) {
        notifContainer.removeAllViews();


        List<Announcement> filtered = new ArrayList<>();
        for (Announcement a : allAnnouncements) {
            if (category.equals("All") || a.category.equals(category))
                filtered.add(a);
        }


        int count = filtered.size();
        tvEventCount.setText(count == 1 ? "Showing 1 Event" : "Showing " + count + " Events");


        for (int i = 0; i < filtered.size(); i++) {
            notifContainer.addView(buildCard(filtered.get(i), i));
        }
    }


    private View buildCard(Announcement a, int index) {
        // Wrapper
        LinearLayout wrapper = new LinearLayout(this);
        LinearLayout.LayoutParams wp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        wp.setMargins(0, 0, 0, dpToPx(12));
        wrapper.setLayoutParams(wp);


        // Card
        CardView card = new CardView(this);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        card.setRadius(dpToPx(14));
        card.setCardElevation(dpToPx(2));
        card.setCardBackgroundColor(0xFFFFFFFF);
        card.setClickable(true);
        card.setFocusable(true);


        // Inner
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        int p = dpToPx(16);
        inner.setPadding(p, p, p, p);


        // Badge
        TextView badge = new TextView(this);
        LinearLayout.LayoutParams badgeP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeP.setMargins(0, 0, 0, dpToPx(8));
        badge.setLayoutParams(badgeP);
        badge.setBackgroundResource(R.drawable.bg_tab_inactive);
        badge.setPadding(dpToPx(12), dpToPx(4), dpToPx(12), dpToPx(4));
        badge.setText(a.category);
        badge.setTextColor(0xFF1A3A6B);
        badge.setTextSize(11);


        // Title
        TextView title = new TextView(this);
        LinearLayout.LayoutParams titleP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        titleP.setMargins(0, 0, 0, dpToPx(6));
        title.setLayoutParams(titleP);
        title.setText(a.title);
        title.setTextColor(0xFF1A1A2E);
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);


        // Description (shortened)
        TextView desc = new TextView(this);
        LinearLayout.LayoutParams descP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descP.setMargins(0, 0, 0, dpToPx(10));
        desc.setLayoutParams(descP);
        desc.setText(a.description);
        desc.setTextColor(0xFF555555);
        desc.setTextSize(13);
        desc.setMaxLines(3);
        desc.setEllipsize(TextUtils.TruncateAt.END);
        desc.setLineSpacing(0, 1.3f);


        // Date
        TextView date = new TextView(this);
        LinearLayout.LayoutParams dateP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        date.setLayoutParams(dateP);
        date.setText("📅  " + a.date);
        date.setTextColor(0xFF444444);
        date.setTextSize(12);


        inner.addView(badge);
        inner.addView(title);
        inner.addView(desc);
        inner.addView(date);
        card.addView(inner);
        wrapper.addView(card);


        // Tap card to show modal
        card.setOnClickListener(v -> showAnnouncementModal(a));


        return wrapper;
    }


    private void showAnnouncementModal(Announcement a) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.announcement_detail);


        // Initialize modal views
        TextView detailCategory = dialog.findViewById(R.id.detailCategory);
        TextView detailTitle = dialog.findViewById(R.id.detailTitle);
        TextView detailDescription = dialog.findViewById(R.id.detailDescription);
        TextView detailDate = dialog.findViewById(R.id.detailDate);
        TextView detailLocation = dialog.findViewById(R.id.detailLocation);


        // Set data
        detailCategory.setText(a.category);
        detailTitle.setText(a.title);
        detailDescription.setText(a.description);
        detailDate.setText(a.date);
        detailLocation.setText(a.location);


        // Make it look like a pop modal
        if (dialog.getWindow() != null) {
            // Force the window container to be transparent
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Set width to 90% of screen width to avoid touching the sides
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
            dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);

            // This allows clicking outside to dismiss
            dialog.setCanceledOnTouchOutside(true);

            // Animation (optional)
            dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        }


        dialog.show();
    }


    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }


    static class Announcement {
        String category, title, description, date, location;
        Announcement(String c, String t, String d, String dt, String l) {
            category = c; title = t; description = d; date = dt; location = l;
        }
    }


    private void setupBottomNav() {
        navHome.setOnClickListener(v ->
                startActivity(new Intent(this, DashboardActivity.class)));
        navHotlines.setOnClickListener(v ->
                startActivity(new Intent(this, hotlines.class)));
        navNotification.setOnClickListener(v -> { /* already here */ });
        navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, Profile.class)));
    }
}


