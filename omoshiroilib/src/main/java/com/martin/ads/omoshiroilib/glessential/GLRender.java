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
import com.martin.ads.omoshiroilib.filter.helper.FilterFactory;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.util.BitmapUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    private FilterType currentFilterType=FilterType.PAST_TIME_FILTER;
    public GLRender(final Context context, CameraEngine cameraEngine) {
        this.context=context;
        this.cameraEngine=cameraEngine;
        filterGroup=new FilterGroup();
        oesFilter=new OESFilter(context);
        filterGroup.addFilter(oesFilter);

        filterGroup.addFilter(
                FilterFactory.createFilter(currentFilterType,context)
        );

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
                File outputFile=makeTempFile(pictureFolderPath.getAbsolutePath(),"IMG_", ".jpg");
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
        Log.d(TAG, "onSurfaceChanged: "+width+" "+height);
        GLES20.glViewport(0,0,width,height);
        filterGroup.onFilterChanged(width,height);
        if(cameraEngine.isCameraOpened()){
            cameraEngine.stopPreview();
            cameraEngine.releaseCamera();
        }
        cameraEngine.setTexture(oesFilter.getGlOESTexture().getTextureId());
        cameraEngine.openCamera(false);
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

    public interface PictureTakenCallBack{
        void saveAsBitmap(final byte[] data);
    }

    public static File makeTempFile(String saveDir, String prefix, String extension) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        final File dir = new File(saveDir);
        dir.mkdirs();
        return new File(dir, prefix + timeStamp + extension);
    }
}
