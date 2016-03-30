package com.example.android.popmovies.data.NetworkingAPI;

import com.example.android.popmovies.data.model.MovieResults;
import com.example.android.popmovies.data.model.MovieReviewResults;
import com.example.android.popmovies.data.model.MovieTrailerResults;
import com.example.android.popmovies.utilities.Constants;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public interface TmdbApi {

    /** Send a GET request for movieList*/

    @GET("3/discover/movie?api_key=" + Constants.apiKey)
    ErrorHandlingCallAdapter.MyCall<MovieResults> getMovieList(
            @Query("sort_by") String sortPreference,
            @Query("page") String page
    );

    /** Send a GET request for movieReviews for a particular movie */

    @GET("3/movie/{id}/reviews?api_key=" + Constants.apiKey)
    ErrorHandlingCallAdapter.MyCall<MovieReviewResults> getMovieReviews(
            @Path("id") String movieId
    );

    /** Send a GET request for movieTrailers for a particular movie*/

    @GET("3/movie/{id}/videos?api_key=" + Constants.apiKey)
    ErrorHandlingCallAdapter.MyCall<MovieTrailerResults> getMovieTrailers(
            @Path("id") String movieId
    );

}
