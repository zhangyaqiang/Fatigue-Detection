package com.martin.ads.testfaceu.faceu.ui;

import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.FilterFactory;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.changeface.ChangeFaceInfo;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.changeface.ChangeFaceNet;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers.DynamicStickerData;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers.DynamicStickerMulti;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup.GPUImageFilterGroup;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup.GPUImageMultiSectionGroup;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup.MultiSectionInfo;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.multitriangle.DrawMultiTriangleNet;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.multitriangle.MultiTriangleInfo;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.switchface.CloneFaceFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.switchface.SwitchFaceInfo;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.switchface.SwitchFaceNet;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.switchface.TwoPeopleSwitch;
import com.martin.ads.omoshiroilib.flyu.sdk.mediaplayer.AudioFocusCore;
import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.DirectionDetector;
import com.martin.ads.omoshiroilib.flyu.EffectAdapter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.common.FilterConstants;
import com.martin.ads.testfaceu.faceu.CameraLoader;
import com.martin.ads.omoshiroilib.flyu.hardcode.DemoConstants;
import com.martin.ads.testfaceu.faceu.GPUImageRenderer;
import com.martin.ads.testfaceu.faceu.GPUVideoViewDecorator;
import com.martin.ads.omoshiroilib.flyu.hardcode.HardCodeData;
import com.martin.ads.omoshiroilib.flyu.fake.Logger;
import com.martin.ads.omoshiroilib.flyu.fake.LoggerFactory;
import com.martin.ads.omoshiroilib.flyu.hardcode.HardCodeHelper;

import org.json.JSONException;

import java.io.IOException;


public class TestFaceUActivity extends AppCompatActivity implements GPUImageFilterGroupBase.IGroupStateChanged {

    private final static Logger log = LoggerFactory.getLogger();

    protected boolean mUseFrontFace = true;
    private RecyclerView effectListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HardCodeHelper.decompressAllResource(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        setContentView(R.layout.debug_test_faceu);

        initUIandEvent();

        effectListView= (RecyclerView) findViewById(R.id.effect_list);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        effectListView.setLayoutManager(linearLayoutManager);

        EffectAdapter effectAdapter=new EffectAdapter(this,HardCodeData.itemList);
        effectListView.setAdapter(effectAdapter);
        effectAdapter.setOnEffectChangeListener(new EffectAdapter.OnEffectChangeListener() {
            @Override
            public void onFilterChanged(HardCodeData.EffectItem effectItem) {
                doUpdateFilter(effectItem);
                if(mCameraLoader.getCamera()!=null)
                    mCameraLoader.getCamera().autoFocus(null);
            }
        });
    }

    protected DirectionDetector mDirectionDetector;

    protected CameraLoader mCameraLoader;
    protected GPUImageFilterGroupBase mCurrentFilter;

    private int mMaxFaceCount = 1;

    private GPUVideoViewDecorator mGPUVideoView;

    private void deinitVDM() {
        if (null != mDirectionDetector) {
            mDirectionDetector.stop();
            mDirectionDetector = null;
        }

        if (null != mCameraLoader) {
            mCameraLoader.releaseCamera();
        }
        mCameraLoader = null;

        if (null != mGPUVideoView) {
            mGPUVideoView.onPause();
            mGPUVideoView.uninit();
            mGPUVideoView = null;
        }
        mCurrentFilter = null;
    }

    private void doUpdateFilter(HardCodeData.EffectItem item) {
        GPUImageFilterGroupBase filterGroup = parseEffect(item.type, DemoConstants.APPDIR + "/" + item.unzipPath);

        filterGroup.setGroupStateChangedListener(this);
        if (null != mGPUVideoView) {
            mCurrentFilter = filterGroup;
            mCurrentFilter.setPhoneDirection(mDirectionDetector.getDirection());
            mGPUVideoView.setFilter(mCurrentFilter);
        }

        if (mMaxFaceCount > FilterConstants.MAX_FACE_COUNT) {
            mMaxFaceCount = FilterConstants.MAX_FACE_COUNT;
        }

        if (null != mGPUVideoView && null != mGPUVideoView.getGPUImage()) {
            mGPUVideoView.getGPUImage().mRenderer.switchDetectMaxFaceCount(mMaxFaceCount);
        }
    }

    private void doUpdateFaceDetector() {
        if (mGPUVideoView == null) {
            return;
        }

        if (mGPUVideoView.getGPUImage() == null) {
            return;
        }

        if (mGPUVideoView.getGPUImage().getRenderer() == null) {
            return;
        }

        GPUImageRenderer renderer = mGPUVideoView.getGPUImage().getRenderer();
        renderer.initFaceDetector();
        renderer.switchDetectMaxFaceCount(mMaxFaceCount);
    }

    public final static int TYPE_CHANGE_FACE       = 0;
    public final static int TYPE_DYNAMIC_STICKER   = 1;
    public final static int TYPE_SWITCH_FACE       = 2;
    public final static int TYPE_MULTI_SECTION     = 3;
    public final static int TYPE_MULTI_TRIANGLE    = 4;  // 注意强制更新的内容
    public final static int TYPE_TWO_PEOPLE_SWITCH = 5;
    public final static int TYPE_CLONE_PEOPLE_FACE = 6;

    protected GPUImageFilterGroupBase parseEffect(int type, String unzipPath) {
        GPUImageFilterGroupBase groupBase = new GPUImageFilterGroup();
        groupBase.addFilter(new GPUImageFilter());
        if(type<0) return groupBase;
        try {
            if (type == TYPE_CHANGE_FACE) {
                ChangeFaceInfo changeFaceInfo = FilterFactory.readChangeFaceInfo(unzipPath);
                groupBase.addFilter(new ChangeFaceNet(unzipPath, changeFaceInfo));
            } else if (type == TYPE_DYNAMIC_STICKER) {
                DynamicStickerData data = FilterFactory.readDynamicStickerData(unzipPath);
                groupBase.addFilter(new DynamicStickerMulti(unzipPath, data));
            } else if (type == TYPE_SWITCH_FACE) {
                SwitchFaceInfo switchFaceInfo = FilterFactory.readSwitchFaceData(unzipPath);
                groupBase.addFilter(new SwitchFaceNet(unzipPath, switchFaceInfo));
            } else if (type == TYPE_MULTI_SECTION) {
                MultiSectionInfo multiSectionInfo = FilterFactory.readMultiSectionData(unzipPath);
                groupBase = new GPUImageMultiSectionGroup(unzipPath, multiSectionInfo);
                groupBase.addFilter(new GPUImageFilter());
            } else if (type == TYPE_MULTI_TRIANGLE) {
                MultiTriangleInfo info = FilterFactory.readMultiTriangleInfo(unzipPath);
                groupBase.addFilter(new DrawMultiTriangleNet(unzipPath, info));
            } else if (type == TYPE_TWO_PEOPLE_SWITCH) {
                groupBase.addFilter(new TwoPeopleSwitch());
            } else if (type == TYPE_CLONE_PEOPLE_FACE) {
                groupBase.addFilter(new CloneFaceFilter());
            }
        } catch (IOException e) {
            log.error("read effect filter data failed, " + e.getMessage());
        } catch (JSONException e) {
            log.error("parse effect filter data failed, " + e.getMessage());
        }
        return groupBase;
    }

    protected void initUIandEvent() {
        // init faceu related
        GlobalConfig.context=this;
        AudioFocusCore.initialize(GlobalConfig.context);
        mCurrentFilter = new GPUImageFilterGroup();
        mCurrentFilter.addFilter(new GPUImageFilter());

        if (null != mCameraLoader) {
            return;
        }

        if (null == mDirectionDetector) {
            mDirectionDetector = new DirectionDetector(this,false);
            mDirectionDetector.start();
        }

        log.info("init camera start");
        mCameraLoader = new CameraLoader(this, mUseFrontFace);
        boolean ret = mCameraLoader.initCamera();

        if (!ret) {
            log.error("init camera failed");
            return;
        }
        log.info("init camera done");

        // GPUVideoViewDecorator 是先做的翻转，再做的旋转
        mGPUVideoView = new GPUVideoViewDecorator(this, (GLSurfaceView) findViewById(R.id.test_camera_view));
        mGPUVideoView.setDirectionDetector(mDirectionDetector);

        mGPUVideoView.getGPUImage().setUpCamera(mCameraLoader.getCamera(), mCameraLoader.getDisplayRotate(),
                mCameraLoader.isUseFrontFace(), false);

        doUpdateFilter(HardCodeData.itemList.get(0));
        doUpdateFaceDetector();
    }


    @Override
    protected void onDestroy() {
        deInitUIandEvent();
        super.onDestroy();
    }

    protected void deInitUIandEvent() {
        deinitVDM();
    }

    @Override
    public void onTipsAndCountChanged(int maxCount, final String tips, final int duration) {
        mMaxFaceCount = maxCount;
        if (null != mGPUVideoView && null != mGPUVideoView.getGPUImage()) {
            mGPUVideoView.getGPUImage().mRenderer.switchDetectMaxFaceCount(mMaxFaceCount);
        }
        log.debug("onTipsAndCountChanged " + maxCount + " " + tips + " " + duration);
    }
}
