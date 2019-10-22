package app.repostit.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import app.repostit.BuildConfig;

public class DownloadAndShareTask extends AsyncTask<String, Integer, Integer> {

    private Context mContext;
    String fileType;
    boolean mIsRepost;

    public DownloadAndShareTask(Context context, boolean isRepost) {
        mContext = context;
        mIsRepost = isRepost;
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
            String appName = ("RepostIt");
            String imageName = (params[1]);
            String videoUrl = (params[2]);
            boolean is_Video = Boolean.valueOf(params[3]);
            fileType = is_Video ? "video/*" : "image/*";
            url = is_Video ? new URL(videoUrl) : new URL(params[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentDateandTime = sdf.format(new Date());

            String format = is_Video ? ".mp4" : ".jpg";
            String targetFileName = imageName + format;//Change name and subname
            int lenghtOfFile = connection.getContentLength();


            //String PATH = mContext.getCacheDir() + "/" + appName + "/";;
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
            openIntent(outputFile);
            return 1;
        } catch (Exception e) {
            Log.e("DownloadAndShareTask", e.getMessage());
        }
        return -1;
    }

    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onPostExecute(Integer result) {

        cancel(true);
    }

    private void openIntent(File outputFile) {
        Intent share = new Intent(Intent.ACTION_SEND);
        if (mIsRepost) {
            share.setPackage("com.instagram.android");
        }
        share.setType(fileType);
        // File media = new File(outputPath);
        //Uri uri = Uri.fromFile(outputFile);
        Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", outputFile);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        Intent chooserIntent = Intent.createChooser(share, "Share");
        chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(chooserIntent);
    }

    private File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            // Error while creating file
        }
        return file;
    }
}