package com.martin.ads.omoshiroilib.filter.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.glessential.object.Plain;
import com.martin.ads.omoshiroilib.glessential.program.GLPassThroughProgram;
import com.martin.ads.omoshiroilib.util.MatrixUtils;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/1/27.
 */

public class DrawImageFilter extends AbsFilter{

    private GLPassThroughProgram glPassThroughProgram;
    private Plain plain;
    private Plain imagePlain;
    private int imageTextureId;
    private int[] imgSize;

    private float[] projectionMatrix = new float[16];

    private Context context;
    private int imageResourceId;

    public DrawImageFilter(Context context,int imageResourceId) {
        imgSize=new int[2];
        this.imageResourceId=imageResourceId;
        this.context=context;
        glPassThroughProgram=new GLPassThroughProgram(context, R.raw.vertex_shader_pass_through,R.raw.fragment_shader_pass_through);
        plain=new Plain(true);
        imagePlain=new Plain(false);
        Matrix.setIdentityM(projectionMatrix,0);
    }

    @Override
    public void init() {
        glPassThroughProgram.create();

        imageTextureId= TextureUtils.loadTexture(context, imageResourceId,imgSize);
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        Matrix.setIdentityM(projectionMatrix,0);
        glPassThroughProgram.use();
        plain.uploadTexCoordinateBuffer(glPassThroughProgram.getMaTextureHandle());
        plain.uploadVerticesBuffer(glPassThroughProgram.getMaPositionHandle());
        GLES20.glUniformMatrix4fv(glPassThroughProgram.getMVPMatrixHandle(), 1, false, projectionMatrix, 0);
    }

    @Override
    public void destroy() {
        glPassThroughProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glPassThroughProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plain.draw();

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        TextureUtils.bindTexture2D(imageTextureId, GLES20.GL_TEXTURE0,glPassThroughProgram.getTextureSamplerHandle(),0);
        imagePlain.uploadTexCoordinateBuffer(glPassThroughProgram.getMaTextureHandle());
        imagePlain.uploadVerticesBuffer(glPassThroughProgram.getMaPositionHandle());
        MatrixUtils.updateProjection(imgSize[0],imgSize[1],surfaceWidth,surfaceHeight,projectionMatrix);
        GLES20.glUniformMatrix4fv(glPassThroughProgram.getMVPMatrixHandle(), 1, false, projectionMatrix, 0);
        imagePlain.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        super.onFilterChanged(surfaceWidth, surfaceHeight);
    }
}
