package com.martin.ads.omoshiroilib.filter.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.omoshiroilib.camera.CameraEngine;
import com.martin.ads.omoshiroilib.glessential.object.Plain;
import com.martin.ads.omoshiroilib.glessential.program.GLPassThroughProgram;
import com.martin.ads.omoshiroilib.util.MatrixUtils;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2016/11/19.
 * let the image pass through
 * and simply fit the image to the screen
 */

public class OrthoFilter extends AbsFilter{

    private GLPassThroughProgram glPassThroughProgram;

    private float[] projectionMatrix = new float[16];

    public OrthoFilter(Context context) {
        glPassThroughProgram=new GLPassThroughProgram(context);
        plain=new Plain(true);
        Matrix.setIdentityM(projectionMatrix,0);
    }

    @Override
    public void init() {
        glPassThroughProgram.create();
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        glPassThroughProgram.use();
        plain.uploadTexCoordinateBuffer(glPassThroughProgram.getTextureCoordinateHandle());
        plain.uploadVerticesBuffer(glPassThroughProgram.getPositionHandle());
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
    }

    public void updateProjection(CameraEngine.PreviewSize previewSize){
        MatrixUtils.updateProjection(previewSize.getWidth(),previewSize.getHeight(),
                surfaceWidth,surfaceHeight,projectionMatrix);
    }
}
