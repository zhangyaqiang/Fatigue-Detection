package com.martin.ads.omoshiroi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.glessential.CameraView;
import com.martin.ads.omoshiroilib.ui.CameraPreviewActivity;

public class TestCamera extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=new Intent(TestCamera.this,CameraPreviewActivity.class);
        startActivity(intent);
        finish();
    }
}
