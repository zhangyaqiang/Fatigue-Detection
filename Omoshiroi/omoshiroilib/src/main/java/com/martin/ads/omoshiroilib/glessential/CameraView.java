package com.martin.ads.omoshiroilib.glessential;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.martin.ads.omoshiroilib.camera.CameraEngine;

/**
 * Created by Ads on 2017/1/27.
 */

public class CameraView{
    private GLRender glRender;
    private CameraEngine cameraEngine;
    private Context context;
    private GLSurfaceView glSurfaceView;
    public CameraView(Context context,GLSurfaceView glSurfaceView) {
        this.glSurfaceView=glSurfaceView;
        this.context = context;
        init();
    }

    private void init(){
        glSurfaceView.setEGLContextClientVersion(2);
        cameraEngine=new CameraEngine();
        cameraEngine.setRenderCallback(new RenderCallback() {
            @Override
            public void renderImmediately() {
                glSurfaceView.requestRender();
            }
        });
        glRender=new GLRender(context,cameraEngine);

        glSurfaceView.setRenderer(glRender);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        glSurfaceView.setClickable(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            glSurfaceView.setPreserveEGLContextOnPause(true);
        }

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP)
                    cameraEngine.focusCamera(event);
                return true;
            }
        });
    }

    public void onPause(){
        glSurfaceView.onPause();
        glRender.onPause();
    }

    public void onResume(){
        glSurfaceView.onResume();
        glRender.onResume();
    }

    public void onDestroy(){
        glRender.onDestroy();
    }

    public interface RenderCallback{
        void renderImmediately();
    }
}
