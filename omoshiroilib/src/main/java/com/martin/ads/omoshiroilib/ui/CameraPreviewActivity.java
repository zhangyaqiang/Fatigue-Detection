package com.martin.ads.omoshiroilib.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.debug.lab.FilterThumbActivity;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalContext;
import com.martin.ads.omoshiroilib.glessential.CameraView;
import com.martin.ads.omoshiroilib.glessential.GLRootView;

public class CameraPreviewActivity extends AppCompatActivity {

    private static final String TAG = "CameraPreviewActivity";
    private static final int REQUEST_PERMISSION = 233;
    private CameraView cameraView;

    private CaptureAnimation captureAnimation;

    private boolean isRecording=false;

    private boolean checkPermission(String permission,int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    showHint("Camera and SDCard access is required by Omoshiroi, please grant the permission in settings.");
                    finish();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{
                                    permission,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            },
                            requestCode);
                }
                return false;
            }else return true;
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(checkPermission(Manifest.permission.CAMERA,REQUEST_PERMISSION))
            init();
    }


    private void init(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.camera_preview);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);

        GLRootView glRootView= (GLRootView) findViewById(R.id.camera_view);
        cameraView=new CameraView(this,glRootView);
        GlobalContext.context=this;

        captureAnimation= (CaptureAnimation) findViewById(R.id.capture_animation_view);
        findViewById(R.id.tmp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureAnimation.setVisibility(View.VISIBLE);
                captureAnimation.startAnimation();
                cameraView.getCameraEngine().takePhoto();
            }
        });

        findViewById(R.id.debug_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CameraPreviewActivity.this,FilterThumbActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.record_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//                int rotation = windowManager.getDefaultDisplay().getRotation();
//                Log.d(TAG, "onClick: "+rotation);
                if(isRecording){
                    cameraView.getCameraEngine().releaseRecorder();
                    showHint("stop record");
                }else {
                    cameraView.getCameraEngine().startRecordingVideo();
                    showHint("start record");
                }
                isRecording=!isRecording;
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }else {
                    showHint("Camera and SDCard access is required by Omoshiroi, please grant the permission in settings.");
                    finish();
                }
                break;
            default:
                finish();
                break;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(cameraView!=null)
            cameraView.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(cameraView!=null)
            cameraView.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(cameraView!=null)
            cameraView.onDestroy();
    }

    private void showHint(String hint){
        Toast.makeText(this,hint , Toast.LENGTH_LONG).show();
    }
}
