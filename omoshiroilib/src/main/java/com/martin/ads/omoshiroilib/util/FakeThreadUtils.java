package com.martin.ads.omoshiroilib.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.martin.ads.omoshiroilib.debug.removeit.GlobalConfig;

import java.io.File;
import java.nio.IntBuffer;

/**
 * Created by Ads on 2017/2/13.
 */

public class FakeThreadUtils {
    public static void postTask(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static class SaveFileTask extends AsyncTask<Void, Integer, Boolean> {
        private String outputPath,fileName,inputPath;
        private FileUtils.FileSavedCallback fileSavedCallback;

        public SaveFileTask(String outputPath, String fileName, String inputPath, FileUtils.FileSavedCallback fileSavedCallback) {
            this.outputPath = outputPath;
            this.fileName = fileName;
            this.inputPath = inputPath;
            this.fileSavedCallback = fileSavedCallback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            FileUtils.copyFileFromTo(
                    outputPath,fileName,inputPath
            );
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            //Toast.makeText(context,"ScreenShot is saved to "+filePath, Toast.LENGTH_LONG).show();
            fileSavedCallback.onFileSaved(new File(outputPath,fileName).getAbsolutePath());
        }
    }
}
