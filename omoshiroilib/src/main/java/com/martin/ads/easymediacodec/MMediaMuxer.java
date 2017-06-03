package com.martin.ads.easymediacodec;

import android.annotation.TargetApi;
import android.media.MediaMuxer;
import android.os.Build;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ads on 2017/6/1.
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class MMediaMuxer {
    private MediaMuxer mMuxer=null;	// API >= 18
    private int startCount;
    private boolean started;

    public MMediaMuxer(File outputFile) {
        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        try {
            mMuxer = new MediaMuxer(outputFile.toString(),
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            startCount=0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void release(){
        if (mMuxer != null) {
            // TODO: stop() throws an exception if you haven't fed it any data.  Keep track
            //       of frames submitted, and don't call stop() if we haven't written anything.
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
    }

    public MediaMuxer getMuxer() {
        return mMuxer;
    }

    synchronized boolean start() {
        if(started) return true;
        startCount++;
        if (startCount==2) {
            mMuxer.start();
            started = true;
            notifyAll();
        }
        return started;
    }

    public synchronized boolean isStarted() {
        return started;
    }
}
