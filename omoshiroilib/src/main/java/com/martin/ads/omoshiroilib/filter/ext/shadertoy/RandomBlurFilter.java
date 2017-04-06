package com.martin.ads.omoshiroilib.filter.ext.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/2/16.
 */

public class RandomBlurFilter extends ShaderToyAbsFilter {
    public RandomBlurFilter(Context context) {
        super(context, "filter/fsh/imgproc/random_blur.glsl");
    }
}