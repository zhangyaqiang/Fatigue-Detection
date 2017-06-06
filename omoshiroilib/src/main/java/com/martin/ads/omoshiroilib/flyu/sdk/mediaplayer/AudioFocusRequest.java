package com.martin.ads.omoshiroilib.flyu.sdk.mediaplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Ads on 2017/6/6.
 */

public class AudioFocusRequest {
    private static String TAG = "AudioFocusRequest";
    private AudioManager mAudioManager;
    private Set<FocusRequestChangeListener> mFocusRequestChangeListeners = new HashSet();
    private int mState = -2;

    public static AudioFocusRequest getInstance()
    {
        return iF;
    }

    public static abstract interface FocusRequestChangeListener
    {
        public abstract void onFocusChange(int paramInt);
    }

    private static final AudioFocusRequest iF = new AudioFocusRequest();

    private AudioManager.OnAudioFocusChangeListener mAfChangeListener = new a(this);

    public AudioFocusRequest()
    {
        this.mAudioManager = ((AudioManager)AudioFocusCore.getCore().getContext().getSystemService(Context.AUDIO_SERVICE));
    }

    public synchronized AudioFocusRequest addFocusRequestChangeListener(FocusRequestChangeListener paramFocusRequestChangeListener)
    {
        this.mFocusRequestChangeListeners.add(paramFocusRequestChangeListener);
        return this;
    }

    public synchronized AudioFocusRequest removeFocusRequestChangeListener(FocusRequestChangeListener paramFocusRequestChangeListener)
    {
        this.mFocusRequestChangeListeners.remove(paramFocusRequestChangeListener);
        return this;
    }

    public synchronized AudioFocusRequest request()
    {
        if (this.mState != 1) {
            this.mState = this.mAudioManager.requestAudioFocus(this.mAfChangeListener, 3, 1);
        }
        fireFocusChange();
        return this;
    }

    public boolean isFocused()
    {
        return this.mState == 1;
    }

    public void release()
    {
        this.mState = 2;
        this.mFocusRequestChangeListeners.clear();
        this.mAudioManager.abandonAudioFocus(this.mAfChangeListener);
        this.mAudioManager = null;
    }

    private void fireFocusChange()
    {
        HashSet localHashSet;
        synchronized (this)
        {
            localHashSet = new HashSet(this.mFocusRequestChangeListeners);
        }
        for (Iterator it = localHashSet.iterator(); it.hasNext();)
        {
            FocusRequestChangeListener localFocusRequestChangeListener = (FocusRequestChangeListener)it.next();
            Log.d(TAG, localFocusRequestChangeListener + "fire focus change state:" + this.mState);
            localFocusRequestChangeListener.onFocusChange(this.mState);
        }
    }

    class a implements AudioManager.OnAudioFocusChangeListener {
        private AudioFocusRequest iE;
        a(AudioFocusRequest var1) {
            this.iE = var1;
        }

        public void onAudioFocusChange(int var1) {
            if(var1 == -1) {
                Log.d(TAG, "audio focus loss");
                mAudioManager.abandonAudioFocus(this);
            }

//            AudioFocusRequest.access$302(this.iE, var1);
//            AudioFocusRequest.access$400(this.iE);
        }
    }
}
