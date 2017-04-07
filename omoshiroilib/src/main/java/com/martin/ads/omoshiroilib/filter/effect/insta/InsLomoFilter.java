package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsLomoFilter extends MultipleTextureFilter {
    public InsLomoFilter(Context context) {
        super(context, "filter/fsh/insta/lomo.glsl");
        textureSize=2;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context,"filter/textures/insta/lomomap_new.png");
        externalBitmapTextures[1].load(context,"filter/textures/insta/vignette_map.png");
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",1.0f);
    }
}
