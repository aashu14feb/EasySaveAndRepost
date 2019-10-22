package app.repostit.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.networks.NetworkListener;
import com.networks.NetworkResponse;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import app.repostit.AppActivity;
import app.repostit.R;
import app.repostit.entity.ImageData;
import app.repostit.utils.Extras;
import app.repostit.utils.Util;

public class ClipboardService extends Service {

    public ClipboardManager mCM;
    Context context;
    int start;
    LocalBinder mBinder;
    public final static String MY_ACTION = "MY_ACTION";
    public final static String TAG = "RepostIt";
    public final static String filter = "https://www.instagram.com/";

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mBinder = new LocalBinder();
        Log.i(TAG, "Service onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        mCM = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        mCM.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {

                String newClip = mCM.getText().toString();
                Log.i(TAG, "onStartCommand Clip" + newClip);

                if (newClip.startsWith(filter)) {
                    Util.getImageData(context, newClip, new NetworkListener() {
                        @Override
                        public void onRequest() {

                        }

                        @Override
                        public void onResponse(@NotNull NetworkResponse response) {
                            if (!response.isSucceed()) {
                                ImageData imageData = Util.parseJsonAndGetUrl(context, response.getText());
                                if (imageData != null) {
                                    Intent intent = new Intent();
                                    intent.setAction(MY_ACTION);
                                    intent.putExtra(Extras.LINK_RAW_DATA, response.getText());
                                    context.sendBroadcast(intent);
                                    ImageData.setImageLastDownload(imageData);
                                    new loadImage(context, imageData).execute();
                                }

                            }
                        }
                    });
                }

            }
        });
        return START_STICKY;
    }

    private class loadImage extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        ImageData imageData;

        public loadImage(Context context, ImageData imageData) {
            super();
            this.ctx = context;
            this.imageData = imageData;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {

                URL url = new URL(imageData.url);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            createNotification(result, imageData);
        }
    }

    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "Service onTaskRemoved");

        stopSelf();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void createNotification(Bitmap bitmap, ImageData data) {

        int REQUEST_CODE = 1000;

        Intent intent = new Intent(context, AppActivity.class);
        intent.putExtra(Extras.IMAGE_JSON, data);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("notification");

        Random random = new Random();
        int requestCode = random.nextInt();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getString(R.string.msg_content_downloaded))
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            //notificationChannel.enableVibration(false);
            //notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        notificationManager.notify(requestCode, notificationBuilder.build());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public ClipboardService getServerInstance() {
            return ClipboardService.this;
        }

    }
}