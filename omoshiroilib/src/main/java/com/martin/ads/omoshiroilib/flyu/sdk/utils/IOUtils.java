package com.martin.ads.omoshiroilib.flyu.sdk.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ads on 2017/6/6.
 */

public class IOUtils {
    private static final String TAG = "IOUtils";
    public static final String separator = "/";
    public static final char separatorChar = '/';

    public static String getSDPath() {
        File localFile = null;
        boolean bool = Environment.getExternalStorageState().equals("mounted");
        if (bool) {
            localFile = Environment.getExternalStorageDirectory();
        }
        if (localFile != null) {
            return localFile.toString();
        }
        return "";
    }

    public static String extractFileName(String paramString) {
        int i = paramString.lastIndexOf("/");
        return i < 0 ? paramString : paramString.substring(i + 1, paramString.length());
    }

    public static String extractFileFolder(String paramString) {
        int i = paramString.length();
        int j = 0;
        int k = paramString.lastIndexOf('/');
        if ((k == -1) || (paramString.charAt(i - 1) == '/')) {
            return paramString;
        }
        if ((paramString.indexOf('/') == k) &&
                (paramString.charAt(j) == '/')) {
            return paramString.substring(0, k + 1);
        }
        return paramString.substring(0, k);
    }

    public static String convertStreamToString(InputStream paramInputStream)
            throws IOException {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramInputStream));

        StringBuilder localStringBuilder = new StringBuilder();
        String str;
        while ((str = localBufferedReader.readLine()) != null) {
            localStringBuilder.append(str).append("\n");
        }
        return localStringBuilder.toString();
    }

    public static void writeLinesToFile(String paramString1, String paramString2, List<String> paramList)
            throws IOException {
        BufferedOutputStream localBufferedOutputStream = null;
        try {
            File localFile = createFile(paramString1, paramString2);
            if (null == localFile) {
                throw new Exception("create file failed");
            }
            localBufferedOutputStream = new BufferedOutputStream(new FileOutputStream(localFile));
            for (int i = 0; i < paramList.size(); i++) {
                localBufferedOutputStream.write(((String) paramList.get(i)).getBytes());
                localBufferedOutputStream.write("\n".getBytes());
            }
        } catch (Exception localException) {
            Log.e("IOUtils", "writeLinesToFile failed!", localException);
        } finally {
            MiscUtils.safeClose(localBufferedOutputStream);
        }
    }

    public static List<String> readLinesFromFile(String paramString)
            throws IOException {
        ArrayList localArrayList = new ArrayList();
        File localFile = new File(paramString);
        if (!localFile.exists()) {
            return localArrayList;
        }
        BufferedReader localBufferedReader = null;
        try {
            localBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(localFile)));
            String str;
            while ((str = localBufferedReader.readLine()) != null) {
                localArrayList.add(str);
            }
        } catch (Exception localException) {
            Log.e("IOUtils", "readLinesFromFile failed!", localException);
        } finally {
            MiscUtils.safeClose(localBufferedReader);
        }
        return localArrayList;
    }

    public static File createFile(String paramString1, String paramString2) {
        if ((paramString1 == null) || (paramString2 == null)) {
            return null;
        }
        if (!MiscUtils.mkdirs(paramString1)) {
            Log.e("IOUtils", "create parent directory failed, " + paramString1);
            return null;
        }
        String str = paramString1 + "/" + paramString2;
        return new File(str);
    }

    public static boolean safeDeleteFile(File paramFile) {
        boolean bool = true;
        if (null != paramFile) {
            bool = paramFile.delete();
        }
        return bool;
    }

    public static boolean safeDeleteFile(String paramString) {
        if (MiscUtils.isNilOrNull(paramString)) {
            return false;
        }
        return safeDeleteFile(new File(paramString));
    }
}
