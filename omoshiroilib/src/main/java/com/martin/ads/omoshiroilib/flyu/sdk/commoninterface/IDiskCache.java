package com.martin.ads.omoshiroilib.flyu.sdk.commoninterface;

import com.martin.ads.omoshiroilib.flyu.sdk.utils.Wrapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ads on 2017/6/6.
 */

public interface IDiskCache
{
    InputStream getInputStream(String paramString, Wrapper.WLong paramWLong);

    OutputStream newOutputStream(String paramString)
            throws IOException;

    void completeOutput(OutputStream paramOutputStream, boolean paramBoolean)
            throws IOException;

    String getCacheIdentity();
}