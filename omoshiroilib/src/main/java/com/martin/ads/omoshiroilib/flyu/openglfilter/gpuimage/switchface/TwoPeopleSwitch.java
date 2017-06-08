package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.switchface;

/**
 * Created by Ads on 2017/6/6.
 */

public class TwoPeopleSwitch extends SwitchFaceBase
{
    static final int[] eU = { 1, 0 };

    public TwoPeopleSwitch()
    {
        i("two_people_switch_tips1.png");
        i("two_people_switch_tips2.png");
    }

    protected int T()
    {
        return 2;
    }

    protected int U()
    {
        return 2;
    }

    protected int[] V()
    {
        return eU;
    }
}
