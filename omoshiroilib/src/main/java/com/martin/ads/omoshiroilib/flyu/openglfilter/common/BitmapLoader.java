package com.martin.ads.omoshiroilib.flyu.openglfilter.common;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;

import java.io.File;
import java.io.IOException;
/**
 * Created by Ads on 2017/6/6.
 */

public class BitmapLoader {
    static final String TAG = "BitmapLoader";

    public static Bitmap loadBitmapFromFile(String filePath) {
        Bitmap bitmap;
        File resFile = new File(filePath);
        if (!resFile.exists()) {
            Log.d("BitmapLoader", "file not exists");
            return null;
        }
        try {
            bitmap = BitmapFactory.decodeFile(filePath);
        }
        catch (Exception localException) {
            Log.e("BitmapLoader", "exception on load from file, " + localException.getMessage());
            bitmap = null;
        }
        return bitmap;
    }

    public static Bitmap loadBitmapFromAssets(String filePath)
    {
        Bitmap bitmap = null;
        AssetManager localAssetManager = GlobalConfig.context.getAssets();
        try {
            bitmap = BitmapFactory.decodeStream(localAssetManager.open(filePath));
        }
        catch (IOException localIOException) {
            Log.e("BitmapLoader", "load assert failed, " + localIOException.getMessage());
        }
        return bitmap;
    }
}
