package com.martin.ads.testfaceu.faceu;

/**
 * @author kevinhuang
 * @since 2015-03-24
 * 用来保存当前机器摄像头的相关信息
 */
public class FuCameraInfo {
    /**
     * 前面这一段内容在{@link FuCameraCompat#initCameraInfo()}的时候一定被会初始化
     **/
    int mCameraNum = 1;
    boolean mIsHasFrontCamera = false;
    boolean mIsHasBackCamera = false;
    int mFrontId = 0;
    int mBackId = 0;
    int mFrontPreRotate = 0;
    int mBackPreRotate = 0;

    public int getCameraNum() {
        return mCameraNum;
    }

    public void setCameraNum(int cameraNum) {
        mCameraNum = cameraNum;
    }

    public boolean getIsHasFrontCamera() {
        return mIsHasFrontCamera;
    }

    public void setIsHasFrontCamera(boolean isHasFrontCamera) {
        mIsHasFrontCamera = isHasFrontCamera;
    }

    public boolean getIsHasBackCamera() {
        return mIsHasBackCamera;
    }

    public void setIsHasBackCamera(boolean isHasBackCamera) {
        mIsHasBackCamera = isHasBackCamera;
    }

    public int getFrontOrien() {
        return mFrontPreRotate;
    }

    public void setFrontOrien(int frontOrien) {
        mFrontPreRotate = frontOrien;
    }

    public int getBackOrien() {
        return mBackPreRotate;
    }

    public void setBackOrien(int backOrien) {
        mBackPreRotate = backOrien;
    }

    public void setFrontId(int frontId) {
        mFrontId = frontId;
    }

    public int getFrontId() {
        return mFrontId;
    }

    public void setBackId(int backId) {
        mBackId = backId;
    }

    public int getBackId() {
        return mBackId;
    }

    public String dump() {
        return "\nmCameraNum: " + mCameraNum
                + "\nmIsHasFrontCamera: " + mIsHasFrontCamera
                + "\nmIsHasBackCamera: " + mIsHasBackCamera
                + "\nmFrontId: " + mFrontId
                + "\nmBackId: " + mBackId
                + "\nmFrontPreRotate: " + mFrontPreRotate
                + "\nmBackPreRotate: " + mBackPreRotate;
    }
}
