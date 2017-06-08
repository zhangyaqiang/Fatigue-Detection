package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.changeface;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.AbsData;

/**
 * Created by Ads on 2017/6/6.
 */

public class ChangeFaceInfo extends AbsData {
    public String bN;
    public float[] bO;
    public String bP;
    public int bQ;
    public String[] bR;
    public String bS;
    public boolean bT;

    public String m()
    {
        return this.bP;
    }

    public int n()
    {
        return 1;
    }
}
