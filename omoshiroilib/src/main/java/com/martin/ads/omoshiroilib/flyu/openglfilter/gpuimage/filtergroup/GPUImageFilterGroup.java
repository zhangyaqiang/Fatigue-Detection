package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup;

import com.martin.ads.omoshiroilib.flyu.openglfilter.common.FilterCompat;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Ads on 2017/6/6.
 */

public class GPUImageFilterGroup extends GPUImageFilterGroupBase
{
    protected List<GPUImageFilter> dv;
    protected List<GPUImageFilter> dw;

    public GPUImageFilterGroup()
    {
        this.dv = new ArrayList();
        this.dw = new ArrayList();
    }

    public List<GPUImageFilter> H()
    {
        return this.dw;
    }

    public void addFilter(GPUImageFilter paramGPUImageFilter)
    {
        if (paramGPUImageFilter == null) {
            return;
        }
        this.dv.add(paramGPUImageFilter);
        O();
    }

    public void l()
    {
        super.l();
        for (int i = 0; i < this.dw.size(); i++)
        {
            ((GPUImageFilter)this.dw.get(i)).init();
            ((GPUImageFilter)this.dw.get(i)).c(i % 2 == 1);
        }
    }

    public void onDestroy()
    {
        for (GPUImageFilter localGPUImageFilter : this.dw) {
            localGPUImageFilter.destroy();
        }
        super.onDestroy();
    }

    public void releaseNoGLESRes()
    {
        Iterator var1 = this.dw.iterator();

        GPUImageFilter var2;
        while(var1.hasNext()) {
            var2 = (GPUImageFilter)var1.next();
            var2.releaseNoGLESRes();
        }
        if(FilterCompat.saveParamsOnRelease) {
            var1 = this.dv.iterator();

            while(var1.hasNext()) {
                var2 = (GPUImageFilter)var1.next();
                var2.releaseNoGLESRes();
            }
        }

        super.releaseNoGLESRes();
    }

    public void t()
    {
        super.t();
        for (GPUImageFilter localGPUImageFilter : this.dw) {
            localGPUImageFilter.t();
        }
    }

    public void u()
    {
        super.u();
        for (GPUImageFilter localGPUImageFilter : this.dw) {
            localGPUImageFilter.u();
        }
    }

    public List<GPUImageFilter> N()
    {
        return this.dw;
    }

    public void O()
    {
        if (this.dv == null) {
            return;
        }
        this.dw.clear();
        for (GPUImageFilter localGPUImageFilter : this.dv) {
            if ((localGPUImageFilter instanceof GPUImageFilterGroup))
            {
                ((GPUImageFilterGroup)localGPUImageFilter).O();
                List localList = ((GPUImageFilterGroup)localGPUImageFilter).N();
                if ((localList != null) && (!localList.isEmpty())) {
                    this.dw.addAll(localList);
                }
            }
            else
            {
                this.dw.add(localGPUImageFilter);
            }
        }
    }
}

