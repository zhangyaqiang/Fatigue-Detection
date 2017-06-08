package com.martin.ads.omoshiroi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.debug.lab.FilterThumbActivity;
import com.martin.ads.omoshiroilib.ui.CameraPreviewActivity;
import com.martin.ads.testfaceu.faceu.ui.TestFaceUActivity;
import com.martin.ads.omoshiroilib.debug.teststmobile.MultitrackerActivity;

/**
 * Created by Ads on 2017/4/3.
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private Intent intent;
    private static final int REQUEST_PERMISSION = 233;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(checkPermission(Manifest.permission.CAMERA,REQUEST_PERMISSION))
            init();
    }

    public static final boolean DEBUG=false;
    private void init(){
        if(DEBUG){
            setContentView(R.layout.home_content);
            intent=new Intent();
            findViewById(R.id.camera_view_btn).setOnClickListener(this);
            findViewById(R.id.face_detect_test_btn).setOnClickListener(this);
            findViewById(R.id.test_faceu_btn).setOnClickListener(this);
            findViewById(R.id.debug_btn).setOnClickListener(this);
        }else {
            startActivity(new Intent(this,CameraPreviewActivity.class));
            finish();
        }
    }

    private void start(){
        startActivity(intent);
        //finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera_view_btn:
                intent.setClass(HomeActivity.this, CameraPreviewActivity.class);
                break;
            case R.id.face_detect_test_btn:
                intent.setClass(HomeActivity.this, MultitrackerActivity.class);
                break;
            case R.id.debug_btn:
                intent.setClass(HomeActivity.this, FilterThumbActivity.class);
                break;
            case R.id.test_faceu_btn:
                intent.setClass(HomeActivity.this, TestFaceUActivity.class);
                break;
        }
        start();
    }

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
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO
                            },
                            requestCode);
                }
                return false;
            }else return true;
        }
        return true;
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

    private void showHint(String hint){
        Toast.makeText(this,hint , Toast.LENGTH_LONG).show();
    }
}
