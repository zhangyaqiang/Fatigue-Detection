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
    private int mState = AudioManager.AUDIOFOCUS_LOSS_TRANSIENT;
    private static final AudioFocusRequest audioFocusRequest = new AudioFocusRequest();

    public static AudioFocusRequest getInstance() {
        return audioFocusRequest;
    }

    public interface FocusRequestChangeListener {
        void onFocusChange(int state);
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new OnAudioFocusChangeListenerImpl(this);

    public AudioFocusRequest() {
        mAudioManager = ((AudioManager)AudioFocusCore.getCore().getContext().getSystemService(Context.AUDIO_SERVICE));
    }

    public synchronized AudioFocusRequest addFocusRequestChangeListener(FocusRequestChangeListener focusRequestChangeListener) {
        mFocusRequestChangeListeners.add(focusRequestChangeListener);
        return this;
    }

    public synchronized AudioFocusRequest removeFocusRequestChangeListener(FocusRequestChangeListener paramFocusRequestChangeListener) {
        mFocusRequestChangeListeners.remove(paramFocusRequestChangeListener);
        return this;
    }

    public synchronized AudioFocusRequest request() {
        if (mState != 1) {
            mState = mAudioManager.requestAudioFocus(
                    onAudioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);
        }
        fireFocusChange();
        return this;
    }

    public boolean isFocused() {
        return mState == AudioManager.AUDIOFOCUS_GAIN;
    }

    public void release() {
        mState = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
        mFocusRequestChangeListeners.clear();
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        mAudioManager = null;
    }

    private void fireFocusChange() {
        HashSet hashSet;
        synchronized (this) {
            hashSet = new HashSet(mFocusRequestChangeListeners);
        }
        for (Iterator it = hashSet.iterator(); it.hasNext();) {
            FocusRequestChangeListener localFocusRequestChangeListener = (FocusRequestChangeListener)it.next();
            Log.d(TAG, localFocusRequestChangeListener + "fire focus change state:" + this.mState);
            localFocusRequestChangeListener.onFocusChange(mState);
        }
    }

    class OnAudioFocusChangeListenerImpl implements AudioManager.OnAudioFocusChangeListener {
        private AudioFocusRequest audioFocusRequest;
        OnAudioFocusChangeListenerImpl(AudioFocusRequest audioFocusRequest) {
            this.audioFocusRequest = audioFocusRequest;
        }

        public void onAudioFocusChange(int focusChange) {
            if(focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                Log.d(TAG, "audio focus loss");
                mAudioManager.abandonAudioFocus(this);
            }
            audioFocusRequest.fireFocusChange();
        }
    }
}
