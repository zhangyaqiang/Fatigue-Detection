package com.martin.ads.omoshiroilib.glessential;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.martin.ads.omoshiroilib.camera.CameraEngine;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;

/**
 * Created by Ads on 2017/1/27.
 */

public class CameraView{
    private static final String TAG = "CameraView";
    private GLRender glRender;
    private CameraEngine cameraEngine;
    private Context context;
    private GLRootView glRootView;
    private ScreenSizeChangedListener screenSizeChangedListener;
    private RootViewClickListener rootViewClickListener;

    public CameraView(Context context,GLRootView glRootView) {
        this.glRootView=glRootView;
        this.context = context;
        init();
    }

    private void init(){
        glRootView.setEGLContextClientVersion(2);
        cameraEngine=new CameraEngine();
        cameraEngine.setRenderCallback(new RenderCallback() {
            @Override
            public void renderImmediately() {
                glRootView.requestRender();
            }
        });

        cameraEngine.setPreviewSizeChangedCallback(new PreviewSizeChangedCallback() {
            @Override
            public void updatePreviewSize(final int previewWidth, final int previewHeight) {
                //heheda
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!GlobalConfig.FULL_SCREEN)
                            glRootView.setAspectRatio(previewWidth,previewHeight);
                        else glRender.getOrthoFilter().updateProjection(previewWidth,previewHeight);
                        if(screenSizeChangedListener!=null){
                            screenSizeChangedListener.updateScreenSize(glRootView.getWidth(),glRootView.getHeight());
                        }
                    }
                });
            }
        });

        glRender=new GLRender(context,cameraEngine);
        glRootView.setRenderer(glRender);
        glRootView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glRootView.setClickable(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            glRootView.setPreserveEGLContextOnPause(true);
        }

        glRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                    cameraEngine.focusCamera(event);
                Log.d(TAG, "onTouch: "+glRootView.getWidth()+" "+glRootView.getHeight());
                if(rootViewClickListener!=null)
                    rootViewClickListener.onRootViewClicked();
                return true;
            }
        });
    }

    public void onPause(){
        glRootView.onPause();
        glRender.onPause();
    }

    public void onResume(){
        glRootView.onResume();
        glRender.onResume();
    }

    public void onDestroy(){
        glRender.onDestroy();
    }

    public interface RenderCallback{
        void renderImmediately();
    }

    public interface PreviewSizeChangedCallback{
        void updatePreviewSize(int previewWidth,int previewHeight);
    }

    public CameraEngine getCameraEngine() {
        return cameraEngine;
    }

    public GLRender getGlRender() {
        return glRender;
    }

    public void setScreenSizeChangedListener(ScreenSizeChangedListener screenSizeChangedListener) {
        this.screenSizeChangedListener = screenSizeChangedListener;
    }

    public interface ScreenSizeChangedListener {
        void updateScreenSize(int width,int height);
    }

    public interface RootViewClickListener{
        void onRootViewClicked();
    }

    public void setRootViewClickListener(RootViewClickListener rootViewClickListener) {
        this.rootViewClickListener = rootViewClickListener;
    }
}
