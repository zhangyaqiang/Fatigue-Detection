package com.martin.ads.omoshiroilib.filter.ext.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class TileMosaicFilter extends ShaderToyAbsFilter{
    public TileMosaicFilter(Context context) {
        super(context, "filter/fsh/shadertoy/tile_mosaic.glsl");
    }
}
