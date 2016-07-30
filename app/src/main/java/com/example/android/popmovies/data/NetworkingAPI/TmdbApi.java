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
