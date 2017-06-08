package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.Pair;

public class MResFileNameReader
        extends MResFileReaderBase
{
    public MResFileNameReader(String paramString1, String paramString2)
    {
        super(paramString1, paramString2);
    }

    public Bitmap loadBitmapForName(String paramString)
    {
        Pair localPair = (Pair)this.mStartPosMap.get(paramString);
        if (null == localPair)
        {
            Log.e("MergeResFileReader", "can't find pos for " + paramString);
            return null;
        }
        return BitmapFactory.decodeByteArray(this.mDataBuffer.array(), this.mDataBuffer.arrayOffset() + (Integer) localPair.first, (Integer) localPair.second);
    }

    public byte[] getFileBuffer()
    {
        return this.mDataBuffer.array();
    }

    public Pair<Integer, Integer> getOffsetAndLength(String paramString)
    {
        Pair localPair = (Pair)this.mStartPosMap.get(paramString);
        if (null == localPair)
        {
            Log.e("MergeResFileReader", "can't find pos for " + paramString);
            return null;
        }
        return new Pair(Integer.valueOf(((Integer)localPair.first).intValue() + this.mDataBuffer.arrayOffset()), localPair.second);
    }
}
