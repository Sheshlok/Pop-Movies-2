/*
 *  Copyright (C) 2016 Sheshlok Samal
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package com.example.android.popmovies;

import android.app.Application;

import com.example.android.popmovies.data.DataModule;

import timber.log.Timber;

/**
 * Created by sheshloksamal on 28/03/16.
 *
 */
public class PopularMoviesApplication extends Application {

    private ApplicationComponent component;
//    private RefWatcher refWatcher;

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

//        refWatcher = LeakCanary.install(this);
    }

//    public static RefWatcher getRefWatcher(Context context) {
//        PopularMoviesApplication application = (PopularMoviesApplication) context.getApplicationContext();
//        return application.refWatcher;
//    }

    public ApplicationComponent getComponent(){
        return component;
    }
}
