package com.martin.ads.omoshiroilib.filter.ext.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class LegofiedFilter extends ShaderToyAbsFilter{
    public LegofiedFilter(Context context) {
        super(context, "filter/fsh/shadertoy/legofied.glsl");
    }
}
