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
        // Inflate the XML layout
        LayoutInflater.from(context).inflate(R.layout.layout_swipe_button, this, true);

        // Set the background track
        setBackground(ContextCompat.getDrawable(context, R.drawable.bg_slider_track));

        slidingThumb = findViewById(R.id.slidingThumb);
        centerText = findViewById(R.id.centerText);

        // Use the CombinedDrawable to overlay the icon on the pink thumb background
        Drawable thumbBg = ContextCompat.getDrawable(context, R.drawable.bg_slider_thumb);
        Drawable icon = ContextCompat.getDrawable(context, R.drawable.ic_emergency_alert);
        
        slidingThumb.setImageDrawable(new CombinedDrawable(thumbBg, icon));

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
                    float maxSlide = getWidth() - slidingThumb.getWidth();
                    
                    if (newX > 0 && newX < maxSlide) {
                        slidingThumb.setX(newX);
                        centerText.setAlpha(1 - (newX / maxSlide));
                    }
                    
                    if (newX >= maxSlide && !isActivated) {
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

    private static class CombinedDrawable extends android.graphics.drawable.LayerDrawable {
        public CombinedDrawable(Drawable background, Drawable foreground) {
            super(new Drawable[]{background, foreground});
            int inset = 30; // Centering the icon inside the thumb
            setLayerInset(1, inset, inset, inset, inset);
        }
    }
}
