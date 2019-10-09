package com.example.aashish.instasaverapp;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.aashish.instasaverapp.entity.ImageData;
import com.example.aashish.instasaverapp.service.ClipboardService;
import com.example.aashish.instasaverapp.service.FloatingWidgetService;
import com.example.aashish.instasaverapp.ui.fragment.FeedActivity;
import com.example.aashish.instasaverapp.utils.Extras;
import com.example.aashish.instasaverapp.utils.Util;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.snackbar.Snackbar;
import com.networks.NetworkListener;
import com.networks.NetworkResponse;

import org.jetbrains.annotations.NotNull;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

public class AppActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String filter = "https://www.instagram.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        Toolbar appbar = findViewById(R.id.toolbar);
        setSupportActionBar(appbar);

        MobileAds.initialize(this, getString(R.string.ad_mob_appId));

        initWidgets();

        BottomAppBar bottomNavigationView = (BottomAppBar) findViewById(R.id.bottomAppBar);
        bottomNavigationView.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_paste:
                        checkAndLoadCopiedLink(bottomNavigationView);
                        break;
                }
                return false;
            }
        });

        bottomNavigationView.replaceMenu(R.menu.menu_bottom_navigation);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        addReplaceHomeFragment();
    }

    private void initWidgets() {

        Util.setLightWhiteStatusBar(this);
        findViewById(R.id.btn_menu).setOnClickListener(this);
        findViewById(R.id.icon_heart).setOnClickListener(this);
        findViewById(R.id.fab_instagram).setOnClickListener(this);
        findViewById(R.id.icon_setting).setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void addReplaceHomeFragment() {

        FeedActivity fragmentFeed = FeedActivity.newInstance();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragmentFeed, fragmentFeed.getClass().getName());
        ft.commit();
    }

    void startIntroActivity() {

        if (Util.isFreshLaunch(this)) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_menu:
                break;
            case R.id.icon_heart:
                Util.openRateApppage(this);
                break;
            case R.id.fab_instagram:
                Util.openInstagram(this);
                break;
            case R.id.icon_setting:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                        @Override
                        public void permissionGranted() {
                            openSettingActivity();
                        }

                        @Override
                        public void permissionRefused() {

                        }
                    });
                else
                    openSettingActivity();
                break;
        }

    }

    void openSettingActivity() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_dialog_title);
        builder.setMessage(R.string.alert_dialog_msg);

        builder.setPositiveButton(getString(R.string.open_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createFloatingWidget();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    void createFloatingWidget() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 0);
        }
    }

    BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getExtras() != null) {
                refreshAdapter(intent.getExtras().getString(Extras.LINK_RAW_DATA));
            }
        }
    };

    void refreshAdapter(String rawdata) {
        if (!rawdata.equals("")) {
            ImageData imgData = Util.parseJsonAndGetUrl(this, rawdata);
            addReplaceHomeFragment();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isMyServiceRunning(ClipboardService.class)) {
            startService(new Intent(this, ClipboardService.class));
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ClipboardService.MY_ACTION);
        registerReceiver(broadCastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        addReplaceHomeFragment();
        startFloatingWidgetService();
    }

    private void startFloatingWidgetService() {
        startService(new Intent(this, FloatingWidgetService.class));
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (broadCastReceiver != null)
            unregisterReceiver(broadCastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_help:
                startActivity(new Intent(this, IntroActivity.class));
                return false;

            case R.id.nav_privacy_policy:
                return false;

            case R.id.nav_rate:
                Util.openRateApppage(this);
                return false;

            case R.id.nav_share:

                return false;
        }

        return false;


    }

    void checkAndLoadCopiedLink(View v) {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        String newClip = "";

        if (!(clipboard.hasPrimaryClip())) {

            Snackbar.make(v, getString(R.string.empty_message), Snackbar.LENGTH_SHORT);
        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {

            Snackbar.make(v, getString(R.string.empty_message), Snackbar.LENGTH_SHORT);

        } else {

            //since the clipboard contains plain text.
            ClipData.Item data = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            newClip = data.getText().toString();

            if (newClip.startsWith(filter)) {
                Util.getImageData(AppActivity.this, newClip, new NetworkListener() {
                    @Override
                    public void onRequest() {
                        Snackbar.make(v, getString(R.string.checking), Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(@NotNull NetworkResponse response) {
                        if (!response.isSucceed()) {
                            ImageData imageData = Util.parseJsonAndGetUrl(AppActivity.this, response.getText());
                            if (imageData != null) {
                                ImageData.setImageLastDownload(imageData);
                                addReplaceHomeFragment();
                                Snackbar.make(v, getString(R.string.success), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }
    }
}
