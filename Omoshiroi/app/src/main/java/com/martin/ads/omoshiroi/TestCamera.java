package com.martin.ads.omoshiroi;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.glessential.CameraView;

public class TestCamera extends AppCompatActivity {

    private static final String TAG = "TestCamera";
    private static final int REQUEST_PERMISSION = 233;
    private GLSurfaceView glSurfaceView;
    private CameraView cameraView;

    private boolean checkPermission(String permission,int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    showHint();
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
        setContentView(R.layout.activity_main);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);

        glSurfaceView= (GLSurfaceView) findViewById(R.id.camera_view);
        cameraView=new CameraView(this,glSurfaceView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }else {
                    showHint();
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

    private void showHint(){
        Toast.makeText(this, "Camera and SDCard access is required by Omoshiroi, please grant the permission in settings.", Toast.LENGTH_LONG).show();
    }
}
