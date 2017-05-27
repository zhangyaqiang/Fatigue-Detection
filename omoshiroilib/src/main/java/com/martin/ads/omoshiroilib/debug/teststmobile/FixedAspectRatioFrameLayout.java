package com.martin.ads.omoshiroilib.debug.teststmobile;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 
 * @author MatrixCV
 *
 * 显示比例相关设置
 * 
 */
public class FixedAspectRatioFrameLayout  extends FrameLayout{
    private int mAspectRatioWidth  = 480;
    private int mAspectRatioHeight = 640;

    public FixedAspectRatioFrameLayout(Context context)
    {
        super(context);
    }

    public FixedAspectRatioFrameLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FixedAspectRatioFrameLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    
    @Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
    {
        int originalWidth  = MeasureSpec.getSize(widthMeasureSpec);
        int originalHeight = MeasureSpec.getSize(heightMeasureSpec);

        int calculatedHeight = originalWidth * mAspectRatioHeight / mAspectRatioWidth;
        int finalWidth, finalHeight;

        if (calculatedHeight > originalHeight)
        {
            finalWidth  = originalHeight * mAspectRatioWidth / mAspectRatioHeight; 
            finalHeight = originalHeight;
        }
        else
        {
            finalWidth  = originalWidth;
            finalHeight = calculatedHeight;
        }

        super.onMeasure(
                MeasureSpec.makeMeasureSpec(finalWidth , MeasureSpec.EXACTLY), 
                MeasureSpec.makeMeasureSpec(finalHeight, MeasureSpec.EXACTLY));
    }
}
