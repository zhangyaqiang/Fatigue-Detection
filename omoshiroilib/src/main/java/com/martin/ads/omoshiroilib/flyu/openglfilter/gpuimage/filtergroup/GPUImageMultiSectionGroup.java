package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.filtergroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.martin.ads.omoshiroilib.flyu.openglfilter.common.FilterCompat;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base.GPUImageFilterGroupBase;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers.DynamicStickerDot;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers.DynamicStickerVignette;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers.DstickerDataBeanExt;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.dstickers.DStickerVignetteBean;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.makeup.MakeUpFilter;
import com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.makeup.MakeupData;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.MiscUtils;

/**
 * Created by Ads on 2017/6/6.
 */

public class GPUImageMultiSectionGroup  extends GPUImageFilterGroupBase
{
    static final String TAG = "GPUImageMultiSectionGroup";
    String db;
    MultiSectionInfo dC;
    Map<String, GPUImageFilter> dD;
    List<GPUImageFilter> dE;
    List<GPUImageFilter> dF;
    String dG;
    long dH = -1L;
    List<GPUImageFilter> dw;

    public GPUImageMultiSectionGroup(String paramString, MultiSectionInfo paramMultiSectionInfo)
    {
        this.db = paramString;
        this.dC = paramMultiSectionInfo;
        this.dD = new HashMap();

        this.dE = new ArrayList();
        this.dF = new ArrayList();
        this.dw = new ArrayList();
    }

    public void l()
    {
        super.l();
        for (Map.Entry localEntry : this.dC.dJ.entrySet())
        {
            Object localObject1 = ((MultiSectionInfo.a)localEntry.getValue()).dQ;
            Object localObject2;
            if ((localObject1 instanceof DstickerDataBeanExt))
            {
                localObject2 = new DynamicStickerDot("file://" + ((MultiSectionInfo.a)localEntry.getValue()).dP, (DstickerDataBeanExt)localObject1);
            }
            else if ((localObject1 instanceof DStickerVignetteBean))
            {
                localObject2 = new DynamicStickerVignette("file://" + ((MultiSectionInfo.a)localEntry.getValue()).dP, (DStickerVignetteBean)localObject1);
            }
            else
            {
                Object localObject3;
                Object localObject4;
                if ((localObject1 instanceof GroupData))
                {
                    localObject3 = (GroupData)localObject1;
                    localObject4 = new ShapeChangeFilter(((MultiSectionInfo.a)localEntry.getValue()).dP, (GroupData) localObject1);
                    if (2 == ((GroupData)localObject3).ee)
                    {
                        ((ShapeChangeFilter)localObject4).F();
                        ((ShapeChangeFilter)localObject4).init();
                    }
                    localObject2 = localObject4;
                }
                else if ((localObject1 instanceof MakeupData))
                {
                    localObject3 = (MakeupData)localObject1;
                    localObject4 = new MakeUpFilter(((MultiSectionInfo.a)localEntry.getValue()).dP, (MakeupData) localObject3);
                    if (2 == ((MakeupData)localObject3).ep)
                    {
                        ((MakeUpFilter)localObject4).F();
                        ((MakeUpFilter)localObject4).init();
                    }
                    localObject2 = localObject4;
                }
                else
                {
                    localObject2 = new GPUImageFilter();
                }
            }
            this.dD.put((String)localEntry.getKey(), (GPUImageFilter)localObject2);
        }
        this.dD.put("__empty__", new GPUImageFilter());
        for (int i = 0; i < this.dE.size(); i++) {
            ((GPUImageFilter)this.dE.get(i)).init();
        }
        for (int i = 0; i < this.dF.size(); i++) {
            ((GPUImageFilter)this.dF.get(i)).init();
        }
        this.dG = this.dC.dM;
        this.dH = System.currentTimeMillis();
        P();
        Q();
    }

    void P()
    {
        ArrayList localArrayList = new ArrayList();
        for (Object localObject1 = this.dE.iterator(); ((Iterator)localObject1).hasNext();)
        {
            GPUImageFilter localObject2 = (GPUImageFilter)((Iterator)localObject1).next();
            if ((localObject2 instanceof GPUImageFilterGroup))
            {
                ((GPUImageFilterGroup)localObject2).O();
                List<GPUImageFilter> localList = ((GPUImageFilterGroup)localObject2).N();
                if ((localList != null) && (!localList.isEmpty())) {
                    localArrayList.addAll(localList);
                }
            }
            else
            {
                localArrayList.add(localObject2);
            }
        }
        List localList;
        MultiSectionInfo.b localObject1 = (MultiSectionInfo.b)this.dC.dK.get(this.dG);
        for (Object localObject2 = ((MultiSectionInfo.b)localObject1).dT.iterator(); ((Iterator)localObject2).hasNext();)
        {
            String localObject3 = (String)((Iterator)localObject2).next();
            GPUImageFilter localGPUImageFilter = (GPUImageFilter)this.dD.get(localObject3);
            MultiSectionInfo.a locala = (MultiSectionInfo.a)this.dC.dJ.get(localObject3);

            localArrayList.add(localGPUImageFilter);
            if (!localGPUImageFilter.isInitialized())
            {
                localGPUImageFilter.init();
                localGPUImageFilter.onOutputSizeChanged(this.aS, this.aT);
            }
            if ((null != locala) && (locala.dO)) {
                localGPUImageFilter.A();
            }
            if (this.bc) {
                localGPUImageFilter.t();
            } else {
                localGPUImageFilter.u();
            }
            localGPUImageFilter.setPhoneDirection(this.ba);
        }
        GPUImageFilter localGPUImageFilter;
        for (Iterator localObject2 = this.dF.iterator(); ((Iterator)localObject2).hasNext();)
        {
            Object localObject3 = (GPUImageFilter)((Iterator)localObject2).next();
            if ((localObject3 instanceof GPUImageFilterGroup))
            {
                ((GPUImageFilterGroup)localObject3).O();
                localList = ((GPUImageFilterGroup)localObject3).N();
                if ((localList != null) && (!localList.isEmpty())) {
                    localArrayList.addAll(localList);
                }
            }
            else
            {
                localArrayList.add(localObject3);
            }
        }
        int i = 0;
        for (Object localObject3 = localArrayList.iterator(); ((Iterator)localObject3).hasNext();)
        {
            localGPUImageFilter = (GPUImageFilter)((Iterator)localObject3).next();
            localGPUImageFilter.c(i % 2 == 1);
            i++;
        }
        for (Iterator localObject3 = this.dw.iterator(); ((Iterator)localObject3).hasNext();)
        {
            localGPUImageFilter = (GPUImageFilter)((Iterator)localObject3).next();
            if (!localArrayList.contains(localGPUImageFilter)) {
                if (localGPUImageFilter.B()) {
                    localGPUImageFilter.destroy();
                } else {
                    localGPUImageFilter.releaseNoGLESRes();
                }
            }
        }
        this.dw.clear();
        this.dw = localArrayList;
    }

    void Q()
    {
        int i = 0;
        for (Object localObject = this.dw.iterator(); ((Iterator)localObject).hasNext();)
        {
            GPUImageFilter localGPUImageFilter = (GPUImageFilter)((Iterator)localObject).next();
            if (localGPUImageFilter.n() > i) {
                i = localGPUImageFilter.n();
            }
        }
        if (null != this.bJ)
        {
            MultiSectionInfo.b localObject = (MultiSectionInfo.b)this.dC.dK.get(this.dG);
            this.bJ.onTipsAndCountChanged(i, ((MultiSectionInfo.b)localObject).bP, -1 == ((MultiSectionInfo.b)localObject).dS ? 65535 : ((MultiSectionInfo.b)localObject).dS);
        }
    }

    public void onDestroy()
    {
        for (Iterator localIterator = this.dE.iterator(); localIterator.hasNext();)
        {
            GPUImageFilter localObject = (GPUImageFilter)localIterator.next();
            ((GPUImageFilter)localObject).destroy();
        }
        Object localObject;
        Iterator localIterator;
        for (localIterator = this.dF.iterator(); localIterator.hasNext();)
        {
            localObject = (GPUImageFilter)localIterator.next();
            ((GPUImageFilter)localObject).destroy();
        }
        for (localIterator = this.dD.entrySet().iterator(); localIterator.hasNext();)
        {
            localObject = (Map.Entry)localIterator.next();
            ((GPUImageFilter)((Map.Entry)localObject).getValue()).destroy();
        }
        super.onDestroy();
    }

    public void releaseNoGLESRes()
    {
        this.bK = null;
        for (Object localObject1 = this.dE.iterator(); ((Iterator)localObject1).hasNext();)
        {
            GPUImageFilter localObject2 = (GPUImageFilter)((Iterator)localObject1).next();
            ((GPUImageFilter)localObject2).releaseNoGLESRes();
        }
        Object localObject2;
        Iterator localObject1;
        for (localObject1 = this.dF.iterator(); ((Iterator)localObject1).hasNext();)
        {
            localObject2 = (GPUImageFilter)((Iterator)localObject1).next();
            ((GPUImageFilter)localObject2).releaseNoGLESRes();
        }
        for (localObject1 = this.dD.entrySet().iterator(); ((Iterator)localObject1).hasNext();)
        {
            localObject2 = (Map.Entry)((Iterator)localObject1).next();
            ((GPUImageFilter)((Map.Entry)localObject2).getValue()).releaseNoGLESRes();
        }

        super.releaseNoGLESRes();
    }

    protected void z()
    {
        super.z();

        Map localMap = (Map)this.dC.dL.get(this.dG);
        if ((!FilterCompat.noFaceuAssist) && (null == localMap)) {
            throw new RuntimeException("section state is null");
        }
        if (null != localMap)
        {
            String str = null;
            MultiSectionInfo.c localc;
            if (this.aV.c())
            {
                localc = (MultiSectionInfo.c)localMap.get(Integer.valueOf(0));
                str = null == localc ? null : localc.dU;
            }
            else if (this.aV.b())
            {
                localc = (MultiSectionInfo.c)localMap.get(Integer.valueOf(1));
                str = null == localc ? null : localc.dU;
            }
            else if (this.aV.d())
            {
                localc = (MultiSectionInfo.c)localMap.get(Integer.valueOf(3));
                str = null == localc ? null : localc.dU;
            }
            else if (this.aV.h > 0)
            {
                localc = (MultiSectionInfo.c)localMap.get(Integer.valueOf(2));
                str = null == localc ? null : localc.dU;
            }
            if (localMap.containsKey(Integer.valueOf(4)))
            {
                localc = (MultiSectionInfo.c)localMap.get(Integer.valueOf(4));
                if (System.currentTimeMillis() - this.dH > localc.dV) {
                    str = localc.dU;
                }
            }
            if ((!MiscUtils.isNilOrNull(str)) && (!str.equals(this.dG)))
            {
                this.dG = str;
                this.dH = System.currentTimeMillis();
                P();
                Q();
            }
        }
    }

    public List<GPUImageFilter> H()
    {
        return this.dw;
    }

    public void addFilter(GPUImageFilter paramGPUImageFilter)
    {
        this.dE.add(paramGPUImageFilter);
    }

    public void c(GPUImageFilter paramGPUImageFilter)
    {
        this.dF.add(paramGPUImageFilter);
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
}
