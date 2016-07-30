package com.example.android.popmovies.data.NetworkingAPI;

import com.example.android.popmovies.utilities.Constants;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */

@Module
public class TmdbApiModule {

    @Provides @Singleton
    @Named("TmdbApi") OkHttpClient provideTmdbApiClient(OkHttpClient client) {
        return client.newBuilder().build();
    }

    @Provides @Singleton
    Retrofit provideRetrofit(@Named("TmdbApi") OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    @Provides @Singleton
    TmdbApi provideTmdbApi(Retrofit retrofit) {
        return retrofit.create(TmdbApi.class);
    }

}
