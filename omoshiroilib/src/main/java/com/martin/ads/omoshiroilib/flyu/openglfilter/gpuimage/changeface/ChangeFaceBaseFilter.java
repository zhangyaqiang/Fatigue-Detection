package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.changeface;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterE;

/**
 * Created by Ads on 2017/6/6.
 */

public class ChangeFaceBaseFilter extends GPUImageFilterE
{
    protected boolean bL;
    protected int bM;

    public ChangeFaceBaseFilter(String paramString1, String paramString2, String paramString3)
    {
        super(paramString1, paramString2, paramString3);
        this.bL = false;
        this.bM = -1;
    }

    public ChangeFaceBaseFilter(String paramString1, String paramString2)
    {
        super(paramString1, paramString2);
        this.bL = false;
        this.bM = -1;
    }

    public boolean K()
    {
        return this.bL;
    }

    public int L()
    {
        return this.bM;
    }
}

