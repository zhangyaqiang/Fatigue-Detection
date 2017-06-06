package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers;

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
        for (a locala : paramDynamicStickerData.cK) {
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
        if (FilterCompat.saveParamsOnRelease) {
            try {
                String str = FilterFactory.writeStickerToJson(this.dc);
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(str);
                IOUtils.writeLinesToFile(this.db, "params.txt", localArrayList);
            } catch (Exception localException) {
                Log.e("DynamicStickerMulti", "save failed");
            }
        }
    }
}
