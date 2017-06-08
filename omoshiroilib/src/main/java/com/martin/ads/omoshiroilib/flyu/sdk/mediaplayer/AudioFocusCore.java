package com.martin.ads.omoshiroilib.flyu.sdk.mediaplayer;

import android.content.Context;

import junit.framework.Assert;

/**
 * Created by Ads on 2017/6/6.
 */

public class AudioFocusCore {
    private Context mContext;
    private static AudioFocusCore mAudioFocusCore;

    public static void initialize(Context paramContext) {
        mAudioFocusCore = new AudioFocusCore();
        mAudioFocusCore.init(paramContext);
    }

    private void init(Context context)
    {
        this.mContext = context;
    }

    public static AudioFocusCore getCore() {
        Assert.assertNotNull(mAudioFocusCore);
        return mAudioFocusCore;
    }

    public Context getContext()
    {
        return mContext;
    }
}
