package com.martin.ads.omoshiroilib.flyu.detect;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.WorkerThread;
import android.util.Log;


import com.lemon.faceu.sdk.utils.JniEntry;
import com.martin.ads.omoshiroilib.constant.Rotation;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.DirectionDetector;
import com.martin.ads.omoshiroilib.flyu.IFaceDetector;
import com.sensetime.stmobileapi.STImageFormat;
import com.sensetime.stmobileapi.STMobile106;
import com.sensetime.stmobileapi.STMobileMultiTrack106;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SenseTimeDetector implements Runnable ,IFaceDetector{
    static final String TAG = "SenseTimeDetector";

    static {
        try {
            System.loadLibrary("st_mobile");
        } catch (Exception e) {
            Log.e(TAG, "can't load st_mobile");
        }
    }

    // 样本的高和宽
    static final int SAMPLE_WIDTH = 720;
    static final int SAMPLE_HEIGHT = 1280;

    Handler mDetectHandler;
    IFaceDetector.FaceDetectorListener mFaceDetectorLsn;
    Context mContext;

    int mInputWidth = -1;
    int mInputHeight = -1;
    int mRotation = 0;
    boolean mMirror = false;

    int mSampleWidth = -1;
    int mSampleHeight = -1;

    ByteBuffer mSampleData;

    final Object mReadyFence = new Object();
    boolean mReady = false;
    boolean mDetecting = false;

    STMobileMultiTrack106 mFaceTrack;
    int mMaxFaceCount = 0;
    STMobile106[] mFaceInfoLst;

    public SenseTimeDetector(IFaceDetector.FaceDetectorListener listener) {
        mFaceDetectorLsn = listener;
    }

    public void init(Context context) {
        if (null != mDetectHandler) {
            throw new RuntimeException("Face detector already initialized!");
        }

        mContext = context;
        new Thread(this, "SenseTimeDetector").start();
    }

    public void uninit() {
        if (null == mDetectHandler) {
            return;
        }

        synchronized (mReadyFence) {
            if (mReady) {
                mDetectHandler.sendMessage(Message.obtain(mDetectHandler, MSG_QUIT));
            }
        }
    }

    /**
     * 重置当前对象,清理未执行的消息
     */
    public void reset() {
        mInputHeight = -1;
        mInputWidth = -1;
        mSampleData = null;
        mSampleWidth = -1;
        mSampleHeight = -1;

        if (null != mDetectHandler) {
            mDetectHandler.removeMessages(MSG_DETECT);
        }
    }

    public void switchMaxFaceCount(int count) {
        if (null != mDetectHandler) {
            mDetectHandler.sendMessage(Message.obtain(mDetectHandler, MSG_SWITCH_FACE_COUNT, count, 0));
        } else {
            mMaxFaceCount = count;
        }
    }

    /**
     * 更新数据输入的尺寸
     */
    @SuppressWarnings("SuspiciousNameCombination")
    void updatePreviewSize(int width, int height, Rotation rotate, boolean mirror) {
        if (mInputWidth == width && mInputHeight == height && mRotation == rotate.asInt()) {
            return;
        }

        // 正常的方向图像的宽和高
        int nWidth, nHeight;
        if (rotate == Rotation.ROTATION_90 || rotate == Rotation.ROTATION_270) {
            nHeight = width;
            nWidth = height;
        } else {
            nHeight = height;
            nWidth = width;
        }

        // 这个宽和高是正常方向上的值
        if (SAMPLE_HEIGHT / (float) SAMPLE_WIDTH < (float) nHeight / nWidth) {
            mSampleHeight = SAMPLE_HEIGHT;
            mSampleWidth = mSampleHeight * nWidth / nHeight;
        } else {
            mSampleWidth = SAMPLE_WIDTH;
            mSampleHeight = mSampleWidth * nHeight / nWidth;
        }

        mRotation = rotate.asInt();
        Log.d(TAG, "updatePreviewSize: "+mRotation);
        mSampleData = ByteBuffer.allocateDirect(mSampleWidth * mSampleHeight).order(ByteOrder.nativeOrder());
        Log.d(TAG, "updatePreviewSize: lalala "+mSampleWidth+" "+mSampleHeight);
        mInputWidth = width;
        mInputHeight = height;
        mMirror = mirror;
    }

    /**
     * 数据帧到了
     * @param yuvData 数据帧,格式是yuv420sp
     */
    public void onFrameAvailable(int width, int height, Rotation rotate, boolean mirror,
                                 byte[] yuvData, int direction) {
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }

            if (mDetecting) {
                return;
            }

            updatePreviewSize(width, height, rotate, mirror);

            JniEntry.YuvToGrayAndScaleJava(yuvData, mInputWidth, mInputHeight, mRotation, mMirror,
                    mSampleData.array(), mSampleWidth, mSampleHeight);
            Log.d(TAG, "onFrameAvailable: "+mInputWidth+ " "+mInputHeight+" "+mSampleWidth+" "+mSampleHeight);
            // 将灰度数组生成bitmap的代码,用于测试
//			ByteBuffer grayBuffer = ByteBuffer.allocateDirect(mSampleWidth * mSampleHeight * 4).order(ByteOrder.nativeOrder());
//			byte[] grayData = grayBuffer.array();
//			byte[] sampleData = mSampleData.array();
//			int offset = 0;
//			for (int i = 0; i < mSampleHeight; ++i) {
//				for (int j = 0; j < mSampleWidth; ++j) {
//					offset = i * mSampleWidth + j;
//					grayData[offset * 4 + 1] = grayData[offset * 4 + 2] = grayData[offset * 4] = sampleData[offset];
//					grayData[offset * 4 + 3] = (byte) 0xff;
//				}
//			}
//
//			Bitmap grayBitmap = Bitmap.createBitmap(mSampleWidth, mSampleHeight, Bitmap.Config.ARGB_8888);
//			grayBitmap.copyPixelsFromBuffer(grayBuffer);
//            BitmapUtils.savePNGBitmap(grayBitmap, Environment.getExternalStorageDirectory()+"/Omoshiroi/test.png");
            mDetectHandler.sendMessage(Message.obtain(mDetectHandler, MSG_DETECT, direction, 0, mSampleData));
        }
    }

    public int getFaceDetectResult(PointF[][] detectResult, int imageScaleWidth, int imageScaleHeight,
                                   int outputWidth, int outputHeight) {
        float width = 100f, height = 100f;
        STMobile106[] cvFaceLst = null;
        int faceCount = 0;
        synchronized (SenseTimeDetector.class) {
            width = mSampleWidth;
            height = mSampleHeight;

            cvFaceLst = mFaceInfoLst;
            faceCount = Math.min(mMaxFaceCount, null == mFaceInfoLst ? 0 : mFaceInfoLst.length);
        }

        int widthTranslate = (imageScaleWidth - outputWidth) / 2;
        int heightTranslate = (imageScaleHeight - outputHeight) / 2;

        if (null != cvFaceLst) {
            for (int i = 0; i < faceCount; ++i) {
                PointF[] pointFs = cvFaceLst[i].getPointsArray();
                PointF[] fResult = detectResult[i];
                int pointCount = Math.min(pointFs.length, fResult.length);

                // 将坐标计算到屏幕范围内
                for (int j = 0; j < pointCount; ++j) {
                    fResult[j].x = pointFs[j].x / width * imageScaleWidth - widthTranslate;
                    fResult[j].y = pointFs[j].y / height * imageScaleHeight - heightTranslate;
                }
            }
        }
        return faceCount;
    }

    @Override
    public void run() {
        // Establish a Looper for this thread, and define a Handler for it.
        Looper.prepare();
        synchronized (mReadyFence) {
            mDetectHandler = new DetectHandler(this);
            mReady = true;
            mReadyFence.notify();
        }
        Looper.loop();

        Log.d(TAG, "Detect thread exiting");
        synchronized (mReadyFence) {
            mReady = mDetecting = false;
            mDetectHandler = null;
        }

        if (null != mFaceTrack) {
            mFaceTrack.destory();
        }
        mFaceTrack = null;
    }

    @WorkerThread
    void handleDetect(ByteBuffer sampleData, int direction) {
        mDetecting = true;

        if (null == mFaceTrack) {
            mFaceTrack = new STMobileMultiTrack106(mContext, STMobileMultiTrack106.FACE_KEY_POINTS_COUNT);
            mFaceTrack.setMaxDetectableFaces(mMaxFaceCount);
        }

        // 人脸识别的方向定义有些特殊
        int faceDirection = 0;
        switch (direction) {
            case DirectionDetector.ROTATION_LANDSCAPE:
                faceDirection = 3;
                break;
            case DirectionDetector.ROTATION_PORTRAIT:
                faceDirection = 0;
                break;
            case DirectionDetector.ROTATION_SEASCAPE:
                faceDirection = 1;
                break;
            case DirectionDetector.ROTATION_UPSIDE_DOWN:
                faceDirection = 2;
                break;
        }

        long startTick = System.currentTimeMillis();

        Log.d("lalala", "dir std-test: "+faceDirection);
        STMobile106[] faces = mFaceTrack.track(sampleData.array(), STImageFormat.ST_PIX_FMT_GRAY8,
                mSampleWidth, mSampleHeight, mSampleWidth, faceDirection);
        Log.d(TAG, "cost: " + (System.currentTimeMillis() - startTick));

        synchronized (SenseTimeDetector.class) {
            if (null != faces && faces.length > 0) {
                mFaceInfoLst = faces;
            } else {
                mFaceInfoLst = null;
            }
        }

        mDetecting = false;
        mFaceDetectorLsn.onDetectFinish();
    }

    /**
     * 切换需要识别的人数
     */
    @WorkerThread
    void handleSwitchFaceCount(int count) {
        if (count == mMaxFaceCount) {
            return;
        }

        mMaxFaceCount = count;
        if (null != mFaceTrack) {
            mFaceTrack.setMaxDetectableFaces(count);
        }
        Log.d(TAG, "switch max face: " + count);
    }

    final static int MSG_DETECT = 0;
    final static int MSG_QUIT = 1;
    final static int MSG_SWITCH_FACE_COUNT = 2;

    static class DetectHandler extends Handler {
        WeakReference<SenseTimeDetector> wrFaceDeteor;

        public DetectHandler(SenseTimeDetector detector) {
            wrFaceDeteor = new WeakReference<>(detector);
        }

        @Override
        public void handleMessage(Message msg) {
            if (wrFaceDeteor.get() == null) {
                return;
            }

            switch (msg.what) {
                case MSG_DETECT:
                    wrFaceDeteor.get().handleDetect((ByteBuffer) msg.obj, msg.arg1);
                    break;
                case MSG_SWITCH_FACE_COUNT:
                    wrFaceDeteor.get().handleSwitchFaceCount(msg.arg1);
                    break;
                case MSG_QUIT:
                    getLooper().quit();
                    break;
            }
        }
    }
}
