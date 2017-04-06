package com.martin.ads.omoshiroilib.filter.ext.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class NoiseWarpFilter extends ShaderToyAbsFilter{
    public NoiseWarpFilter(Context context) {
        super(context, "filter/fsh/shadertoy/noise_warp.glsl");
    }
}
