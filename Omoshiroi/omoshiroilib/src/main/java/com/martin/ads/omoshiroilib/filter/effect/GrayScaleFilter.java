package com.martin.ads.omoshiroilib.filter.effect;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.glessential.object.Plain;
import com.martin.ads.omoshiroilib.glessential.program.GLSimpleProgram;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2016/11/19.
 */

public class GrayScaleFilter extends AbsFilter {

    private GLSimpleProgram glSimpleProgram;
    private Plain plain;

    public GrayScaleFilter(Context context) {
        glSimpleProgram=new GLSimpleProgram(context, R.raw.vertex_shader_simple,R.raw.fragment_shader_gray_scale);
        plain=new Plain(true);
    }

    @Override
    public void init() {
        glSimpleProgram.create();
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        glSimpleProgram.use();
        plain.uploadTexCoordinateBuffer(glSimpleProgram.getMaTextureHandle());
        plain.uploadVerticesBuffer(glSimpleProgram.getMaPositionHandle());
    }

    @Override
    public void destroy() {
        glSimpleProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plain.draw();
    }
}
