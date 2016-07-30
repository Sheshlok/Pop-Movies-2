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

package com.example.android.popmovies.data.NetworkingAPI;

import com.example.android.popmovies.BuildConfig;
import com.example.android.popmovies.data.model.Genre;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.model.MovieReview;
import com.example.android.popmovies.data.model.MovieTrailer;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public interface TmdbApi {

    /** Send a GET request for genres */

    @GET("genre/movie/list?api_key=" + BuildConfig.TMDB_API_KEY)
    Observable<Genre.Response> getGenres();

    /** Send a GET request for movieList */

    @GET("discover/movie?api_key=" + BuildConfig.TMDB_API_KEY)
    Observable<MovieItem.Response> getMovies(
            @Query("sort_by") String sortPreference,
            @Query("page") int page
    );

    /** Send a GET request for movieReviews for a particular movie */

    @GET("movie/{id}/reviews?api_key=" + BuildConfig.TMDB_API_KEY)
   Observable<MovieReview.Response> getMovieReviews(
            @Path("id") String movieId
    );

    /** Send a GET request for movieTrailers for a particular movie */

    @GET("movie/{id}/videos?api_key=" + BuildConfig.TMDB_API_KEY)
    Observable<MovieTrailer.Response> getMovieTrailers(
            @Path("id") String movieId
    );

}
