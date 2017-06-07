package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers;

import android.graphics.Matrix;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.flyu.ysj.OmoshiroiNative;

/**
 * Created by Ads on 2017/6/6.
 */

public class DynamicStickerDot extends DynamicStickerBase
{
    int[] cO = new int[5];
    int[] cP = new int[5];
    int cQ;
    int cR;
    int cS;
    int[] cT = new int[5];
    DstickerDataBeanExt cU;
    float[] cV = new float[16];
    float[] cW = new float[16];
    float[] cX = new float[9];
    float cY;
    float cZ;
    float da;

    public DynamicStickerDot(String paramString, DstickerDataBeanExt paramb)
    {
        super(paramb, paramString, "attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\n \nvarying vec2 textureCoordinate;\n \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n}", "varying highp vec2 textureCoordinate;\n \nuniform sampler2D inputImageTexture;\n \nvoid main()\n{\n     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n}");
        this.cU = paramb;

        this.cY = this.cU.scaleWidth;
        this.cZ = this.cU.dn;
        this.da = this.cU.jdField_do;
        this.bi = (this.bj = this.bk = 50);
    }

    protected int k() {
        return OmoshiroiNative.loadDStickerDotFilter();
    }

    public void l()
    {
        super.l();
        this.cQ = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
        this.cR = GLES20.glGetUniformLocation(getProgram(), "faceCnt");
        this.cS = GLES20.glGetUniformLocation(getProgram(), "flipSticker");
        for (int i = 0; i < 5; i++)
        {
            this.cO[i] = GLES20.glGetUniformLocation(getProgram(), "alignPoint" + i);
            this.cP[i] = GLES20.glGetUniformLocation(getProgram(), "size" + i);
            this.cT[i] = GLES20.glGetUniformLocation(getProgram(), "rotateMatrix" + i);
        }
    }

    float g(int paramInt)
    {
        return (paramInt - 50) * 2 / 100.0F;
    }

    protected void d(int var1) {
        super.d(var1);
        this.cU.scaleWidth = (int)(this.cY + this.g(this.bi) * this.cY);
        this.cU.dn = (int)(this.cZ + this.g(this.bj) * this.cZ);
        this.cU.jdField_do = (int)(this.da + this.g(this.bk) * this.da);
        if(this.cU.scaleWidth == 0) {
            this.cU.scaleWidth = 1;
        }

        int var2 = Math.min(this.aV.h, this.cU.cN);
        GLES20.glUniform1i(this.cR, var2);
        GLES20.glUniform1i(this.cS, this.aY?1:0);

        for(int var3 = 0; var3 < var2; ++var3) {
            float var4 = (float)this.a(this.aV.i[var3][this.cU.dq].x, this.aV.i[var3][this.cU.dq].y, this.aV.i[var3][this.cU.dp].x, this.aV.i[var3][this.cU.dp].y) / (float)this.cU.scaleWidth;
            float var5 = var4 * (float)this.cU.width;
            float var6 = var5 * (float)this.cU.height / (float)this.cU.width;
            float var8 = 0.0F;
            float var9 = -1.0F;
            float var10 = this.aV.i[var3][43].x - this.aV.i[var3][46].x;
            float var11 = this.aV.i[var3][43].y - this.aV.i[var3][46].y;
            float var7 = (float)Math.acos((double)(var8 * var10 + var9 * var11) / Math.sqrt((double)(var8 * var8 + var9 * var9)) / Math.sqrt((double)(var10 * var10 + var11 * var11)));
            if(var8 > var10) {
                var7 = -var7;
            }

            float var12 = 0.0F;
            float var13 = 0.0F;

            int var14;
            for(var14 = 0; var14 < this.cU.dm.length; ++var14) {
                var12 += this.g(var3, this.cU.dm[var14]);
                var13 += this.h(var3, this.cU.dm[var14]);
            }

            var12 /= (float)this.cU.dm.length;
            var13 /= (float)this.cU.dm.length;
            var14 = this.cU.width / 2 - this.cU.dn;
            int var15 = this.cU.height / 2 - this.cU.jdField_do;
            float var16 = (float)var14 * 1.0F / (float)this.cU.width * var5;
            float var17 = (float)var15 * 1.0F / (float)this.cU.height * var6;
            float var18;
            float var19;
            if(!this.aY) {
                var18 = var12 + var16;
                var19 = var13 + var17;
            } else {
                var18 = var12 + var16;
                var19 = var13 - var17;
            }

            Matrix var20 = new Matrix();
            var20.setValues(new float[]{1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F});
            if(this.aY) {
                var20.setRotate(-((float)((double)(var7 * 180.0F) / 3.141592653589793D)), var12, var13);
            } else {
                var20.setRotate((float)((double)(var7 * 180.0F) / 3.141592653589793D), var12, var13);
            }

            var20.getValues(this.cX);
            float var21 = this.cX[0] * var18 + this.cX[1] * var19 + this.cX[2];
            float var22 = this.cX[3] * var18 + this.cX[4] * var19 + this.cX[5];
            float var23 = 1.0F * (float)this.aX / (float)this.aW;
            android.opengl.Matrix.setIdentityM(this.cV, 0);
            android.opengl.Matrix.scaleM(this.cW, 0, this.cV, 0, var23, 1.0F, 1.0F);
            android.opengl.Matrix.translateM(this.cV, 0, this.cW, 0, var21 / (float)this.aW / var23, var22 / (float)this.aX, 0.0F);
            if(this.aY) {
                android.opengl.Matrix.rotateM(this.cW, 0, this.cV, 0, (float)((double)(var7 * 180.0F) / 3.141592653589793D), 0.0F, 0.0F, 1.0F);
            } else {
                android.opengl.Matrix.rotateM(this.cW, 0, this.cV, 0, -((float)((double)(var7 * 180.0F) / 3.141592653589793D)), 0.0F, 0.0F, 1.0F);
            }

            android.opengl.Matrix.translateM(this.cV, 0, this.cW, 0, -var21 / (float)this.aW / var23, -var22 / (float)this.aX, 0.0F);
            android.opengl.Matrix.scaleM(this.cW, 0, this.cV, 0, 1.0F / var23, 1.0F, 1.0F);
            this.a(this.cO[var3], new float[]{var21 / (float)this.aW, var22 / (float)this.aX});
            this.a(this.cP[var3], new float[]{var5 / (float)this.aW, var6 / (float)this.aX});
            this.setUniformMatrix4f(this.cT[var3], this.cW);
        }

        if(this.cC != -1) {
            GLES20.glActiveTexture('蓃');
            GLES20.glBindTexture(3553, this.cC);
            GLES20.glUniform1i(this.cQ, 3);
        } else {
            GLES20.glUniform1i(this.cR, 0);
        }

    }
}
