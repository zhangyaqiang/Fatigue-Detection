

package com.martin.ads.testfaceu.faceu;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.lemon.faceu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.DirectionDetector;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilter;
import com.martin.ads.testfaceu.faceu.fake.Logger;
import com.martin.ads.testfaceu.faceu.fake.LoggerFactory;
import com.martin.ads.testfaceu.faceu.ui.VideoViewDecorator;

public class GPUVideoViewDecorator extends VideoViewDecorator {
    private final static Logger log = LoggerFactory.getLogger();

    public GPUImage mGPUImage;
    public GPUImageFilter mFilter;

    OnGestureListener mOuterGestureLsn;
    GestureDetector mGestureDector;
    ScaleGestureDetector mScaleGestureDector;

    public GPUVideoViewDecorator(Context context) {
        super(context);
        init(context, null);
    }

    public GPUVideoViewDecorator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setOnGestureListener(OnGestureListener listener) {
        mOuterGestureLsn = listener;
    }

    public void setDirectionDetector(DirectionDetector directionDetector) {
        mGPUImage.setDirectionDetector(directionDetector);
    }

    private void init(Context context, AttributeSet attrs) {
        mSurfaceView = new GLSurfaceView(context, attrs);

        mSurfaceView.setZOrderOnTop(false);
        mSurfaceView.setZOrderMediaOverlay(false);
        addView(mSurfaceView);

        mGPUImage = new GPUImage(getContext());
        mGPUImage.setGLSurfaceView((GLSurfaceView) mSurfaceView);

        mGestureDector = new GestureDetector(context, mGestureLsn);
        mGestureDector.setOnDoubleTapListener(mDoubleTapLsn);
        mScaleGestureDector = new ScaleGestureDetector(context, mScaleGestureLsn);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDector.onTouchEvent(event) || mScaleGestureDector.onTouchEvent(event)) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (null != mOuterGestureLsn) {
                    mOuterGestureLsn.onActionUp();
                }
            }
            return true;
        }

        if (event.getPointerCount() > 1) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Retrieve the GPUImage instance used by this view.
     *
     * @return used GPUImage instance
     */
    public GPUImage getGPUImage() {
        return mGPUImage;
    }

    /**
     * Set the filter to be applied on the image.
     *
     * @param filter Filter that should be applied on the image.
     */
    public void setFilter(GPUImageFilterGroupBase filter) {
        mFilter = filter;
        mGPUImage.setFilter(filter);
        requestRender();
    }

    /**
     * Get the current applied filter.
     *
     * @return the current filter
     */
    public GPUImageFilter getFilter() {
        return mFilter;
    }

    public void requestRender() {
        ((GLSurfaceView) mSurfaceView).requestRender();
    }

    /**
     * Pauses the GLSurfaceView.
     */
    public void onPause() {
        ((GLSurfaceView) mSurfaceView).onPause();
    }

    public void uninit() {
        mGPUImage.uninit();
    }

    public void clearImage() {
        if (null != mGPUImage && null != mGPUImage.mRenderer) {
            mGPUImage.mRenderer.clearImage();
        }
    }

    /**
     * Resumes the GLSurfaceView.
     */
    public void onResume() {
        ((GLSurfaceView) mSurfaceView).onResume();
    }

    GestureDetector.OnDoubleTapListener mDoubleTapLsn = new GestureDetector.OnDoubleTapListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (null != mOuterGestureLsn) {
                mOuterGestureLsn.onSingleTap(e);
                return true;
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (null != mOuterGestureLsn) {
                mOuterGestureLsn.onDoubleTap();
                return true;
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    };

    ScaleGestureDetector.OnScaleGestureListener mScaleGestureLsn = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (null != mOuterGestureLsn) {
                mOuterGestureLsn.onScale(detector.getScaleFactor());
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
        }
    };

    GestureDetector.OnGestureListener mGestureLsn = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            if (null != mOuterGestureLsn) {
                mOuterGestureLsn.showPress();
            }
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (null != mOuterGestureLsn) {
                mOuterGestureLsn.onLongPress();
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    };

    public interface OnGestureListener {
        boolean onSingleTap(MotionEvent e);

        void onDoubleTap();

        void onScale(float factor);

        void showPress();

        void onLongPress();

        void onActionUp();
    }
}
