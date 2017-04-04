package com.martin.ads.testfaceu.faceu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class VideoViewDecorator extends FrameLayout {

    private static final String TAG = "VideoViewDecorator";

    protected SurfaceView mSurfaceView;

    public VideoViewDecorator(Context context) {
        super(context);
    }

    public VideoViewDecorator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoViewDecorator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void attachView(SurfaceView sv) {
        mSurfaceView = sv;

        if (getChildCount() > 0) {
            removeAllViewsInLayout();
        }

        addView(mSurfaceView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
