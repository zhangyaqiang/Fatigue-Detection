package com.martin.ads.omoshiroilib.filter.ext;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.omoshiroilib.R;
import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.glessential.object.Plain;
import com.martin.ads.omoshiroilib.glessential.program.GLPassThroughProgram;
import com.martin.ads.omoshiroilib.util.MatrixUtils;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/1/27.
 */

public class DrawImageFilter extends PassThroughFilter {

    private Plain imagePlain;
    private int imageTextureId;
    private int[] imgSize;
    private int imageResourceId;

    public DrawImageFilter(Context context,int imageResourceId) {
        super(context);
        imgSize=new int[2];
        this.imageResourceId=imageResourceId;
        imagePlain=new Plain(false);
    }

    @Override
    public void init() {
        super.init();
        imageTextureId= TextureUtils.loadTexture(context, imageResourceId,imgSize);
    }

    @Override
    public void onDrawFrame(int textureId) {
        super.onDrawFrame(textureId);

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
