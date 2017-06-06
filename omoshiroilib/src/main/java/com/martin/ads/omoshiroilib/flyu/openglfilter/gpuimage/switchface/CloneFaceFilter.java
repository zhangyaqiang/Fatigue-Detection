package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.switchface;

/**
 * Created by Ads on 2017/6/6.
 */

public class CloneFaceFilter extends SwitchFaceBase
{
    static final int[] eU = { 0, 0, 0, 0, 0 };

    public CloneFaceFilter()
    {
        i("clone_face_tips1.png");
        i("clone_face_tips2.png");
    }

    protected int T()
    {
        return 2;
    }

    protected int U()
    {
        return this.aV.h;
    }

    protected int[] V()
    {
        return eU;
    }
}
