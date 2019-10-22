package app.repostit.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import app.repostit.BuildConfig;
import app.repostit.R;
import app.repostit.entity.ImageData;
import com.networks.NetworkListener;
import com.networks.NetworkRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Util {


    public static void setLightWhiteStatusBar(@NonNull Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            activity.getWindow().getDecorView().setSystemUiVisibility(flags);
            activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.md_white_1000));
        }
    }

    public static void openDownloads(Context context) {
        int PICKFILE_RESULT_CODE = 1;
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
        }else{
            Toast.makeText(context, R.string.error_open_instagram, Toast.LENGTH_SHORT).show();
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

        new DownloadAndShareTask(context, true).execute(surl, name, videourl, String.valueOf(isVideo));
    }

    public static void shareImageTask(final Context context, final String surl, final String name, String desc, final boolean isVideo, final String videourl) {
        //ShareContent(context,name,desc ,isVideo,videourl);
        new DownloadAndShareTask(context, false).execute(surl, name, videourl, String.valueOf(isVideo));
    }

    public static void openFolder(final Context context) {
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
        File mediaPath = new File(PATH, name + format);

        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            //Uri uri = Uri.fromFile(mediaPath);
            Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", mediaPath);
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
            }else{
                sharedPref.edit().putBoolean("isFreshLaunch", false).commit();
            }
        }
        return true;
    }

    public static ImageData parseJsonAndGetUrl(Context context, String json) {

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

    public static void getImageData(Context context, String rawUrl, NetworkListener networkListener) {
        rawUrl = rawUrl + "&__a=1;";
        Map params = new HashMap<String, String>();
        NetworkRequest.INSTANCE.doGet(rawUrl, params, networkListener);
    }

    public static boolean checkPermission(Activity activity, int code) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    code);

            return false;
        } else {

        }

        return true;

    }


    public static void shareApp(Context context) {

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
            String shareMessage= "\nHi, I am using " +  context.getString(R.string.app_name) + " app\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            context.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }
}
