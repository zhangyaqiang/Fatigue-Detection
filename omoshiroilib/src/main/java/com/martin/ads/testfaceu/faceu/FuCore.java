package com.martin.ads.testfaceu.faceu;

import android.content.Context;

import junit.framework.Assert;


public class FuCore {
    private static final String TAG = "FuCore";
    static FuCore theCore = null;

    public static void initialize(Context context) {
        if (null != theCore) {
            return;
        }

        theCore = new FuCore();
        theCore.init(context);
    }

    public static FuCore getCore() {
        Assert.assertNotNull("FuCore not initialize!", theCore);
        return theCore;
    }

    Context mContext;

    public void init(Context context) {
        mContext = context;

    }

    public Context getContext() {
        return mContext;
    }

}
