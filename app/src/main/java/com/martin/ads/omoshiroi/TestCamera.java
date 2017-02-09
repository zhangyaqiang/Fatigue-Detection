package com.martin.ads.omoshiroi;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaRecorder;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.glessential.CameraView;
import com.martin.ads.omoshiroilib.ui.CameraPreviewActivity;

import java.io.IOException;

public class TestCamera extends AppCompatActivity {
    private static final String TAG = "TestCamera";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            try {
                int len=MediaCodecList.getCodecCount();
                Log.d(TAG, "onCreate: MediaCodecList size :"+len);
                for(int i=0;i<len;i++){
                    MediaCodecInfo info=MediaCodecList.getCodecInfoAt(i);
                    Log.d(TAG, i+" "+info.getName());
                }
                //MediaCodec mediaCodec=MediaCodec.createEncoderByType("video/mp4v-es");
                //MediaCodec mediaCodec=MediaCodec.createEncoderByType("video/avc");
                MediaCodec mediaCodec=MediaCodec.createDecoderByType("video/avc");
                //MediaCodec mediaCodec=MediaCodec.createByCodecName("AVCEncoder");
                //MediaCodec.createEncoderByType("video/avc");
                //Log.d(TAG, "onCreate: "+mediaCodec.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent intent=new Intent(TestCamera.this,CameraPreviewActivity.class);
        startActivity(intent);
        finish();
    }
}
