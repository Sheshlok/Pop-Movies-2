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
