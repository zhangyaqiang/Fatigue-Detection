package com.martin.ads.omoshiroilib.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.constant.MimeType;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.filter.helper.FilterResourceHelper;
import com.martin.ads.omoshiroilib.ui.module.EffectsButton;
import com.martin.ads.omoshiroilib.util.AnimationUtils;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;

import java.io.File;

/**
 * Created by Ads on 2017/5/30.
 */

//FIXME:change it to save to temp dir
public class DecorateActivity extends AppCompatActivity {
    private static final String TAG = "DecorateActivity";
    public static final String SAVED_MEDIA_FILE="saved_media_file";
    public static final String SAVED_MEDIA_TYPE="saved_media_type";
    private RelativeLayout decorateTool;

    private String filePath;
    private int mediaType;

    ImageView imagePreview;
    VideoView videoPreview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.frag_decorate_picture);
        imagePreview= (ImageView) findViewById(R.id.img_preview);
        videoPreview= (VideoView) findViewById(R.id.video_preview);
        decorateTool= (RelativeLayout) findViewById(R.id.rl_frag_decorate_tool);
        filePath=getIntent().getStringExtra(SAVED_MEDIA_FILE);
        mediaType=getIntent().getIntExtra(SAVED_MEDIA_TYPE,-1);
        if(mediaType<0) finish();
        if(mediaType== MimeType.PHOTO){
            videoPreview.setVisibility(View.GONE);
            imagePreview.setImageBitmap(BitmapUtils.loadBitmapFromFile(filePath));
        }else if(mediaType== MimeType.VIDEO){
            imagePreview.setVisibility(View.GONE);
            videoPreview.setVideoURI(Uri.parse(filePath));
            Log.d(TAG, "init: "+filePath);
            videoPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mp.setLooping(true);
                }
            });
            videoPreview.start();
        }
        decorateTool.bringToFront();
        AnimationUtils.displayAnim(decorateTool,DecorateActivity.this,R.anim.fadein,View.VISIBLE);

        EffectsButton decorateSaveBtn= (EffectsButton) findViewById(R.id.btn_frag_decorate_save);
        EffectsButton decorateCancelBtn= (EffectsButton) findViewById(R.id.btn_frag_decorate_cancel);
        EffectsButton decorateShareBtn= (EffectsButton) findViewById(R.id.btn_frag_decorate_share);

        decorateSaveBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                //TODO:show dialog
                Toast.makeText(DecorateActivity.this,"已保存",Toast.LENGTH_LONG).show();
            }
        });

        decorateCancelBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                File file=new File(filePath);
                if(file.exists()){
                    if(file.delete())
                        Toast.makeText(DecorateActivity.this,"已删除",Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });

        decorateShareBtn.setOnClickEffectButtonListener(new EffectsButton.OnClickEffectButtonListener() {
            @Override
            public void onClickEffectButton() {
                Intent intent = new Intent(Intent.ACTION_SEND);
                if(mediaType==MimeType.PHOTO)
                    intent.setType("image/*");
                else if(mediaType==MimeType.VIDEO)
                    intent.setType("video/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(filePath)));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "分享给朋友"));
            }
        });

        findViewById(R.id.btn_frag_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoPreview != null) {
            videoPreview.suspend();
        }
    }
}
