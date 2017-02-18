package com.martin.ads.omoshiroilib.filter.ext;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.FilterGroup;
import com.martin.ads.omoshiroilib.filter.ext.shadertoy.FastBlurFilter;
import com.martin.ads.omoshiroilib.filter.imgproc.GaussianBlurFilter;

/**
 * Created by Ads on 2017/2/16.
 */

public class BlurredFrameEffect extends FilterGroup{

    private static final int BLUR_RADIUS=6;
    private static final float SCALING_FACTOR=0.6f;
    private ScalingFilter scalingFilter;
    public BlurredFrameEffect(Context context) {
        super();
        addFilter(new FastBlurFilter(context).setScale(true));
        addFilter(new GaussianBlurFilter(context).setTexelHeightOffset(BLUR_RADIUS).setScale(true));
        addFilter(new GaussianBlurFilter(context).setTexelWidthOffset(BLUR_RADIUS).setScale(true));
        addFilter(new GaussianBlurFilter(context).setTexelHeightOffset(BLUR_RADIUS));
        addFilter(new GaussianBlurFilter(context).setTexelWidthOffset(BLUR_RADIUS));
        scalingFilter=new ScalingFilter(context).setScalingFactor(SCALING_FACTOR).setDrawOnTop(true);
    }

    @Override
    public void onDrawFrame(int textureId) {
        super.onDrawFrame(textureId);
        scalingFilter.onDrawFrame(textureId);
    }

    @Override
    public void init() {
        super.init();
        scalingFilter.init();
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        super.onFilterChanged(surfaceWidth, surfaceHeight);
        scalingFilter.onFilterChanged(surfaceWidth,surfaceHeight);
    }

    @Override
    public void destroy() {
        super.destroy();
        scalingFilter.destroy();
    }
}
