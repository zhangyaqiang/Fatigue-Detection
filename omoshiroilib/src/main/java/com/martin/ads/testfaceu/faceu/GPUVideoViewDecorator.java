/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martin.ads.testfaceu.faceu;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.lemon.faceu.openglfilter.detect.DirectionDetector;
import com.lemon.faceu.openglfilter.gpuimage.base.GPUImageFilter;
import com.lemon.faceu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.martin.ads.testfaceu.faceu.fake.Logger;
import com.martin.ads.testfaceu.faceu.fake.LoggerFactory;
import com.martin.ads.testfaceu.faceu.ui.VideoViewDecorator;


import java.nio.IntBuffer;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class GPUVideoViewDecorator extends VideoViewDecorator {
    private final static Logger log = LoggerFactory.getLogger(GPUVideoViewDecorator.class);

    public GPUImage mGPUImage;
    public GPUImageFilter mFilter;

    OnGestureListener mOuterGestureLsn;
    GestureDetector mGestureDector;
    ScaleGestureDetector mScaleGestureDector;

    EGLContext mEGLCurrentContext;

    private static class MyContextFactory implements GLSurfaceView.EGLContextFactory {
        private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        GPUVideoViewDecorator mRenderer;

        public MyContextFactory(GPUVideoViewDecorator renderer) {
            log.debug("MyContextFactory " + renderer);
            this.mRenderer = renderer;
        }

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
            log.debug("GPUVideoViewDecorator: createContext " + egl + " " + display + " " + eglConfig);
            checkEglError("before createContext", egl);
            int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};

            EGLContext ctx;

            if (mRenderer.mEGLCurrentContext == null) {
                mRenderer.mEGLCurrentContext = egl.eglCreateContext(display, eglConfig,
                        EGL10.EGL_NO_CONTEXT, attrib_list);
                ctx = mRenderer.mEGLCurrentContext;
            } else {
                ctx = mRenderer.mEGLCurrentContext;
            }
            checkEglError("after createContext", egl);
            return ctx;
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            log.debug("GPUVideoViewDecorator: destroyContext " + egl + " " + display + " " + context + " " + mRenderer.mEGLCurrentContext);
            if (mRenderer.mEGLCurrentContext == null) {
                egl.eglDestroyContext(display, context);
            }
        }

        private static void checkEglError(String prompt, EGL10 egl) {
            int error;
            while ((error = egl.eglGetError()) != EGL10.EGL_SUCCESS) {
                log.debug(String.format(Locale.US, "%s: EGL error: 0x%x", prompt, error));
            }
        }
    }

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
        ((GLSurfaceView) mSurfaceView).setEGLContextFactory(new MyContextFactory(this));

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
     * Set the scale type of GPUImage.
     *
     * @param scaleType the new ScaleType
     */
    public void setScaleType(GPUImage.ScaleType scaleType) {
        mGPUImage.setScaleType(scaleType);
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
     * Capture the current image with the size as it is displayed and retrieve it as Bitmap.
     *
     * @return current output as Bitmap
     * @throws InterruptedException
     */
    public Bitmap capture() throws InterruptedException {
        // 如果surface都没创建,那么render线程应该也没创建出来,会导致watier.acquire()一直卡死在那
        if (null == mGPUImage || null == mGPUImage.mRenderer || !mGPUImage.mRenderer.isSurfaceCreated()) {
            log.error("surface not create, can't capture");
            throw new InterruptedException();
        }

        final Semaphore waiter = new Semaphore(0);

        final int width = mGPUImage.getRenderer().getOutputWidth();
        final int height = mGPUImage.getRenderer().getOutputHeight();

        // Take picture on OpenGL thread
        final int[] pixelMirroredArray = new int[width * height];
        mGPUImage.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                final IntBuffer pixelBuffer = IntBuffer.allocate(width * height);
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
                int[] pixelArray = pixelBuffer.array();

                // Convert upside down mirror-reversed image to right-side up normal image.
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width; j++) {
                        pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i * width + j];
                    }
                }
                waiter.release();
            }
        });
        requestRender();
        waiter.acquire();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixelMirroredArray));
        return bitmap;
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
