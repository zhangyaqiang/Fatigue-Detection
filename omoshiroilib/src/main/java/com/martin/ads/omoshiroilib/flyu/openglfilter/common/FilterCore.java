package com.martin.ads.omoshiroilib.flyu.openglfilter.common;

import android.content.Context;
import android.util.Log;

import com.lemon.faceu.sdk.utils.JniEntry;
import com.martin.ads.omoshiroilib.flyu.sdk.commoninterface.IDiskCache;

import junit.framework.Assert;

/**
 * Created by Ads on 2017/6/6.
 */

public class FilterCore {
    static FilterCore theCore = null;
    Context mContext;
    IDiskCache mFilterResCache;

    public static int initialize(Context paramContext, IDiskCache paramIDiskCache) {
        if (null != theCore) {
            return 0;
        }
        theCore = new FilterCore();
        int ret=theCore.init(paramContext, paramIDiskCache);
        Log.d("lalala", "initialize: "+System.currentTimeMillis()+" "+ret);
        return ret;
    }

    public static FilterCore getCore()
    {
        Assert.assertNotNull("Core not initialize!", theCore);
        return theCore;
    }

    public static Context getContext()
    {
        return getCore().mContext;
    }

    public int init(Context paramContext, IDiskCache paramIDiskCache)
    {
        this.mContext = paramContext;
        this.mFilterResCache = paramIDiskCache;

        return JniEntry.initJava(paramContext);
    }

    public IDiskCache getFilterResCache()
    {
        return this.mFilterResCache;
    }
}
