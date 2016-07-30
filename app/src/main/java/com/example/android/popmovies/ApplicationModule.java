package com.example.android.popmovies;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sheshloksamal on 28/03/16.
 *
 */

@Module
public class ApplicationModule {

    private final PopularMoviesApplication application;

    public ApplicationModule(PopularMoviesApplication application){
        this.application = application;
    }

    @Provides @Singleton
    Application provideApplication() {
        return this.application;
    }
}
