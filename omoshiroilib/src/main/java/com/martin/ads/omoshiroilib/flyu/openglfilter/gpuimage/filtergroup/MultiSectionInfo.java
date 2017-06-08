package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.AbsData;

import java.util.List;
import java.util.Map;

/**
 * Created by Ads on 2017/6/6.
 */

public class MultiSectionInfo extends AbsData {
    public static final String dI = "__empty__";
    public Map<String, a> dJ;
    public Map<String, b> dK;
    public Map<String, Map<Integer, c>> dL;
    public String dM;

    public String m()
    {
        return "";
    }

    public int n()
    {
        return 5;
    }

    public static class c
    {
        public String dU;
        public long dV;
    }

    public static class b
    {
        public String dR;
        public String bP;
        public int dS;
        public List<String> dT;
    }

    public static class a
    {
        public String dN;
        public boolean dO;
        public String dP;
        public Object dQ;
    }
}
