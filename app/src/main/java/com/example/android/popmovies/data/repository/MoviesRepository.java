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
