package com.martin.ads.omoshiroilib.flyu.openglfilter.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.martin.ads.omoshiroilib.flyu.sdk.commoninterface.IImageLoader;

/**
 * Created by Ads on 2017/6/6.
 */

public class ImageLoader {
    public static IImageLoader imageLoaderImpl = new ImageLoaderImpl();

    public static IImageLoader getImageLoaderImpl() {
        return imageLoaderImpl;
    }

    public static void setImageLoaderImpl(IImageLoader imageLoaderImpl) {
        ImageLoader.imageLoaderImpl = imageLoaderImpl;
    }

    private static class ImageLoaderImpl implements IImageLoader {
        static final String TAG = "DefaultImageLoader";
        public void asyncLoadImage(String path, IImageLoader.IAsyncLoadImgListener imgListener) {
            Bitmap bitmap = null;
            if (path.startsWith("http://")) {
                Log.e("DefaultImageLoader", "no support http load");
            } else if (path.startsWith("file://")) {
                bitmap = BitmapLoader.loadBitmapFromFile(path.substring("file://".length()));
            } else if (path.startsWith("assets://")) {
                bitmap = BitmapLoader.loadBitmapFromAssets(path.substring("assets://".length()));
            }
            imgListener.onLoadFinish(path, bitmap);
        }

        public void cancelLoad(String paramString, IImageLoader.IAsyncLoadImgListener paramIAsyncLoadImgListener) {
            Log.d("DefaultImageLoader", "default imageloader ignore cancel");
        }

        public void asyncLoadImage(String path, byte[] byteData, int offset, int length, IImageLoader.IAsyncLoadImgListener imgListener) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteData, offset, length);
            imgListener.onLoadFinish(path, bitmap);
        }
    }
}
