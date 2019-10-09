package com.example.aashish.instasaverapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.example.aashish.instasaverapp.MainActivity;
import com.example.aashish.instasaverapp.R;
import com.example.aashish.instasaverapp.entity.ImageData;
import com.example.aashish.instasaverapp.utils.Extras;
import com.example.aashish.instasaverapp.utils.Util;
import com.networks.NetworkListener;
import com.networks.NetworkRequest;
import com.networks.NetworkResponse;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ClipboardService extends Service {

    public ClipboardManager mCM;
    Context context;
    int start;
    LocalBinder mBinder;
    public final static String MY_ACTION = "MY_ACTION";
    public final static String TAG = "InstaSaver";
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
                                ImageData imageData = Util.parseJsonAndGetUrl(context,response.getText());
                                if (imageData != null) {
                                    Intent intent = new Intent();
                                    intent.setAction(MY_ACTION);
                                    intent.putExtra(Extras.LINK_RAW_DATA, response.getText());
                                    context.sendBroadcast(intent);
                                    ImageData.setImageLastDownload(imageData);
                                }
                                createNotification(response.getText());
                            }
                        }
                    });
                }

            }
        });
        return START_STICKY;
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

    public void createNotification(String data) {

        int REQUEST_CODE = 1000;

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Extras.IMAGE_JSON, data);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("notification");

        Random random = new Random();
        int requestCode = random.nextInt();
        PendingIntent pendingIntent = PendingIntent.getActivity(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getString(R.string.msg_content_downloaded))
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