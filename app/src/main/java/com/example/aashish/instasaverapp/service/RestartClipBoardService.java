package com.example.aashish.instasaverapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartClipBoardService extends BroadcastReceiver {
    public RestartClipBoardService() {

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("InstaSaver", "RestartClipBoardService onReceive");
        context.startService(new Intent(context,ClipboardService.class));
    }
}
