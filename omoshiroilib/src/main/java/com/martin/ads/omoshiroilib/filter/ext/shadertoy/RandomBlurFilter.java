package com.martin.ads.omoshiroilib.filter.ext.shadertoy;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;
import com.martin.ads.omoshiroilib.util.TextureUtils;

import java.nio.FloatBuffer;

/**
 * Created by Ads on 2017/2/16.
 */

public class RandomBlurFilter extends ShaderToyAbsFilter {
    public RandomBlurFilter(Context context) {
        super(context, "filter/fsh/random_blur.glsl");
    }
}