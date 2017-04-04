package com.martin.ads.testfaceu.faceu.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lemon.faceu.openglfilter.common.FilterConstants;
import com.lemon.faceu.openglfilter.detect.DirectionDetector;
import com.lemon.faceu.openglfilter.gpuimage.base.FilterFactory;
import com.lemon.faceu.openglfilter.gpuimage.base.GPUImageFilter;
import com.lemon.faceu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.lemon.faceu.openglfilter.gpuimage.changeface.ChangeFaceInfo;
import com.lemon.faceu.openglfilter.gpuimage.changeface.ChangeFaceNet;
import com.lemon.faceu.openglfilter.gpuimage.dstickers.DynamicStickerData;
import com.lemon.faceu.openglfilter.gpuimage.dstickers.DynamicStickerMulti;
import com.lemon.faceu.openglfilter.gpuimage.filtergroup.GPUImageFilterGroup;
import com.lemon.faceu.openglfilter.gpuimage.filtergroup.GPUImageMultiSectionGroup;
import com.lemon.faceu.openglfilter.gpuimage.filtergroup.MultiSectionInfo;
import com.lemon.faceu.openglfilter.gpuimage.multitriangle.DrawMultiTriangleNet;
import com.lemon.faceu.openglfilter.gpuimage.multitriangle.MultiTriangleInfo;
import com.lemon.faceu.openglfilter.gpuimage.switchface.CloneFaceFilter;
import com.lemon.faceu.openglfilter.gpuimage.switchface.SwitchFaceInfo;
import com.lemon.faceu.openglfilter.gpuimage.switchface.SwitchFaceNet;
import com.lemon.faceu.openglfilter.gpuimage.switchface.TwoPeopleSwitch;
import com.lemon.faceu.sdk.utils.SdkConstants;
import com.lemon.faceusdkdemo.detect.FaceDetectorType;
import com.martin.ads.omoshiroilib.R;
import com.martin.ads.testfaceu.faceu.CameraLoader;
import com.martin.ads.testfaceu.faceu.DemoConstants;
import com.martin.ads.testfaceu.faceu.FuCameraCompat;
import com.martin.ads.testfaceu.faceu.FuCore;
import com.martin.ads.testfaceu.faceu.GPUImageRenderer;
import com.martin.ads.testfaceu.faceu.GPUVideoViewDecorator;
import com.martin.ads.testfaceu.faceu.HardCodeData;
import com.martin.ads.testfaceu.faceu.fake.Logger;
import com.martin.ads.testfaceu.faceu.fake.LoggerFactory;

import org.json.JSONException;

import java.io.IOException;


public class TestFaceUActivity extends BaseActivity implements GPUImageFilterGroupBase.IGroupStateChanged {

    private final static Logger log = LoggerFactory.getLogger(TestFaceUActivity.class);

    protected boolean mUseFrontFace = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUIandEvent();
        setContentView(R.layout.debug_test_faceu);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        mGPUVideoView.setLayoutParams(layoutParams);
        RelativeLayout relativeLayout= (RelativeLayout) findViewById(R.id.root_view);
        relativeLayout.addView(mGPUVideoView);
        findViewById(R.id.swtich_filter_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % HardCodeData.sItems.length;
                doUpdateFilter();
                if(mCameraLoader.getCamera()!=null)
                    mCameraLoader.getCamera().autoFocus(null);
            }
        });
        findViewById(R.id.swtich_camera_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCameraLoader.switchCamera();
            }
        });
        findViewById(R.id.control_layout).bringToFront();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }


    @SdkConstants.NotationClockwiseDirection
    protected int mRecordPhoneDirection = SdkConstants.ClockwiseDirection.DEG_0;
    protected DirectionDetector mDirectionDetector;

    protected CameraLoader mCameraLoader;
    protected GPUImageFilterGroupBase mCurrentFilter;

    private int mMaxFaceCount = 1;
    private int mCurrentIndex = 0;
    private FaceDetectorType mDetectType = FaceDetectorType.SENSETIME;

    private GPUVideoViewDecorator mGPUVideoView;

    private void initVDM() {
        if (null != mCameraLoader) {
            return;
        }

        if (null == mDirectionDetector) {
            mDirectionDetector = new DirectionDetector(false);
            mDirectionDetector.start();
            mRecordPhoneDirection = mDirectionDetector.getDirection();
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
        mGPUVideoView = new GPUVideoViewDecorator(getBaseContext());
        mGPUVideoView.setDirectionDetector(mDirectionDetector);

        mGPUVideoView.getGPUImage().setUpCamera(mCameraLoader.getCamera(), mCameraLoader.getDisplayRotate(),
                mCameraLoader.isUseFrontFace(), false);

        doUpdateFilter();
        doUpdateFaceDetector();
    }

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

    private void doUpdateFilter() {
        HardCodeData.EffectItem item = HardCodeData.sItems[mCurrentIndex];
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
        renderer.initFaceDetector(mDetectType);
        renderer.switchDetectMaxFaceCount(mMaxFaceCount);

//        renderer.setOnProcessedFrameListener((byte[] data, int width, int height, long timestamp, Rotation rotation) -> {
//            deliverVideoFrame(data, width, height, rotation.asInt(), timestamp);
//        });
    }

    public final static int TYPE_CHANGE_FACE       = 0;
    public final static int TYPE_DYNAMIC_STICKER   = 1;
    public final static int TYPE_SWITCH_FACE       = 2;
    public final static int TYPE_MULTI_SECTION     = 3;
    public final static int TYPE_MULTI_TRIANGLE    = 4;  // 注意强制更新的内容
    public final static int TYPE_TWO_PEOPLE_SWITCH = 5;
    public final static int TYPE_CLONE_PEOPLE_FACE = 6;

    protected GPUImageFilterGroupBase parseEffect(int type, String unzipPath) {
        Log.d("lalala", "parseEffect: "+type+" "+unzipPath);
        GPUImageFilterGroupBase groupBase = new GPUImageFilterGroup();
        groupBase.addFilter(new GPUImageFilter());
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
        FuCore.initialize(getApplicationContext());

        FuCameraCompat.initCameraInfo();
        mCurrentFilter = new GPUImageFilterGroup();
        mCurrentFilter.addFilter(new GPUImageFilter());
        // SETUP SurfaceView
        initVDM();
    }


    @Override
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
