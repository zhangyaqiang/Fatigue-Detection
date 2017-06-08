package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.multitriangle;

import android.graphics.PointF;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.AbsData;

import java.util.List;

/**
 * Created by Ads on 2017/6/6.
 */

public class MultiTriangleInfo extends AbsData {
    public List<a> eI;
    public String bP;

    public String m()
    {
        return this.bP;
    }

    public int n()
    {
        return this.eI.size();
    }

    public static class a
    {
        public String eq;
        public int[] eJ;
        public int[] eK;
        public int[] eL;
        public PointF[] eM;
        public int[] eN;
        public PointF[] eO;
    }
}
