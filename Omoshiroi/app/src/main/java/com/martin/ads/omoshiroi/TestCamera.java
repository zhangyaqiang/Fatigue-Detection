package com.martin.ads.omoshiroi;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.martin.ads.omoshiroilib.glessential.CameraView;

public class TestCamera extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private CameraView cameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                finish();
            }
        }

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);

        glSurfaceView= (GLSurfaceView) findViewById(R.id.camera_view);
        cameraView=new CameraView(this,glSurfaceView);
    }

    @Override
    protected void onPause(){
        super.onPause();
        cameraView.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        cameraView.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        cameraView.onDestroy();
    }
}
