package com.martin.ads.omoshiroilib.flyu.sdk.utils;

import java.util.Stack;

/**
 * Created by Ads on 2017/6/5.
 */

public abstract class ObjectCache<T> {
    int mCacheCnt;
    final Stack<T> mObjCacheStack = new Stack();

    protected abstract T newInstance();

    public ObjectCache(int var1) {
        this.mCacheCnt = var1;
    }

    public T obtain() {
        T var = null;
        synchronized(mObjCacheStack) {
            if(0 != mObjCacheStack.size()) {
                var = mObjCacheStack.pop();
            }
        }
        if(null == var) {
            var = newInstance();
        }
        return var;
    }

    public void cache(T var) {
        synchronized(mObjCacheStack) {
            mObjCacheStack.push(var);
        }
    }
}