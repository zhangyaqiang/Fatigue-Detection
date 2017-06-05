package com.martin.ads.omoshiroilib.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.testfaceu.faceu.HardCodeData;

import java.io.File;

/**
 * Created by Ads on 2017/6/5.
 */

public class EffectUtils {
    public static Bitmap getEffectThumbFromFile(Context context, HardCodeData.EffectItem effectItem){
        return BitmapUtils.loadBitmapFromAssets(context,effectItem.getThumbFilePath());
    }
}
