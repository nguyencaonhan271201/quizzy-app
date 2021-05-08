package com.example.quizzyapplication_v2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

public class CustomDrawableProgressBar extends RoundCornerProgressBar {

    private Drawable rcProgressDrawable;

    public CustomDrawableProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomDrawableProgressBar);

        Drawable progressDrawable = attributes.getDrawable(R.styleable.CustomDrawableProgressBar_rcProgressDrawable);
        if(progressDrawable != null){
            rcProgressDrawable = progressDrawable;
        }

        attributes.recycle();
    }

    public CustomDrawableProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomDrawableProgressBar, defStyleAttr, 0);

        Drawable progressDrawable = attributes.getDrawable(R.styleable.CustomDrawableProgressBar_rcProgressDrawable);
        if(progressDrawable != null){
            rcProgressDrawable = progressDrawable;
        }

        attributes.recycle();
    }

    @Override
    protected void drawProgress(@NonNull LinearLayout layoutProgress, @NonNull GradientDrawable progressDrawable, float max, float progress, float totalWidth, int radius, int padding, boolean isReverse) {
        super.drawProgress(layoutProgress, progressDrawable, max, progress, totalWidth, radius, padding, isReverse);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if(rcProgressDrawable != null){
                layoutProgress.setBackground(rcProgressDrawable);
            }
        }
    }
}
