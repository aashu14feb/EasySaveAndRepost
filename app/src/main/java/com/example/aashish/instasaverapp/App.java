package com.example.aashish.instasaverapp;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.aashish.instasaverapp.database.AppDataBase;
import com.example.aashish.instasaverapp.entity.ImageData;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("InstaSaver", "Application");

        DatabaseConfig appDatabaseConfig = new DatabaseConfig.Builder(AppDataBase.class)
                .addModelClasses(ImageData.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                .addDatabaseConfigs(appDatabaseConfig)
                .build());

        ImageData.init();

        // Make sure we use vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}