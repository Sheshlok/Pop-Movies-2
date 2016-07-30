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

package com.example.android.popmovies.utilities.glide;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.ActivityManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.module.GlideModule;
import com.example.android.popmovies.PopularMoviesApplication;
import com.example.android.popmovies.utilities.ImageUtils;

import java.io.InputStream;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 19/03/16.
 *
 */
public final class GlideSetup implements GlideModule {

    @Inject OkHttpClient mOkHttpClient;

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Prefer higher quality images unless we are on a low RAM device
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        builder.setDecodeFormat(ActivityManagerCompat.isLowRamDevice(activityManager) ?
                DecodeFormat.PREFER_RGB_565: DecodeFormat.PREFER_ARGB_8888);
    }
    @Override
    public void registerComponents(Context context, Glide glide) {
        ((PopularMoviesApplication) context.getApplicationContext()).getComponent().inject(this);

        glide.register(String.class, InputStream.class, new ImageLoader.Factory());
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));

    }

    private static class ImageLoader extends BaseGlideUrlLoader<String> {

        private Context mContext;

        public ImageLoader(Context context){
            super(context);
            mContext = context;
        }

        @Override
        protected String getUrl(String imagePath, int width, int height){
            String url = ImageUtils.getAdaptivePosterPathURL(mContext, imagePath, width);
            Timber.tag("Glide").v("Loading image: " + url);
            return url;
        }

        public static class Factory implements ModelLoaderFactory<String, InputStream> {
            @Override
            public StreamModelLoader<String> build(Context context, GenericLoaderFactory factories){
                return new ImageLoader(context);
            }

            @Override
            public void teardown() {/** ignore */}
        }
    }
}
