package com.martin.ads.omoshiroilib.flyu.sdk.commoninterface;

import android.graphics.Bitmap;

/**
 * Created by Ads on 2017/6/6.
 */

public interface IImageLoader
{
    void asyncLoadImage(String paramString, IAsyncLoadImgListener paramIAsyncLoadImgListener);

    void cancelLoad(String paramString, IAsyncLoadImgListener paramIAsyncLoadImgListener);

    void asyncLoadImage(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, IAsyncLoadImgListener paramIAsyncLoadImgListener);

    public interface IAsyncLoadImgListener {
        void onLoadFinish(String paramString, Bitmap paramBitmap);
    }
}
