package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base;

/**
 * Created by Ads on 2017/6/6.
 */

public class UnnamedD implements Runnable {
    private GPUImageFilter bo;
    private int bl,bm,bn;
    UnnamedD(GPUImageFilter var1, int var2, int var3, int var4) {
        this.bo = var1;
        this.bl = var2;
        this.bm = var3;
        this.bn = var4;
    }

    public void run() {
        this.bo.bi = this.bl;
        this.bo.bj = this.bm;
        this.bo.bk = this.bn;
    }
}
