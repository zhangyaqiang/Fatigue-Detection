package com.martin.ads.omoshiroilib.flyu.sdk.utils;

/**
 * Created by Ads on 2017/6/6.
 */

public class Wrapper {
    public static class Int
    {
        public int value;

        public Int(int paramInt)
        {
            this.value = paramInt;
        }
    }

    public static class WLong
    {
        public long value;

        public WLong(long paramLong)
        {
            this.value = paramLong;
        }
    }

    public static class Bool
    {
        public boolean value;

        public Bool(boolean paramBoolean)
        {
            this.value = paramBoolean;
        }
    }

    public static class Size
    {
        public int width;
        public int height;

        public Size(int paramInt1, int paramInt2)
        {
            this.width = paramInt1;
            this.height = paramInt2;
        }

        public boolean equals(Object paramObject)
        {
            if (!(paramObject instanceof Size)) {
                return false;
            }
            Size localSize = (Size)paramObject;
            return (this.width == localSize.width) && (this.height == localSize.height);
        }

        public int hashCode()
        {
            return this.width * 32713 + this.height;
        }
    }
}
