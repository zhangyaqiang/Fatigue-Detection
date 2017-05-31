package com.martin.ads.omoshiroilib.filter.base;

import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ads on 2016/11/19.
 */

public class FilterGroup extends AbsFilter {
    private static final String TAG = "FilterGroup";
    protected FBO[] fboList;
    protected List<AbsFilter> filters;
    protected boolean isRunning;

    public FilterGroup() {
        super();
        filters=new ArrayList<AbsFilter>();
    }

    @Override
    public void init() {
        for (AbsFilter filter : filters) {
            filter.init();
        }
        isRunning=true;
    }

    @Override
    public void destroy() {
        destroyFrameBuffers();
        for (AbsFilter filter : filters) {
            filter.destroy();
        }
        isRunning=false;
    }

    @Override
    public void onDrawFrame(int textureId) {
        runPreDrawTasks();
        if (fboList==null) {
            return ;
        }
        int size = filters.size();
        int previousTexture = textureId;
        for (int i = 0; i < size; i++) {
            AbsFilter filter = filters.get(i);
            Log.d(TAG, "onDrawFrame: "+i+" / "+size +" "+filter.getClass().getSimpleName()+" "+
                    filter.surfaceWidth+" "+filter.surfaceHeight);
            if (i < size - 1) {
                filter.setViewport();
                fboList[i].bind();
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                filter.onDrawFrame(previousTexture);
                fboList[i].unbind();
                previousTexture = fboList[i].getFrameBufferTextureId();
            }else{
                filter.setViewport();
                filter.onDrawFrame(previousTexture);
            }
        }
        runPostDrawTasks();
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        super.onFilterChanged(surfaceWidth, surfaceHeight);
        int size = filters.size();
        for (int i = 0; i < size; i++) {
            filters.get(i).onFilterChanged(surfaceWidth, surfaceHeight);
        }
        if(fboList != null){
            destroyFrameBuffers();
            fboList=null;
        }
        if (fboList == null) {
            fboList = new FBO[size-1];
            for (int i = 0; i < size-1; i++) {
                AbsFilter filter=filters.get(i);
                fboList[i]=FBO.newInstance().create(filter.getSurfaceWidth(),filter.getSurfaceHeight());
            }
        }
    }

    private void destroyFrameBuffers() {
        for(FBO fbo:fboList)
            fbo.destroy();
    }

    public void addFilter(final AbsFilter filter){
        if (filter==null) return;
        //If one filter is added multiple times,
        //it will execute the same times
        //BTW: Pay attention to the order of execution
        if (!isRunning){
            filters.add(filter);
        }
        else
            addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                filter.init();
                filters.add(filter);
                onFilterChanged(surfaceWidth,surfaceHeight);
            }
        });
    }

    public void addFilterList(final List<AbsFilter> filterList){
        if (filterList==null) return;
        //If one filter is added multiple times,
        //it will execute the same times
        //BTW: Pay attention to the order of execution
        if (!isRunning){
            for(AbsFilter filter:filterList){
                filters.add(filter);
            }
        }
        else
            addPreDrawTask(new Runnable() {
                @Override
                public void run() {
                    for(AbsFilter filter:filterList){
                        filter.init();
                        filters.add(filter);
                    }
                    onFilterChanged(surfaceWidth,surfaceHeight);
                }
            });
    }

    public void switchLastFilter(final AbsFilter filter){
        if (filter==null) return;
        Log.d(TAG, "onFilterChanged: "+filter.getClass().getSimpleName());
        if (!isRunning){
            if(filters.size()>0) {
                filters.remove(filters.size()-1)
                        .destroy();
            }
            filters.add(filter);
        }
        else
            addPreDrawTask(new Runnable() {
                @Override
                public void run() {
                    if(filters.size()>0) {
                        filters.remove(filters.size()-1)
                                .destroy();
                    }
                    filter.init();
                    filters.add(filter);
                    onFilterChanged(surfaceWidth,surfaceHeight);
                }
            });
    }

}
