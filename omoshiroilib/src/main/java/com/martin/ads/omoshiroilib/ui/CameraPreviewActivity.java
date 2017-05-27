package com.martin.ads.omoshiroilib.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.debug.removeit.FilterAdapter;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.filter.helper.FilterResourceHelper;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.glessential.CameraView;
import com.martin.ads.omoshiroilib.glessential.GLRootView;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class CameraPreviewActivity extends AppCompatActivity {

    private static final String TAG = "CameraPreviewActivity";
    private static final boolean DEBUG=false;

    private CameraView cameraView;

    private CaptureAnimation captureAnimation;

    private boolean isRecording=false;

    private RecyclerView filterListView;

    private int surfaceWidth,surfaceHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        if(DEBUG)
            FilterResourceHelper.generateFilterThumbs(CameraPreviewActivity.this,false);
        FileUtils.upZipFile(this,"filter/thumbs/thumbs.zip",getFilesDir().getAbsolutePath());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.camera_preview);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);

        GLRootView glRootView= (GLRootView) findViewById(R.id.camera_view);
        cameraView=new CameraView(this,glRootView);

        cameraView.setScreenSizeChangedListener(new CameraView.ScreenSizeChangedListener() {
            @Override
            public void updateScreenSize(int width, int height) {
                Log.d(TAG, "updateScreenSize: "+width+" "+height);
                surfaceWidth=width;
                surfaceHeight=height;
                captureAnimation.resetAnimationSize(width,height);
            }
        });

        GlobalConfig.context=this;

        captureAnimation= (CaptureAnimation) findViewById(R.id.capture_animation_view);
        findViewById(R.id.tmp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureAnimation.setVisibility(View.VISIBLE);
                captureAnimation.startAnimation();
                //cameraView.getCameraEngine().takePhoto();
                cameraView.getGlRender().getFilterGroup().addPostDrawTask(new Runnable() {
                    @Override
                    public void run() {
                        BitmapUtils.sendImage(surfaceWidth,surfaceHeight, GlobalConfig.context);
                    }
                });
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

        filterListView= (RecyclerView) findViewById(R.id.filter_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        filterListView.setLayoutManager(linearLayoutManager);

        List<FilterType> filterTypeList=new ArrayList<>();
        for(FilterType filterType:FilterType.values()){
            filterTypeList.add(filterType);
        }
        FilterAdapter filterAdapter=new FilterAdapter(this,filterTypeList);
        filterListView.setAdapter(filterAdapter);
        filterAdapter.setOnFilterChangeListener(new FilterAdapter.OnFilterChangeListener() {
            @Override
            public void onFilterChanged(FilterType filterType) {
                cameraView.getGlRender().switchLastFilter(filterType);
            }
        });

        findViewById(R.id.switch_camera_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.getGlRender().switchCamera();
            }
        });
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
