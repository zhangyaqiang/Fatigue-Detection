package com.lemon.faceu.sdk.utils;

import android.content.Context;
import android.util.*;

import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.flyu.ysj.OmoshiroiNative;
import com.martin.ads.omoshiroilib.util.ShaderUtils;

/**
 * Created by Ads on 2017/6/6.
 */

public class JniEntry {
    private static final String TAG = "JniEntry";

    public static native void YUVtoRBGA(byte[] var0, int var1, int var2, byte[] var3);

    public static native void YUVtoARBG(byte[] var0, int var1, int var2, int[] var3);

    public static void YuvToGrayAndScaleJava(
            byte[] yuvData, int mInputWidth, int mInputHeight,
            int mRotation, boolean mMirror,
            byte[] mSampleData, int mSampleWidth, int mSampleHeight){
        //about 24ms on mi4
        //Log.d(TAG, "YuvToGrayAndScaleJava: start");
        YuvToGrayAndScale(yuvData, mInputWidth, mInputHeight, mRotation, mMirror,
                mSampleData, mSampleWidth, mSampleHeight);
        //Log.d(TAG, "YuvToGrayAndScaleJava: end");
    }

    public static native void YuvToGrayAndScale(byte[] var0, int var1, int var2, int var3, boolean var4, byte[] var5, int var6, int var7);

    public static int initJava(Context var0){
        return init(var0);
    }

    public static native int init(Context var0);

    static {
        System.loadLibrary("fucommon");
    }

}
