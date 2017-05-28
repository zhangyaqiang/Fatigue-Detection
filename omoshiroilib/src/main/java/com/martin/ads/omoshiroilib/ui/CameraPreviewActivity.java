package com.martin.ads.omoshiroilib.ui;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.debug.removeit.FilterAdapter;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.filter.ext.BlurredFrameEffect;
import com.martin.ads.omoshiroilib.filter.helper.FilterResourceHelper;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.glessential.CameraView;
import com.martin.ads.omoshiroilib.glessential.GLRootView;
import com.martin.ads.omoshiroilib.ui.module.EffectsButton;
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

    //EffectsButtons
    private EffectsButton switchCameraBtn;
    private EffectsButton cameraSettingBtn;
    private RelativeLayout cameraSettingsFrag;
    private CameraSettingBtn cameraTouchBtn;
    private CameraSettingBtn cameraTimeLapseBtn;
    private CameraSettingBtn cameraLightBtn;
    private CameraSettingBtn cameraPictureTypeBtn;
    private ImageView cameraActionTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        GlobalConfig.context=this;
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
                cameraView.getGlRender().switchLastFilterOfCustomizedFilters(filterType);
            }
        });

        initEffectsButtons();
        cameraSettingsFrag= (RelativeLayout) findViewById(R.id.rl_camera_setting_content);
        cameraSettingsFrag.setVisibility(View.GONE);
        //TODO:hide it when click shutter
        cameraActionTip= (ImageView) findViewById(R.id.iv_frag_camera_action_tip);
    }

    private void initEffectsButtons() {
        switchCameraBtn= getEffectsBtn(R.id.btn_switch_camera);
        switchCameraBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                switchCameraBtn.setSelected(!switchCameraBtn.isSelected());
                cameraView.getGlRender().switchCamera();
            }
        });
        cameraSettingBtn= getEffectsBtn(R.id.btn_camera_setting);
        cameraSettingBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                if(cameraSettingBtn.isSelected())
                    requestHideCameraSettingsFrag();
                else requestShowCameraSettingsFrag();
            }
        });
        cameraView.setRootViewClickListener(new CameraView.RootViewClickListener() {
            @Override
            public void onRootViewClicked() {
                requestHideCameraSettingsFrag();
                cameraActionTip.setVisibility(View.GONE);
            }
        });

       cameraTouchBtn=
               new CameraSettingBtn(R.id.btn_camera_touch,R.id.tv_camera_touch)
                       .register(new EffectsButton.OnClickEffectButtonListener() {
                           @Override
                           public void onClickEffectButton() {
                               cameraTouchBtn.changeState();
                               if(cameraTouchBtn.isSelected()){

                               }else{

                               }
                           }
                       });
       cameraTimeLapseBtn=new CameraSettingBtn(R.id.btn_camera_time_lapse,R.id.tv_camera_time_lapse)
               .register(new EffectsButton.OnClickEffectButtonListener() {
                   @Override
                   public void onClickEffectButton() {
                       cameraTimeLapseBtn.changeState();
                       if(cameraTimeLapseBtn.isSelected()){

                       }else{

                       }
                   }
               });
       cameraLightBtn=new CameraSettingBtn(R.id.btn_camera_light,R.id.tv_camera_light)
               .register(new EffectsButton.OnClickEffectButtonListener() {
                   @Override
                   public void onClickEffectButton() {
                       cameraLightBtn.changeState();
                       if(cameraLightBtn.isSelected()){

                       }else{

                       }
                   }
               });
       cameraPictureTypeBtn=new CameraSettingBtn(R.id.btn_camera_picture_type,R.id.tv_camera_picture_type)
               .register(new EffectsButton.OnClickEffectButtonListener() {
                   @Override
                   public void onClickEffectButton() {
                       cameraPictureTypeBtn.changeState();
                       if(cameraPictureTypeBtn.isSelected()){
                            cameraView.getGlRender().switchLastFilterOfPostProcess(new BlurredFrameEffect(GlobalConfig.context));
                       }else{
                           cameraView.getGlRender().switchLastFilterOfPostProcess(new PassThroughFilter(GlobalConfig.context));
                       }
                   }
               });
    }

    private void requestShowCameraSettingsFrag(){
        if(!cameraSettingBtn.isSelected()){
            cameraSettingsFrag.clearAnimation();
            Animation showAnim = AnimationUtils.loadAnimation(GlobalConfig.context, R.anim.anim_setting_content_show);
            cameraSettingsFrag.setVisibility(View.VISIBLE);
            cameraSettingsFrag.startAnimation(showAnim);
            cameraSettingBtn.setSelected(true);
        }
    }

    private void requestHideCameraSettingsFrag(){
        if(cameraSettingBtn.isSelected()){
            cameraSettingsFrag.clearAnimation();
            Animation hideAnim = AnimationUtils.loadAnimation(GlobalConfig.context, R.anim.anim_setting_content_hide);
            cameraSettingsFrag.setVisibility(View.GONE);
            cameraSettingsFrag.startAnimation(hideAnim);
            cameraSettingBtn.setSelected(false);
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

    private EffectsButton getEffectsBtn(int id){
        return (EffectsButton) findViewById(id);
    }

    private TextView getTextView(int id){
        return (TextView) findViewById(id);
    }

    class CameraSettingBtn{
        EffectsButton effectsButton;
        TextView hintText;

        CameraSettingBtn(int btnId,int textId){
            effectsButton=getEffectsBtn(btnId);
            hintText=getTextView(textId);
        }
        boolean isSelected(){
            return effectsButton.isSelected();
        }
        void changeState(){
            if(!effectsButton.isSelected()){
                effectsButton.setSelected(true);
                hintText.setTextColor(GlobalConfig.context.getResources().getColor(R.color.app_color));
            }else{
                effectsButton.setSelected(false);
                hintText.setTextColor(Color.WHITE);
            }
        }
        CameraSettingBtn register(EffectsButton.OnClickEffectButtonListener listener){
            effectsButton.setOnClickEffectButtonListener(listener);
            return this;
        }
    }
}
