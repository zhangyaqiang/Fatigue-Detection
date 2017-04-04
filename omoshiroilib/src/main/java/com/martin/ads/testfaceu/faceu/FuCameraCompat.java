package com.martin.ads.testfaceu.faceu;

import android.hardware.Camera;

import com.lemon.faceu.sdk.utils.Log;

import java.lang.reflect.Method;

/**
 * @author kevinhuang
 * @since 2015-03-24
 * 根据api返回的结果以及服务器的配置计算和保存最终的摄像头的结果，这种计算不像fps之类的，这种是通用的
 */
public class FuCameraCompat {
    final static String TAG = "FuCameraCompat";

    public static FuCameraInfo gCameraInfo;

    public static void initCameraInfo() {
        initCameraInfo(false);
    }

    public static void initCameraInfo(boolean force) {
        if (!force && null != gCameraInfo) {
            return;
        }

        gCameraInfo = new FuCameraInfo();
        Log.i(TAG, "isSupportHiApi: " + checkSupportHiAPI());

        initCameraInfoFromApi();
    }

    static void initCameraInfoFromApi() {
        gCameraInfo.setCameraNum(Camera.getNumberOfCameras());
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        for (int i = 0; i < gCameraInfo.getCameraNum(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                gCameraInfo.setFrontId(i);
                gCameraInfo.setFrontOrien(cameraInfo.orientation);
                gCameraInfo.setIsHasFrontCamera(true);
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                gCameraInfo.setBackId(i);
                gCameraInfo.setBackOrien(cameraInfo.orientation);
                gCameraInfo.setIsHasBackCamera(true);
            }
        }
    }

    static boolean checkSupportHiAPI() {
        boolean ret;
        // 如果支持 还是要测试一下，因为可能是刷机的
        try {
            Method fcMethod = Class.forName("android.hardware.Camera").getDeclaredMethod("getNumberOfCameras", (Class[]) null);
            if (fcMethod == null) {
                Log.d(TAG, "GetfcMethod is null");
                ret = false;
            } else {
                ret = true;
            }
        } catch (Exception e) {
            ret = false;
            Log.e(TAG, "find getNumberOfCameras failed: " + e.getMessage());
        }
        return ret;
    }
}
