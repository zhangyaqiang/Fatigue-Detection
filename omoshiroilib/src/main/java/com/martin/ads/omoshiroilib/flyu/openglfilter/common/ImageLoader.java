package com.martin.ads.omoshiroilib.flyu.openglfilter.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.martin.ads.omoshiroilib.flyu.sdk.commoninterface.IDiskCache;
import com.martin.ads.omoshiroilib.flyu.sdk.commoninterface.IImageLoader;

/**
 * Created by Ads on 2017/6/6.
 */

public class ImageLoader {
    static IImageLoader a = new ImageLoaderImpl();

    public static void a(IImageLoader paramIImageLoader)
    {
        a = paramIImageLoader;
    }

    public static IImageLoader a()
    {
        return a;
    }

    private static class ImageLoaderImpl
            implements IImageLoader
    {
        static final String TAG = "DefaultImageLoader";

        public void asyncLoadImage(String paramString, IImageLoader.IAsyncLoadImgListener paramIAsyncLoadImgListener)
        {
            Bitmap localBitmap = null;
            if (paramString.startsWith("http://")) {
                Log.e("DefaultImageLoader", "no support http load");
            } else if (paramString.startsWith("file://")) {
                localBitmap = BitmapLoader.a(paramString.substring("file://".length()));
            } else if (paramString.startsWith("assets://")) {
                localBitmap = BitmapLoader.b(paramString.substring("assets://".length()));
            }
            paramIAsyncLoadImgListener.onLoadFinish(paramString, localBitmap);
        }

        public void cancelLoad(String paramString, IImageLoader.IAsyncLoadImgListener paramIAsyncLoadImgListener)
        {
            Log.d("DefaultImageLoader", "default imageloader ignore cancel");
        }

        public Bitmap syncLoadFromDiskcache(IDiskCache paramIDiskCache, String paramString)
        {
            Log.e("DefaultImageLoader", "no support load from cache");
            return null;
        }

        public void asyncLoadImage(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2, IImageLoader.IAsyncLoadImgListener paramIAsyncLoadImgListener)
        {
            Bitmap localBitmap = BitmapFactory.decodeByteArray(paramArrayOfByte, paramInt1, paramInt2);
            paramIAsyncLoadImgListener.onLoadFinish(paramString, localBitmap);
        }
    }
}
