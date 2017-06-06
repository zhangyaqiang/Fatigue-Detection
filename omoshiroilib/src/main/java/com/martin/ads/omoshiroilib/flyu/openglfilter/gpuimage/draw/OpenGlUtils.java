package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.draw;

/**
 * Created by Ads on 2017/6/6.
 */


import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.Buffer;
import java.nio.IntBuffer;

public class OpenGlUtils
{
    public static final int NO_TEXTURE = -1;
    public static final float[] CUBE = { -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F };

    public static int loadTexture(Bitmap paramBitmap, int paramInt)
    {
        return loadTexture(paramBitmap, paramInt, true);
    }

    public static int loadTexture(Bitmap paramBitmap, int paramInt, boolean paramBoolean)
    {
        int[] arrayOfInt = new int[1];
        if (paramInt == -1)
        {
            GLES20.glGenTextures(1, arrayOfInt, 0);
            GLES20.glBindTexture(3553, arrayOfInt[0]);
            GLES20.glTexParameterf(3553, 10240, 9729.0F);

            GLES20.glTexParameterf(3553, 10241, 9729.0F);

            GLES20.glTexParameterf(3553, 10242, 33071.0F);

            GLES20.glTexParameterf(3553, 10243, 33071.0F);

            GLUtils.texImage2D(3553, 0, paramBitmap, 0);
        }
        else
        {
            GLES20.glBindTexture(3553, paramInt);
            GLUtils.texSubImage2D(3553, 0, 0, 0, paramBitmap);
            arrayOfInt[0] = paramInt;
        }
        if (paramBoolean) {
            paramBitmap.recycle();
        }
        return arrayOfInt[0];
    }

    public static int loadTexture(Buffer paramBuffer, Point paramPoint, int paramInt)
    {
        return loadTexture(paramBuffer, paramPoint.x, paramPoint.y, paramInt);
    }

    public static int loadTexture(Buffer paramBuffer, int paramInt1, int paramInt2, int paramInt3)
    {
        int[] arrayOfInt = new int[1];
        if (paramInt3 == -1)
        {
            GLES20.glGenTextures(1, arrayOfInt, 0);
            GLES20.glBindTexture(3553, arrayOfInt[0]);
            GLES20.glTexParameterf(3553, 10240, 9729.0F);

            GLES20.glTexParameterf(3553, 10241, 9729.0F);

            GLES20.glTexParameterf(3553, 10242, 33071.0F);

            GLES20.glTexParameterf(3553, 10243, 33071.0F);

            GLES20.glTexImage2D(3553, 0, 6408, paramInt1, paramInt2, 0, 6408, 5121, paramBuffer);
        }
        else
        {
            GLES20.glBindTexture(3553, paramInt3);
            GLES20.glTexSubImage2D(3553, 0, 0, 0, paramInt1, paramInt2, 6408, 5121, paramBuffer);

            arrayOfInt[0] = paramInt3;
        }
        return arrayOfInt[0];
    }

    public static int loadTextureAsBitmap(IntBuffer paramIntBuffer, Camera.Size paramSize, int paramInt)
    {
        Bitmap localBitmap = Bitmap.createBitmap(paramIntBuffer.array(), paramSize.width, paramSize.height, Bitmap.Config.ARGB_8888);
        return loadTexture(localBitmap, paramInt);
    }

    public static int loadShader(String paramString, int paramInt)
    {
        int[] arrayOfInt = new int[1];
        int i = GLES20.glCreateShader(paramInt);
        GLES20.glShaderSource(i, paramString);
        GLES20.glCompileShader(i);
        GLES20.glGetShaderiv(i, 35713, arrayOfInt, 0);
        if (arrayOfInt[0] == 0)
        {
            Log.d("Load Shader Failed", "Compilation\n" + GLES20.glGetShaderInfoLog(i));
            return 0;
        }
        return i;
    }

    public static int loadProgram(String paramString1, String paramString2)
    {
        int[] arrayOfInt = new int[1];
        int i = loadShader(paramString1, 35633);
        if (i == 0)
        {
            Log.d("Load Program", "Vertex Shader Failed");
            return 0;
        }
        int j = loadShader(paramString2, 35632);
        if (j == 0)
        {
            Log.d("Load Program", "Fragment Shader Failed");
            return 0;
        }
        int k = GLES20.glCreateProgram();

        GLES20.glAttachShader(k, i);
        GLES20.glAttachShader(k, j);

        GLES20.glLinkProgram(k);

        GLES20.glGetProgramiv(k, 35714, arrayOfInt, 0);
        if (arrayOfInt[0] <= 0)
        {
            Log.d("Load Program", "Linking Failed");
            return 0;
        }
        GLES20.glDeleteShader(i);
        GLES20.glDeleteShader(j);
        return k;
    }

    public static float rnd(float paramFloat1, float paramFloat2)
    {
        float f = (float)Math.random();
        return paramFloat1 + (paramFloat2 - paramFloat1) * f;
    }

    public static void deleteTexture(int paramInt)
    {
        int[] arrayOfInt = new int[1];
        arrayOfInt[0] = paramInt;
        GLES20.glDeleteTextures(1, arrayOfInt, 0);
    }

    public static void bindTextureToFrameBuffer(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
        GLES20.glBindTexture(3553, paramInt2);
        GLES20.glTexImage2D(3553, 0, 6408, paramInt3, paramInt4, 0, 6408, 5121, null);

        GLES20.glTexParameterf(3553, 10240, 9729.0F);

        GLES20.glTexParameterf(3553, 10241, 9729.0F);

        GLES20.glTexParameterf(3553, 10242, 33071.0F);

        GLES20.glTexParameterf(3553, 10243, 33071.0F);

        GLES20.glBindFramebuffer(36160, paramInt1);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, paramInt2, 0);

        GLES20.glBindTexture(3553, 0);
        GLES20.glBindFramebuffer(36160, 0);
    }
}