package com.example.android.popmovies.data.repository;

import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.model.MovieReview;
import com.example.android.popmovies.data.model.MovieTrailer;

import java.util.List;

import rx.Observable;

/**
 * Created by sheshloksamal on 20/03/16.
 * An interface which activities and fragments can use to get/store data as needed without
 * understanding how the data is retrieved/stored
 *
 * @see MoviesRepositoryImpl
 */
public interface MoviesRepository {

    Observable<List<MovieItem>> getMoviesFromApiWithSavedDataAndGenreNames(String sortPreference, int page);

    Observable<List<MovieReview>> getMovieReviews(String movieId);

    Observable<List<MovieTrailer>> getMovieTrailers(String movieId);

    /* Observables for retrieving saved movies with genre names and options for
    * saving and deleting movies from the database */
    Observable<List<MovieItem>> getSavedMoviesWithGenreNames();

    void saveMovie(MovieItem movieItem);

    void deleteMovie(MovieItem movieItem);



}
