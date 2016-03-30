package com.example.android.popmovies.ui.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.android.popmovies.R;

/**
 * Created by sheshloksamal on 18/03/16.
 * Jake Wharton's gist - https://gist.gisthub.com/JakeWharton/2856179 - with some modification
 */

/* Maintains an aspect ratio based on either width or height. Disabled by default */

public final class FixedAspectRatioImageView extends ImageView {

    // These must be kept in sync with the sync with the AspectRatioImageView attributes in
    // attrs.xml

    private static final float DEFAULT_ASPECT_RATIO = 0f;

    private float aspectRatio;
    private boolean aspectRatioEnabled;

    /*
        We will include 2 constructors to support our view being created in code or through a
        resource: The 2nd one is what we will be using.
     */

    // Constructor to support our view being created in code
    public FixedAspectRatioImageView(Context context) {
       super(context);
    }

    // Constructor to support our view being created through a resource
    public FixedAspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FixedAspectRatioImageView);
        aspectRatio = a.getFloat(R.styleable.FixedAspectRatioImageView_aspectRatio, DEFAULT_ASPECT_RATIO);

        a.recycle();
    }

    /*
        Now override onMeasure and set dimensions. onMeasure is called is called when the parent
        is laying out its children. When a view's onMeasure is called by its parent layout, it
        asks how much space is needed and passes in how much space is available and whether the
        view will be given EXACTLY that much space or AT_MOST that much space using the params
        wMeasureSpec/hMeasureSpec
     */

    @Override
    protected void onMeasure(int wMeasureSpec, int hMeasureSpec) {
        if (aspectRatio == DEFAULT_ASPECT_RATIO) {
            super.onMeasure(wMeasureSpec, hMeasureSpec);
        } else {
            int hPadding = getPaddingLeft() + getPaddingRight();
            int vPadding = getPaddingTop() + getPaddingBottom();

            int newWidth = MeasureSpec.getSize(wMeasureSpec) - hPadding;
            int newHeight = MeasureSpec.getSize(hMeasureSpec) - vPadding;

            if (newHeight > 0 && (newWidth > newHeight * aspectRatio)) {
                newWidth = (int) (newHeight * aspectRatio + 0.5);
            } else {
                newHeight = (int) (newWidth / aspectRatio + 0.5);
            }

            newWidth += hPadding;
            newHeight += vPadding;

            super.onMeasure(MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY));
        }

    }

    /** Get the aspect ratio of this imageView */
    public float getAspectRatio() {
        return this.aspectRatio;
    }

    /** Set the aspect ratio of this imageView. This will change the view instantly */
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        if (aspectRatioEnabled) requestLayout();
    }

    /** Gets whether or not forcing the aspect ratio is enabled */
    public boolean getAspectRatioEnabled() {
        return this.aspectRatioEnabled;
    }

    /** Sets whether or not forcing the aspect-ratio is enabled. This will re-layout the view. */
    public void setAspectRatioEnabled(boolean aspectRatioEnabled){
        this.aspectRatioEnabled = aspectRatioEnabled;
    }

}

