package com.martin.ads.omoshiroilib.flyu.sdk.mediaplayer;

import android.media.MediaPlayer;
import android.util.Log;


/**
 * Created by Ads on 2017/6/6.
 */

public class FMediaPlayer extends MediaPlayer
{
    private static final String TAG = "FMediaPlayer";
    private AudioFocusRequest mAudioFocusRequest;
    private AudioFocusRequest.FocusRequestChangeListener mFocusRequestChangeListener = new b(this);

    public FMediaPlayer()
    {
        this.mAudioFocusRequest = AudioFocusRequest.getInstance().addFocusRequestChangeListener(this.mFocusRequestChangeListener);
    }

    public void release()
    {
        Log.d("FMediaPlayer", "release");
        this.mAudioFocusRequest.removeFocusRequestChangeListener(this.mFocusRequestChangeListener);
        super.release();
        released();
    }

    public void start()
            throws IllegalStateException
    {
        if (this.mAudioFocusRequest.isFocused())
        {
            Log.d("FMediaPlayer", "focus then start");
            super.start();
            started();
        }
        else
        {
            Log.d("FMediaPlayer", "not focus request focus");
            this.mAudioFocusRequest.request();
        }
    }

    protected void audioFocusLoss(boolean paramBoolean)
    {
        Log.d("FMediaPlayer", "focus loss and isPlaying:" + paramBoolean);
        if (paramBoolean) {
            pause();
        }
    }

    protected void audioFocusGain()
    {
        start();
    }

    protected void started() {}

    protected void released() {}
}

