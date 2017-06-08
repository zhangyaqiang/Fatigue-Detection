package com.martin.ads.omoshiroilib.flyu.openglfilter.detector;

import android.content.Context;
import android.view.OrientationEventListener;

/**
 * Created by Ads on 2017/6/4.
 */

public class DirectionDetector {
    private static final String TAG = "DirectionDetector";


    private boolean lockDirection;
    private boolean started;

    //手机左转90度或画面右转90度
    //Surface.ROTATION_90
    public static final int ROTATION_LANDSCAPE = 0;
    //Surface.ROTATION_0
    // (natural orientation) 平板横屏 手机竖屏
    public static final int ROTATION_PORTRAIT = 1;
    //Surface.ROTATION_270
    public static final int ROTATION_SEASCAPE = 2;
    //Surface.ROTATION_180
    public static final int ROTATION_UPSIDE_DOWN = 3;


    private int currentDirection = ROTATION_PORTRAIT;
    private OrientationEventListenerImpl orientationEventListener;
    private OrientationChangedCallback orientationChangedCallback;

    public DirectionDetector(Context context,boolean lockDirection) {
        this.lockDirection = lockDirection;
        orientationEventListener =new OrientationEventListenerImpl(context);
        started=false;
    }

    public void start() {
        if(!started) {
            started = true;
            currentDirection = ROTATION_PORTRAIT;
            orientationEventListener.enable();
        }
    }

    public void stop() {
        if(started) {
            started = false;
            orientationEventListener.disable();
        }
    }


    public int getDirection() {
        if(lockDirection) {
            currentDirection = ROTATION_PORTRAIT;
        }
        return currentDirection;
    }

    class OrientationEventListenerImpl extends OrientationEventListener{

        public OrientationEventListenerImpl(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            //Log.d(TAG, "onOrientationChanged: "+orientation);
            if((0 > orientation || orientation > 45) && (315 > orientation || orientation >= 360)) {
                if(45 <= orientation && orientation <= 135) {
                    currentDirection = ROTATION_SEASCAPE;
                } else if(135 <= orientation && orientation <= 225) {
                    currentDirection = ROTATION_UPSIDE_DOWN;
                } else {
                    currentDirection = ROTATION_LANDSCAPE;
                }
            } else {
                currentDirection = ROTATION_PORTRAIT;
            }

            if(null != orientationChangedCallback) {
                orientationChangedCallback.onOrientationChanged(orientation);
            }
        }
    }

    public interface OrientationChangedCallback {
        void onOrientationChanged(int orientation);
    }
}
