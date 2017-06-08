package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers;

import android.util.Log;
import android.util.Pair;


import android.graphics.Bitmap;
import android.net.Uri;

import java.io.IOException;

import com.martin.ads.omoshiroilib.flyu.openglfilter.common.BitmapLoader;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterE;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.MResFileIndexReader;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.MResFileReaderBase;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.draw.OpenGlUtils;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.MiscUtils;

/**
 * Created by Ads on 2017/6/6.
 */

public class DynamicStickerBase extends GPUImageFilterE {
    static final String TAG = "DynamicStickerBase";
    protected int cC;
    static final int cD = 0;
    static final int K = 1;
    String cE;
    DstickerDataBean cF;
    MResFileIndexReader cG = null;
    int cH = -1;
    long cI = -1L;
    int cJ = 0;

    public DynamicStickerBase(DstickerDataBean parama, String paramString1, String paramString2, String paramString3) {
        super(paramString2, paramString3);
        this.cE = paramString1;
        this.cF = parama;
        this.bh = this.cF.name;

        String str = this.cE.substring("file://".length());
        Pair localPair = MResFileReaderBase.tryGetMergeFile(str);
        if (null != localPair) {
            this.cG = new MResFileIndexReader(str + "/" + (String) localPair.first, str + "/" + (String) localPair.second);
        }
    }

    public void l() {
        super.l();
        if (null != this.cG) {
            try {
                this.cG.init();
            } catch (IOException localIOException) {
                Log.e("DynamicStickerBase", "init merge res reader failed", localIOException);
                this.cG = null;
            }
        }
        this.cC = -1;
        if ((!MiscUtils.isNilOrNull(this.cF.bS)) &&
                (this.cE.startsWith("file://"))) {
            String str = this.cE.substring("file://".length());
            a(Uri.parse(str + "/" + this.cF.bS));
            b(this.cF.dj);
        }
    }

    public void A() {
        super.A();

        this.cI = -1L;
        this.cH = -1;
    }

    protected void z() {
        super.z();
        if (this.aV.h <= 0) {
            this.cI = -1L;
            stop();
            return;
        }
        if (((1 != this.cF.di) || (!this.aV.b())) && ((0 != this.cF.di) ||
                (!this.aV.c())) && (2 != this.cF.di)) {
            if (3 != this.cF.di) {

            }
        }
        int i = this.aV.d() ? 1 : 0;
        if ((i == 0) && (!this.cF.dk)) {
            this.cJ = 0;
            stop();
            this.cI = -1L;
        } else if ((i == 0) && (this.cJ == 1)) {
            this.cJ = 1;
            start();
        } else if (i != 0) {
            this.cJ = 1;
            start();
        } else {
            this.cJ = 0;
            stop();
        }
        if (this.cJ != 1) {
            this.cC = -1;
            this.cH = -1;
            return;
        }
        if (this.cI == -1L) {
            this.cI = System.currentTimeMillis();
        }
        int j = (int) ((System.currentTimeMillis() - this.cI) / this.cF.dh);
        if (j >= this.cF.dg) {
            if (!this.cF.dj) {
                this.cI = -1L;
                this.cC = -1;
                this.cH = -1;
                this.cJ = 0;
                return;
            }
            j = 0;
            this.cI = System.currentTimeMillis();
        }
        if (j < 0) {
            j = 0;
        }
        if (this.cH == j) {
            return;
        }
        if ((j == 0) && (this.cF.dl)) {
            r();
        }
        Bitmap localBitmap = null;
        if (null != this.cG) {
            localBitmap = this.cG.loadBitmapAtIndex(j);
        }
        if (null == localBitmap) {
            String str1 = String.format(this.cF.name + "_%03d.png", new Object[]{Integer.valueOf(j)});
            if (this.cE.startsWith("file://")) {
                String str2 = this.cE.substring("file://".length()) + "/" + str1;

                localBitmap = BitmapLoader.loadBitmapFromFile(str2);
            } else {
                localBitmap = null;
            }
        }
        if (null != localBitmap) {
            this.cC = OpenGlUtils.loadTexture(localBitmap, this.cC, true);
            this.cH = j;
        } else {
            this.cC = -1;
            this.cH = -1;
        }
    }

    public int n() {
        return this.cF.cN;
    }

    public void onDestroy() {
        OpenGlUtils.deleteTexture(this.cC);
        this.cC = -1;
        super.onDestroy();
    }
}
