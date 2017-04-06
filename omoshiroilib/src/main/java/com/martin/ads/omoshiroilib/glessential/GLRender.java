package com.martin.ads.omoshiroilib.glessential;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.Log;

import com.martin.ads.omoshiroilib.camera.CameraEngine;
import com.martin.ads.omoshiroilib.camera.IWorkerCallback;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalContext;
import com.martin.ads.omoshiroilib.filter.base.FilterGroup;
import com.martin.ads.omoshiroilib.filter.base.OESFilter;
import com.martin.ads.omoshiroilib.filter.ext.BlurredFrameEffect;
import com.martin.ads.omoshiroilib.filter.helper.FilterFactory;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import com.martin.ads.omoshiroilib.filter.effect.*;
//import com.martin.ads.omoshiroilib.filter.effect.mx.*;
//import com.martin.ads.omoshiroilib.filter.ext.*;
//import com.martin.ads.omoshiroilib.filter.imgproc.*;

/**
 * Created by Ads on 2017/1/26.
 */

public class GLRender implements GLSurfaceView.Renderer {
    private static final String TAG = "GLRender";

    private CameraEngine cameraEngine;
    private FilterGroup filterGroup;
    private OESFilter oesFilter;
    private Context context;
    private FilterGroup customizedFilters;

    private FilterType currentFilterType=FilterType.NONE;

    private int surfaceWidth;
    private int surfaceHeight;
    private boolean isCameraFacingFront;
    public GLRender(final Context context, CameraEngine cameraEngine) {
        this.context=context;
        this.cameraEngine=cameraEngine;
        filterGroup=new FilterGroup();
        oesFilter=new OESFilter(context);
        filterGroup.addFilter(oesFilter);

        customizedFilters=new FilterGroup();
        customizedFilters.addFilter(FilterFactory.createFilter(currentFilterType,context));
        filterGroup.addFilter(customizedFilters);

        filterGroup.addFilter(new BlurredFrameEffect(context));

        cameraEngine.setPictureTakenCallBack(new PictureTakenCallBack() {
            @Override
            public void saveAsBitmap(final byte[] data) {
                Log.d(TAG, "onPictureTaken - jpeg, size: " + data.length);
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Log.d(TAG, "onPictureTaken: "+bitmap.getWidth()+" "+bitmap.getHeight());
                final File pictureFolderPath = new File(
                        Environment.getExternalStorageDirectory(), "/Omoshiroi/pictures");
                if (!pictureFolderPath.exists())
                    pictureFolderPath.mkdirs();
                File outputFile= FileUtils.makeTempFile(pictureFolderPath.getAbsolutePath(),"IMG_", ".jpg");
                IWorkerCallback workerCallback=new IWorkerCallback() {
                    @Override
                    public void onPostExecute(Exception exception) {
                        if (exception == null) {
                            Log.d(TAG, "Picture saved to disk - jpeg, size: " + data.length);
                        }
                    }
                };

                //BitmapUtils.saveBitmap(bitmap,outputFile.getAbsolutePath()+".jpg",workerCallback);

                //BitmapUtils.saveByteArray(data, outputFile.getAbsolutePath(), workerCallback);
                BitmapUtils.saveBitmapWithFilterApplied(GlobalContext.context,currentFilterType,bitmap,outputFile.getAbsolutePath(),workerCallback);

            }
        });
        isCameraFacingFront=false;
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
        Log.d(TAG, "onSurfaceChanged: "+width+" "+height);
        this.surfaceWidth=width;
        this.surfaceHeight=height;
        GLES20.glViewport(0,0,width,height);
        filterGroup.onFilterChanged(width,height);
        if(cameraEngine.isCameraOpened()){
            cameraEngine.stopPreview();
            cameraEngine.releaseCamera();
        }
        cameraEngine.setTexture(oesFilter.getGlOESTexture().getTextureId());
        cameraEngine.openCamera(isCameraFacingFront);
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

    public void switchCamera(){
        isCameraFacingFront=!isCameraFacingFront;
        cameraEngine.switchCamera(isCameraFacingFront);
    }

    public interface PictureTakenCallBack{
        void saveAsBitmap(final byte[] data);
    }

    public void switchLastFilter(FilterType filterType){
        if (filterType==null) return;
        currentFilterType=filterType;
        customizedFilters.switchLastFilter(FilterFactory.createFilter(filterType,context));
    }
}
