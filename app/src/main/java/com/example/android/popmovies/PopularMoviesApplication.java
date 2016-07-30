package com.example.android.popmovies;

import android.app.Application;
import android.content.Context;

import com.example.android.popmovies.data.DataModule;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import timber.log.Timber;

/**
 * Created by sheshloksamal on 28/03/16.
 *
 */
public class PopularMoviesApplication extends Application {

    private ApplicationComponent component;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .dataModule(new DataModule())
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        PopularMoviesApplication application = (PopularMoviesApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public ApplicationComponent getComponent(){
        return component;
    }
}
