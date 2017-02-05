package com.martin.ads.omoshiroilib.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;

import com.martin.ads.omoshiroilib.glessential.CameraView;

import java.io.IOException;

/**
 * Created by Ads on 2017/1/26.
 */
@SuppressWarnings("deprecation")
public class CameraEngine
        implements SurfaceTexture.OnFrameAvailableListener,
        Camera.AutoFocusCallback, Camera.PreviewCallback {

    private static final String TAG = "CameraEngine";
    private SurfaceTexture mSurfaceTexture;
    private CameraView.RenderCallback renderCallback;

    private Camera camera;
    private Camera.Parameters mParams;
    private boolean cameraOpened;

    private byte[]              mBuffer;

    //frameWidth=size.height
    private int frameWidth;
    private int frameHeight;

    private FakeMat[] mFrameChain;
    private int mChainIdx;
    private Thread mWorkerThread;
    private boolean mStopThread;
    private boolean mCameraFrameReady;

    public CameraEngine() {
        frameWidth=720; frameHeight=1280;
        cameraOpened=false;

        mChainIdx = 0;
        mFrameChain=new FakeMat[2];
        mFrameChain[0]=new FakeMat();
        mFrameChain[1]=new FakeMat();
    }

    public void setTexture(int mTextureID){
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
    }
    public void doTextureUpdate(float[] mSTMatrix){
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
    }

    public void openCamera(boolean facingFront) {
        synchronized (this) {
            int facing=facingFront? Camera.CameraInfo.CAMERA_FACING_FRONT:Camera.CameraInfo.CAMERA_FACING_BACK;
            camera = Camera.open(getCameraIdWithFacing(facing));
            camera.setPreviewCallbackWithBuffer(this);
            if (camera != null) {
                mParams = camera.getParameters();
//                List<Camera.Size> list=mParams.getSupportedPreviewSizes();
//                for(Camera.Size i:list){
//                    Log.d(TAG, "openCamera: "+i.width+" "+i.height);
//                }
                mParams.setPreviewSize(frameHeight,frameWidth);
                //mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                int size = frameWidth*frameHeight;
                size = size * ImageFormat.getBitsPerPixel(mParams.getPreviewFormat()) / 8;
                if (mBuffer==null) mBuffer = new byte[size];
                mFrameChain[0].init(size);
                mFrameChain[1].init(size);
                camera.addCallbackBuffer(mBuffer);
                camera.setParameters(mParams);
                cameraOpened=true;
            }
        }
    }

    public void startPreview(){
        if(camera!=null){
            try {
                camera.setPreviewTexture(mSurfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();

            mCameraFrameReady = false;
            mStopThread = false;
            mWorkerThread = new Thread(new CameraWorker());
            mWorkerThread.start();
        }
    }

    public void stopPreview(){
        synchronized (this) {
            if(camera!=null){
                mStopThread = true;
                synchronized (this) {
                    this.notify();
                }
                mWorkerThread =  null;
                camera.stopPreview();
            }
        }
    }

    public void releaseCamera() {
        synchronized (this) {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.release();
                camera = null;
            }
            cameraOpened = false;
        }
    }



    @Override
    public void onAutoFocus(boolean success, Camera camera) {
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        renderCallback.renderImmediately();
    }

    //Camera.CameraInfo.CAMERA_FACING_FRONT
    //Camera.CameraInfo.CAMERA_FACING_BACK
    private int getCameraIdWithFacing(int facing){
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == facing) {
                return camIdx;
            }
        }
        return 0;
    }


    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //Log.d(TAG, "onPreviewFrame: ");
        synchronized (this) {
            mFrameChain[mChainIdx].putData(data);
            mCameraFrameReady = true;
            camera.addCallbackBuffer(mBuffer);
            this.notify();
        }
    }


    private class CameraWorker implements Runnable {
        @Override
        public void run() {
            do {
                boolean hasFrame = false;
                synchronized (CameraEngine.this) {
                    try {
                        while (!mCameraFrameReady && !mStopThread) {
                            CameraEngine.this.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mCameraFrameReady) {
                        mChainIdx = 1 - mChainIdx;
                        mCameraFrameReady = false;
                        hasFrame = true;
                    }
                }

                if (!mStopThread && hasFrame) {
                   //processCameraFrame(frameHeight,frameWidth,mFrameChain[1 - mChainIdx].getFrame());
                }
            } while (!mStopThread);
            Log.d(TAG, "Finish processing thread");
        }
    }


    public void setRenderCallback(CameraView.RenderCallback renderCallback) {
        this.renderCallback = renderCallback;
    }

    public boolean isCameraOpened() {
        return cameraOpened;
    }

    public void focusCamera(MotionEvent event){
        synchronized (this) {
            if (camera != null) {
                camera.cancelAutoFocus();
                camera.autoFocus(this);
            }
        }
    }

    public PreviewSize getPreviewSize(){
        return new PreviewSize(frameWidth,frameHeight);
    }

    public void setPreviewSize(PreviewSize previewSize){
        frameWidth=previewSize.width;
        frameHeight=previewSize.height;
    }

    public class PreviewSize{
        private int width,height;

        public PreviewSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
