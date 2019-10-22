package app.repostit;

import android.app.Application;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import app.repostit.database.AppDataBase;
import app.repostit.entity.ImageData;
import com.reactiveandroid.ReActiveAndroid;
import com.reactiveandroid.ReActiveConfig;
import com.reactiveandroid.internal.database.DatabaseConfig;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("RepostIt", "Application");

        DatabaseConfig appDatabaseConfig = new DatabaseConfig.Builder(AppDataBase.class)
                .addModelClasses(ImageData.class)
                .build();

        ReActiveAndroid.init(new ReActiveConfig.Builder(this)
                .addDatabaseConfigs(appDatabaseConfig)
                .build());

        ImageData.loadFromDB();

        // Make sure we use vector drawables
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

}