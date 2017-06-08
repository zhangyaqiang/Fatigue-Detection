package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.switchface;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.util.Log;

import com.lemon.faceu.sdk.utils.JniEntry;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterE;
import com.martin.ads.omoshiroilib.flyu.ysj.OmoshiroiNative;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Ads on 2017/6/6.
 */

public abstract class SwitchFaceBase  extends GPUImageFilterE
{
    static final int[] eV = { 2, 34, 52, 52, 34, 74, 74, 34, 43, 43, 34, 41, 43, 41, 77, 77, 41, 61, 61, 41, 30, 8, 2, 82, 82, 2, 52, 82, 52, 74, 82, 74, 43, 82, 43, 46, 46, 43, 83, 83, 43, 77, 83, 77, 61, 83, 61, 24, 24, 61, 30, 12, 8, 82, 12, 82, 46, 12, 46, 16, 16, 46, 20, 20, 46, 83, 20, 83, 24 };
    static final float[] eW = { 0.15012F, 0.35474F, 0.15148F, 0.40127F, 0.1544F, 0.44733F, 0.16009F, 0.49307F, 0.1678F, 0.53845F, 0.17653F, 0.58373F, 0.18657F, 0.62876F, 0.19858F, 0.67353F, 0.21423F, 0.71724F, 0.23612F, 0.75824F, 0.2634F, 0.7963F, 0.29488F, 0.8317F, 0.33008F, 0.86414F, 0.36667F, 0.89504F, 0.40873F, 0.91889F, 0.45607F, 0.93237F, 0.50487F, 0.93892F, 0.55547F, 0.93213F, 0.60388F, 0.9177F, 0.64753F, 0.89389F, 0.68644F, 0.86313F, 0.72418F, 0.8309F, 0.75894F, 0.79552F, 0.79024F, 0.75732F, 0.81636F, 0.7161F, 0.83574F, 0.67159F, 0.85079F, 0.62574F, 0.8631F, 0.5795F, 0.87373F, 0.53296F, 0.88322F, 0.48626F, 0.89068F, 0.43926F, 0.89537F, 0.39185F, 0.89845F, 0.34374F, 0.21226F, 0.33568F, 0.25519F, 0.30104F, 0.31017F, 0.29597F, 0.36602F, 0.30409F, 0.41695F, 0.32129F, 0.57944F, 0.32199F, 0.63539F, 0.30333F, 0.69763F, 0.29424F, 0.76109F, 0.29988F, 0.81198F, 0.33376F, 0.49784F, 0.40566F, 0.49678F, 0.47346F, 0.49568F, 0.54176F, 0.49456F, 0.60963F, 0.43603F, 0.64221F, 0.46804F, 0.64896F, 0.49965F, 0.65815F, 0.53134F, 0.64702F, 0.5645F, 0.63874F, 0.26936F, 0.4087F, 0.31424F, 0.38104F, 0.36886F, 0.38588F, 0.40702F, 0.42761F, 0.36095F, 0.43438F, 0.30806F, 0.42931F, 0.60172F, 0.42532F, 0.63997F, 0.38311F, 0.6958F, 0.37773F, 0.74554F, 0.40457F, 0.70321F, 0.42535F, 0.649F, 0.43106F, 0.25691F, 0.33305F, 0.3092F, 0.33122F, 0.36194F, 0.33633F, 0.41134F, 0.34605F, 0.58537F, 0.34726F, 0.63931F, 0.33669F, 0.69851F, 0.32982F, 0.75859F, 0.33191F, 0.34161F, 0.37951F, 0.33391F, 0.43479F, 0.34536F, 0.40529F, 0.6677F, 0.37647F, 0.67644F, 0.43099F, 0.67023F, 0.40123F, 0.45071F, 0.41555F, 0.55108F, 0.41399F, 0.42925F, 0.57013F, 0.5703F, 0.56679F, 0.40384F, 0.61838F, 0.59787F, 0.61462F, 0.37414F, 0.72621F, 0.42487F, 0.71952F, 0.46999F, 0.71225F, 0.50351F, 0.7188F, 0.53735F, 0.7099F, 0.58626F, 0.71316F, 0.64316F, 0.71436F, 0.5907F, 0.76066F, 0.54473F, 0.78321F, 0.50576F, 0.78953F, 0.46712F, 0.78586F, 0.4229F, 0.76818F, 0.39565F, 0.73227F, 0.46877F, 0.73686F, 0.50394F, 0.73889F, 0.53913F, 0.73474F, 0.62149F, 0.72145F, 0.54085F, 0.74536F, 0.50483F, 0.74957F, 0.46881F, 0.74767F, 0.33784F, 0.41015F, 0.67263F, 0.40675F };
    int eX = -1;
    int eY = -1;
    int eZ = -1;
    int fa;
    int fb;
    FloatBuffer el;
    FloatBuffer fc;
    FloatBuffer fd;
    FloatBuffer fe;

    public SwitchFaceBase()
    {
        super("attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\n \nvarying vec2 textureCoordinate;\n \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n}", "varying highp vec2 textureCoordinate;\n \nuniform sampler2D inputImageTexture;\n \nvoid main()\n{\n     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n}");

        int i = eV.length;
        this.el = ByteBuffer.allocateDirect(4 * i * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.fd = ByteBuffer.allocateDirect(4 * i * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.fc = ByteBuffer.allocateDirect(4 * i * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.fe = ByteBuffer.allocateDirect(4 * i * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();

        i("exchangefacealpha.png");
    }

    protected int k()
    {
        return OmoshiroiNative.loadSwitchFilterBase();
    }

    public void l()
    {
        super.l();

        this.eX = GLES20.glGetUniformLocation(getProgram(), "drawMask");
        this.fa = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate2");
        this.fb = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate3");
        this.eY = GLES20.glGetUniformLocation(getProgram(), "m_orientation");
        this.eZ = GLES20.glGetUniformLocation(getProgram(), "faceCnt");

        this.fe.position(0);
        for (int i = 0; i < eV.length; i++)
        {
            int j = eV[i];
            if (!this.aY) {
                this.fe.put(eW[(2 * j)]).put(eW[(2 * j + 1)]);
            } else {
                this.fe.put(eW[(2 * j)]).put(1.0F - eW[(2 * j + 1)]);
            }
        }
    }

    protected void d(int paramInt)
    {
        super.d(paramInt);

        i(this.eX, 0);
        i(this.eZ, this.aV.h);

        int i = 0;
        switch (this.ba)
        {
            case 0:
                i = 3;
                break;
            case 1:
                i = 1;
                break;
            case 2:
                i = 4;
                break;
            case 3:
                i = 2;
        }
        if (-1 != this.eY) {
            i(this.eY, i);
        }
    }

    protected abstract int T();

    protected abstract int U();

    protected abstract int[] V();

    protected void e(int paramInt)
    {
        super.e(paramInt);
        if (this.aV.h < T()) {
            return;
        }
        for (int i = 0; i < U(); i++)
        {
            i(this.eX, i + 1);
            PointF[] arrayOfPointF1 = this.aV.i[i];
            PointF[] arrayOfPointF2 = this.aV.i[V()[i]];

            this.el.position(0);
            for (int j = 0; j < eV.length; j++)
            {
                PointF localPointF;
                if ((this.aY) && (!this.aZ)) {
                    localPointF = a(arrayOfPointF1[eV[j]].x, this.aT - arrayOfPointF1[eV[j]].y);
                } else {
                    localPointF = a(arrayOfPointF1[eV[j]].x, arrayOfPointF1[eV[j]].y);
                }
                this.el.put(localPointF.x).put(localPointF.y);
            }
            this.fc.position(0);
            for (int j = 0; j < eV.length; j++) {
                if (!this.aY) {
                    this.fc.put(arrayOfPointF1[eV[j]].x / this.aS).put(arrayOfPointF1[eV[j]].y / this.aT);
                } else {
                    this.fc.put(arrayOfPointF1[eV[j]].x / this.aS).put(1.0F - arrayOfPointF1[eV[j]].y / this.aT);
                }
            }
            this.fd.position(0);
            for (int j = 0; j < eV.length; j++) {
                if (!this.aY) {
                    this.fd.put(arrayOfPointF2[eV[j]].x / this.aS).put(arrayOfPointF2[eV[j]].y / this.aT);
                } else {
                    this.fd.put(arrayOfPointF2[eV[j]].x / this.aS).put(1.0F - arrayOfPointF2[eV[j]].y / this.aT);
                }
            }
            this.fc.position(0);
            GLES20.glVertexAttribPointer(this.aR, 2, 5126, false, 0, this.fc);

            GLES20.glEnableVertexAttribArray(this.aR);
            if (paramInt != -1)
            {
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(y(), paramInt);
                GLES20.glUniform1i(this.aQ, 0);
            }
            this.el.position(0);
            GLES20.glVertexAttribPointer(this.aP, 2, 5126, false, 0, this.el);
            GLES20.glEnableVertexAttribArray(this.aP);

            this.fd.position(0);
            GLES20.glVertexAttribPointer(this.fb, 2, 5126, false, 0, this.fd);
            GLES20.glEnableVertexAttribArray(this.fb);

            this.fe.position(0);
            GLES20.glVertexAttribPointer(this.fa, 2, 5126, false, 0, this.fe);
            GLES20.glEnableVertexAttribArray(this.fa);

            GLES20.glDrawArrays(4, 0, eV.length);

            GLES20.glDisableVertexAttribArray(this.aR);

            GLES20.glDisableVertexAttribArray(this.aP);
            GLES20.glDisableVertexAttribArray(this.fa);
            GLES20.glDisableVertexAttribArray(this.fb);
        }
        GLES20.glBindTexture(y(), 0);
    }
}
