package com.example.android.popmovies.data;

import android.app.Application;

import com.example.android.popmovies.data.NetworkingAPI.TmdbApiModule;
import com.example.android.popmovies.data.provider.ProviderModule;
import com.example.android.popmovies.data.repository.RepositoryModule;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by sheshloksamal on 28/03/16.
 *
 */
@Module(
        includes = {
                TmdbApiModule.class,
                RepositoryModule.class,
                ProviderModule.class
        }
)
public class DataModule {

    private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024; // 50MB

    /*
        This OkHttpClient is used in TmdbApi and Glide. Setting a cache here not only keeps the
        observables alive in case of no internet connectivity but also optimizes the default
        Glide cache (from a max. size of 250 MB).
     */

    @Provides @Singleton OkHttpClient provideOkHttpClient(Application app) {
        return createOkHttpClient(app);
    }

    private OkHttpClient createOkHttpClient(Application app) {

        // Install an HTTP Cache in the application cache directory
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .cache(cache)
                .build();

    }

}
