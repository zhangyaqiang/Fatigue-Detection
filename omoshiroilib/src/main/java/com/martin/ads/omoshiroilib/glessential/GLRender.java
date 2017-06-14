package com.martin.ads.omoshiroilib.glessential;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.util.Log;

import com.lemon.faceu.sdk.utils.JniEntry;
import com.martin.ads.omoshiroilib.camera.CameraEngine;
import com.martin.ads.omoshiroilib.camera.IWorkerCallback;
import com.martin.ads.omoshiroilib.codec.MediaAudioEncoder;
import com.martin.ads.omoshiroilib.codec.MediaEncoder;
import com.martin.ads.omoshiroilib.codec.MediaMuxerWrapper;
import com.martin.ads.omoshiroilib.codec.MediaVideoEncoder;
import com.martin.ads.omoshiroilib.constant.GLEtc;
import com.martin.ads.omoshiroilib.constant.Rotation;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.encoder.gles.EGLFilterDispatcher;
import com.martin.ads.omoshiroilib.encoder.gles.GLTextureSaver;
import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.filter.base.FilterGroup;
import com.martin.ads.omoshiroilib.filter.base.OESFilter;
import com.martin.ads.omoshiroilib.filter.base.OrthoFilter;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.filter.base.Rotate2DFilter;
import com.martin.ads.omoshiroilib.filter.helper.FilterFactory;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.flyu.IFaceDetector;
import com.martin.ads.omoshiroilib.flyu.detect.SenseTimeDetector;
import com.martin.ads.omoshiroilib.flyu.fake.Logger;
import com.martin.ads.omoshiroilib.flyu.fake.LoggerFactory;
import com.martin.ads.omoshiroilib.flyu.openglfilter.common.FilterConstants;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.DirectionDetector;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup.GPUImageFilterGroup;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.ObjectCache;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.BufferUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;
import com.martin.ads.omoshiroilib.util.PlaneTextureRotationUtils;
import com.martin.ads.omoshiroilib.util.TextureUtils;
import com.martin.ads.testfaceu.faceu.GPUImage;
import com.martin.ads.testfaceu.faceu.GPUImageRenderer;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//import com.martin.ads.omoshiroilib.filter.effect.*;
//import com.martin.ads.omoshiroilib.filter.effect.mx.*;
//import com.martin.ads.omoshiroilib.filter.ext.*;
//import com.martin.ads.omoshiroilib.filter.imgproc.*;

/**
 * Created by Ads on 2017/1/26.
 */

public class GLRender implements GLSurfaceView.Renderer , IFaceDetector.FaceDetectorListener{
    private static final String TAG = "GLRender";
    public static final boolean USE_OES_TEXTURE=false;

    private final static Logger log = LoggerFactory.getLogger();

    @IntDef(value = {
            CMD_PROCESS_FRAME,
            CMD_SETUP_SURFACE_TEXTURE,
            CMD_SET_FILTER,
            CMD_RERUN_ONDRAW_RUNNABLE,
            CMD_RERUN_DRAWEND_RUNNABLE,
    }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface RenderCmd {
    }

    public final static int CMD_PROCESS_FRAME = 0;
    public final static int CMD_SETUP_SURFACE_TEXTURE = 1;
    public final static int CMD_SET_FILTER = 2;
    public final static int CMD_RERUN_ONDRAW_RUNNABLE = 5;
    public final static int CMD_RERUN_DRAWEND_RUNNABLE = 6;

    /**
     * 命令的一项
     */
    static class CmdItem {
        @GPUImageRenderer.RenderCmd
        int cmdId;
        Object param1;
        Object param2;
    }

    static final float CUBE[] =
            {
                    -1.0f, 1.0f,
                    -1.0f, -1.0f,
                    1.0f, 1.0f,
                    1.0f, -1.0f,
            };

    GPUImageFilterGroupBase mGroupBase;

    final FloatBuffer mGLCubeBuffer;
    final FloatBuffer mGLTextureBuffer;

    int mOutputWidth = 0;
    int mOutputHeight = 0;
    int mImageWidth = 1;
    int mImageHeight = 1;

    // 输入图像可能会和屏幕比例不一样，所以这两个变量存储放大后整个图像的大小
    int mImageScaleWidth = -1;
    int mImageScaleHeight = -1;

    final Queue<CmdItem> mRunOnDraw;
    final Queue<CmdItem> mRunOnDrawEnd;

    Rotation mRotation;
    boolean mFlipHorizontal;
    boolean mFlipVertical;
    GPUImage.ScaleType mScaleType = GPUImage.ScaleType.CENTER_CROP;

    Camera mCacheCamera = null;
    Point mCachePrevSize;
    DirectionDetector mDirectionDetector;

    final Object mFaceDetectorLock = new Object();
    IFaceDetector mFaceDetector;

    int mFaceCount = 0;
    PointF[][] mFaceDetectResultLst;

    GLSurfaceView mSurfaceView;

    int mCameraFrameRate = 30; // 录制的时候,不好改变摄像头的帧率,所以需要在收到数据的时候丢帧
    long mFirstFrameTick = -1;
    long mFrameCount = 0;

    int mGLTextureId;
    ByteBuffer mGLRgbBuffer;

    public void setDirectionDetector(DirectionDetector detector) {
        mDirectionDetector = detector;
    }

    public void setFilter(final GPUImageFilter filter) {
        runOnDraw(CMD_SET_FILTER, filter, null);
    }

    ObjectCache<CmdItem> mCmdItemCacher = new ObjectCache<CmdItem>(20) {
        @Override
        public CmdItem newInstance() {
            return new CmdItem();
        }
    };

    public void onDetectFinish() {
        if (null != mSurfaceView) {
            mSurfaceView.requestRender();
        }
    }

    ////////////////////
    private CameraEngine cameraEngine;
    private FilterGroup filterGroup;
    private FilterGroup postProcessFilters;
    private GLTextureSaver lastProcessFilter;
    private OESFilter oesFilter;
    private Context context;
    private FilterGroup customizedFilters;

    private FilterType currentFilterType=FilterType.NONE;

    private int surfaceWidth;
    private int surfaceHeight;
    private boolean isCameraFacingFront;

    private OrthoFilter orthoFilter;

    private MediaVideoEncoder mVideoEncoder;

    private Rotate2DFilter rotate2DFilter;

    public GLRender(final Context context, CameraEngine cameraEngine) {
        this.context=context;
        this.cameraEngine=cameraEngine;
        filterGroup=new FilterGroup();
        postProcessFilters=new FilterGroup();
        oesFilter=new OESFilter(context);
        rotate2DFilter =new Rotate2DFilter(context);
        if(USE_OES_TEXTURE)
            filterGroup.addFilter(oesFilter);
        else{
            filterGroup.addFilter(rotate2DFilter);
        }
        orthoFilter=new OrthoFilter(context);
        if(GlobalConfig.FULL_SCREEN)
            filterGroup.addFilter(orthoFilter);

        customizedFilters=new FilterGroup();
        customizedFilters.addFilter(FilterFactory.createFilter(currentFilterType,context));

        postProcessFilters.addFilter(new PassThroughFilter(context));
        lastProcessFilter= new GLTextureSaver(context);
        postProcessFilters.addFilter(lastProcessFilter);

        filterGroup.addFilter(customizedFilters);
        filterGroup.addFilter(postProcessFilters);

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
                BitmapUtils.saveBitmapWithFilterApplied(GlobalConfig.context,currentFilterType,bitmap,outputFile.getAbsolutePath(),workerCallback);

            }
        });
        isCameraFacingFront=true;

        mGroupBase = new GPUImageFilterGroup();
        mRunOnDraw = new LinkedList<>();
        mRunOnDrawEnd = new LinkedList<>();

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(PlaneTextureRotationUtils.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        setRotation(Rotation.NORMAL, false, false);


        mFaceDetectResultLst = new PointF[FilterConstants.MAX_FACE_COUNT][106];
        for (int i = 0; i < FilterConstants.MAX_FACE_COUNT; ++i) {
            PointF[] pointFs = mFaceDetectResultLst[i];
            for (int j = 0; j < pointFs.length; ++j) {
                pointFs[j] = new PointF(0, 0);
            }
        }
    }


    public void setRotation(final Rotation rotation) {
        mRotation = rotation;
        adjustImageScaling();
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void adjustImageScaling() {
        float outputWidth = mOutputWidth;
        float outputHeight = mOutputHeight;
        if (mRotation == Rotation.ROTATION_270
                || mRotation == Rotation.ROTATION_90) {
            outputWidth = mOutputHeight;
            outputHeight = mOutputWidth;
        }

        float ratio1 = outputWidth / mImageWidth;
        float ratio2 = outputHeight / mImageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mImageWidth * ratioMax);
        int imageHeightNew = Math.round(mImageHeight * ratioMax);

        float ratioWidth = imageWidthNew / outputWidth;
        float ratioHeight = imageHeightNew / outputHeight;

        float[] cube = CUBE;
        float[] textureCords = PlaneTextureRotationUtils.getRotation(mRotation,
                mFlipHorizontal, mFlipVertical);
        if (mScaleType == GPUImage.ScaleType.CENTER_CROP) {
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distHorizontal),
                    addDistance(textureCords[1], distVertical),
                    addDistance(textureCords[2], distHorizontal),
                    addDistance(textureCords[3], distVertical),
                    addDistance(textureCords[4], distHorizontal),
                    addDistance(textureCords[5], distVertical),
                    addDistance(textureCords[6], distHorizontal),
                    addDistance(textureCords[7], distVertical),};
        } else {
            cube = new float[]{CUBE[0] / ratioHeight, CUBE[1] / ratioWidth,
                    CUBE[2] / ratioHeight, CUBE[3] / ratioWidth,
                    CUBE[4] / ratioHeight, CUBE[5] / ratioWidth,
                    CUBE[6] / ratioHeight, CUBE[7] / ratioWidth,};
        }

        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(cube).position(0);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(textureCords).position(0);

        float normalImageHeight = mImageHeight;
        float normalImageWidth = mImageWidth;
        if (mRotation == Rotation.ROTATION_270 || mRotation == Rotation.ROTATION_90) {
            normalImageHeight = mImageWidth;
            normalImageWidth = mImageHeight;
        }

        if (1.0f * mOutputHeight / mOutputWidth > 1.0f * normalImageHeight / normalImageWidth) {
            mImageScaleHeight = mOutputHeight;
            mImageScaleWidth = (int) (1.0f * normalImageWidth * mImageScaleHeight / normalImageHeight);
        } else {
            mImageScaleWidth = mOutputWidth;
            mImageScaleHeight = (int) (1.0f * normalImageHeight * mImageScaleWidth / normalImageWidth);
        }
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        mFlipHorizontal = flipHorizontal;
        mFlipVertical = flipVertical;
        setRotation(rotation);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        filterGroup.init();
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        runAll(mRunOnDraw);
        long timeStamp=cameraEngine.doTextureUpdate(oesFilter.getSTMatrix());
        Log.d(TAG, "onDrawFrame mGLTextureId: " +mGLTextureId);
        if(USE_OES_TEXTURE){
            filterGroup.onDrawFrame(oesFilter.getGlOESTexture().getTextureId());
        }else {
            System.arraycopy(oesFilter.getSTMatrix(),0,rotate2DFilter.getSTMatrix(),0,oesFilter.getSTMatrix().length);
            filterGroup.onDrawFrame(mGLTextureId);
        }

        if(mVideoEncoder!=null){
            Log.d(TAG, "onDrawFrame: "+mVideoEncoder.toString());
            mVideoEncoder.frameAvailableSoon();
        }

        synchronized (mFaceDetectorLock) {
            mFaceCount = mFaceDetector.getFaceDetectResult(mFaceDetectResultLst, mImageScaleWidth, mImageScaleHeight,
                    mOutputWidth, mOutputHeight);
        }

        // 坐标已经规范到了屏幕范围内了
        Log.d(TAG, "lalalalala: "+mFaceCount+" "+mOutputWidth+" "+mOutputHeight);
        mGroupBase.setFaceDetResult(mFaceCount, mFaceDetectResultLst, mOutputWidth, mOutputHeight);

        mGroupBase.draw(lastProcessFilter.getSavedTextureId(), GLEtc.NO_TEXTURE, mGLCubeBuffer, mGLTextureBuffer);
        runAll(mRunOnDrawEnd);
    }

    void runOnDrawEnd(int cmdId, Object param1, Object param2) {
        CmdItem item = mCmdItemCacher.obtain();
        item.cmdId = cmdId;
        item.param1 = param1;
        item.param2 = param2;

        synchronized (mRunOnDrawEnd) {
            mRunOnDrawEnd.add(item);
        }
    }

    public void addRunnableOnDrawEnd(Runnable runnable) {
        runOnDrawEnd(CMD_RERUN_DRAWEND_RUNNABLE, runnable, null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private EGLContext getSharedContext() {
        return EGL14.eglGetCurrentContext();
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

        mOutputWidth = width;
        mOutputHeight = height;

        if(!isCameraFacingFront)
            setUpCamera(cameraEngine.getDisplayRotate(),
                    isCameraFacingFront, true);
        else setUpCamera(cameraEngine.getDisplayRotate(),
                isCameraFacingFront, false);
        mGroupBase.onOutputSizeChanged(width, height);

        adjustImageScaling();
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
        filterGroup.addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                isCameraFacingFront=!isCameraFacingFront;
                mGLTextureId = GLEtc.NO_TEXTURE;
                cameraEngine.switchCamera(isCameraFacingFront);
                if(!isCameraFacingFront)
                    setUpCamera(cameraEngine.getDisplayRotate(),
                            isCameraFacingFront, true);
                else setUpCamera(cameraEngine.getDisplayRotate(),
                        isCameraFacingFront, false);
            }
        });
    }

    public interface PictureTakenCallBack{
        void saveAsBitmap(final byte[] data);
    }

    public void switchLastFilterOfCustomizedFilters(FilterType filterType){
        if (filterType==null) return;
        currentFilterType=filterType;
        customizedFilters.switchLastFilter(FilterFactory.createFilter(filterType,context));
    }

    public void switchFilterOfPostProcessAtPos(AbsFilter filter,int pos){
        if (filter==null) return;
        postProcessFilters.switchFilterAt(filter,pos);
    }

    public FilterGroup getFilterGroup() {
        return filterGroup;
    }

    public OrthoFilter getOrthoFilter() {
        return orthoFilter;
    }

    private MediaMuxerWrapper mMuxer;
    public static final boolean DEBUG=true;
    private String outputPath;
    FileUtils.FileSavedCallback fileSavedCallback;

    public void startRecording() {
        try {
            File vidFolder=GlobalConfig.context.getCacheDir();
            if (!vidFolder.exists())
                vidFolder.mkdirs();
            outputPath=vidFolder.getAbsolutePath()+FileUtils.getVidName();
            mMuxer = new MediaMuxerWrapper(outputPath);	// if you record audio only, ".m4a" is also OK.
            if (true) {
                // for video capturing
                new MediaVideoEncoder(mMuxer, mMediaEncoderListener, /*surfaceWidth/2*2,surfaceHeight/2*2*/720,1280);
            }
            if (true) {
                // for audio capturing
                new MediaAudioEncoder(mMuxer, mMediaEncoderListener);
            }
            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * request stop recording
     */
    public void stopRecording() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
            mMuxer = null;
            mVideoEncoder=null;
            if(fileSavedCallback!=null)
                fileSavedCallback.onFileSaved(outputPath);
        }
    }

    public void setVideoEncoder(final MediaVideoEncoder encoder) {
        filterGroup.addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                if (encoder != null) {
                    encoder.getRenderHandler().setEglDrawer(new EGLFilterDispatcher(context));
                    encoder.setEglContext(getSharedContext(), lastProcessFilter.getSavedTextureId());
                    mVideoEncoder=encoder;
                }
            }
        });
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onPrepared:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                setVideoEncoder((MediaVideoEncoder)encoder);
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            if (DEBUG) Log.v(TAG, "onStopped:encoder=" + encoder);
            if (encoder instanceof MediaVideoEncoder)
                setVideoEncoder(null);
        }
    };

    public void setFileSavedCallback(FileUtils.FileSavedCallback fileSavedCallback) {
        this.fileSavedCallback = fileSavedCallback;
    }

    public boolean isCameraFacingFront() {
        return isCameraFacingFront;
    }

    public void runOnDraw(int cmdId, Object param1, Object param2) {
        CmdItem item = mCmdItemCacher.obtain();
        item.cmdId = cmdId;
        item.param1 = param1;
        item.param2 = param2;

        synchronized (mRunOnDraw) {
            mRunOnDraw.add(item);
        }
    }

    public void stopFaceDetector() {
        synchronized (mFaceDetectorLock) {
            if (null != mFaceDetector) {
                mFaceDetector.uninit();
                mFaceDetector = null;
            }
        }
    }

    public void uninit() {
        stopFaceDetector();
    }

    void setFilterInternal(final GPUImageFilterGroupBase filter) {
        final GPUImageFilter oldFilter = mGroupBase;
        mGroupBase = filter;
        if (oldFilter != null) {
            oldFilter.releaseNoGLESRes();
            oldFilter.destroy();
        }
        mGroupBase.init();
        GLES20.glUseProgram(mGroupBase.getProgram());
        mGroupBase.onOutputSizeChanged(mOutputWidth, mOutputHeight);
        mGroupBase.setFilterDrawListener(new GPUImageFilterGroupBase.IFilterDrawListener() {
            @Override
            public void onSingleFilterDrawed(int width, int height) {
                Log.d("lalala", "onSingleFilterDrawed: ");
                /*
                ByteBuffer buffer = ByteBuffer.allocateDirect(width * height * 4);
                GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(buffer.array()));
                Log.d(TAG, "test");
                */
            }
        });
    }


    void runAll(Queue<CmdItem> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                CmdItem cmdItem = queue.poll();

                switch (cmdItem.cmdId) {
                    case CMD_PROCESS_FRAME:
                        processFrame((byte[]) cmdItem.param1, (Camera) cmdItem.param2);
                        break;
                    case CMD_SET_FILTER:
                        setFilterInternal((GPUImageFilterGroupBase) cmdItem.param1);
                        break;
                    case CMD_RERUN_ONDRAW_RUNNABLE:
                        ((Runnable) cmdItem.param1).run();
                        break;
                    case CMD_RERUN_DRAWEND_RUNNABLE:
                        ((Runnable) cmdItem.param1).run();
                        break;
                    default:
                        throw new RuntimeException("can't find command");
                }

                cmdItem.param1 = cmdItem.param2 = null;
                mCmdItemCacher.cache(cmdItem);
            }
        }
    }

    void processFrame(final byte[] data, final Camera camera) {
        if(camera==null) return;
        try {
            if (mCacheCamera != camera) {
                mCacheCamera = camera;
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                mCachePrevSize = new Point(previewSize.width, previewSize.height);
            }
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

        if (mImageWidth != mCachePrevSize.x || mImageHeight != mCachePrevSize.y) {
            mImageWidth = mCachePrevSize.x;
            mImageHeight = mCachePrevSize.y;
            adjustImageScaling();

            synchronized (mFaceDetectorLock) {
                if (null != mFaceDetector) {
                    mFaceDetector.reset();
                }
            }
        }

        synchronized (mFaceDetectorLock) {
            if (null != mFaceDetector) {
                mFaceDetector.onFrameAvailable(mCachePrevSize.x, mCachePrevSize.y, mRotation, mFlipVertical,
                        data, mDirectionDetector.getDirection());
            }
        }

        if(!USE_OES_TEXTURE){
            if (mGLRgbBuffer == null || mGLRgbBuffer.capacity() != mCachePrevSize.x * mCachePrevSize.y * 4) {
                mGLRgbBuffer = ByteBuffer.allocate(mCachePrevSize.x * mCachePrevSize.y * 4);
            }
            JniEntry.YUVtoRBGA(data, mCachePrevSize.x, mCachePrevSize.y, mGLRgbBuffer.array());
            mGLTextureId = TextureUtils.getTextureFromByteBufferWithOldTexId(
                    mGLRgbBuffer,mCachePrevSize.x,mCachePrevSize.y,mGLTextureId);
            mGLRgbBuffer.clear();
        }
    }

    public void initFaceDetector() {
        synchronized (mFaceDetectorLock) {
            if (null != mFaceDetector) {
                return;
            }
            mFaceDetector = new SenseTimeDetector(this);
            mFaceDetector.init(GlobalConfig.context);
        }
    }

    public void setUpCamera(final int degrees, final boolean flipHorizontal,
                            final boolean flipVertical) {
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
        setRotationCamera(rotation, flipHorizontal, flipVertical);
    }

    public void setRotationCamera(final Rotation rotation,
                                  final boolean flipHorizontal, final boolean flipVertical) {
        setRotation(rotation, flipVertical, flipHorizontal);
    }

    public void switchDetectMaxFaceCount(int maxFaceCount) {
        synchronized (mFaceDetectorLock) {
            if (null != mFaceDetector) {
                mFaceDetector.switchMaxFaceCount(maxFaceCount);
            }
        }
    }
}
