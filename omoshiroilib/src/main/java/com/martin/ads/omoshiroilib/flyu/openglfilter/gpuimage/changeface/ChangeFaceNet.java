package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.changeface;

import android.net.Uri;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.flyu.sdk.utils.MiscUtils;

/**
 * Created by Ads on 2017/6/6.
 */

public class ChangeFaceNet extends ChangeFaceBaseFilter
{
    ChangeFaceInfo bU;
    String bV;
    int bW;
    int bX;
    int bY;
    int bZ;
    int ca;
    int cb;
    int cc;
    int cd;
    int ce;
    int cf;
    int cg;
    int ch;
    int ci;
    int cj;
    int ck;
    int cl;
    int cm;
    int cn;
    int co;
    int cp;
    int cq;
    int cr;
    int cs;
    float ct;
    float cu;

    public ChangeFaceNet(String paramString, ChangeFaceInfo paramChangeFaceInfo)
    {
        super(paramString, "attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\n \nvarying vec2 textureCoordinate;\n \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n}", paramChangeFaceInfo.bN);
        this.bU = paramChangeFaceInfo;
        this.bV = paramString;
        this.bL = this.bU.bT;
        if ((this.bU.bO != null) && (this.bU.bO.length == 8))
        {
            int i = (int)(this.bU.bO[1] + 0.1F);
            if (i == 0) {
                this.bM = 0;
            } else if (i == 1) {
                this.bM = 1;
            } else {
                this.bM = 2;
            }
        }
        for (int i = 0; i < this.bU.bR.length; i++) {
            j(this.bV + "/" + this.bU.bR[i]);
        }
        if (!MiscUtils.isNilOrNull(this.bU.bS)) {
            a(Uri.parse(this.bV + "/" + this.bU.bS));
        }
    }

    public void l()
    {
        super.l();

        this.bW = GLES20.glGetUniformLocation(getProgram(), "p_left");
        this.bX = GLES20.glGetUniformLocation(getProgram(), "p_right");
        this.bY = GLES20.glGetUniformLocation(getProgram(), "p_top");
        this.bZ = GLES20.glGetUniformLocation(getProgram(), "p_bottom");
        this.ca = GLES20.glGetUniformLocation(getProgram(), "p_nose");
        this.cb = GLES20.glGetUniformLocation(getProgram(), "p_eyea");
        this.cc = GLES20.glGetUniformLocation(getProgram(), "p_eyeb");
        this.cd = GLES20.glGetUniformLocation(getProgram(), "p_faceleft");
        this.ce = GLES20.glGetUniformLocation(getProgram(), "p_faceright");
        this.cf = GLES20.glGetUniformLocation(getProgram(), "p_eyea_up");
        this.cg = GLES20.glGetUniformLocation(getProgram(), "p_eyea_down");
        this.ch = GLES20.glGetUniformLocation(getProgram(), "p_eyea_left");
        this.ci = GLES20.glGetUniformLocation(getProgram(), "p_eyea_right");
        this.cj = GLES20.glGetUniformLocation(getProgram(), "p_eyeb_up");
        this.ck = GLES20.glGetUniformLocation(getProgram(), "p_eyeb_down");
        this.cl = GLES20.glGetUniformLocation(getProgram(), "p_eyeb_left");
        this.cm = GLES20.glGetUniformLocation(getProgram(), "p_eyeb_right");

        this.cn = GLES20.glGetUniformLocation(getProgram(), "p_noseleg");

        this.co = GLES20.glGetUniformLocation(getProgram(), "p_chin");
        this.cp = GLES20.glGetUniformLocation(getProgram(), "p_chin_left");
        this.cq = GLES20.glGetUniformLocation(getProgram(), "p_chin_right");

        this.cr = GLES20.glGetUniformLocation(getProgram(), "m_detect");
        this.cs = GLES20.glGetUniformLocation(getProgram(), "m_time");
    }

    protected void d(int paramInt)
    {
        super.d(paramInt);
        if (this.bU.bO.length != 8) {
            return;
        }
        if (this.aV.h > 0)
        {
            b(this.bW, 0, 84);
            b(this.bX, 0, 90);
            b(this.bY, 0, 87);
            b(this.bZ, 0, 93);
            b(this.ca, 0, 46);
            b(this.cb, 0, 74);
            b(this.cc, 0, 77);
            b(this.cd, 0, 2);
            b(this.ce, 0, 30);
            b(this.cf, 0, 72);
            b(this.cg, 0, 73);
            b(this.ch, 0, 52);
            b(this.ci, 0, 55);
            b(this.cj, 0, 75);
            b(this.ck, 0, 76);
            b(this.cl, 0, 58);
            b(this.cm, 0, 61);
            b(this.cn, 0, 44);

            b(this.co, 0, 16);
            b(this.cp, 0, 10);
            b(this.cq, 0, 22);
            if (this.cu >= 1.9F) {
                this.cu = this.bU.bO[0];
            } else {
                this.cu = 1.0F;
            }
            if (((this.bM == 1) && (this.aV.b())) || ((this.bM == 0) &&
                    (this.aV.c())) || (this.bM == 2))
            {
                this.cu = 2.1F;
                start();
                if (this.ct >= this.bU.bO[2]) {
                    this.cu = this.bU.bO[3];
                }
            }
            else
            {
                if (this.bU.bQ == 1) {
                    stop();
                }
                this.cu += this.bU.bO[4];
            }
        }
        else
        {
            this.cu = 0.0F;
            this.ct = 0.0F;
            stop();
        }
        if (this.cu >= this.bU.bO[5])
        {
            this.ct += E() * this.bU.bO[6];
            if (this.ct > this.bU.bO[7])
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
