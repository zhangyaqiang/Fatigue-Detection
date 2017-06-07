package com.martin.ads.omoshiroilib.flyu.sdk.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.Iterator;
import java.util.Random;

/**
 * Created by Ads on 2017/6/5.
 */

public class MiscUtils {
    static final String TAG = "MiscUtils";
    static Random sRandom;
    static int gStatusBarHeight = -1;
    static double DEF_PI180 = 0.01745329252D;
    static double DEF_R = 6370693.5D;

    public MiscUtils() {
    }

    public static void initSeedWithLong(long var0) {
        synchronized(MiscUtils.class) {
            if(null == sRandom) {
                sRandom = new Random();
            }
        }
        sRandom.setSeed(var0);
    }

    public static void initSeedWithUid(String var0) {
        if(!isNilOrNull(var0)) {
            var0 = var0.split("@")[0];
        }
        long var1 = safeParseLong(var0, 0L);
        long var3 = getRandom().nextLong() + var1;
        getRandom().setSeed(var3);
    }

    public static Random getRandom() {
        if(sRandom == null) {
            throw new RuntimeException("sRandom is null, call initSeedWithLong first!");
        } else {
            return sRandom;
        }
    }

    public static String formatTimeMills(int mil) {
        mil /= 1000;
        int var1 = mil / 60;
        int var2 = var1 / 60;
        int var3 = mil % 60;
        var1 %= 60;
        return var2 > 0?String.format("%02d:%02d:%02d", new Object[]{Integer.valueOf(var2), Integer.valueOf(var1), Integer.valueOf(var3)}):String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }

    public static boolean isEqualsString(String var0, String var1) {
        return var0 == null && var1 == null?true:var0 != null && var0.equals(var1);
    }

    public static boolean mkdirs(String var0) {
        File var1 = new File(var0);
        return var1.exists()?var1.isDirectory():var1.mkdirs();
    }

    public static boolean safeClose(Closeable var0) {
        if(null != var0) {
            try {
                var0.close();
            } catch (IOException var2) {
                return false;
            }
        }

        return true;
    }

    public static String getProcessNameByPid(Context context, int pid) {
        if(context != null && pid > 0) {
            try {
                ActivityManager var2 = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
                Iterator var3 = var2.getRunningAppProcesses().iterator();
                while(var3.hasNext()) {
                    ActivityManager.RunningAppProcessInfo var4 = (ActivityManager.RunningAppProcessInfo)var3.next();
                    if(var4.pid == pid && !isNilOrNull(var4.processName)) {
                        return var4.processName;
                    }
                }
            } catch (Exception var7) {
                var7.printStackTrace();
            }

            byte[] var8 = new byte[128];

            try {
                FileInputStream var9 = new FileInputStream("/proc/" + pid + "/cmdline");
                int var10 = var9.read(var8);
                var9.close();
                if(var10 > 0) {
                    for(int var5 = 0; var5 < var10; ++var5) {
                        if(var8[var5] > 128 || var8[var5] <= 0) {
                            var10 = var5;
                            break;
                        }
                    }

                    return new String(var8, 0, var10);
                }
            } catch (Exception var6) {
                var6.printStackTrace();
            }

            return "";
        } else {
            return "";
        }
    }

    public static int safeParseInt(String var0, int var1) {
        int var2 = var1;

        try {
            var2 = Integer.parseInt(var0);
        } catch (Exception var4) {
            if(var0 != null) {
                Log.e("MiscUtils", "parserInt error " + var0);
            }
        }

        return var2;
    }

    public static int safeParseInt(String var0) {
        return safeParseInt(var0, 0);
    }

    public static long safeParseLong(String var0, long var1) {
        long var3 = var1;

        try {
            var3 = Long.parseLong(var0);
        } catch (Exception var6) {
            if(var0 != null) {
                Log.e("MiscUtils", "parseLong error " + var0, var6);
            }
        }

        return var3;
    }

    public static long safeParseLong(String var0) {
        return safeParseLong(var0, 0L);
    }

    public static double safeParseDouble(String var0) {
        double var1 = 0.0D;

        try {
            var1 = Double.parseDouble(var0);
        } catch (Exception var4) {
            if(var0 != null) {
                Log.e("MiscUtils", "parseDouble error " + var0);
            }
        }

        return var1;
    }

    public static float safeParseFloat(String var0) {
        float var1 = 0.0F;

        try {
            var1 = Float.parseFloat(var0);
        } catch (Exception var3) {
            if(var0 != null) {
                Log.e("MiscUtils", "parseFloat error " + var0);
            }
        }

        return var1;
    }

    public static boolean isNilOrNull(String var0) {
        return null == var0 || 0 == var0.length();
    }

    public static boolean isNilOrNull(byte[] var0) {
        return null == var0 || 0 == var0.length;
    }

    public static int nullAsNil(Integer var0) {
        return var0 == null?0:var0.intValue();
    }

    public static String nullAsNil(String var0) {
        return null == var0?"":var0;
    }

    public static String getStack(boolean var0) {
        StackTraceElement[] var1 = (new Throwable()).getStackTrace();
        if(var1 != null && var1.length >= 4) {
            StringBuilder var2 = new StringBuilder();

            for(int var3 = 1; var3 < var1.length; ++var3) {
                var2.append("[");
                var2.append(var1[var3].getClassName());
                var2.append(":");
                var2.append(var1[var3].getMethodName());
                if(var0) {
                    var2.append("(").append(var1[var3].getLineNumber()).append(")]\n");
                } else {
                    var2.append("]\n");
                }
            }

            return var2.toString();
        } else {
            return "";
        }
    }

    static char byte2hexChar(byte var0) {
        return (char)(var0 <= 9?var0 + 48:var0 - 10 + 97);
    }

    static byte hexChar2byte(char var0) {
        if(var0 >= 65 && var0 <= 90) {
            var0 = (char)(var0 - 65 + 97);
        }

        return (byte)(var0 >= 48 && var0 <= 57?var0 - 48:var0 - 97 + 10);
    }

    public static byte[] hexStrToByte(String var0) {
        if(null == var0) {
            return null;
        } else if(var0.length() % 2 != 0) {
            throw new RuntimeException("hex string must be in multiple of 2");
        } else {
            byte[] var1 = new byte[var0.length() / 2];

            for(int var2 = 0; var2 < var1.length; ++var2) {
                var1[var2] = (byte)(hexChar2byte(var0.charAt(2 * var2)) << 4 | hexChar2byte(var0.charAt(2 * var2 + 1)));
            }

            return var1;
        }
    }

    public static String byteToHexStr(byte[] var0) {
        if(null == var0) {
            return "";
        } else {
            StringBuilder var1 = new StringBuilder();

            for(int var2 = 0; var2 < var0.length; ++var2) {
                int var3 = var0[var2] & 255;
                var1.append(byte2hexChar((byte)(var3 / 16)));
                var1.append(byte2hexChar((byte)(var3 % 16)));
            }

            return var1.toString();
        }
    }

    public static void xorByteArray(byte[] var0, byte[] var1) {
        int var2 = 0;

        for(int var3 = 0; var2 < var0.length; ++var3) {
            if(var3 == var1.length) {
                var3 = 0;
            }

            var0[var2] ^= var1[var3];
            ++var2;
        }

    }

    public static byte[] generateRandomByteArr(int var0) {
        if(var0 % 4 != 0) {
            throw new InvalidParameterException("length must be in multiples of four");
        } else {
            byte[] var1 = new byte[var0];
            Random var2 = new Random();

            for(int var3 = 0; var3 < var0; var3 += 4) {
                int var4 = var2.nextInt();
                var1[var3] = (byte)(var4 >> 24);
                var1[var3 + 1] = (byte)(var4 >> 16);
                var1[var3 + 2] = (byte)(var4 >> 8);
                var1[var3 + 3] = (byte)var4;
            }

            return var1;
        }
    }

    public static int getStatusBarHeightFromSysR(Context var0) {
        if(-1 == gStatusBarHeight) {
            gStatusBarHeight = 25;

            try {
                Class var1 = Class.forName("com.android.internal.R$dimen");
                Object var2 = var1.newInstance();
                Field var3 = var1.getField("status_bar_height");
                int var4 = Integer.parseInt(var3.get(var2).toString());
                gStatusBarHeight = var0.getResources().getDimensionPixelSize(var4);
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        return gStatusBarHeight;
    }

    public static long getDistance(double var0, double var2, double var4, double var6) {
        double var8 = var0 * DEF_PI180;
        double var10 = var2 * DEF_PI180;
        double var12 = var4 * DEF_PI180;
        double var14 = var6 * DEF_PI180;
        double var16 = Math.sin(var10) * Math.sin(var14) + Math.cos(var10) * Math.cos(var14) * Math.cos(var8 - var12);
        if(var16 > 1.0D) {
            var16 = 1.0D;
        } else if(var16 < -1.0D) {
            var16 = -1.0D;
        }

        var16 = DEF_R * Math.acos(var16);
        return (long)var16;
    }

    public static class TestTime {
        long msBegin;

        public TestTime() {
            this.reset();
        }

        public void reset() {
            this.msBegin = getTime();
        }

        public long getDiff() {
            return getTime() - this.msBegin;
        }

        public static long getTime() {
            return SystemClock.elapsedRealtime();
        }

        public static long diffMS(long var0, long var2) {
            return var2 - var0;
        }

        public void printfDiff() {
            Log.d("MiscUtils", "diff: " + this.getDiff());
        }
    }
}
