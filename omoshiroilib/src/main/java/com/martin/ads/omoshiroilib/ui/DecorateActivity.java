package com.martin.ads.omoshiroilib.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.filter.helper.FilterResourceHelper;
import com.martin.ads.omoshiroilib.util.AnimationUtils;
import com.martin.ads.omoshiroilib.util.FileUtils;

/**
 * Created by Ads on 2017/5/30.
 */

public class DecorateActivity extends AppCompatActivity {
    private RelativeLayout decorateTool;

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
        decorateTool= (RelativeLayout) findViewById(R.id.rl_frag_decorate_tool);

        findViewById(R.id.tmp_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(decorateTool.getVisibility()==View.VISIBLE)
                    AnimationUtils.displayAnim(decorateTool,DecorateActivity.this,R.anim.fadeout,View.GONE);
                else AnimationUtils.displayAnim(decorateTool,DecorateActivity.this,R.anim.fadein,View.VISIBLE);
            }
        });
    }
}
