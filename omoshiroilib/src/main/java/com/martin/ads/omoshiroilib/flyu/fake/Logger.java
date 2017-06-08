package com.martin.ads.omoshiroilib.flyu.fake;

import android.util.Log;

/**
 * Created by Ads on 2017/4/4.
 */

public class Logger {
    private static final String TAG = "Logger";
    public void debug(String str){
        Log.d(TAG, str);
    }

    public void info(String str){
        Log.i(TAG, str);
    }

    public void warn(String str){
        Log.w(TAG, str);
    }

    public void error(String str){
        Log.e(TAG, str);
    }
}
