package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.decorateface;

import android.graphics.PointF;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterE;

/**
 * Created by Ads on 2017/6/6.
 */

public class DecorateFaceNet  extends GPUImageFilterE
{
    DecorateFaceBean cz;
    String bV;
    PointF cA = new PointF(0.0F, 0.0F);
    int[] cB;

    public DecorateFaceNet(String paramString1, String paramString2, DecorateFaceBean parama)
    {
        super(paramString1, "attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\n \nvarying vec2 textureCoordinate;\n \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n}", paramString2);
        this.cz = parama;
        this.bV = paramString1;
        for (int i = 0; i < this.cz.bR.length; i++) {
            j(this.bV + "/" + this.cz.bR[i]);
        }
        F();
    }

    public void l()
    {
        super.l();

        this.cB = new int[this.cz.cv.size()];
        for (int i = 0; i < this.cz.cv.size(); i++) {
            this.cB[i] = GLES20.glGetUniformLocation(getProgram(), "location" + i);
        }
    }

    protected void d(int paramInt)
    {
        super.d(paramInt);
        for (int i = 0; i < this.cB.length; i++)
        {
            DecorateFaceBean.a locala = (DecorateFaceBean.a)this.cz.cv.get(i);
            if (locala.cx >= this.aV.h) {
                a(this.cB[i], this.cA);
            } else {
                b(this.cB[i], locala.cx, locala.cy);
            }
        }
    }
}
