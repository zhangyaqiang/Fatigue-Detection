package com.martin.ads.omoshiroilib.flyu.sdk.commoninterface;

import android.graphics.Bitmap;

/**
 * Created by Ads on 2017/6/6.
 */

public interface IMemCache {
    Bitmap getBitmapFromCache(String paramString);

    void putBitmapToCache(String paramString, Bitmap paramBitmap);
}