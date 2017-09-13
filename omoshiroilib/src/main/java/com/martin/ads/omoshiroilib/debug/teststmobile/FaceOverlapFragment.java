package com.martin.ads.omoshiroilib.debug.teststmobile;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sensetime.stmobileapi.STMobile106;
import com.sensetime.stmobileapi.STMobileFaceAction;
import com.sensetime.stmobileapi.STMobileMultiTrack106;
import com.sensetime.stmobileapi.STUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author MatrixCV
 * 
 *         实时人脸检测接口调用示例
 * 
 */
public class FaceOverlapFragment extends CameraOverlapFragment {
	///< 检测脸部动作：张嘴、眨眼、抬眉、点头、摇头
	private static final int ST_MOBILE_TRACKING_ENABLE_FACE_ACTION = 0x00000020;
	private static final int ST_MOBILE_FACE_DETECT   =  0x00000001;    ///<  人脸检测
	private static final int ST_MOBILE_EYE_BLINK     =  0x00000002;  ///<  眨眼
	private static final int ST_MOBILE_MOUTH_AH      =  0x00000004;    ///<  嘴巴大张
	private static final int ST_MOBILE_HEAD_YAW      =  0x00000008;    ///<  摇头
	private static final int ST_MOBILE_HEAD_PITCH    =  0x00000010;    ///<  点头
	private static final int ST_MOBILE_BROW_JUMP     =  0x00000020;    ///<  眉毛挑动

	// private FaceTrackerBase tracker = null;
	private STMobileMultiTrack106 tracker = null;
	TrackCallBack mListener;
	private Thread thread;
	private boolean killed = false;
	private byte nv21[];
	private Bitmap bitmap;
	public static int fps;
	static boolean DEBUG = false;
	private boolean isNV21ready = false;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		nv21 = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];

		this.setPreviewCallback(new PreviewCallback() {
			@Override
			public void onPreviewFrame(byte[] data, Camera camera) {
				synchronized (nv21) {
					System.arraycopy(data, 0, nv21, 0, data.length);
					isNV21ready = true;
				}
			}

		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (MultitrackerActivity.acc != null)
			MultitrackerActivity.acc.start();

		/**
		 * 
		 * 初始化实时人脸检测的帧宽高 目前只支持宽640*高480
		 * 
		 */

		if (tracker == null) {
			long start_init = System.currentTimeMillis();
//			int config = 0; //default config
			int config = ST_MOBILE_TRACKING_ENABLE_FACE_ACTION;
			tracker = new STMobileMultiTrack106(getActivity(), config);
//			最大识别人脸数
			int max = 1;
			tracker.setMaxDetectableFaces(max);
			long end_init = System.currentTimeMillis();
			Log.i("track106", "init cost "+(end_init - start_init) +" ms");
		}

		killed = false;
		final byte[] tmp = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
		thread = new Thread() {
			@Override
			public void run() {
				List<Long> timeCounter = new ArrayList<Long>();
				int start = 0;
				while (!killed) {
					
					if(!isNV21ready)
						continue;

					synchronized (nv21) {
						System.arraycopy(nv21, 0, tmp, 0, nv21.length);
						isNV21ready = false;
					}

					/**
					 * 如果使用前置摄像头，请注意显示的图像与帧图像左右对称，需处理坐标
					 */
					boolean frontCamera = (CameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT);

					/**
					 * 获取重力传感器返回的方向
					 */
					int dir = Accelerometer.getDirection();

					/**
					 * 请注意前置摄像头与后置摄像头旋转定义不同
					 * 请注意不同手机摄像头旋转定义不同
					 */
					if (frontCamera && 
							((mCameraInfo.orientation == 270 && (dir & 1) == 1) ||
							 (mCameraInfo.orientation == 90 && (dir & 1) == 0)))
						dir = (dir ^ 2);

					/**
					 * 调用实时人脸检测函数，返回当前人脸信息
					 */
					long start_track = System.currentTimeMillis();
//					STMobile106[] faces = tracker.track(tmp, dir,PREVIEW_WIDTH,PREVIEW_HEIGHT);
					Log.d("lalala", "dir st-test: "+dir);
					STMobileFaceAction[] faceActions = tracker.trackFaceAction(tmp, dir, PREVIEW_WIDTH, PREVIEW_HEIGHT);
					long end_track = System.currentTimeMillis();
					Log.i("track106", "track cost "+(end_track - start_track)+" ms");

					long timer = System.currentTimeMillis();
					timeCounter.add(timer);
					while (start < timeCounter.size()
							&& timeCounter.get(start) < timer - 1000) {
						start++;
					}
					fps = timeCounter.size() - start;
					try {
						Log.i(TAG, "-->> faceActions: faceActions[0].face="+faceActions[0].face.rect.toString()+", pitch = "+faceActions[0].face.pitch+", roll="+faceActions[0].face.roll+", yaw="
								+faceActions[0].face.yaw+", face_action = "+faceActions[0].face_action+", face_count = "+faceActions.length);
//						mListener.onTrackdetected(fps, faces[0].pitch, faces[0].roll, faces[0].yaw);
//						通过listener获取每一帧的人脸检测结果，最后一个参数faceActions[0].getFace()是通过人脸检测核心所获取的数据,
// 						在package com.sensetime.stmobileapi.STMobileFaceAction中有具体的函数
						mListener.onTrackdetected(fps,  faceActions[0].face.pitch, faceActions[0].face.roll, faceActions[0].face.yaw, faceActions[0].face.eye_dist, faceActions[0].face.ID,
								checkFlag(faceActions[0].face_action, ST_MOBILE_EYE_BLINK), checkFlag(faceActions[0].face_action, ST_MOBILE_MOUTH_AH), checkFlag(faceActions[0].face_action, ST_MOBILE_HEAD_YAW),
								checkFlag(faceActions[0].face_action, ST_MOBILE_HEAD_PITCH), checkFlag(faceActions[0].face_action, ST_MOBILE_BROW_JUMP), faceActions[0].getFace());
					} catch(Exception e) {
						e.printStackTrace();
					}
					if (start > 100) {
						timeCounter = timeCounter.subList(start,
								timeCounter.size() - 1);
						start = 0;
					}

					/**
					 * 绘制人脸框
					 */
//					if (faces != null) {
					if(faceActions != null) {
						if(DEBUG){
//							for (int i = 0; i < faces.length; i++) {
							for(int i=0; i<faceActions.length; i++) {
//								Log.i("Test", "detect faces: "+ faces[i].getRect().toString());
								Log.i("Test", "detect faces: "+ faceActions[i].getFace().getRect().toString());
							}
						}
						
						Canvas canvas = mOverlap.getHolder().lockCanvas();

						if (canvas == null)
							continue;

						canvas.drawColor(0, PorterDuff.Mode.CLEAR);
						canvas.setMatrix(getMatrix());
						boolean rotate270 = mCameraInfo.orientation == 270;
//						for (STMobile106 r : faces) {
						for (STMobileFaceAction r : faceActions) {
							// Rect rect = r.getRect();

							Log.i(TAG, "-->> face count = "+faceActions.length);
							Rect rect;
							if (rotate270) {
//								rect = STUtils.RotateDeg270(r.getRect(),PREVIEW_WIDTH, PREVIEW_HEIGHT);
								rect = STUtils.RotateDeg270(r.getFace().getRect(), PREVIEW_WIDTH, PREVIEW_HEIGHT);
							} else {
//								rect = STUtils.RotateDeg90(r.getRect(),PREVIEW_WIDTH, PREVIEW_HEIGHT);
								rect = STUtils.RotateDeg90(r.getFace().getRect(), PREVIEW_WIDTH, PREVIEW_HEIGHT);
							}
							
//							PointF[] points = r.getPointsArray();
							PointF[] points = r.getFace().getPointsArray();
							for (int i = 0; i < points.length; i++) {
								if (rotate270) {
									points[i] = STUtils.RotateDeg270(points[i], PREVIEW_WIDTH, PREVIEW_HEIGHT);
								} else {
									points[i] = STUtils.RotateDeg90(points[i], PREVIEW_WIDTH, PREVIEW_HEIGHT);
								}
								
							}
							STUtils.drawFaceRect(canvas, rect, PREVIEW_HEIGHT,
									PREVIEW_WIDTH, frontCamera);
							STUtils.drawPoints(canvas, points, PREVIEW_HEIGHT,
									PREVIEW_WIDTH, frontCamera);

						}
						mOverlap.getHolder().unlockCanvasAndPost(canvas);
					}
				}
			}
		};

		thread.start();
	}

	@Override
	public void onPause() {
		if (MultitrackerActivity.acc != null)
			MultitrackerActivity.acc.stop();
		killed = true;
		if (thread != null)
			try {
				thread.join(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		if (tracker != null) {
			System.out.println("destroy tracker");
			tracker.destory();
			tracker = null;
		}
		super.onPause();
	}

	public void registTrackCallback(TrackCallBack callback) {
		mListener = callback;
	}

	public interface TrackCallBack {
		public void onTrackdetected(int value, float pitch, float roll, float yaw, int eye_dist,
                                    int id, int eyeBlink, int mouthAh, int headYaw, int headPitch, int browJump,
									STMobile106 faceLandmarks);
	}

	private int checkFlag(int action, int flag) {
		int res = action & flag;
		return res==0?0:1;
	}
}
