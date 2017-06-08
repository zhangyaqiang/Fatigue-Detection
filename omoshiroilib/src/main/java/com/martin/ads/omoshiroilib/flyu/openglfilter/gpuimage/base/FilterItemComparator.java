package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base;

import java.util.Comparator;

/**
 * Created by Ads on 2017/6/6.
 */

public class FilterItemComparator
        implements Comparator<MResFileReaderBase.FileItem>
{
    @Override
    public int compare(MResFileReaderBase.FileItem o1, MResFileReaderBase.FileItem o2) {
        return o1.fileName.compareTo(o2.fileName);
    }
}
