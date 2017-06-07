package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup;

import android.graphics.PointF;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterE;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.IOUtils;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.MiscUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Ads on 2017/6/6.
 */

public class ShapeChangeFilter extends GPUImageFilterE
{
    static final String TAG = "ShapeChangeFilter";
    static final String dW = "#define parameter";
    static final String dX = "uniform float parameter;";
    GroupData dY;
    String bV;
    PointF cA = new PointF(0.0F, 0.0F);
    int[] cB;
    int[] dZ;
    int ea;
    int cr;
    int cs;
    float ct;
    float cu;
    boolean eb = false;
    int ec = -1;

    public ShapeChangeFilter(String paramString, GroupData parama)
    {
        super(paramString, "attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\n \nvarying vec2 textureCoordinate;\n \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n}", parama.bN);
        this.bV = paramString;
        this.dY = parama;
        this.fragmentSource = l(this.fragmentSource);
        this.bh = this.dY.name;
        for (int i = 0; i < this.dY.ed.size(); i++) {
            j(this.bV + "/" + (String)this.dY.ed.get(i));
        }
        if (!MiscUtils.isNilOrNull(this.dY.bS)) {
            a(Uri.parse(this.bV + "/" + this.dY.bS));
        }
        if (1 == this.dY.ee) {
            F();
        }
    }

    String l(String paramString)
    {
        if (paramString.contains("#define parameter"))
        {
            int i = paramString.indexOf("#define parameter");
            int j = paramString.indexOf('\n', i);
            String str = paramString.substring(i + "#define parameter".length(), j);
            this.bi = ((int)(MiscUtils.safeParseFloat(str) * 100.0F));

            paramString = paramString.substring(0, i) + "uniform float parameter;" + paramString.substring(j, paramString.length());
            this.eb = true;
        }
        return paramString;
    }

    public void l()
    {
        super.l();

        this.cB = new int[this.dY.cv.size()];
        for (int i = 0; i < this.dY.cv.size(); i++) {
            this.cB[i] = GLES20.glGetUniformLocation(getProgram(), "location" + i);
        }
        this.dZ = new int[this.dY.cN];
        for (int i = 0; i < this.dY.cN; i++) {
            this.dZ[i] = GLES20.glGetUniformLocation(getProgram(), "angle" + i);
        }
        this.ea = GLES20.glGetUniformLocation(getProgram(), "m_orientation");
        this.cr = GLES20.glGetUniformLocation(getProgram(), "m_detect");
        this.cs = GLES20.glGetUniformLocation(getProgram(), "m_time");
        this.ec = GLES20.glGetUniformLocation(getProgram(), "parameter");
    }

    protected void d(int paramInt)
    {
        super.d(paramInt);
        int i = Math.min(this.aV.h, this.dY.cN);

        int j = 0;
        switch (this.ba)
        {
            case 0:
                j = 3;
                break;
            case 1:
                j = 1;
                break;
            case 2:
                j = 4;
                break;
            case 3:
                j = 2;
        }
        if (-1 != this.ea) {
            i(this.ea, j);
        }
        if (-1 != this.ec) {
            setFloat(this.ec, this.bi * 0.01F);
        }
        for (int k = 0; k < this.cB.length; k++)
        {
            GroupData.b locala = (GroupData.b)this.dY.cv.get(k);
            if (locala.cx >= i) {
                a(this.cB[k], this.cA);
            } else {
                b(this.cB[k], locala.cx, locala.cy);
            }
        }
        for (int k = 0; k < this.dZ.length; k++) {
            if (k >= i)
            {
                a(this.dZ[k], new PointF(0.0F, 0.0F));
            }
            else
            {
                float f2 = 0.0F;
                float f3 = -1.0F;
                float f4 = this.aV.i[k][43].x - this.aV.i[k][46].x;
                float f5 = this.aV.i[k][43].y - this.aV.i[k][46].y;
                float f1 = (float)Math.acos((f2 * f4 + f3 * f5) / Math.sqrt(f2 * f2 + f3 * f3) / Math.sqrt(f4 * f4 + f5 * f5));
                if (f2 > f4) {
                    f1 = -f1;
                }
                a(this.dZ[k], new PointF(
                        (float)Math.sin(-f1 + 1.5707963267948966D), (float)Math.cos(-f1 + 1.5707963267948966D)));
            }
        }
        if ((-1 != this.cs) && (-1 != this.cr))
        {
            if (i > 0)
            {
                if (this.cu >= 1.9F) {
                    this.cu = this.dY.bO[0];
                } else {
                    this.cu = 1.0F;
                }
                if (((this.dY.di == 1) && (this.aV.b())) || ((this.dY.di == 0) &&
                        (this.aV.c())) || (this.dY.di == 2))
                {
                    this.cu = 2.1F;
                    start();
                    if (this.ct >= this.dY.bO[2]) {
                        this.cu = this.dY.bO[3];
                    }
                }
                else
                {
                    if (this.dY.bQ == 1) {
                        stop();
                    }
                    this.cu += this.dY.bO[4];
                }
            }
            else
            {
                this.cu = 0.0F;
                this.ct = 0.0F;
                stop();
            }
            if (this.cu >= this.dY.bO[5])
            {
                this.ct += E() * this.dY.bO[6];
                if (this.ct > this.dY.bO[7])
                {
                    this.ct = 0.0F;
                    this.cu = 1.0F;
                    stop();
                }
            }
            else
            {
                this.ct = 0.0F;
            }
            setFloat(this.cs, this.ct);
            setFloat(this.cr, this.cu);
        }
    }

    public void releaseNoGLESRes()
    {
        super.releaseNoGLESRes();
        if (this.eb) {
            S();
        }
    }

    public void A()
    {
        super.A();

        this.ct = 0.0F;
        this.cu = 0.0F;
    }

    public int n()
    {
        return this.dY.cN;
    }

    public boolean B()
    {
        return 2 != this.dY.ee;
    }

    public boolean R()
    {
        return this.eb;
    }

    public void S()
    {
        String str = this.fragmentSource.replace("uniform float parameter;", "#define parameter " + this.bi * 0.01F);
        ArrayList localArrayList = new ArrayList();
        localArrayList.add(str);
        try
        {
            IOUtils.writeLinesToFile(this.bV, "glsl", localArrayList);
        }
        catch (IOException localIOException)
        {
            Log.e("ShapeChangeFilter", "write failed");
        }
    }
}
