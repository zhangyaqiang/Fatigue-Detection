package com.martin.ads.testfaceu.faceu.ui;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.lemon.faceu.openglfilter.common.FilterCore;
import com.lemon.faceu.openglfilter.gpuimage.base.MResFileReaderBase;
import com.lemon.faceu.sdk.utils.MiscUtils;
import com.martin.ads.testfaceu.faceu.DemoConstants;
import com.martin.ads.testfaceu.faceu.HardCodeData;
import com.martin.ads.testfaceu.faceu.fake.Logger;
import com.martin.ads.testfaceu.faceu.fake.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;


public abstract class BaseActivity extends AppCompatActivity {
    private final static Logger log = LoggerFactory.getLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mkdirs(DemoConstants.APPDIR);
        makeNoMediaFile(DemoConstants.APPDIR);

        HardCodeData.initHardCodeData();
        for (HardCodeData.EffectItem item : HardCodeData.itemList) {
            uncompressAsset(this, item.zipFilePath, item.unzipPath);
        }

        int ret = FilterCore.initialize(this, null);
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getSupportActionBar().hide();
        log.debug("FilterCore.initialize ret "+ret);
    }
    protected abstract void deInitUIandEvent();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }
    @Override
    protected void onDestroy() {
        deInitUIandEvent();
        super.onDestroy();
    }

    public static void uncompressAsset(Context context, String assetName, String unzipDirName) {
        AssetManager assManager = context.getAssets();
        InputStream is;
        try {
            is = assManager.open(assetName);
        } catch (IOException e) {
            log.error("open zip failed, " + Log.getStackTraceString(e));
            return;
        }

        if (isFileExist(DemoConstants.APPDIR + "/" + unzipDirName)) {
            log.error("unzipDirName is exists");
            return;
        }

        mkdirs(DemoConstants.APPDIR);

        Map<String, ArrayList<MResFileReaderBase.FileItem>> dirItems = null;
        try {
            dirItems = MResFileReaderBase.getFileListFromZip(is);
        } catch (IOException e) {
            log.error("IOException on get file list from zip, " + assetName + " " + e.getMessage());
        } finally {
            MiscUtils.safeClose(is);
        }

        if (null == dirItems) {
            return;
        }

        try {
            is = assManager.open(assetName);
        } catch (IOException e) {
            log.error("open zip2 failed, " + Log.getStackTraceString(e));
            return;
        }

        try {
            if (null != is) {
                MResFileReaderBase.unzipToAFile(is, new File(DemoConstants.APPDIR), dirItems);
            }
        } catch (IOException e) {
            log.error("IOException on unzip " + assetName + " " + e.getMessage());
        } finally {
            MiscUtils.safeClose(is);
        }
    }

    public static boolean isFileExist(String filePath) {
        return new File(filePath).exists();
    }

    public static boolean mkdirs(String dir) {
        File file = new File(dir);
        if (file.exists()) {
            return file.isDirectory();
        }

        return file.mkdirs();
    }

    public static boolean makeNoMediaFile(String path) {
        File file = createFile(path, ".nomedia");
        try {
            if (null != file && !file.createNewFile()) {
                log.error("create nomedia failed");
            }
            return true;
        } catch (IOException e) {
            log.error("create nomedia failed" + e);
            return false;
        }
    }

    public static File createFile(String filedir, String filename) {
        if (filedir == null || filename == null)
            return null;

        if (!MiscUtils.mkdirs(filedir)) {
            log.error("create parent directory failed, " + filedir);
            return null;
        }

        String filepath = filedir + "/" + filename;
        return new File(filepath);
    }
}
