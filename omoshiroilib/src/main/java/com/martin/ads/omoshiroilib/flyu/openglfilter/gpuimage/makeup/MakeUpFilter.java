package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.makeup;


import android.graphics.PointF;
import android.opengl.GLES20;

import com.lemon.faceu.sdk.utils.JniEntry;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterE;
import com.martin.ads.omoshiroilib.flyu.ysj.OmoshiroiNative;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ads on 2017/6/6.
 */

public class MakeUpFilter extends GPUImageFilterE {
    static final int ef = 106;
    static final int eg = 114;
    static final int[] eh = {34, 6, 12, 16, 20, 26, 41, 43};
    String db;
    MakeupData ei;
    int ej;
    int ek;
    FloatBuffer el;
    FloatBuffer em;
    FloatBuffer en;

    public MakeUpFilter(String paramString, MakeupData parama) {
        super(paramString, "attribute vec4 position;\nattribute vec4 inputTextureCoordinate;\n \nvarying vec2 textureCoordinate;\n \nvoid main()\n{\n    gl_Position = position;\n    textureCoordinate = inputTextureCoordinate.xy;\n}", "varying highp vec2 textureCoordinate;\n \nuniform sampler2D inputImageTexture;\n \nvoid main()\n{\n     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n}");
        this.db = paramString;
        this.ei = parama;
        if (this.ei.ep == 1) {
            F();
        }
        int i = 0;
        for (Iterator localIterator = this.ei.eo.iterator(); localIterator.hasNext(); ) {
            MakeupData.a locala = (MakeupData.a) localIterator.next();
            if (locala.er.length > i) {
                i = locala.er.length;
            }
        }
        MakeupData.a locala;
        this.el = ByteBuffer.allocateDirect(4 * i * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.em = ByteBuffer.allocateDirect(4 * i * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.en = ByteBuffer.allocateDirect(4 * i * 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
        Iterator localIterator;
        for (localIterator = this.ei.eo.iterator(); localIterator.hasNext(); ) {
            locala = (MakeupData.a) localIterator.next();
            j(this.db + "/" + locala.eq);
        }
        for (localIterator = this.ei.eo.iterator(); localIterator.hasNext(); ) {
            locala = (MakeupData.a) localIterator.next();
            locala.eu = new PointF[eh.length];

            PointF localPointF1 = locala.et[46];
            for (int j = 0; j < eh.length; j++) {
                PointF localPointF2 = locala.et[eh[j]];
                float f1 = 2.0F;
                if (j == eh.length - 1) {
                    f1 = 3.0F;
                }
                float f2 = f1 * (localPointF2.x - localPointF1.x);
                float f3 = f1 * (localPointF2.y - localPointF1.y);
                locala.eu[j] = new PointF(localPointF1.x + f2, localPointF1.y + f3);
            }
        }
    }

    protected int k() {
        return OmoshiroiNative.loadMakeUpFilter();
    }

    public void l() {
        super.l();

        this.ej = GLES20.glGetAttribLocation(getProgram(), "inputTextureCoordinate2");
        this.ek = GLES20.glGetUniformLocation(getProgram(), "drawMask");
    }

    PointF a(PointF[] paramArrayOfPointF, MakeupData.b paramb) {
        PointF localPointF = new PointF(paramArrayOfPointF[paramb.ew].x, paramArrayOfPointF[paramb.ew].y);
        localPointF.x += (paramArrayOfPointF[paramb.ex].x - localPointF.x) * paramb.ey;
        localPointF.y += (paramArrayOfPointF[paramb.ex].y - localPointF.y) * paramb.ey;

        localPointF.x += (paramArrayOfPointF[paramb.ez].x - localPointF.x) * paramb.eA;
        localPointF.y += (paramArrayOfPointF[paramb.ez].y - localPointF.y) * paramb.eA;

        return localPointF;
    }

    public PointF[][] setFaceDetResult(int paramInt1, PointF[][] paramArrayOfPointF, int paramInt2, int paramInt3) {
        Object localObject1 = paramArrayOfPointF;
        if (paramInt1 != 0) {
            PointF[][] arrayOfPointF1 = new PointF[5][114];
            int i = Math.min(paramInt1, this.ei.cN);
            PointF[] arrayOfPointF3;
            for (int j = 0; j < 5; j++) {
                PointF[] localObject2 = arrayOfPointF1[j];
                arrayOfPointF3 = paramArrayOfPointF[j];
                for (int n = 0; n < 106; n++) {
                    localObject2[n] = new PointF(arrayOfPointF3[n].x, arrayOfPointF3[n].y);
                }
                for (int n = 0; n < eh.length; n++) {
                    localObject2[(106 + n)] = new PointF(0.0F, 0.0F);
                }
            }
            Object localObject4;
            for (int j = 0; j < i; j++) {
                MakeupData.a localObject2 = (MakeupData.a) this.ei.eo.get(j);
                arrayOfPointF3 = arrayOfPointF1[j];
                for (MakeupData.b localb : ((MakeupData.a) localObject2).es) {
                    arrayOfPointF3[localb.ew] = a(paramArrayOfPointF[j], localb);
                }
                PointF cur=paramArrayOfPointF[j][46];
                for (int pp =0; pp <eh.length; pp++)
                {
                    localObject4 = paramArrayOfPointF[j][eh[pp]];
                    float f1 = 2.0F;
                    if (i ==eh.length - 1){
                        f1 = 3.0F;
                    }
                    float f2 = f1 * (((PointF) localObject4).x - cur.x);
                    float f3 = f1 * (((PointF) localObject4).y - cur.y);
                    arrayOfPointF3[(106 +pp)].x = cur.x + f2;
                    arrayOfPointF3[(106 +pp)].y = cur.y + f3;
                }
            }
            super.setFaceDetResult(paramInt1, arrayOfPointF1, paramInt2, paramInt3);

            int j = ((MakeupData.a) this.ei.eo.get(0)).ev ? 1 : 0;
            int m;
            for (int k = 1; k < this.ei.eo.size(); k++) {
                m = ((MakeupData.a) this.ei.eo.get(k)).ev ? 1 : 0;
                if (j != m) {
                    j = 2;
                }
            }
            if (0 == j) {
                localObject1 = paramArrayOfPointF;
            } else if (1 == j) {
                localObject1 = arrayOfPointF1;
            } else {
                PointF[][] arrayOfPointF2 = new PointF[5][114];
                for (m = 0; m < 5; m++) {
                    PointF[] hehe =paramArrayOfPointF[m];
                    PointF[] arrayOfPointF4 = arrayOfPointF1[m];
                    int i3 = (this.ei.eo.size() > m) && (((MakeupData.a) this.ei.eo.get(m)).ev) ? 1 : 0;
                    for (int i4 = 0; i4 < paramArrayOfPointF[m].length;i4++){
                        if (i3 == 0) {
                            arrayOfPointF2[m][i4] = new PointF(hehe[i4].x, hehe[i4].y);
                        } else {
                            arrayOfPointF2[m][i4] = new PointF(arrayOfPointF4[i4].x, arrayOfPointF4[i4].y);
                        }
                    }
                }
                localObject1 = arrayOfPointF2;
            }
        } else {
            super.setFaceDetResult(paramInt1, paramArrayOfPointF, paramInt2, paramInt3);
            localObject1 = paramArrayOfPointF;
        }
        return (PointF[][]) localObject1;
    }

    protected void d(int paramInt) {
        super.d(paramInt);

        i(this.ek, 0);
    }

    protected void e(int paramInt) {
        super.e(paramInt);
        if (this.aV.h == 0) {
            return;
        }
        int i = Math.min(this.aV.h, this.ei.cN);
        for (int j = 0; j < i; j++) {
            i(this.ek, j + 1);
            PointF[] arrayOfPointF = this.aV.i[j];
            MakeupData.a locala = (MakeupData.a) this.ei.eo.get(j);

            int[] arrayOfInt = locala.er;
            this.el.position(0);
            for (int k = 0; k < arrayOfInt.length; k++) {
                PointF localPointF;
                if ((this.aY) && (!this.aZ)) {
                    localPointF = a(arrayOfPointF[arrayOfInt[k]].x, this.aT - arrayOfPointF[arrayOfInt[k]].y);
                } else {
                    localPointF = a(arrayOfPointF[arrayOfInt[k]].x, arrayOfPointF[arrayOfInt[k]].y);
                }
                this.el.put(localPointF.x).put(localPointF.y);
            }
            this.em.position(0);
            this.en.position(0);
            for (int k = 0; k < arrayOfInt.length; k++) {
                int m = arrayOfInt[k];
                if (!this.aY) {
                    this.em.put(arrayOfPointF[m].x / this.aS).put(arrayOfPointF[m].y / this.aT);
                } else {
                    this.em.put(arrayOfPointF[m].x / this.aS).put(1.0F - arrayOfPointF[m].y / this.aT);
                }
                if (m >= 106) {
                    this.en.put(locala.eu[(m - 106)].x).put(locala.eu[(m - 106)].y);
                } else {
                    this.en.put(locala.et[m].x).put(locala.et[m].y);
                }
            }
            this.em.position(0);
            GLES20.glVertexAttribPointer(this.aR, 2, 5126, false, 0, this.em);

            GLES20.glEnableVertexAttribArray(this.aR);
            if (paramInt != -1) {
                GLES20.glActiveTexture(33984);
                GLES20.glBindTexture(y(), paramInt);
                GLES20.glUniform1i(this.aQ, 0);
            }
            this.el.position(0);
            GLES20.glVertexAttribPointer(this.aP, 2, 5126, false, 0, this.el);
            GLES20.glEnableVertexAttribArray(this.aP);
            this.en.position(0);
            GLES20.glVertexAttribPointer(this.ej, 2, 5126, false, 0, this.en);

            GLES20.glEnableVertexAttribArray(this.ej);

            GLES20.glDrawArrays(4, 0, arrayOfInt.length);

            GLES20.glDisableVertexAttribArray(this.aR);

            GLES20.glDisableVertexAttribArray(this.aP);
            GLES20.glDisableVertexAttribArray(this.ej);
        }
    }
}
