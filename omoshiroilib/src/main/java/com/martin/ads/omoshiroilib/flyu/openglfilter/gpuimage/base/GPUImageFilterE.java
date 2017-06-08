package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base;

import android.graphics.Bitmap;

import com.martin.ads.omoshiroilib.flyu.openglfilter.common.BitmapLoader;
import com.martin.ads.omoshiroilib.flyu.openglfilter.common.ImageLoader;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.draw.OpenGlUtils;
import com.martin.ads.omoshiroilib.flyu.sdk.commoninterface.IImageLoader;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.IOUtils;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.MiscUtils;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.support.annotation.CallSuper;
import android.util.Log;
import android.util.Pair;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ads on 2017/6/6.
 */

public class GPUImageFilterE extends GPUImageAudioFilter
        implements IImageLoader.IAsyncLoadImgListener
{
    final int bp = 2;
    final int bq = 3;
    final int br = 8;
    static int[] bs = { 33987, 33988, 33989, 33990, 33991, 33992, 33993, 33994 };
    int[] bt = new int[8];
    int[] bu = new int[8];
    List<SoftReference<Bitmap>> bv = new ArrayList();
    public List<String> bw;
    boolean bx = false;
    MResFileNameReader by = null;
    long bz = -1L;

    public GPUImageFilterE(String paramString1, String paramString2)
    {
        this(null, paramString1, paramString2);
    }

    public GPUImageFilterE(String paramString1, String paramString2, String paramString3)
    {
        super(paramString2, paramString3);
        for (int i = 0; i < 8; i++)
        {
            this.bu[i] = -1;
            this.bv.add(null);
        }
        if (!MiscUtils.isNilOrNull(paramString1))
        {
            Pair localPair = MResFileReaderBase.tryGetMergeFile(paramString1);
            if (null != localPair) {
                this.by = new MResFileNameReader(paramString1 + "/" + (String)localPair.first, paramString1 + "/" + (String)localPair.second);
            }
        }
    }

    public void l()
    {
        super.l();
        if (null != this.by) {
            try
            {
                this.by.init();
            }
            catch (IOException localIOException)
            {
                Log.e("GPUImageAudioFilter", "init res file name reader failed", localIOException);
                this.by = null;
            }
        }
        for (int i = 0; i < 8; i++) {
            this.bt[i] = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture" + (i + 2));
        }
        G();
    }

    protected float E()
    {
        if (-1L == this.bz) {
            return 0.0F;
        }
        return (float)(System.currentTimeMillis() - this.bz) / 1000.0F;
    }

    public void onDestroy()
    {
        super.onDestroy();

        for(int var1 = 0; var1 < 8; ++var1) {
            if(this.bu[var1] != -1) {
                int[] var2 = new int[]{this.bu[var1]};
                GLES20.glDeleteTextures(1, var2, 0);
                this.bu[var1] = -1;
            }

            this.bv.set(var1, null);
        }

        if(null != this.bw) {
            Iterator var3 = this.bw.iterator();

            while(var3.hasNext()) {
                String var4 = (String)var3.next();
                ImageLoader.getImageLoaderImpl().cancelLoad(var4, this);
            }
        }
    }

    public void onOutputSizeChanged(int paramInt1, int paramInt2)
    {
        this.aS = paramInt1;
        this.aT = paramInt2;
    }

    public void onDraw(int paramInt, FloatBuffer paramFloatBuffer1, FloatBuffer paramFloatBuffer2)
    {
        super.onDraw(paramInt, paramFloatBuffer1, paramFloatBuffer2);
        this.bz = System.currentTimeMillis();
    }

    @CallSuper
    protected void d(int paramInt)
    {
        super.d(paramInt);
        for (int i = 0; i < 8; i++) {
            if (this.bu[i] != -1)
            {
                GLES20.glActiveTexture(bs[i]);
                GLES20.glBindTexture(3553, this.bu[i]);
                GLES20.glUniform1i(this.bt[i], 3 + i);
            }
        }
    }

    public void A()
    {
        super.A();
        this.bz = -1L;
    }

    public void F()
    {
        this.bx = true;
    }

    public void i(String paramString)
    {
        if (this.bw == null) {
            this.bw = new ArrayList();
        }
        this.bw.add("assets://" + paramString);
    }

    public void j(String paramString)
    {
        if (this.bw == null) {
            this.bw = new ArrayList();
        }
        this.bw.add("file://" + paramString);
    }

    public void G()
    {
        if (this.bw == null) {
            return;
        }
        for (int i = 0; i < this.bw.size(); i++) {
            if ((null != this.bv.get(i)) && (null != ((SoftReference)this.bv.get(i)).get()))
            {
                this.bu[i] = OpenGlUtils.loadTexture((Bitmap)((SoftReference)this.bv.get(i)).get(), -1, false);
            }
            else
            {
                Object localObject;
                String str;
                if (this.bx)
                {
                    localObject = aK;
                    if (((String)this.bw.get(i)).startsWith("assets://"))
                    {
                        str = (bw.get(i)).substring("assets://".length());
                        localObject = BitmapLoader.loadBitmapFromAssets(str);
                    }
                    else if (((String)this.bw.get(i)).startsWith("file://"))
                    {
                        str = ((String)this.bw.get(i)).substring("file://".length());
                        if (null != this.by) {
                            localObject = this.by.loadBitmapForName(IOUtils.extractFileName(str));
                        } else {
                            localObject = BitmapLoader.loadBitmapFromFile(str);
                        }
                    }
                    else if (((String)this.bw.get(i)).startsWith("http://"))
                    {
                        if (null != this.by)
                        {
                            str = ((String)this.bw.get(i)).substring("http://".length());
                            localObject = this.by.loadBitmapForName(IOUtils.extractFileName(str));
                        }
                    }
                    if (localObject == null)
                    {
                        Log.i("GPUImageAudioFilter", "filter res is null:" + (String)this.bw.get(i));
                        localObject = aK;
                    }
                    this.bu[i] = OpenGlUtils.loadTexture((Bitmap)localObject, -1, false);
                }
                else
                {
                    this.bu[i] = OpenGlUtils.loadTexture(aK, -1, false);
                    if (null != this.by)
                    {
                        localObject = this.by.getFileBuffer();
                        if (((String)this.bw.get(i)).startsWith("http://")) {
                            str = ((String)this.bw.get(i)).substring("http://".length());
                        } else {
                            str = ((String)this.bw.get(i)).substring("file://".length());
                        }
                        Pair localPair = this.by.getOffsetAndLength(IOUtils.extractFileName(str));
                        if (null != localPair) {
                            ImageLoader.getImageLoaderImpl().asyncLoadImage((String)this.bw.get(i), (byte[])localObject, ((Integer)localPair.first)
                                    .intValue(), ((Integer)localPair.second).intValue(), this);
                        }
                    }
                    else
                    {
                        ImageLoader.getImageLoaderImpl().asyncLoadImage((String)this.bw.get(i), this);
                    }
                }
            }
        }
    }

    protected void b(int paramInt1, int paramInt2, int paramInt3)
    {
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = (this.aV.i[paramInt2][paramInt3].x / this.aW);
        if (this.aY) {
            arrayOfFloat[1] = (1.0F - this.aV.i[paramInt2][paramInt3].y / this.aX);
        } else {
            arrayOfFloat[1] = (this.aV.i[paramInt2][paramInt3].y / this.aX);
        }
        GLES20.glUniform2fv(paramInt1, 1, arrayOfFloat, 0);
    }

    protected PointF a(float paramFloat1, float paramFloat2)
    {
        PointF localPointF = new PointF();
        localPointF.x = (2.0F * paramFloat1 / this.aW - 1.0F);
        localPointF.y = (2.0F * (1.0F - paramFloat2 / this.aX) - 1.0F);
        return localPointF;
    }

    public void onLoadFinish(String paramString, Bitmap paramBitmap)
    {
        addTask(new e(this, paramString, paramBitmap));
    }

    class e implements Runnable {
        private GPUImageFilterE bC;
        private String bA;
        Bitmap bB;
        e(GPUImageFilterE var1, String var2, Bitmap var3) {
            this.bC = var1;
            this.bA = var2;
            this.bB = var3;
        }

        public void run() {
            for(int var1 = 0; var1 < this.bC.bw.size(); ++var1) {
                if(((String)this.bC.bw.get(var1)).equals(this.bA)) {
                    this.bC.bv.set(var1, new SoftReference(this.bB));
                    if(null != this.bB) {
                        int[] var2 = new int[]{this.bC.bu[var1]};
                        GLES20.glDeleteTextures(1, var2, 0);
                        this.bC.bu[var1] = OpenGlUtils.loadTexture(this.bB, -1, false);
                    }
                }
            }

        }
    }
}
