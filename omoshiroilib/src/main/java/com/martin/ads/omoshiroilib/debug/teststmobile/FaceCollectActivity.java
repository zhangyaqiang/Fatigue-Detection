package com.martin.ads.omoshiroilib.debug.teststmobile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.PointF;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.martin.ads.omoshiroilib.R;
import com.sensetime.stmobileapi.STMobile106;

/**
 * Created by zhangyaqiang on 2017/6/27.
 */

public class FaceCollectActivity extends Activity{
    static FaceCollectActivity instance = null;

    private SoundPool soundPool;
    private static int musicId, streamId;
    private static final int FPS = 30;
    private static final int DETECTION_FRAMES = 90;
    private static double normalYaw,normalPitch;
    private static boolean FACE_COLLECTED = false;
    private static boolean IS_FATIGUE = false;
    //采集1秒的人脸特征
    STMobile106[] faceLandmarks = new STMobile106[FPS];
    //3s内的人脸特征点
    STMobile106[] faceLandmarksPer3s = new STMobile106[DETECTION_FRAMES];
    private static double yawPer3s, pitchPer3s;
    private static double eyeDistance, lipDistance;

    TextView newfpstText, newactionText;

    static Accelerometer acc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_facecollect);
        newfpstText = (TextView)findViewById(R.id.newfpstext);
        newactionText = (TextView) findViewById(R.id.newactionText);
        newfpstText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPool.stop(streamId);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newfpstText.setText("小心驾驶！");
                            }
                        });
                    }
                }
        );
        instance = this;

        soundPool = new SoundPool(5, 0, 5);
        musicId = soundPool.load(this, R.raw.collide, 1);

        acc = new Accelerometer(this);
        acc.start();

    }

    @Override
    public void onResume() {
        FACE_COLLECTED = false;
        IS_FATIGUE = false;
        super.onResume();
        final FaceOverlapFragment fragment = (FaceOverlapFragment) getFragmentManager()
                .findFragmentById(R.id.newoverlapFragment);
        fragment.registTrackCallback(new FaceOverlapFragment.TrackCallBack() {
            int frameNum=0;
            @Override
            public void onTrackdetected(final int value, final float pitch, final float roll, final float yaw, final int eye_dist,
                                        final int id, final int eyeBlink, final int mouthAh, final int headYaw, final int headPitch, final int browJump, final STMobile106 Landmarks) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newactionText.setText("pitch："+ pitch + "\nroll："+ roll + "\nyaw:" + yaw);
                    }
                });
//              脸部特征未采集
                if(!FACE_COLLECTED){
                    if (frameNum < FPS) {
                        faceLandmarks[frameNum] = Landmarks;
                        frameNum++;
                        normalYaw += yaw;
                        normalPitch += pitch;
                    }
                    else {
                        getFaceFeature();
                        FACE_COLLECTED = true;
                        frameNum = 0;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                newfpstText.setText("采集完成");
//                                newactionText.setText("上下眼平均距离："+eyeDistance + "\n上下唇平均距离："+lipDistance);
                            }
                        });
                    }
                }
//                实时疲劳检测
                else{
                    if (frameNum < DETECTION_FRAMES){
                        faceLandmarksPer3s[frameNum] = Landmarks;
                        yawPer3s += yaw;
                        pitchPer3s += pitch;
                        frameNum++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PointF[] tempFace = Landmarks.getPointsArray();
//                                newactionText.setText("上下眼平均距离："+ ((tempFace[72].x - tempFace[73].x)+(tempFace[75].x - tempFace[76].x))/2+ "\n上下唇平均距离："+ ((tempFace[86].x - tempFace[94].x)+(tempFace[87].x - tempFace[93].x)+(tempFace[88].x - tempFace[92].x))/3);
                            }
                        });
                    }
                    else{
                        IS_FATIGUE = isFatigue();
                        if(IS_FATIGUE) {
                            streamId = soundPool.play(musicId, 1, 1, 0, -1, 1);
//                            dialog();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    newfpstText.setText("处于疲劳状态，请停车休息！\n点击停止响铃");
                                }
                            });
                        }
                        else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    newfpstText.setText("小心驾驶！");
                                }
                            });
                        }
                        frameNum = 0;
                        yawPer3s = 0;
                        pitchPer3s = 0;
                    }
                }

            }
        });
    }

//    疲劳警告
    protected void dialog() {
        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("疲劳警告！");
        builder.setMessage("您已处于疲劳状态，请尽快休息！");
//        builder.setNegativeButton("取消", null);
        builder.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                soundPool.stop(musicId);
                dialog.dismiss();
            }
        });
        builder.show();
    }
// 判断是否处于疲劳状态
    private boolean isFatigue() {
        int eyeCloseFrames = 0;
        int lipOpenFrames = 0;
        for(int i = 0; i< DETECTION_FRAMES; i++) {
            PointF[] tempFace = faceLandmarksPer3s[i].getPointsArray();
            double curEyeDistance = ((tempFace[72].x - tempFace[73].x)+(tempFace[75].x - tempFace[76].x))/2;
            double curLipDistance = ((tempFace[86].x - tempFace[94].x)+(tempFace[87].x - tempFace[93].x)+(tempFace[88].x - tempFace[92].x))/3;
            if(curEyeDistance < 0.7*eyeDistance) {
                eyeCloseFrames++;
            }
            if(curLipDistance > 1.5*lipDistance) {
                lipOpenFrames++;
            }
        }
        double yawAver = yawPer3s/DETECTION_FRAMES;
        double pitchAver = pitchPer3s/DETECTION_FRAMES;
        if(eyeCloseFrames >= 0.25*DETECTION_FRAMES || lipOpenFrames >= 0.3*DETECTION_FRAMES ||
                pitchAver >= normalPitch+10 || Math.abs(yawAver) >= normalYaw+20){
            return true;
        }
        else return false;
    }

//    获取脸部特征
    private void getFaceFeature() {
        double eyeSumDistance=0, lipSumDistance=0;
        for(int i = 0; i< FPS; i++) {
            PointF[] tempFace = faceLandmarks[i].getPointsArray();
            eyeSumDistance += ((tempFace[72].x - tempFace[73].x)+(tempFace[75].x - tempFace[76].x))/2;
            lipSumDistance += ((tempFace[86].x - tempFace[94].x)+(tempFace[87].x - tempFace[93].x)+(tempFace[88].x - tempFace[92].x))/3;
        }
        eyeDistance = eyeSumDistance/ FPS;
        lipDistance = lipSumDistance/ FPS;
        normalPitch = normalPitch/ FPS;
        normalYaw = normalYaw/ FPS;
    }
}
