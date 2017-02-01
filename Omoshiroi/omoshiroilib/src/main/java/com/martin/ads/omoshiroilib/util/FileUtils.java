package com.martin.ads.omoshiroilib.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ads on 2016/11/28.
 */

public class FileUtils {
    public static void copyFileFromAssets(Context context, String outputPath, String fileName, String inputPath){
        File file = new File(outputPath, fileName);
        try
        {
            if (!file.exists()) {
                File fileParentDir = file.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                file.createNewFile();
            }else return;
            InputStream in = context.getResources().getAssets().open(inputPath);
            OutputStream out = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
