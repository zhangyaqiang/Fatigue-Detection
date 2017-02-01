package com.martin.ads.omoshiroilib.filter.effect.mx;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;

/**
 * Created by Ads on 2017/1/31.
 * ShiftColorFilter (提取红色)
 */

public class ShiftColorFilter extends SimpleFragmentShaderFilter {
    public ShiftColorFilter(Context context) {
        super(context, "filter/fsh/mx_shift_color.glsl");
    }
}
