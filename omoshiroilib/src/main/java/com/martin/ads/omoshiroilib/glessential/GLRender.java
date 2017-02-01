package com.martin.ads.omoshiroilib.glessential;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.martin.ads.omoshiroilib.camera.CameraEngine;
import com.martin.ads.omoshiroilib.filter.base.*;
//import com.martin.ads.omoshiroilib.filter.effect.*;
//import com.martin.ads.omoshiroilib.filter.effect.mx.*;
//import com.martin.ads.omoshiroilib.filter.ext.*;
//import com.martin.ads.omoshiroilib.filter.imgproc.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ads on 2017/1/26.
 */

public class GLRender implements GLSurfaceView.Renderer {
    private CameraEngine cameraEngine;
    private FilterGroup filterGroup;
    private OESFilter oesFilter;
    private OrthoFilter orthoFilter;
    private Context context;

    public GLRender(Context context,CameraEngine cameraEngine) {
        this.context=context;
        this.cameraEngine=cameraEngine;
        filterGroup=new FilterGroup();
        oesFilter=new OESFilter(context);
        orthoFilter=new OrthoFilter(context);
        filterGroup.addFilter(oesFilter);
        filterGroup.addFilter(orthoFilter);

//        filterGroup.addFilter(new SphereReflector(context));
//        filterGroup.addFilter(new InvertColorFilter(context));
//        filterGroup.addFilter(new BraSizeTestRightFilter(context));
//        filterGroup.addFilter(new BraSizeTestLeftFilter(context));
//        filterGroup.addFilter(new GrayScaleShaderFilter(context));
//
//        filterGroup.addFilter(new FillLightFilter(context));
//        filterGroup.addFilter(new GreenHouseFilter(context));
//        filterGroup.addFilter(new BlackWhiteFilter(context));
//        filterGroup.addFilter(new PastTimeFilter(context));
//        filterGroup.addFilter(new MoonLightFilter(context));
//        filterGroup.addFilter(new PrintingFilter(context));
//        filterGroup.addFilter(new ToyFilter(context));
//        filterGroup.addFilter(new BrightnessFilter(context));
//        filterGroup.addFilter(new VignetteFilter(context));
//        filterGroup.addFilter(new MultiplyFilter(context));
//        filterGroup.addFilter(new ReminiscenceFilter(context));
//        filterGroup.addFilter(new SunnyFilter(context));
//        filterGroup.addFilter(new MxLomoFilter(context));
//        filterGroup.addFilter(new ShiftColorFilter(context));
//        filterGroup.addFilter(new MxFaceBeautyFilter(context));
//        filterGroup.addFilter(new MxProFilter(context));
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        filterGroup.init();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        cameraEngine.doTextureUpdate(oesFilter.getSTMatrix());
        filterGroup.onDrawFrame(oesFilter.getGlOESTexture().getTextureId());
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        filterGroup.onFilterChanged(width,height);
        if(cameraEngine.isCameraOpened()){
            cameraEngine.stopPreview();
            cameraEngine.releaseCamera();
        }
        cameraEngine.setTexture(oesFilter.getGlOESTexture().getTextureId());
        cameraEngine.openCamera(false);
        orthoFilter.updateProjection(cameraEngine.getPreviewSize());
        cameraEngine.startPreview();
    }

    public void onPause(){
        if(cameraEngine.isCameraOpened()){
            cameraEngine.stopPreview();
            cameraEngine.releaseCamera();
        }
    }

    public void onResume() {
    }

    public void onDestroy(){
        if(cameraEngine.isCameraOpened()){
            cameraEngine.releaseCamera();
        }
    }

}
