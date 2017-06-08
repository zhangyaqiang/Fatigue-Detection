package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.switchface;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.AbsData;

import java.util.List;

/**
 * Created by Ads on 2017/6/6.
 */

public class SwitchFaceInfo extends AbsData {
    public String bN;
    public String bP;
    public int bQ;
    public String bS;
    public String[] bR;
    public List<a> cv;
    public int cw;

    public String m()
    {
        return this.bP;
    }

    public int n()
    {
        return this.cw;
    }

    public static class a
    {
        public int cx;
        public int cy;
    }
}
