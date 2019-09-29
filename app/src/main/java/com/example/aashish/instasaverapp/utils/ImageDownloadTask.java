package com.example.aashish.instasaverapp.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.example.aashish.instasaverapp.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageDownloadTask extends AsyncTask<String, Integer, Integer> {

    String strFolderName;
    private Context mContext;

    public ImageDownloadTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
        int count;
        try {


            URL url = new URL(params[0]);
            String appName = mContext.getString(R.string.app_folder_name);
            String imageName = (params[1]);
            String videoUrl = (params[2]);
            boolean is_Video = Boolean.valueOf(params[3]);
            url = is_Video ? new URL(videoUrl) : new URL(params[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());

            String format = is_Video ? ".mp4" : ".jpg";
            String targetFileName = imageName + format;//Change name and subname
            int lenghtOfFile = conexion.getContentLength();
            //String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + "TM_Store" + "/"  + prodName + "/" ;
            String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + appName + "/";
            File folder = new File(PATH);
            if (!folder.exists()) {
                folder.mkdirs();//If there is no folder it will be created.
            }

            InputStream input = new BufferedInputStream(url.openStream());
            File outputFile = new File(folder, targetFileName);


            FileOutputStream output = new FileOutputStream(outputFile);
            byte data[] = new byte[1024];
            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress((int) (total * 100 / lenghtOfFile));
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();
            return 1;
        } catch (Exception e) {
            //Log.e("Tm Store ImageDownloadTask",e.getMessage());
            //Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return -1;
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer result) {

        if (mContext != null && result == 1) {
            Toast.makeText(mContext, "ImageData download success!", Toast.LENGTH_SHORT).show();
        }
        cancel(true);
    }
}