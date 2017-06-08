package com.martin.ads.omoshiroilib.flyu.openglfilter.gpuimage.base;

import android.util.Log;
import android.util.Pair;

import com.martin.ads.omoshiroilib.flyu.sdk.utils.IOUtils;
import com.martin.ads.omoshiroilib.flyu.sdk.utils.MiscUtils;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Ads on 2017/6/6.
 */

public class MResFileReaderBase {
    static final String TAG = "MergeResFileReader";
    String mIdxFilePath;
    String mDataFilePath;
    protected Map<String, Pair<Integer, Integer>> mStartPosMap;
    protected ByteBuffer mDataBuffer;

    public MResFileReaderBase(String var1, String var2) {
        this.mIdxFilePath = var1;
        this.mDataFilePath = var2;
    }

    public void init() throws IOException {
        this.mStartPosMap = parseIndexFile(this.mIdxFilePath);
        File var1 = new File(this.mDataFilePath);
        this.mDataBuffer = ByteBuffer.allocateDirect((int)var1.length());
        FileInputStream var2 = new FileInputStream(var1);
        byte[] var3 = new byte[2048];
        boolean var5 = false;

        try {
            int var4;
            while((var4 = var2.read(var3)) != -1) {
                this.mDataBuffer.put(var3, 0, var4);
            }

            var5 = true;
        } catch (IOException var10) {
            Log.e("MergeResFileReader", "read data file failed, " + var10.getMessage());
        } finally {
            MiscUtils.safeClose(var2);
        }

        if(!var5) {
            throw new IOException("parse data file failed!");
        }
    }

    public static Map<String, Pair<Integer, Integer>> parseIndexFile(String var0) throws IOException {
        String var1 = IOUtils.convertStreamToString(new FileInputStream(new File(var0)));
        HashMap var2 = new HashMap();
        String[] var3 = var1.split(";");

        for(int var4 = 0; var4 < var3.length; ++var4) {
            if(!MiscUtils.isNilOrNull(var3[var4])) {
                String[] var5 = var3[var4].split(":");
                if(var5.length == 3) {
                    int var6 = MiscUtils.safeParseInt(var5[1], -1);
                    int var7 = MiscUtils.safeParseInt(var5[2], -1);
                    if(-1 == var6 || -1 == var7) {
                        throw new IOException("can\'t parse pos or len for " + var3[var4]);
                    }

                    var2.put(var5[0], new Pair(Integer.valueOf(var6), Integer.valueOf(var7)));
                }
            }
        }

        return var2;
    }

    public static boolean mergeResToFile(File var0, List<String> var1) {
        long var2 = System.currentTimeMillis();
        String var4 = "fres_" + var2 + ".idx";
        String var5 = "fres_" + var2 + ".dat";
        boolean var6 = false;
        FileOutputStream var7 = null;
        FileInputStream var8 = null;
        ArrayList var9 = new ArrayList();

        int var34;
        try {
            var7 = new FileOutputStream(new File(var0, var5));

            for(int var10 = 0; var10 < 16; ++var10) {
                var7.write(0);
            }

            byte[] var32 = new byte[8192];
            boolean var11 = false;

            for(Iterator var12 = var1.iterator(); var12.hasNext(); var8 = null) {
                String var13 = (String)var12.next();
                File var14 = new File(var0, var13);
                var9.add(Integer.valueOf((int)var14.length()));
                var8 = new FileInputStream(var14);

                while((var34 = var8.read(var32)) != -1) {
                    var7.write(var32, 0, var34);
                }

                MiscUtils.safeClose(var8);
            }

            var6 = true;
        } catch (FileNotFoundException var29) {
            Log.e("MergeResFileReader", "can\'t find data file, errMsg: " + var29.getMessage());
        } catch (IOException var30) {
            Log.e("MergeResFileReader", "write file failed, errMsg: " + var30.getMessage());
        } finally {
            MiscUtils.safeClose(var7);
            MiscUtils.safeClose(var8);
        }

        if(var6 && var9.size() == var1.size()) {
            StringBuilder var33 = new StringBuilder();
            var34 = 16;

            for(int var35 = 0; var35 < var1.size(); ++var35) {
                var33.append((String)var1.get(var35)).append(':').append(var34).append(':').append(var9.get(var35)).append(';');
                var34 += ((Integer)var9.get(var35)).intValue();
            }

            FileOutputStream var36 = null;
            var6 = false;

            try {
                var36 = new FileOutputStream(new File(var0, var4));
                var36.write(var33.toString().getBytes("UTF-8"));
                var6 = true;
            } catch (Exception var27) {
                Log.e("MergeResFileReader", "writeLinesToFile failed!", var27);
            } finally {
                MiscUtils.safeClose(var36);
            }

            Iterator var37 = var1.iterator();

            while(var37.hasNext()) {
                String var38 = (String)var37.next();
                IOUtils.safeDeleteFile(new File(var0, var38));
            }

            return var6;
        } else {
            return false;
        }
    }

    public static Map<String, ArrayList<MResFileReaderBase.FileItem>> getFileListFromZip(InputStream var0) throws IOException {
        HashMap var1 = new HashMap();
        ZipInputStream var2 = new ZipInputStream(new BufferedInputStream(var0));

        try {
            byte[] var4 = new byte[8192];

            ZipEntry var3;
            while((var3 = var2.getNextEntry()) != null) {
                if(!var3.isDirectory() && !var3.getName().endsWith(".DS_Store") && !var3.getName().contains("__MACOSX") && !IOUtils.extractFileName(var3.getName()).startsWith(".") && var3.getName().endsWith(".png")) {
                    String var5 = IOUtils.extractFileFolder(var3.getName());
                    ArrayList var6 = (ArrayList)var1.get(var5);
                    if(null == var6) {
                        var6 = new ArrayList();
                        var1.put(var5, var6);
                    }

                    int var7;
                    int var8;
                    for(var7 = 0; (var8 = var2.read(var4)) != -1; var7 += var8) {
                        ;
                    }

                    var6.add(new MResFileReaderBase.FileItem(var3.getName(), (long)var7));
                }
            }
        } finally {
            var2.close();
        }

        return var1;
    }

    public static void unzipToAFile(InputStream var0, File var1, Map<String, ArrayList<MResFileReaderBase.FileItem>> var2) throws IOException {
        HashMap var3 = new HashMap();
        HashMap var4 = new HashMap();
        long var5 = System.currentTimeMillis();
        Iterator var7 = var2.entrySet().iterator();

        int var9;
        while(var7.hasNext()) {
            Map.Entry var8 = (Map.Entry)var7.next();
            Collections.sort((List)var8.getValue(), new FilterItemComparator());
            MiscUtils.mkdirs(var1 + "/" + (String)var8.getKey());
            var9 = 16;
            HashMap var10 = new HashMap();
            HashMap var11 = new HashMap();

            MResFileReaderBase.FileItem var13;
            for(Iterator var12 = ((ArrayList)var8.getValue()).iterator(); var12.hasNext(); var9 += (int)var13.fileSize) {
                var13 = (MResFileReaderBase.FileItem)var12.next();
                String var14 = IOUtils.extractFileName(var13.fileName);
                var10.put(var14, Integer.valueOf(var9));
                var11.put(var14, Integer.valueOf((int)var13.fileSize));
            }

            var4.put(var8.getKey(), var10);
            StringBuilder var46 = new StringBuilder();
            Iterator var49 = var10.entrySet().iterator();

            while(var49.hasNext()) {
                Map.Entry var51 = (Map.Entry)var49.next();
                var46.append((String)var51.getKey()).append(':').append(var51.getValue()).append(':').append(var11.get(var51.getKey())).append(';');
            }

            boolean var50 = false;
            FileOutputStream var52 = null;

            File var15;
            try {
                var15 = new File(var1 + "/" + (String)var8.getKey(), "fres_" + var5 + ".idx");
                var52 = new FileOutputStream(var15);
                var52.write(var46.toString().getBytes("UTF-8"));
                var50 = true;
            } catch (Exception var35) {
                Log.e("MergeResFileReader", "writeLinesToFile failed!", var35);
            } finally {
                MiscUtils.safeClose(var52);
            }

            if(!var50) {
                throw new IOException("write index file failed!");
            }

            var15 = new File(var1 + "/" + (String)var8.getKey(), "fres_" + var5 + ".dat");
            RandomAccessFile var16 = new RandomAccessFile(var15, "rw");

            for(int var17 = 0; var17 < 16; ++var17) {
                var16.write(0);
            }

            var3.put(var8.getKey(), var16);
        }

        ZipInputStream var39 = new ZipInputStream(new BufferedInputStream(var0));
        boolean var30 = false;

        try {
            var30 = true;
            byte[] var42 = new byte[8192];

            label545:
            while(true) {
                while(true) {
                    ZipEntry var40;
                    do {
                        do {
                            do {
                                do {
                                    if((var40 = var39.getNextEntry()) == null) {
                                        var30 = false;
                                        break label545;
                                    }
                                } while(var40.isDirectory());
                            } while(var40.getName().endsWith(".DS_Store"));
                        } while(var40.getName().contains("__MACOSX"));
                    } while(IOUtils.extractFileName(var40.getName()).startsWith("."));

                    if(var40.getName().endsWith(".png")) {
                        String var45 = IOUtils.extractFileFolder(var40.getName());
                        RandomAccessFile var48 = (RandomAccessFile)var3.get(var45);
                        int var54 = ((Integer)((Map)var4.get(var45)).get(IOUtils.extractFileName(var40.getName()))).intValue();
                        var48.seek((long)var54);

                        while((var9 = var39.read(var42)) != -1) {
                            var48.write(var42, 0, var9);
                        }
                    } else {
                        File var44 = new File(var1, var40.getName());
                        File var47 = var40.isDirectory()?var44:var44.getParentFile();
                        if(!var47.isDirectory() && !var47.mkdirs()) {
                            throw new FileNotFoundException("Failed to ensure directory: " + var47.getAbsolutePath());
                        }

                        FileOutputStream var53 = new FileOutputStream(var44);

                        try {
                            while((var9 = var39.read(var42)) != -1) {
                                var53.write(var42, 0, var9);
                            }
                        } finally {
                            var53.close();
                        }
                    }
                }
            }
        } finally {
            if(var30) {
                var39.close();
                Iterator var20 = var3.entrySet().iterator();

                while(var20.hasNext()) {
                    Map.Entry var21 = (Map.Entry)var20.next();
                    MiscUtils.safeClose((Closeable)var21.getValue());
                }

            }
        }

        var39.close();
        Iterator var41 = var3.entrySet().iterator();

        while(var41.hasNext()) {
            Map.Entry var43 = (Map.Entry)var41.next();
            MiscUtils.safeClose((Closeable)var43.getValue());
        }

    }

    public static Pair<String, String> tryGetMergeFile(String var0) {
        String var1 = null;
        String var2 = null;
        File var3 = new File(var0);
        String[] var4 = var3.list();
        if(null == var4) {
            return null;
        } else {
            for(int var5 = 0; var5 < var4.length; ++var5) {
                if(var4[var5].startsWith("fres_")) {
                    if(var4[var5].endsWith(".idx")) {
                        var1 = var4[var5];
                    } else if(var4[var5].endsWith(".dat")) {
                        var2 = var4[var5];
                    }
                }
            }

            if(!MiscUtils.isNilOrNull(var1) && !MiscUtils.isNilOrNull(var2)) {
                return new Pair(var1, var2);
            } else {
                return null;
            }
        }
    }

    public static class FileItem {
        String fileName;
        long fileSize;

        public FileItem(String var1, long var2) {
            this.fileName = var1;
            this.fileSize = var2;
        }
    }
}
