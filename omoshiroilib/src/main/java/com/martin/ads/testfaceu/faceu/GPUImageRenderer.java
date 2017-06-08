

package com.martin.ads.testfaceu.faceu;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Build;
import android.support.annotation.IntDef;
import android.util.Log;

import com.lemon.faceu.sdk.utils.JniEntry;

import com.martin.ads.omoshiroilib.constant.GLEtc;
import com.martin.ads.omoshiroilib.constant.Rotation;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.DirectionDetector;
import com.martin.ads.omoshiroilib.flyu.openglfilter.common.FilterConstants;
import com.martin.ads.omoshiroilib.flyu.IFaceDetector;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.ObjectCache;
import com.martin.ads.omoshiroilib.util.PlaneTextureRotationUtils;
import com.martin.ads.omoshiroilib.util.TextureUtils;
import com.martin.ads.omoshiroilib.flyu.detect.SenseTimeDetector;
import com.martin.ads.omoshiroilib.flyu.fake.Logger;
import com.martin.ads.omoshiroilib.flyu.fake.LoggerFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class GPUImageRenderer implements Renderer, PreviewCallback, IFaceDetector.FaceDetectorListener {
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

    final static int CMD_PROCESS_FRAME = 0;
    final static int CMD_SETUP_SURFACE_TEXTURE = 1;
    final static int CMD_SET_FILTER = 2;
    final static int CMD_RERUN_ONDRAW_RUNNABLE = 5;
    final static int CMD_RERUN_DRAWEND_RUNNABLE = 6;

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

    // 用来缓存当前摄像头的信息，这里假设了一个camera的实例的预览大小一旦设置了，就不会再变
    Camera mCacheCamera = null;
    Point mCachePrevSize;
    DirectionDetector mDirectionDetector;

    final Object mFaceDetectorLock = new Object();
    IFaceDetector mFaceDetector;

    int mFaceCount = 0;
    PointF[][] mFaceDetectResultLst;

    boolean mSurfaceCreated = false; // surface是否创建了，如果surface没有创建，意味着render线程还没开始执行

    GLSurfaceView mSurfaceView;

    int mCameraFrameRate = 30; // 录制的时候,不好改变摄像头的帧率,所以需要在收到数据的时候丢帧
    long mFirstFrameTick = -1;
    long mFrameCount = 0;

    public void setDirectionDetector(DirectionDetector detector) {
        mDirectionDetector = detector;
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

    public GPUImageRenderer(final GPUImageFilterGroupBase filter) {
        mGroupBase = filter;
        mRunOnDraw = new LinkedList<>();
        mRunOnDrawEnd = new LinkedList<>();
        mGLTextureId = GLEtc.NO_TEXTURE;

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

    public void initFaceDetector() {
        synchronized (mFaceDetectorLock) {
            if (null != mFaceDetector) {
                return;
            }

            mFaceDetector = new SenseTimeDetector(this);
            mFaceDetector.init(GlobalConfig.context);
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
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        log.debug("onSurfaceChanged, width: " + width + ", height: " + height);

        mOutputWidth = width;
        mOutputHeight = height;
        GLES20.glViewport(0, 0, width, height);

        mGroupBase.onOutputSizeChanged(width, height);

        adjustImageScaling();
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll(mRunOnDraw);

        synchronized (mFaceDetectorLock) {
            mFaceCount = mFaceDetector.getFaceDetectResult(mFaceDetectResultLst, mImageScaleWidth, mImageScaleHeight,
                    mOutputWidth, mOutputHeight);
        }

        if (null != mSurfaceTexture) {
            mSurfaceTexture.updateTexImage();
        }

        // 坐标已经规范到了屏幕范围内了
        mGroupBase.setFaceDetResult(mFaceCount, mFaceDetectResultLst, mOutputWidth, mOutputHeight);

        mGroupBase.draw(mGLTextureId, GLEtc.NO_TEXTURE, mGLCubeBuffer, mGLTextureBuffer);

        runAll(mRunOnDrawEnd);
    }

    public void uninit() {
        stopFaceDetector();
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

        JniEntry.YUVtoRBGA(data, mCachePrevSize.x, mCachePrevSize.y, mGLRgbBuffer.array());

        mGLTextureId = TextureUtils.getTextureFromByteBufferWithOldTexId(
                mGLRgbBuffer,mCachePrevSize.x,mCachePrevSize.y,mGLTextureId);
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


    public void addRunnableOnDrawEnd(Runnable runnable) {
        runOnDrawEnd(CMD_RERUN_DRAWEND_RUNNABLE, runnable, null);
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

    public void setRotationCamera(final Rotation rotation,
                                  final boolean flipHorizontal, final boolean flipVertical) {
        mGLTextureId = GLEtc.NO_TEXTURE;
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

        mGLTextureId = GLEtc.NO_TEXTURE;
        if (null != mGLRgbBuffer) {
            mGLRgbBuffer.clear();
            mGLRgbBuffer = null;
        }
    }

}
