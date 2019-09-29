package com.example.aashish.instasaverapp.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import android.view.View;
import android.widget.Toast;

import com.example.aashish.instasaverapp.BuildConfig;
import com.example.aashish.instasaverapp.R;
import com.example.aashish.instasaverapp.entity.ImageData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Util {


    public static void setLightWhiteStatusBar(@NonNull Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.md_white_1000));
        }
    }

    public static void openDownloads(Context context){
        int PICKFILE_RESULT_CODE=1;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        context.startActivity(intent);
    }

    public static void openRateApppage(Context context) {

        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }

    public static void openDownloadFolder(Context context) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String PATH = Environment.getExternalStorageDirectory() + "/Pictures/" + "InstaSaver" + "/";
        Uri uri = Uri.parse(PATH);
        //intent.setDataAndType(uri, "resource/folder");
        intent.setDataAndType(uri, "*/*");
        context.startActivity(Intent.createChooser(intent, "Open folder"));


    }

    public static void openInstagram(Context context) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (launchIntent != null) {
            try {
                context.startActivity(launchIntent);
            } catch (ActivityNotFoundException ex) // in case Instagram not installed in your device
            {
                ex.printStackTrace();
                Toast.makeText(context, R.string.error_open_instagram, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean isPackageInstalled(String packageName, PackageManager packageManager) {

        boolean found = true;
        try {
            packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    public static void postOnInstagram(final Context context, final String surl, final String name, String desc, final boolean isVideo, final String videourl) {

        new DownloadAndShareTask(context,true).execute(surl, name, videourl,String.valueOf(isVideo));

        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {


                String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + "InstaSaver" + "/";
                String format = isVideo ? ".mp4" : ".png";
                String type = isVideo ? "video/*" : "image/*";
                String url = isVideo ? videourl : surl;
                String mediaPath = (PATH +name + format);

                try {

                   /* Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setPackage("com.instagram.android");
                    URL furl = new URL(url);
                    Bitmap image = BitmapFactory.decodeStream(furl.openConnection().getInputStream());
                    String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), image, "title", null);
                    Uri bitmapUri = Uri.parse(bitmapPath);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    shareIntent.setType(type);
                    context.startActivity(shareIntent);*/

                    /*Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType(type);
                    File media = new File(mediaPath);
                    Uri uri = Uri.fromFile(media);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    context.startActivity(Intent.createChooser(share, "Share to"));*/

                } catch (Exception e) {
                    System.out.println(e);
                }

                return null;
            }

        }.execute();
    }

    public static void shareImageTask(final Context context, final String surl, final String name, String desc, final boolean isVideo, final String videourl) {
        //ShareContent(context,name,desc ,isVideo,videourl);
        new DownloadAndShareTask(context,false).execute(surl, name, videourl,String.valueOf(isVideo));
    }

    public static void openFolder(final Context context)
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + context.getString(R.string.app_folder_name) + "/";
        Uri uri = Uri.parse(PATH);
        intent.setDataAndType(uri, "image/*");
        context.startActivity(Intent.createChooser(intent, "Open folder"));
    }

    public static void ShareContent(final Context context, final String name, String desc, final boolean isVideo, final String videourl) {

        String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/" + "InstaSaver" + "/";
        String format = isVideo ? ".mp4" : ".png";
        String type = isVideo ? "video/*" : "image/*";
        File mediaPath = new File(PATH ,name + format);

        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            //Uri uri = Uri.fromFile(mediaPath);
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider", mediaPath);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType(type);
            context.startActivity(Intent.createChooser(shareIntent, "Share to"));

        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public static void shareImageWithBitmapUri(Context context, Uri bitmapUri) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("image/*");
        i.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        context.startActivity(Intent.createChooser(i, "Share Image"));
    }

    public static boolean isFreshLaunch(Context context) {
        if (context != null) {
            SharedPreferences sharedPref = context.getSharedPreferences("AppInfo", Context.MODE_PRIVATE);
            if (sharedPref != null) {
                boolean isFirstlaunch = sharedPref.getBoolean("isFreshLaunch", true);
                if (isFirstlaunch) {
                    sharedPref.edit().putBoolean("isFreshLaunch", false).commit();
                }
                return isFirstlaunch;
            }
        }
        return true;
    }

    public static ImageData parseJsonAndGetUrl(Context context, String json){

        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONObject graphql = jsonObj.getJSONObject("graphql");
            JSONObject shortcode_media = graphql.optJSONObject("shortcode_media");

            String url = shortcode_media.getString("display_url");
            String imageName = shortcode_media.getString("id");
            JSONObject owner = shortcode_media.optJSONObject("owner");
            JSONObject dimensions = shortcode_media.optJSONObject("dimensions");
            int width = dimensions.getInt("width");
            int height = dimensions.getInt("height");

            Boolean is_video = shortcode_media.getBoolean("is_video");
            String video_url = "";
            if (is_video)
                video_url = shortcode_media.getString("video_url");

            String profileUrl = owner.getString("profile_pic_url");
            String profileName = owner.getString("username");
            String profileid = owner.getString("id");


            JSONObject edge_media_to_caption = shortcode_media.optJSONObject("edge_media_to_caption");
            JSONArray edges = edge_media_to_caption.optJSONArray("edges");
            String desc = "";
            if (edges.length() > 0) {
                JSONObject zero = (JSONObject) edges.get(0);
                JSONObject node = zero.optJSONObject("node");
                desc = node.getString("text");
            }

            int likes = shortcode_media.getJSONObject("edge_media_preview_like").getInt("count");

            ImageData imagedata = new ImageData(imageName, desc, url, is_video, video_url, width, height, profileid, profileName, profileUrl, likes);
            return imagedata;


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Permission denied.Please try different post", Toast.LENGTH_SHORT).show();
        }

        return null;

    }


}
