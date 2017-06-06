
package com.martin.ads.testfaceu.faceu;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.martin.ads.omoshiroilib.constant.Rotation;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.DirectionDetector;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup.GPUImageFilterGroup;

import java.nio.ByteBuffer;

/**
 * The main accessor for GPUImage functionality. This class helps to do common
 * tasks through a simple interface.
 */
public class GPUImage {
    static final String TAG = "GPUImage";
    final static int DEFAULT_SURFACE_FIXED_WIDTH = 720;
    final static int DEFAULT_SURFACE_FIXED_HEIGHT = 1440;

    public final GPUImageRenderer mRenderer;
    private GLSurfaceView mGlSurfaceView;
    private GPUImageFilterGroupBase mFilter;
    public Bitmap mCurrentBitmap;

    ByteBuffer mPreviewBuf;
    int mSurfaceFixedWidth = DEFAULT_SURFACE_FIXED_WIDTH;
    int mSurfaceFixedHeight = DEFAULT_SURFACE_FIXED_HEIGHT;

    /**
     * Instantiates a new GPUImage object.
     *
     * @param context the context
     */
    public GPUImage(final Context context) {
        if (!supportsOpenGLES2(context)) {
            throw new IllegalStateException("OpenGL ES 2.0 is not supported on this phone.");
        }
        mCurrentBitmap = null;
        mFilter = new GPUImageFilterGroup();
        mRenderer = new GPUImageRenderer(mFilter);
    }

    public GPUImageRenderer getRenderer() {
        return mRenderer;
    }

    public void setDirectionDetector(DirectionDetector directionDetector) {
        mRenderer.setDirectionDetector(directionDetector);
    }

    /**
     * Checks if OpenGL ES 2.0 is supported on the current device.
     *
     * @param context the context
     * @return true, if successful
     */
    private boolean supportsOpenGLES2(final Context context) {
        final ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        return configurationInfo.reqGlEsVersion >= 0x20000;
    }

    /**
     * Sets the GLSurfaceView which will display the preview.
     *
     * @param view the GLSurfaceView
     */
    public void setGLSurfaceView(final GLSurfaceView view) {
        mGlSurfaceView = view;
        mGlSurfaceView.setEGLContextClientVersion(2);
        mGlSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGlSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        mGlSurfaceView.setRenderer(mRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mRenderer.setGlSurfaceView(mGlSurfaceView);
        mGlSurfaceView.requestRender();

        mGlSurfaceView.getViewTreeObserver().addOnGlobalLayoutListener(new OnSurfaceViewLayoutLsn());
    }

    public void setMaxFixedSize(int maxWidth, int maxHeight) {
        mSurfaceFixedWidth = maxWidth;
        mSurfaceFixedHeight = maxHeight;
    }

    class OnSurfaceViewLayoutLsn implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            mGlSurfaceView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

            // 如果超过720p的手机的话,使用fixSize提高性能
            if (mGlSurfaceView.getWidth() > mSurfaceFixedWidth || mGlSurfaceView.getHeight() > mSurfaceFixedHeight) {
                int width = mSurfaceFixedWidth;
                int height = mGlSurfaceView.getHeight() * width / mGlSurfaceView.getWidth();
                if (height > mSurfaceFixedHeight) {
                    height = mSurfaceFixedHeight;
                    width = mGlSurfaceView.getWidth() * height / mGlSurfaceView.getHeight();
                }

                mGlSurfaceView.getHolder().setFixedSize(width, height);
            }
        }
    }

    /**
     * Request the preview to be rendered again.
     */
    public void requestRender() {
        if (mGlSurfaceView != null) {
            mGlSurfaceView.requestRender();
        }
    }

    /**
     * Sets the up camera to be connected to GPUImage to get a filtered preview.
     *
     * @param camera         the camera
     * @param degrees        by how many degrees the image should be rotated
     * @param flipHorizontal if the image should be flipped horizontally
     * @param flipVertical   if the image should be flipped vertically
     */
    public void setUpCamera(final Camera camera, final int degrees, final boolean flipHorizontal,
                            final boolean flipVertical) {
        if (null == camera) {
            Log.e(TAG, "setup camera failed, camera is null");
            return;
        }

        // allocate memory for preview
        Camera.Size size = camera.getParameters().getPreviewSize();
        mPreviewBuf = ByteBuffer.allocateDirect(size.width * size.height * 3 / 2);

        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        setUpCameraGingerbread(camera, mPreviewBuf.array());

        Rotation rotation = Rotation.NORMAL;
        switch (degrees) {
            case 90:
                rotation = Rotation.ROTATION_90;
                break;
            case 180:
                rotation = Rotation.ROTATION_180;
                break;
            case 270:
                rotation = Rotation.ROTATION_270;
                break;
        }
        android.util.Log.d(TAG, "setUpCamera: "+rotation);
        mRenderer.setRotationCamera(rotation, flipHorizontal, flipVertical);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setUpCameraGingerbread(final Camera camera, byte[] data) {
        mRenderer.setUpSurfaceTexture(camera, data);
    }

    /**
     * Sets the filter which should be applied to the image which was (or will
     * be) set by setImage(...).
     *
     * @param filter the new filter
     */
    public void setFilter(final GPUImageFilterGroupBase filter) {
        mFilter = filter;
        mRenderer.setFilter(mFilter);
        requestRender();
    }

    public void uninit() {
        if (null != mRenderer) {
            mRenderer.uninit();
        }

        if (null != mFilter) {
            mFilter.releaseNoGLESRes();
        }
    }

    public enum ScaleType {CENTER_INSIDE, CENTER_CROP}
}
