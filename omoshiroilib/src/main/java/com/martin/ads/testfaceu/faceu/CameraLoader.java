package com.martin.ads.testfaceu.faceu;

import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.view.Surface;

import com.martin.ads.omoshiroilib.camera.CameraEngine;
import com.martin.ads.omoshiroilib.flyu.hardcode.ApiLevel;
import com.martin.ads.omoshiroilib.flyu.fake.Logger;
import com.martin.ads.omoshiroilib.flyu.fake.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author kevinhuang
 * @since 2015-04-01
 */
public class CameraLoader {
    private final static Logger log = LoggerFactory.getLogger();

    public final static int MAX_FRAME_RATE = 30;

    final static int HIGH_PHONE_WIDTH = 1080;
    final static int HIGH_PHONE_HEIGH = 1920;

    // 分辨率系数，选取摄像头预览和图片大小的时候，需要与预期值进行比例和差距加权求出差异值，然后取差异最小的
    final static double COEFFICIENT = 1000.0d;

    // 闪光灯的模式定义
    final static int MODE_OFF = 0; // 关闭闪光灯
    final static int MODE_AUTO = 1; // 闪关灯自动
    final static int MODE_MANUAL = 2; // 对焦的时候，手动打开闪关灯

    Camera mCamera;

    // 摄像头支持的比例放大比例的列表，和camera一起被初始化，如果不支持缩放，则不允许设置放大比例
    List<Integer> mZoomRatios = null;
    float mZoomValue = 100f;

    Activity mActivity;
    boolean mFocusEnd;
    boolean mUseFrontFace; // 当前是否是使用前置摄像头
    Point mPreviewSize;

    int mFlashMode = MODE_OFF;

    int mDisplayRotate;
    int mMaxWidth;
    int mMaxHeight;
    private int useCamId=0;
    public CameraLoader(Activity activity, boolean useFrontFace, int highPhoneWidth, int highPhoneHeigt) {
        int maxWidth = highPhoneWidth;
        int maxHeight = highPhoneHeigt;

        init(activity, useFrontFace, maxWidth, maxHeight);
    }

    public CameraLoader(Activity activity, boolean useFrontFace) {
        this(activity, useFrontFace, HIGH_PHONE_WIDTH, HIGH_PHONE_HEIGH);
    }

    void init(Activity activity, boolean useFrontFace, int maxWidth, int maxHeight) {
        mActivity = activity;
        mFocusEnd = true;

        mUseFrontFace = useFrontFace;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
    }

    /**
     * 设置缩放比例，里面会按照当前的比例再去缩放
     *
     * @param factor 缩放比例
     */
    public void setZoom(float factor) {
        if (null == mZoomRatios || null == mCamera) {
            return;
        }

        mZoomValue *= factor;
        try {
            if (mZoomValue < mZoomRatios.get(0)) {
                mZoomValue = mZoomRatios.get(0);
            }

            if (mZoomValue > mZoomRatios.get(mZoomRatios.size() - 1)) {
                mZoomValue = mZoomRatios.get(mZoomRatios.size() - 1);
            }

            Camera.Parameters params = mCamera.getParameters();
            int zoomIndex = getNearestZoomIndex((int) (mZoomValue));
            if (params.getZoom() != zoomIndex) {
                params.setZoom(zoomIndex);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            log.error("setZoom failed, " + e.getMessage());
        }
    }

    int getNearestZoomIndex(int prefectVal) {
        int left = 0, right = mZoomRatios.size() - 1, middle;
        while (right - left > 1) {
            middle = (left + right) / 2;
            if (prefectVal > mZoomRatios.get(middle)) {
                left = middle;
            } else {
                right = middle;
            }
        }

        if (Math.abs(prefectVal - mZoomRatios.get(left)) > Math.abs(prefectVal - mZoomRatios.get(right))) {
            return right;
        } else {
            return left;
        }
    }

    Camera safeOpenCamera(boolean useFrontFace) {
        log.info("useFrontFace: " + useFrontFace);
        Camera camera = openCameraByHighApiLvl(useFrontFace);
        if (null == camera) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                log.error("openCameraFailed, " + e.getMessage());
            }

				/*
                if (null != camera) {
					try {
						Parameters parameters = camera.getParameters();
						parameters.set("camera-id", useFrontFace ? 2 : 1);
						camera.setParameters(parameters);
					} catch (Exception e) {
						Log.e(TAG, "set camera-id error:" + e.toString());
					}
				}
				*/
        }
        return camera;
    }

    Camera openCameraByHighApiLvl(boolean useFrontFace) {
        if (CameraEngine.getNumberOfCameras() <= 0) {
            log.info("CameraNum is 0");
            return null;
        }

        Camera camera = null;
        try {
            if (true == useFrontFace) {
                useCamId=CameraEngine.getCameraIdWithFacing(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                useCamId=CameraEngine.getCameraIdWithFacing(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
            camera = Camera.open(useCamId);
        } catch (Exception e) {
            log.error("openCamera by high api level failed, " + e.getMessage());
        }

        return camera;
    }

    void safeSetPreviewFrameRate(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        int fitRate = -1;

        @SuppressWarnings("deprecation")
        List<Integer> rateList = params.getSupportedPreviewFrameRates();
        if (null == rateList || 0 == rateList.size()) {
            log.error("getSupportedPrviewFrameRates failed");
            return;
        }

        for (Integer rate : rateList) {
            log.error("supportPriviewFrameRate, rate: " + rate);
            if (rate <= MAX_FRAME_RATE && (-1 == fitRate || rate > fitRate)) {
                fitRate = rate;
            }
        }

        if (-1 == fitRate) {
            log.error("can't find fit rate, use camera default value");
            return;
        }

        try {
            log.error("setPreviewFrameRate, fitRate: " + fitRate);
            //noinspection deprecation
            params.setPreviewFrameRate(fitRate);
            camera.setParameters(params);
        } catch (Exception e) {
            log.error("setPreviewFrameRate failed, " + e.getMessage());
        }
    }

    @SuppressWarnings("SuspiciousNameCombination")
    boolean safeSetPreviewSize(Camera camera) {
        Camera.Parameters params = camera.getParameters();
        Point size = null;

        List<Camera.Size> sizeLst = params.getSupportedPreviewSizes();
        if (null == sizeLst || 0 == sizeLst.size()) {
            log.error("getSupportedPrviewSizes failed");
            return false;
        }

        int diff = Integer.MAX_VALUE;
        for (Camera.Size it : sizeLst) {
            int width = it.width;
            int height = it.height;
            if (mDisplayRotate == 90 || mDisplayRotate == 270) {
                height = it.width;
                width = it.height;
            }

            log.debug("supported preview, width: " + width + ", height: " + height);
            if (width * height <= mMaxHeight * mMaxWidth) {
                int newDiff = diff(height, width, mMaxHeight, mMaxWidth);
                log.debug("diff: " + newDiff);
                if (null == size || newDiff < diff) {
                    size = new Point(it.width, it.height);
                    diff = newDiff;
                }
            }
        }

        if (null == size) {
            Collections.sort(sizeLst, new Comparator<Camera.Size>() {
                @Override
                public int compare(Camera.Size lhs, Camera.Size rhs) {
                    return lhs.width * lhs.height - rhs.width * rhs.height;
                }
            });

            Camera.Size it = sizeLst.get(sizeLst.size() / 2);
            size = new Point(it.width, it.height);
        }

        try {
            log.info("setPreviewSize, width: " + size.x + ", height: " + size.y);
            params.setPreviewSize(size.x, size.y);
            if (mDisplayRotate == 90 || mDisplayRotate == 270) {
                mPreviewSize = new Point(size.y, size.x);
            } else {
                mPreviewSize = new Point(size.x, size.y);
            }
            camera.setParameters(params);
        } catch (Exception e) {
            log.error("setPreviewSize failed, " + e.getMessage());
            return false;
        }
        return true;
    }

    int diff(double realH, double realW, double expH, double expW) {
        double rateDiff = Math.abs(COEFFICIENT * (realH / realW - expH / expW));
        return (int) (rateDiff + Math.abs(realH - expH) + Math.abs(realW - expW));
    }

    void initRotateDegree(int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        log.debug("cameraId: " + cameraId + ", rotation: " + info.orientation);
        int rotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        mDisplayRotate = (info.orientation - degrees + 360) % 360;
    }

    Camera.AutoFocusCallback mAutoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            mFocusEnd = true;
            if (success) {
                if (MODE_MANUAL == mFlashMode) {
                    switchLight(false);
                }
            }
        }
    };

    public boolean initCamera() {
        return initCamera(mUseFrontFace);
    }

    public boolean switchCamera() {
        releaseCamera();
        mUseFrontFace = !mUseFrontFace;
        return initCamera(mUseFrontFace);
    }

    public boolean isUseFrontFace() {
        return mUseFrontFace;
    }

    public void switchAutoFlash(boolean open) {
        if (null == mCamera) {
            return;
        }

        log.info("switch auto flash: " + open);
        try {
            Camera.Parameters params = mCamera.getParameters();
            if (open) {
                // android L上面有BUG会导致开了auto之后，无法再off。
                if (Build.VERSION.SDK_INT < ApiLevel.API21_LOLLIPOP &&
                        params.getSupportedFlashModes().contains(Camera.Parameters.FLASH_MODE_AUTO)) {
                    params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    mFlashMode = MODE_AUTO;
                } else {
                    mFlashMode = MODE_MANUAL;
                }
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mFlashMode = MODE_OFF;
            }
            log.debug("flash mode: " + params.getFlashMode());
            mCamera.setParameters(params);
        } catch (Exception e) {
            log.debug("can't set flash mode");
        }
    }

    public void switchLight(boolean open) {
        if (null == mCamera) {
            return;
        }

        try {
            Camera.Parameters params = mCamera.getParameters();
            if (open) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                params.setFlashMode(MODE_MANUAL == mFlashMode ? Camera.Parameters.FLASH_MODE_OFF :
                        Camera.Parameters.FLASH_MODE_AUTO);
            }
            mCamera.setParameters(params);
        } catch (Exception e) {
            log.error("light up failed, " + e.getMessage());
        }
    }

    boolean initCamera(boolean useFrontFace) {
        log.debug("initCamera");
        mCamera = safeOpenCamera(useFrontFace);
        if (null == mCamera) {
            log.error("open camera failed");
            return false;
        }

        initRotateDegree(useCamId);

        try {
            safeSetPreviewFrameRate(mCamera);

            // 设置预览图片大小
            if (!safeSetPreviewSize(mCamera)) {
                log.error("safeSetPreviewSize failed");
                return false;
            }

            Camera.Parameters parameters = mCamera.getParameters();
            mZoomRatios = null;
            if (parameters.isZoomSupported()) {
                mZoomRatios = parameters.getZoomRatios();
                Collections.sort(mZoomRatios);
                log.debug("ratios: " + mZoomRatios);
                mZoomValue = 100f;
            } else {
                log.error("camera don't support zoom");
            }

            List<String> supportModes = parameters.getSupportedFocusModes();
            if (supportModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (supportModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else if (supportModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }

            log.debug("focusMode: " + parameters.getFocusMode());
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            releaseCamera();
            log.error("setParametersError false");
            return false;
        }

        return true;
    }

    public void releaseCamera() {
        if (null != mCamera) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.release();
            } catch (Exception e) {
                log.error("exception on releaseCamera, " + e.getMessage());
            }
        }
        mCamera = null;
    }

    /**
     * 这个仅仅给GPUImage的接口使用
     *
     * @return 返回camera
     */
    public Camera getCamera() {
        return mCamera;
    }

    public int getDisplayRotate() {
        return mDisplayRotate;
    }

    public int getPreviewWidth() {
        return mPreviewSize.x;
    }

    public int getPreviewHeight() {
        return mPreviewSize.y;
    }
}
