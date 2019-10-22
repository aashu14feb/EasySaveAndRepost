package app.repostit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    public static String MyReceiver_ACTION = "com.MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "This is BroadcastReceiver Reporting", Toast.LENGTH_SHORT).show();
    }
}
