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

package com.example.android.popmovies.data.repository;

import android.content.ContentResolver;

import com.example.android.popmovies.data.NetworkingAPI.TmdbApi;
import com.squareup.sqlbrite.BriteContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by sheshloksamal on 20/03/16.
 *
 */

@Module
public class RepositoryModule {

    @Provides @Singleton
    public GenresRepository provideGenresRepository(TmdbApi tmdbApi, BriteContentResolver briteContentResolver,
                                                    ContentResolver contentResolver) {
        return new GenresRepositoryImpl(tmdbApi, briteContentResolver, contentResolver);
    }

    @Provides @Singleton
    public MoviesRepository provideMoviesRepository(TmdbApi tmdbApi, GenresRepository genresRepository,
                                                    BriteContentResolver briteContentResolver,
                                                    ContentResolver contentResolver) {
        return new MoviesRepositoryImpl(tmdbApi, genresRepository, briteContentResolver, contentResolver);
    }
}
