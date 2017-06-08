

package com.martin.ads.omoshiroilib.ui.face;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.martin.ads.omoshiroilib.flyu.fake.Logger;
import com.martin.ads.omoshiroilib.flyu.fake.LoggerFactory;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.DirectionDetector;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.martin.ads.omoshiroilib.glessential.GLRender;

public class FaceWrapper {
    private final static Logger log = LoggerFactory.getLogger();

    public GLRender glRender;
    public GPUImageFilter mFilter;
    private Context context;

    public FaceWrapper(Context context,GLRender glRender) {
        this.context=context;
        this.glRender=glRender;
    }

    public void setDirectionDetector(DirectionDetector directionDetector) {
        glRender.setDirectionDetector(directionDetector);
    }

    public GLRender getGlRender() {
        return glRender;
    }

    public void setFilter(GPUImageFilterGroupBase filter) {
        mFilter = filter;
        glRender.setFilter(filter);
    }

    public GPUImageFilter getFilter() {
        return mFilter;
    }

    public void uninit() {
        glRender.uninit();
        mFilter.releaseNoGLESRes();
    }

}
