package com.martin.ads.omoshiroilib.flyu.ysj;

import android.util.Log;

import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.util.ShaderUtils;

/**
 * Created by Ads on 2017/6/7.
 */

public class OmoshiroiNative {
    private static final String TAG = "OmoshiroiNative";

    public static int loadDStickerDotFilter(){
        int ret= ShaderUtils.createProgram(
                ShaderUtils.readAssetsTextFile(GlobalConfig.context,"filter/vsh/fu/no_filter.glsl"),
                ShaderUtils.readAssetsTextFile(GlobalConfig.context,"filter/fsh/fu/DStickerDotFilter.glsl"));
        return ret;
    }

    public static int loadMakeUpFilter(){
        int ret= ShaderUtils.createProgram(
                ShaderUtils.readAssetsTextFile(GlobalConfig.context, "filter/vsh/fu/two_texture_filter.glsl"),
                ShaderUtils.readAssetsTextFile(GlobalConfig.context,"filter/fsh/fu/MakeUpFilter.glsl"));
        return ret;
    }

    public static int loadDrawMultiTriangleFilter(){
        int ret= ShaderUtils.createProgram(
                ShaderUtils.readAssetsTextFile(GlobalConfig.context, "filter/vsh/fu/two_texture_filter.glsl"),
                ShaderUtils.readAssetsTextFile(GlobalConfig.context, "filter/fsh/fu/DrawMultiTriangleFilter.glsl"));
        Log.d(TAG, "loadDrawMultiTriangleFilter: "+ret);
        return ret;
    }

    public static int loadDStickerVignetteFilter(){
        int ret= ShaderUtils.createProgram(
                ShaderUtils.readAssetsTextFile(GlobalConfig.context, "filter/vsh/fu/no_filter.glsl"),
                ShaderUtils.readAssetsTextFile(GlobalConfig.context, "filter/fsh/fu/DStickerVignetteFilter.glsl"));
        Log.d(TAG, "loadDStickerVignetteFilter: "+ret);
        return ret;
    }

    public static int loadSwitchFilterBase(){
        int ret= ShaderUtils.createProgram(
                ShaderUtils.readAssetsTextFile(GlobalConfig.context, "filter/vsh/fu/three_texture_filter.glsl"),
                ShaderUtils.readAssetsTextFile(GlobalConfig.context, "filter/fsh/fu/SwitchFilterBase.glsl"));
        Log.d(TAG, "loadSwitchFilterBase: "+ret);
        return ret;
    }

}
