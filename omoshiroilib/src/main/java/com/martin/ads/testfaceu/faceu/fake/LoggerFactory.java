package com.martin.ads.testfaceu.faceu.fake;

import android.content.Context;

/**
 * Created by Ads on 2017/4/4.
 */

public class LoggerFactory {
    public static Logger getLogger(Class<?> clazz){
        return new Logger();
    }
}
