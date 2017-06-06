package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers;

import android.util.Log;

import com.martin.ads.omoshiroilib.flyu.sdk.utils.IOUtils;
import com.martin.ads.omoshiroilib.flyu.openglfilter.common.FilterCompat;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup.GPUImageFilterGroup;

import java.util.ArrayList;

/**
 * Created by Ads on 2017/6/6.
 */

public class DynamicStickerMulti extends GPUImageFilterGroup {
    static final String TAG = "DynamicStickerMulti";
    String db;
    DynamicStickerData dc;

    public DynamicStickerMulti(String paramString, DynamicStickerData paramDynamicStickerData) {
        this.db = paramString;
        this.dc = paramDynamicStickerData;
        for (DstickerDataBean locala : paramDynamicStickerData.cK) {
            String str = "file://" + paramString + "/" + locala.name;
            if ((locala instanceof b)) {
                addFilter(new DynamicStickerDot(str, (b) locala));
            } else if ((locala instanceof c)) {
                addFilter(new DynamicStickerVignette(str, (c) locala));
            }
        }
    }
    public void releaseNoGLESRes() {
        super.releaseNoGLESRes();
    }
}
