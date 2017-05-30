/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martin.ads.testfaceu.faceu;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.util.Pair;

import com.lemon.faceu.openglfilter.common.FilterConstants;
import com.lemon.faceu.openglfilter.detect.DirectionDetector;
import com.lemon.faceu.openglfilter.gpuimage.base.FixedFrameBufferQueue;
import com.lemon.faceu.openglfilter.gpuimage.base.GPUImageFilter;
import com.lemon.faceu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.lemon.faceu.openglfilter.gpuimage.draw.OpenGlUtils;
import com.lemon.faceu.openglfilter.gpuimage.draw.Rotation;
import com.lemon.faceu.openglfilter.gpuimage.util.TextureRotationUtil;
import com.lemon.faceu.openglfilter.grab.IImageReader;
import com.lemon.faceu.openglfilter.grab.SyncEGLImageReader;
import com.lemon.faceu.sdk.utils.JniEntry;
import com.lemon.faceu.sdk.utils.ObjectCacher;
import com.lemon.faceusdkdemo.detect.FaceDetectorType;
import com.lemon.faceusdkdemo.detect.IFaceDetector;
import com.martin.ads.testfaceu.faceu.detect.SenseTimeDetector;
import com.martin.ads.testfaceu.faceu.fake.Logger;
import com.martin.ads.testfaceu.faceu.fake.LoggerFactory;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class GPUImageRenderer implements Renderer, PreviewCallback, SenseTimeDetector.FaceDetectorListener {
    private final static Logger log = LoggerFactory.getLogger(GPUImageRenderer.class);

    /**
     * 摄像头数据监听器
     */
    public interface OnPrevFrameListener {
        /**
         * 一帧数据的回复
         *
         * @param data YUV数据，上层不能保存该引用，如果需要保存，则自己复制一份
         */
        void onPrevFrame(byte[] data, int width, int height);
    }

    /**
     * 绘制相关的回调接口
     */
    public interface OnDrawFrameListener {
        void onDrawFrame();
    }

    public interface OnProcessedFrameListener {
        void onProcessedFrame(byte[] data, int width, int height, long timestamp, Rotation rotation);
    }

    @IntDef(value = {
            CMD_PROCESS_FRAME,
            CMD_SETUP_SURFACE_TEXTURE,
            CMD_SET_FILTER,
            CMD_DELETE_IMAGE,
            CMD_SET_IMAGE_BITMAP,
            CMD_RERUN_ONDRAW_RUNNABLE,
            CMD_RERUN_DRAWEND_RUNNABLE,
            CMD_RESET_RS_SIZE
    }
    )
    @Retention(RetentionPolicy.SOURCE)
    public @interface RenderCmd {
    }

    final static int CMD_PROCESS_FRAME = 0;
    final static int CMD_SETUP_SURFACE_TEXTURE = 1;
    final static int CMD_SET_FILTER = 2;
    final static int CMD_DELETE_IMAGE = 3;
    final static int CMD_SET_IMAGE_BITMAP = 4;
    final static int CMD_RERUN_ONDRAW_RUNNABLE = 5;
    final static int CMD_RERUN_DRAWEND_RUNNABLE = 6;
    final static int CMD_RESET_RS_SIZE = 7;

    /**
     * 命令的一项
     */
    static class CmdItem {
        @RenderCmd
        int cmdId;
        Object param1;
        Object param2;
    }

    static final float CUBE[] = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};

    GPUImageFilterGroupBase mGroupBase;
    int mGLTextureId;

    final FloatBuffer mNormalCubeBuffer;
    final FloatBuffer mNormalTextureFlipBuffer;

    SurfaceTexture mSurfaceTexture = null;
    final FloatBuffer mGLCubeBuffer;
    final FloatBuffer mGLTextureBuffer;
    ByteBuffer mGLRgbBuffer;

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

    OnPrevFrameListener mPrevFrameLsn;
    OnDrawFrameListener mDrawFrameLsn;
    OnProcessedFrameListener mProcessedFrameLsn;

    // 用来缓存当前摄像头的信息，这里假设了一个camera的实例的预览大小一旦设置了，就不会再变
    Camera mCacheCamera = null;
    Point mCachePrevSize;
    DirectionDetector mDirectionDetector;

    //RsYuv mRsYuv;
    FixedFrameBufferQueue mFrameBufferQueue;
    SyncEGLImageReader mSyncEGLImageReader;

    final Object mFaceDetectorLock = new Object();
    IFaceDetector mFaceDetector;

    int mFaceCount = 0;
    PointF[][] mFaceDetectResultLst;

    boolean mSurfaceCreated = false; // surface是否创建了，如果surface没有创建，意味着render线程还没开始执行

    int mOutputTextureId[] = new int[]{OpenGlUtils.NO_TEXTURE};
    int mOutputFrameBufferId[] = new int[]{OpenGlUtils.NO_TEXTURE};

    GPUImageFilter mNormalFilter = new GPUImageFilter();
    GLSurfaceView mSurfaceView;

    int mCameraFrameRate = 30; // 录制的时候,不好改变摄像头的帧率,所以需要在收到数据的时候丢帧
    long mFirstFrameTick = -1;
    long mFrameCount = 0;

    public void setDirectionDetector(DirectionDetector detector) {
        mDirectionDetector = detector;
    }

    ObjectCacher<CmdItem> mCmdItemCacher = new ObjectCacher<CmdItem>(20) {
        @Override
        public CmdItem newInstance() {
            return new CmdItem();
        }
    };

    @Override
    public void onDetectFinish() {
        if (null != mSurfaceView) {
            mSurfaceView.requestRender();
        }
    }

    public GPUImageRenderer(final GPUImageFilterGroupBase filter) {
        mGroupBase = filter;
        mRunOnDraw = new LinkedList<>();
        mRunOnDrawEnd = new LinkedList<>();
        mGLTextureId = OpenGlUtils.NO_TEXTURE;

        mGLCubeBuffer = ByteBuffer.allocateDirect(CUBE.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        setRotation(Rotation.NORMAL, false, false);

//        if (FuCore.getCore().canUseRs()) {
//            mRsYuv = new RsYuv(FuCore.getCore().getGlobalRs());
//        }

        mFaceDetectResultLst = new PointF[FilterConstants.MAX_FACE_COUNT][106];
        for (int i = 0; i < FilterConstants.MAX_FACE_COUNT; ++i) {
            PointF[] pointFs = mFaceDetectResultLst[i];
            for (int j = 0; j < pointFs.length; ++j) {
                pointFs[j] = new PointF(0, 0);
            }
        }

        mNormalCubeBuffer = ByteBuffer.allocateDirect(FilterConstants.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mNormalCubeBuffer.put(FilterConstants.CUBE).position(0);

        float[] flipTexture = TextureRotationUtil.getRotation(Rotation.NORMAL, false, true);
        mNormalTextureFlipBuffer = ByteBuffer.allocateDirect(flipTexture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mNormalTextureFlipBuffer.put(flipTexture).position(0);
    }

    public void initFaceDetector(FaceDetectorType type) {
        synchronized (mFaceDetectorLock) {
            if (null != mFaceDetector) {
                return;
            }

            if (type == FaceDetectorType.SENSETIME) {
                mFaceDetector = new SenseTimeDetector(this);
            }
            mFaceDetector.init(FuCore.getCore().getContext());
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

    public void switchDetectMaxFaceCount(int maxFaceCount) {
        synchronized (mFaceDetectorLock) {
            if (null != mFaceDetector) {
                mFaceDetector.switchMaxFaceCount(maxFaceCount);
            }
        }
    }

    public boolean isSurfaceCreated() {
        return mSurfaceCreated;
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        mSurfaceCreated = true;

        GLES20.glClearColor(0, 0, 0, 1);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        mGroupBase.init();
        mNormalFilter.init();

        if (null != mFrameBufferQueue) {
            mFrameBufferQueue.destroy();
            mFrameBufferQueue = null;
        }
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        log.debug("onSurfaceChanged, width: " + width + ", height: " + height);

        mOutputWidth = width;
        mOutputHeight = height;
        GLES20.glViewport(0, 0, width, height);

        mGroupBase.onOutputSizeChanged(width, height);

        adjustImageScaling();

        if (null != mSyncEGLImageReader) {
            mSyncEGLImageReader.stopRecording();
            mSyncEGLImageReader = null;
        }

        mSyncEGLImageReader = new SyncEGLImageReader();
        mSyncEGLImageReader.startRecording(null, width, height);
        mSyncEGLImageReader.setImageReaderCallback(new IImageReader.ImageReaderCallback() {
            @Override
            public void onReadData(long timestamp, ByteBuffer pixelBuf, int height, int width, Rotation rotation) {
//                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(pixelBuf.array()));
//                saveBmpToFile(bitmap, new File(Environment.getExternalStorageDirectory()+"/Omoshiroi/" + timestamp + ".jpg"));
                if (mProcessedFrameLsn != null) {
                    mProcessedFrameLsn.onProcessedFrame(pixelBuf.array(), width, height, timestamp, rotation);
                }
            }
        });

        // 大小变化的时候，并且正在录制，则需要重新初始化 FrameBuffer
        if (null != mSyncEGLImageReader) {
            if (null != mFrameBufferQueue) {
                mFrameBufferQueue.destroy();
            }
            mFrameBufferQueue = new FixedFrameBufferQueue();
            mFrameBufferQueue.init(width, height);
        }

        if (mOutputTextureId[0] == OpenGlUtils.NO_TEXTURE) { // first time
            GLES20.glGenTextures(1, mOutputTextureId, 0);
        }

        if (mOutputFrameBufferId[0] == OpenGlUtils.NO_TEXTURE) { // first time
            GLES20.glGenFramebuffers(1, mOutputFrameBufferId, 0);

            bindFrameBuffer(mOutputFrameBufferId[0], mOutputTextureId[0], width, height);
        }

        mNormalFilter.onOutputSizeChanged(width, height);
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll(mRunOnDraw);

        synchronized (mFaceDetectorLock) {
            mFaceCount = mFaceDetector.getFaceDetectResult(mFaceDetectResultLst, mImageScaleWidth, mImageScaleHeight,
                    mOutputWidth, mOutputHeight);
        }

        long timestamp = 0;
        if (null != mSurfaceTexture) {
            mSurfaceTexture.updateTexImage();
            timestamp = mSurfaceTexture.getTimestamp();
        }

        Pair<Integer, Integer> fbo = null;
        if (null != mFrameBufferQueue) {
            fbo = (Pair<Integer, Integer>) mFrameBufferQueue.getFrameBufferId(); // frame buffer -- texture id
        }

        // 坐标已经规范到了屏幕范围内了
        mGroupBase.setFaceDetResult(mFaceCount, mFaceDetectResultLst, mOutputWidth, mOutputHeight);

        if (null != fbo) {
            // 如果上层没有设置绘制到屏幕时使用的filter，则创建一个普通的filter
            if (null == mNormalFilter) {
                mNormalFilter = new GPUImageFilter();
                mNormalFilter.init();
                mNormalFilter.onOutputSizeChanged(mOutputWidth, mOutputHeight);
            }

            // textureId：图像输入
            // outFrameBufferId：需要绘制到哪里。如果为-1，表示需要绘制到屏幕；如果不为-1，则必须先行绑定好Texture等。
            // cubeBuffer：绘制的矩阵
            // textureBuffer：需要使用图像的哪一部分作为输入
            // timestamp：时间戳，目前传为0即可
            mGroupBase.draw(mGLTextureId, fbo.first, mGLCubeBuffer, mGLTextureBuffer);
            mNormalFilter.onDraw(fbo.second, mNormalCubeBuffer, mNormalTextureFlipBuffer);
        } else {
            mGroupBase.draw(mGLTextureId, OpenGlUtils.NO_TEXTURE, mGLCubeBuffer, mGLTextureBuffer);
        }

        // 使用 mVideoRecorder 需要加锁
        if (null != mSyncEGLImageReader && null != mSurfaceTexture && null != fbo) {
            Semaphore semaphore = mSyncEGLImageReader.frameAvailable(fbo.second, timestamp, true);
            mFrameBufferQueue.markCurrentTextureBeenUsed(fbo.second, semaphore);
        }

        if (null != mDrawFrameLsn) {
            mDrawFrameLsn.onDrawFrame();
        }

        runAll(mRunOnDrawEnd);
    }

    public void uninit() {
        stopFaceDetector();

        if (null != mSyncEGLImageReader) {
            mSyncEGLImageReader.stopRecording();
            mSyncEGLImageReader = null;
        }
    }

    public void setOnPrevFrameListener(OnPrevFrameListener listener) {
        mPrevFrameLsn = listener;
    }

    public void setOnDrawFrameListener(OnDrawFrameListener listener) {
        mDrawFrameLsn = listener;
    }

    public void setOnProcessedFrameListener(OnProcessedFrameListener listener) {
        mProcessedFrameLsn = listener;
    }

    void runAll(Queue<CmdItem> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                CmdItem cmdItem = queue.poll();

                switch (cmdItem.cmdId) {
                    case CMD_PROCESS_FRAME:
                        processFrame((byte[]) cmdItem.param1, (Camera) cmdItem.param2);
                        break;
                    case CMD_SETUP_SURFACE_TEXTURE:
                        setUpSurfaceTextureInternal((Camera) cmdItem.param1, (byte[]) cmdItem.param2);
                        break;
                    case CMD_SET_FILTER:
                        setFilterInternal((GPUImageFilterGroupBase) cmdItem.param1);
                        break;
                    case CMD_DELETE_IMAGE:
                        deleteImageInternal();
                        break;
                    case CMD_SET_IMAGE_BITMAP:
                        setImageBitmapInternal((Bitmap) cmdItem.param1, (Boolean) cmdItem.param2);
                        break;
                    case CMD_RERUN_ONDRAW_RUNNABLE:
                        ((Runnable) cmdItem.param1).run();
                        break;
                    case CMD_RERUN_DRAWEND_RUNNABLE:
                        ((Runnable) cmdItem.param1).run();
                        break;
                    case CMD_RESET_RS_SIZE:
                        resetRsSize((Integer) cmdItem.param1, (Integer) cmdItem.param2);
                        break;
                    default:
                        throw new RuntimeException("can't find command");
                }

                cmdItem.param1 = cmdItem.param2 = null;
                mCmdItemCacher.cache(cmdItem);
            }
        }
    }

    void resetRsSize(int width, int height) {
//        if (null != mRsYuv) {
//            mRsYuv.reset(width, height);
//        }
    }

    void deleteImageInternal() {
        GLES20.glDeleteTextures(1, new int[]{mGLTextureId}, 0);
        mGLTextureId = OpenGlUtils.NO_TEXTURE;
    }

    void processFrame(final byte[] data, final Camera camera) {
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

//        if (!FuCore.getCore().canUseRs()) {
            JniEntry.YUVtoRBGA(data, mCachePrevSize.x, mCachePrevSize.y, mGLRgbBuffer.array());
//        }else {
//            mRsYuv.execute(data, mGLRgbBuffer.array());
//        }

        mGLTextureId = OpenGlUtils.loadTexture(mGLRgbBuffer, mCachePrevSize, mGLTextureId);
        camera.addCallbackBuffer(data);
        mGLRgbBuffer.clear();
    }

    private void setUpSurfaceTextureInternal(final Camera camera, byte[] data) {
        if (null == camera) {
            log.error("setup camera failed, camera is null");
            return;
        }

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mSurfaceTexture = new SurfaceTexture(textures[0]);

        try {
            camera.addCallbackBuffer(data);
            camera.setPreviewTexture(mSurfaceTexture);
            camera.setPreviewCallbackWithBuffer(GPUImageRenderer.this);
            camera.startPreview();
        } catch (Exception e) {
            log.error("setup camera failed, " + e.getMessage());
        }
        log.debug("setUpSurfaceTextureInternal " + camera + " " + data.length);
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

    void setImageBitmapInternal(final Bitmap bitmap, final boolean recycle) {
        Bitmap resizedBitmap = null;
        if (bitmap.getWidth() % 2 == 1) {
            resizedBitmap = Bitmap.createBitmap(bitmap.getWidth() + 1, bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas can = new Canvas(resizedBitmap);
            can.drawARGB(0x00, 0x00, 0x00, 0x00);
            can.drawBitmap(bitmap, 0, 0, null);
        }

        mGLTextureId = OpenGlUtils.loadTexture(resizedBitmap != null ? resizedBitmap : bitmap,
                mGLTextureId, recycle);
        if (resizedBitmap != null) {
            resizedBitmap.recycle();
        }
        mImageWidth = bitmap.getWidth();
        mImageHeight = bitmap.getHeight();
        adjustImageScaling();
    }

    @Override
    public void onPreviewFrame(final byte[] data, final Camera camera) {
        // 如果还没到下一帧所要求的时间点,则丢弃这一帧
        if ((System.currentTimeMillis() - mFirstFrameTick) < (mFrameCount + 1) * (1000 / mCameraFrameRate)) {
            camera.addCallbackBuffer(data);
            log.warn("too many frame from camera, drop it");
            return;
        }

        if (-1 == mFirstFrameTick) {
            mFirstFrameTick = System.currentTimeMillis();
        }
        mFrameCount++;

        if (mCacheCamera != camera) {
            mCacheCamera = camera;
            Size previewSize = camera.getParameters().getPreviewSize();
            mCachePrevSize = new Point(previewSize.width, previewSize.height);
        }

        if (null != mPrevFrameLsn) {
            mPrevFrameLsn.onPrevFrame(data, mCachePrevSize.x, mCachePrevSize.y);
        }

        if (mGLRgbBuffer == null || mGLRgbBuffer.capacity() != mCachePrevSize.x * mCachePrevSize.y * 4) {
            mGLRgbBuffer = ByteBuffer.allocate(mCachePrevSize.x * mCachePrevSize.y * 4);
        }

        runOnDraw(CMD_PROCESS_FRAME, data, camera);
        mSurfaceView.requestRender();
    }

    public void setUpSurfaceTexture(final Camera camera, byte[] data) {
        runOnDraw(CMD_SETUP_SURFACE_TEXTURE, camera, data);
    }

    public void setFilter(final GPUImageFilter filter) {
        runOnDraw(CMD_SET_FILTER, filter, null);
    }

    public void deleteImage() {
        runOnDraw(CMD_DELETE_IMAGE, null, null);
    }

    public void setImageBitmap(final Bitmap bitmap) {
        setImageBitmap(bitmap, true);
    }

    public void setImageBitmap(final Bitmap bitmap, final boolean recycle) {
        if (bitmap == null) {
            return;
        }

        runOnDraw(CMD_SET_IMAGE_BITMAP, bitmap, recycle);
    }

    public void addRunnableOnDrawEnd(Runnable runnable) {
        runOnDrawEnd(CMD_RERUN_DRAWEND_RUNNABLE, runnable, null);
    }

    public void setScaleType(GPUImage.ScaleType scaleType) {
        mScaleType = scaleType;
    }

    public void setGlSurfaceView(GLSurfaceView surfaceView) {
        mSurfaceView = surfaceView;
    }

    public void setFrameRate(int frameRate) {
        mCameraFrameRate = frameRate;
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
        float[] textureCords = TextureRotationUtil.getRotation(mRotation,
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

    public void setRotationCamera(final Rotation rotation,
                                  final boolean flipHorizontal, final boolean flipVertical) {
        mGLTextureId = OpenGlUtils.NO_TEXTURE;
        setRotation(rotation, flipVertical, flipHorizontal);
    }

    public void setRotation(final Rotation rotation) {
        mRotation = rotation;
        adjustImageScaling();
    }

    public void setRotation(final Rotation rotation, final boolean flipHorizontal, final boolean flipVertical) {
        mFlipHorizontal = flipHorizontal;
        mFlipVertical = flipVertical;
        setRotation(rotation);
    }

    public Rotation getRotation() {
        return mRotation;
    }

    void runOnDraw(@RenderCmd int cmdId, Object param1, Object param2) {
        CmdItem item = mCmdItemCacher.obtain();
        item.cmdId = cmdId;
        item.param1 = param1;
        item.param2 = param2;

        synchronized (mRunOnDraw) {
            mRunOnDraw.add(item);
        }
    }

    void runOnDrawEnd(@RenderCmd int cmdId, Object param1, Object param2) {
        CmdItem item = mCmdItemCacher.obtain();
        item.cmdId = cmdId;
        item.param1 = param1;
        item.param2 = param2;

        synchronized (mRunOnDrawEnd) {
            mRunOnDrawEnd.add(item);
        }
    }

    public int getOutputWidth() {
        return mOutputWidth;
    }

    public int getOutputHeight() {
        return mOutputHeight;
    }

    public void clearImage() {
        runAll(mRunOnDraw);
        runAll(mRunOnDrawEnd);

        mGLTextureId = OpenGlUtils.NO_TEXTURE;
        if (null != mGLRgbBuffer) {
            mGLRgbBuffer.clear();
            mGLRgbBuffer = null;
        }
    }

    private void bindFrameBuffer(int fbBufferId, int fbTextureId, int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, fbTextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbBufferId);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, fbTextureId, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public static boolean saveBmpToFile(Bitmap bmp, File file) {
        if (null == bmp || null == file) {
            log.error("bmp or file is null");
            return false;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return writeToFile(baos.toByteArray(), file);
    }

    public static boolean writeToFile(byte[] data, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
