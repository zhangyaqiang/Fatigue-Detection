package com.martin.ads.omoshiroilib.flyu.sdk.mediaplayer;

import android.media.AudioManager;
import android.util.Log;

/**
 * Created by Ads on 2017/6/6.
 */

public class FocusRequestChangeListenerImpl implements AudioFocusRequest.FocusRequestChangeListener {

    private FMediaPlayer fMediaPlayer;
    FocusRequestChangeListenerImpl(FMediaPlayer fMediaPlayer) {
        this.fMediaPlayer=fMediaPlayer;
    }

    public void onFocusChange(int state) {
        Log.d("FMediaPlayer", "Focus change current state is " + state);
        switch (state) {
            case AudioManager.AUDIOFOCUS_GAIN:
                Log.d("FMediaPlayer", "Focus change then start again");
                fMediaPlayer.audioFocusGain();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                Log.d("FMediaPlayer", "Focus change then pause isPlaying:" + fMediaPlayer.isPlaying());
                fMediaPlayer.audioFocusLoss(fMediaPlayer.isPlaying());
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                Log.d("FMediaPlayer", "Focus change then pause isPlaying:" + fMediaPlayer.isPlaying());
                fMediaPlayer.audioFocusLoss(fMediaPlayer.isPlaying());
        }
    }
}

