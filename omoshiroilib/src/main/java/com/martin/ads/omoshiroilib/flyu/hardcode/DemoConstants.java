package com.martin.ads.omoshiroilib.flyu.hardcode;

import android.os.Environment;

import java.io.File;

public class DemoConstants {
    public static final String SDCARD = getSDPath();
    public static final String APPDIR = SDCARD + "/Omoshiroi/TestFaceU";

    public static String getSDPath() {
        File sdDir = null;
        // 判断 SD 卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory(); // 获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
    }
}
