package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.AbsData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ads on 2017/6/6.
 */

public class DynamicStickerData extends AbsData
{
    public List<DstickerDataBean> cK;
    public String bS;
    public boolean cL;
    public String cM;
    public int cN;

    public DynamicStickerData()
    {
        this.cK = new ArrayList();
    }

    public String m()
    {
        return this.cM;
    }

    public int n()
    {
        return this.cN;
    }
}
