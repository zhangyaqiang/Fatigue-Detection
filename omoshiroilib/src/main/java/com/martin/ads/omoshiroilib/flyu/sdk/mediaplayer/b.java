package com.martin.ads.omoshiroilib.flyu.sdk.mediaplayer;

import android.util.Log;

/**
 * Created by Ads on 2017/6/6.
 */

public class b implements AudioFocusRequest.FocusRequestChangeListener {

    private FMediaPlayer iG;
    b(FMediaPlayer paramFMediaPlayer) {
        this.iG=paramFMediaPlayer;
    }

    public void onFocusChange(int paramInt) {
        Log.d("FMediaPlayer", "Focus change current state is " + paramInt);
        switch (paramInt) {
            case 1:
                Log.d("FMediaPlayer", "Focus change then start again");
                this.iG.audioFocusGain();
                break;
            case -2:
                Log.d("FMediaPlayer", "Focus change then pause isPlaying:" + this.iG.isPlaying());
                this.iG.audioFocusLoss(this.iG.isPlaying());
                break;
            case -1:
                Log.d("FMediaPlayer", "Focus change then pause isPlaying:" + this.iG.isPlaying());
                this.iG.audioFocusLoss(this.iG.isPlaying());
        }
    }
}

