package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup.GPUImageFilterGroup;

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
            if ((locala instanceof DstickerDataBeanExt)) {
                addFilter(new DynamicStickerDot(str, (DstickerDataBeanExt) locala));
            } else if ((locala instanceof DStickerVignetteBean)) {
                addFilter(new DynamicStickerVignette(str, (DStickerVignetteBean) locala));
            }
        }
    }
    public void releaseNoGLESRes() {
        super.releaseNoGLESRes();
    }
}
