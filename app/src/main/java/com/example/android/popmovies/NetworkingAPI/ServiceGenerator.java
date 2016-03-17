package com.example.android.popmovies.NetworkingAPI;

import com.example.android.popmovies.utilities.Constants;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class ServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            // Todo: Remove later. Only for debug purposes
            .addNetworkInterceptor(new StethoInterceptor());

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(Constants.API_BASE_URL)
            .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
            .callbackExecutor(new ErrorHandlingCallAdapter.MainThreadExecutor())
            .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService (Class <S> serviceClass){
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }
}
