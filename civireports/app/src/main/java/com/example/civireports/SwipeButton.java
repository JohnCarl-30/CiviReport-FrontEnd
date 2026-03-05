package com.example.civireports;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class SwipeButton extends FrameLayout {

    private ImageView slidingThumb;
    private TextView centerText;
    private float initialX;
    private boolean isActivated = false;
    private OnSwipeCompleteListener listener;

    public interface OnSwipeCompleteListener {
        void onSwipeComplete();
    }

    public SwipeButton(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SwipeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Create the background (track)
        setBackground(ContextCompat.getDrawable(context, R.drawable.bg_slider_track));

        // Create the thumb
        slidingThumb = new ImageView(context);
        slidingThumb.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bg_slider_thumb));
        slidingThumb.setPadding(10, 10, 10, 10);
        
        // Add phone icon inside thumb
        Drawable phoneIcon = ContextCompat.getDrawable(context, R.drawable.ic_phone_red);
        slidingThumb.setImageDrawable(new CombinedDrawable(
                ContextCompat.getDrawable(context, R.drawable.bg_slider_thumb),
                phoneIcon
        ));

        LayoutParams thumbParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        slidingThumb.setLayoutParams(thumbParams);
        addView(slidingThumb);

        // Create the text
        centerText = new TextView(context);
        centerText.setText("Slide to send emergency alert");
        centerText.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        centerText.setGravity(android.view.Gravity.CENTER);
        centerText.setTextSize(14);
        centerText.setTypeface(null, android.graphics.Typeface.BOLD);

        LayoutParams textParams = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        centerText.setLayoutParams(textParams);
        addView(centerText);

        setupTouchListener();
    }

    private void setupTouchListener() {
        slidingThumb.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = event.getX();
                    return true;

                case MotionEvent.ACTION_MOVE:
                    float newX = event.getRawX() - initialX - getLeft();
                    if (newX > 0 && newX + slidingThumb.getWidth() < getWidth()) {
                        slidingThumb.setX(newX);
                        centerText.setAlpha(1 - (newX / (getWidth() - slidingThumb.getWidth())));
                    }
                    if (newX + slidingThumb.getWidth() >= getWidth() && !isActivated) {
                        isActivated = true;
                        if (listener != null) listener.onSwipeComplete();
                        reset();
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    if (!isActivated) {
                        reset();
                    }
                    return true;
            }
            return false;
        });
    }

    private void reset() {
        slidingThumb.animate().x(0).setDuration(200).start();
        centerText.animate().alpha(1).setDuration(200).start();
        isActivated = false;
    }

    public void setOnSwipeCompleteListener(OnSwipeCompleteListener listener) {
        this.listener = listener;
    }

    // Helper class to overlay phone icon on thumb background
    private static class CombinedDrawable extends android.graphics.drawable.LayerDrawable {
        public CombinedDrawable(Drawable background, Drawable foreground) {
            super(new Drawable[]{background, foreground});
            int inset = 35; // Adjust this to center the icon properly
            setLayerInset(1, inset, inset, inset, inset);
        }
    }
}
