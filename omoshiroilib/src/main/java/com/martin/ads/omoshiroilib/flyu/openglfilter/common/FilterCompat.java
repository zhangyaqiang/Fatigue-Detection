package com.martin.ads.omoshiroilib.flyu.openglfilter.common;

import android.os.Build;

/**
 * Created by Ads on 2017/6/6.
 */

public class FilterCompat {
    public static boolean useMultipleOf16 = false;
    public static boolean useXiaomiCompatFilter = false;
    public static boolean noFaceuAssist = true;
    public static boolean saveParamsOnRelease = false;
    public static String nameOfEditing = null;
    public static boolean useReflectionToCreateBuffer = Build.VERSION.SDK_INT >= 24;

    public static void useMultipleOf16ForRecord(boolean paramBoolean)
    {
        useMultipleOf16 = paramBoolean;
    }

    public static void setUseXiaomiCompatFilter(boolean paramBoolean)
    {
        useXiaomiCompatFilter = paramBoolean;
    }

    public static void setNoFaceuAssist(boolean paramBoolean)
    {
        noFaceuAssist = paramBoolean;
    }

    public static void setSaveParamsOnRelease(boolean paramBoolean)
    {
        saveParamsOnRelease = paramBoolean;
    }

    public static void setNameOfEditing(String paramString)
    {
        nameOfEditing = paramString;
    }
}
