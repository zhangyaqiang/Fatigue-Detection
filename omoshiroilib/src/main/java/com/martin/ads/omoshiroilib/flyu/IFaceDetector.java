package com.martin.ads.omoshiroilib.flyu;

import android.content.Context;
import android.graphics.PointF;

import com.martin.ads.omoshiroilib.constant.Rotation;

/**
 * Created by Ads on 2017/6/4.
 */


public interface IFaceDetector {
    void init(Context context);

    void uninit();

    void reset();

    void switchMaxFaceCount(int count);

    void onFrameAvailable(int width, int height, Rotation rotate, boolean mirror,
                     byte[] yuvData,int direction);

    int getFaceDetectResult(PointF[][] detectResult, int imageScaleWidth, int imageScaleHeight,
                                   int outputWidth, int outputHeight) ;

    public interface FaceDetectorListener {
        void onDetectFinish();
    }
}