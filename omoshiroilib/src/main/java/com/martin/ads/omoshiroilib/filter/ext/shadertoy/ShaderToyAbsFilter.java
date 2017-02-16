package com.martin.ads.omoshiroilib.filter.ext.shadertoy;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;
import com.martin.ads.omoshiroilib.util.TextureUtils;

import java.nio.FloatBuffer;

/**
 * Created by Ads on 2017/2/16.
 */

public class ShaderToyAbsFilter extends SimpleFragmentShaderFilter {
    public ShaderToyAbsFilter(Context context, String fragmentShaderPath) {
        super(context, fragmentShaderPath);
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        int iResolutionLocation = GLES20.glGetUniformLocation(glSimpleProgram.getProgramId(), "iResolution");
        GLES20.glUniform3fv(iResolutionLocation, 1,
                FloatBuffer.wrap(new float[]{(float) surfaceWidth, (float) surfaceHeight, 1.0f}));

        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plain.draw();
    }
}
