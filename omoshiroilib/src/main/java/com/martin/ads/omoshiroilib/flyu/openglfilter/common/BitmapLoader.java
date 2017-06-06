package com.martin.ads.omoshiroilib.flyu.openglfilter.common;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.IOException;
/**
 * Created by Ads on 2017/6/6.
 */

public class BitmapLoader {
    static final String TAG = "BitmapLoader";

    public static Bitmap a(String paramString)
    {
        Bitmap localBitmap = null;
        File localFile = new File(paramString);
        if (!localFile.exists())
        {
            Log.d("BitmapLoader", "file not exists");
            return null;
        }
        try
        {
            localBitmap = BitmapFactory.decodeFile(paramString);
        }
        catch (Exception localException)
        {
            Log.e("BitmapLoader", "exception on load from file, " + localException.getMessage());
            localBitmap = null;
        }
        return localBitmap;
    }

    public static Bitmap b(String paramString)
    {
        Bitmap localBitmap = null;
        AssetManager localAssetManager = FilterCore.getContext().getAssets();
        try
        {
            localBitmap = BitmapFactory.decodeStream(localAssetManager.open(paramString));
        }
        catch (IOException localIOException)
        {
            Log.e("BitmapLoader", "load assert failed, " + localIOException.getMessage());
        }
        return localBitmap;
    }
}
