package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;
import com.martin.ads.omoshiroilib.flyu.openglfilter.detector.UnnamedA;
import com.martin.ads.omoshiroilib.util.ShaderUtils;

/**
 * Created by Ads on 2017/6/6.
 */

public class GPUImageFilter {
    protected static final Bitmap aK = BitmapFactory.decodeResource(GlobalConfig.context.getResources(), R.drawable.filter_res_hold);
    private final LinkedList<Runnable> aL;
    private String vertexSource;
    protected String fragmentSource;
    protected int aO;
    protected int aP;
    protected int aQ;
    protected int aR;
    public int aS;
    public int aT;
    private boolean aU;
    protected UnnamedA aV = new UnnamedA();
    protected int aW;
    protected int aX;
    protected boolean aY = false;
    protected boolean aZ = false;
    protected int ba = 1;
    protected float[] bb;
    protected boolean bc = false;
    private int bd;
    private int be;
    private int bf;
    private int bg;
    protected String bh = null;
    protected int bi = 0;
    protected int bj = 0;
    protected int bk = 0;

    public GPUImageFilter()
    {
        this("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\n \nvarying vec2 textureCoordinate;\n \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n}", "varying highp vec2 textureCoordinate;\n \nuniform sampler2D inputImageTexture;\n \nvoid main()\n{\n     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n}");
    }

    public GPUImageFilter(String paramString1, String paramString2)
    {
        this.aL = new LinkedList();
        this.vertexSource = paramString1;
        this.fragmentSource = paramString2;
    }

    public void setPhoneDirection(int paramInt)
    {
        this.ba = paramInt;
    }

    public final void init()
    {
        l();
        this.aU = true;
        w();
    }

    protected int k() {
        return ShaderUtils.createProgram(vertexSource, fragmentSource);
    }

    public void c(boolean paramBoolean)
    {
        this.aY = paramBoolean;
    }

    public void d(boolean paramBoolean)
    {
        this.aZ = paramBoolean;
    }

    protected float g(int paramInt1, int paramInt2)
    {
        return this.aV.i[paramInt1][paramInt2].x;
    }

    protected float h(int paramInt1, int paramInt2)
    {
        if (!this.aY) {
            return this.aV.i[paramInt1][paramInt2].y;
        }
        return this.aX - this.aV.i[paramInt1][paramInt2].y;
    }

    public void l()
    {
        this.aO = k();
        this.aP = GLES20.glGetAttribLocation(this.aO, "position");
        this.aQ = GLES20.glGetUniformLocation(this.aO, "inputImageTexture");
        this.aR = GLES20.glGetAttribLocation(this.aO, "inputTextureCoordinate");

        this.bd = GLES20.glGetUniformLocation(this.aO, "isAndroid");
        this.be = GLES20.glGetUniformLocation(this.aO, "surfaceWidth");
        this.bf = GLES20.glGetUniformLocation(this.aO, "surfaceHeight");
        this.bg = GLES20.glGetUniformLocation(this.aO, "needFlip");
        this.aU = true;
    }

    public void w() {}

    public String x()
    {
        return this.bh;
    }

    public PointF[][] setFaceDetResult(int paramInt1, PointF[][] paramArrayOfPointF, int paramInt2, int paramInt3)
    {
        this.aV.a(paramInt1, paramArrayOfPointF);
        this.aW = paramInt2;
        this.aX = paramInt3;
        return paramArrayOfPointF;
    }

    public void t()
    {
        this.bc = true;
    }

    public void u()
    {
        this.bc = false;
    }

    public void releaseNoGLESRes() {}

    public final void destroy()
    {
        C();

        this.aU = false;
        GLES20.glDeleteProgram(this.aO);
        onDestroy();
    }

    public void onDestroy() {}

    public void onOutputSizeChanged(int paramInt1, int paramInt2)
    {
        this.aS = paramInt1;
        this.aT = paramInt2;
    }

    public int y()
    {
        return 3553;
    }

    public void onDraw(int paramInt, FloatBuffer paramFloatBuffer1, FloatBuffer paramFloatBuffer2)
    {
        z();
        GLES20.glUseProgram(this.aO);
        C();
        if (!this.aU) {
            return;
        }
        paramFloatBuffer1.position(0);
        GLES20.glVertexAttribPointer(this.aP, 2, 5126, false, 0, paramFloatBuffer1);
        GLES20.glEnableVertexAttribArray(this.aP);
        paramFloatBuffer2.position(0);
        GLES20.glVertexAttribPointer(this.aR, 2, 5126, false, 0, paramFloatBuffer2);

        GLES20.glEnableVertexAttribArray(this.aR);
        if (paramInt != -1)
        {
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(y(), paramInt);
            GLES20.glUniform1i(this.aQ, 0);
        }
        d(paramInt);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glDisableVertexAttribArray(this.aP);
        GLES20.glDisableVertexAttribArray(this.aR);

        e(paramInt);

        GLES20.glBindTexture(y(), 0);
    }

    protected void z() {}

    protected void e(int paramInt) {}

    protected void d(int paramInt)
    {
        if (-1 != this.bd) {
            i(this.bd, 1);
        }
        if (-1 != this.be) {
            i(this.be, this.aS);
        }
        if (-1 != this.bf) {
            i(this.bf, this.aT);
        }
        if (-1 != this.bg) {
            i(this.bg, this.aY ? 1 : 0);
        }
    }

    public void A() {}

    public boolean B()
    {
        return true;
    }

    public int n()
    {
        return 5;
    }

    protected void C()
    {
        LinkedList localLinkedList = new LinkedList();
        synchronized (this.aL)
        {
            for (Runnable localRunnable : this.aL) {
                localLinkedList.add(localRunnable);
            }
            this.aL.clear();
        }
        while (!localLinkedList.isEmpty()) {
            ((Runnable)localLinkedList.removeFirst()).run();
        }
    }

    public boolean isInitialized()
    {
        return this.aU;
    }

    public int getProgram()
    {
        return this.aO;
    }

    protected void i(int paramInt1, int paramInt2)
    {
        GLES20.glUniform1i(paramInt1, paramInt2);
    }

    protected void setFloat(int paramInt, float paramFloat)
    {
        GLES20.glUniform1f(paramInt, paramFloat);
    }

    protected void a(int paramInt, float[] paramArrayOfFloat)
    {
        GLES20.glUniform2fv(paramInt, 1, FloatBuffer.wrap(paramArrayOfFloat));
    }

    protected void a(int paramInt, PointF paramPointF)
    {
        float[] arrayOfFloat = new float[2];
        arrayOfFloat[0] = paramPointF.x;
        arrayOfFloat[1] = paramPointF.y;
        GLES20.glUniform2fv(paramInt, 1, arrayOfFloat, 0);
    }

    protected void setUniformMatrix4f(int paramInt, float[] paramArrayOfFloat)
    {
        GLES20.glUniformMatrix4fv(paramInt, 1, false, paramArrayOfFloat, 0);
    }

    protected double a(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
    {
        return Math.sqrt((paramFloat1 - paramFloat3) * (paramFloat1 - paramFloat3) + (paramFloat2 - paramFloat4) * (paramFloat2 - paramFloat4));
    }

    public void addTask(Runnable paramRunnable)
    {
        synchronized (this.aL)
        {
            this.aL.addLast(paramRunnable);
        }
    }

    public void b(float[] paramArrayOfFloat)
    {
        this.bb = paramArrayOfFloat;
    }

    public void f(int paramInt)
    {
        this.bi = paramInt;
    }

    public int[] D()
    {
        return new int[] { this.bi, this.bj, this.bk };
    }

    public void a(int paramInt1, int paramInt2, int paramInt3)
    {
        addTask(new UnnamedD(this, paramInt1, paramInt2, paramInt3));
    }
}
