package com.martin.ads.omoshiroilib.util;

/**
 * Created by Ads on 2017/2/13.
 */

public class FakeThreadUtils {
    public static void postTask(Runnable runnable) {
        new Thread(runnable).start();
    }
}
