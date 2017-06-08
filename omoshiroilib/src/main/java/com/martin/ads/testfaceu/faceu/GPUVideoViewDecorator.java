

package com.martin.ads.testfaceu.faceu;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.martin.ads.omoshiroilib.flyu.fake.Logger;
import com.martin.ads.omoshiroilib.flyu.fake.LoggerFactory;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.DirectionDetector;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;

public class GPUVideoViewDecorator {
    private final static Logger log = LoggerFactory.getLogger();

    public GPUImage mGPUImage;
    public GPUImageFilter mFilter;
    private GLSurfaceView mSurfaceView;
    private Context context;

    public GPUVideoViewDecorator(Context context,GLSurfaceView mSurfaceView) {
        this.context=context;
        this.mSurfaceView=mSurfaceView;
        init();
    }

    public void setDirectionDetector(DirectionDetector directionDetector) {
        mGPUImage.setDirectionDetector(directionDetector);
    }

    private void init() {
        mGPUImage = new GPUImage(context);
        mGPUImage.setGLSurfaceView(mSurfaceView);
      }

    public GPUImage getGPUImage() {
        return mGPUImage;
    }

    public void setFilter(GPUImageFilterGroupBase filter) {
        mFilter = filter;
        mGPUImage.setFilter(filter);
        requestRender();
    }

    public GPUImageFilter getFilter() {
        return mFilter;
    }

    public void requestRender() {
        mSurfaceView.requestRender();
    }

    /**
     * Pauses the GLSurfaceView.
     */
    public void onPause() {
        mSurfaceView.onPause();
    }

    public void uninit() {
        mGPUImage.uninit();
    }

}
