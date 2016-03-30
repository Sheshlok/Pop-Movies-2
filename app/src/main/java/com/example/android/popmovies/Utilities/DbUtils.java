package com.example.android.popmovies.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.model.MovieReview;
import com.example.android.popmovies.data.model.MovieTrailer;
import com.example.android.popmovies.data.provider.MovieContract;

import java.util.List;
import java.util.Vector;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class DbUtils {

    /**
     * Store a list of {@link MovieItem} into the database
     * @param context      The context
     * @param movieList    The {@link MovieItem} list fetched from the TmdbApiService
     */
    public static void storeMovieList(Context context, List<MovieItem> movieList) {
        Vector<ContentValues> cVVector = new Vector<>(movieList.size());
        Log.d("storeMovieList: ", movieList.size() + " items fetched");
        for (int i = 0; i < movieList.size(); i++){
            MovieItem movieItem = movieList.get(i);
            ContentValues contentValues = new ContentValues();

            // Insert data from the movieItem into the contentValues
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieItem.getMovieId());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movieItem.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, movieItem.getPosterPath());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, movieItem.getSynopsis());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING, movieItem.getUserRating());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movieItem.getReleaseDate());

            // Add the contentValues to the vector
            cVVector.add(contentValues);
        }

        // First clear all records. We never maintain old records of Top10 or Top20s anyways.
        //deleteAllRecords(context);

        // Do a bulkInsert of movieItems into the database
        int moviesInserted = 0;
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            moviesInserted = context.getContentResolver().bulkInsert
                    (MovieContract.MovieEntry.CONTENT_URI, cvArray);
        }
        if (moviesInserted != movieList.size()) {
            Log.e("storeMovieList: ", moviesInserted + "/" + movieList.size() + " movies inserted");
        } else {
            Log.d("storeMovieList: ", moviesInserted + " movies inserted into the database");
        }
    }

    public static void storeReviewList(Context context, String movieId,  List<MovieReview> reviewList) {
        Vector<ContentValues> cVVector = new Vector<>(reviewList.size());
        Log.d("storeReviewList: ", reviewList.size() + " items fetched");
        for (int i = 0; i < reviewList.size(); i++) {
            MovieReview movieReview = reviewList.get(i);
            ContentValues contentValues = new ContentValues();

            // Insert data from the movieReview item into the contentValues
            contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, movieReview.getId());
            contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, movieReview.getAuthor());
            contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, movieReview.getContent());
            contentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL, movieReview.getUrl());
            contentValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieId);

            // Add the contentValues to the vector
            cVVector.add(contentValues);
        }

        // Do a bulk insert of reviewItems into the database
        int reviewsInserted = 0;
        if (cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[reviewList.size()];
            cVVector.toArray(cvArray);
            reviewsInserted = context.getContentResolver().bulkInsert
                    (MovieContract.ReviewEntry.CONTENT_URI, cvArray);
        }

        if (reviewsInserted != reviewList.size()) {
            Log.e("storeReviewList: ", reviewsInserted + "/" + reviewList.size() + " reviews inserted");
        } else {
            Log.d("storeReviewList: ", reviewsInserted + " reviews inserted into the database");
        }
    }

    public static void storeTrailerList(Context context, String movieId, List<MovieTrailer> trailerList) {
        Vector<ContentValues> cvVector = new Vector<>(trailerList.size());
        Log.d("storeTrailerList: ", trailerList.size() + " items fetched");

        for (int i = 0; i < trailerList.size(); i++) {
            MovieTrailer movieTrailer = trailerList.get(i);
            ContentValues contentValues = new ContentValues();

            // Insert data from the MovieTrailer item into the database
            contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, movieTrailer.getId());
            contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, movieTrailer.getKey());
            contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, movieTrailer.getName());
            contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SITE, movieTrailer.getSite());
            contentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SIZE, movieTrailer.getSize());
            contentValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movieId);

            // Add contentValues to the vector
            cvVector.add(contentValues);
        }

        // Do a bulkInsert of trailerItems into the database
        int trailersInserted = 0;
        if (cvVector.size()> 0) {
            ContentValues[] cVArray = new ContentValues[trailerList.size()];
            cvVector.toArray(cVArray);
            trailersInserted = context.getContentResolver().bulkInsert
                    (MovieContract.TrailerEntry.CONTENT_URI, cVArray);
        }

        if (trailersInserted != trailerList.size()) {
            Log.e("storeTrailerList: ", trailersInserted + "/" + trailerList.size() + " trailers inserted");
        } else {
            Log.d("storeTrailerList: ", trailersInserted + " trailers inserted into the database");
        }
    }

//    public static void deleteAllRecords(Context context){
//        /*
//            MovieEntry records should be deleted last because of the foreign key constraint
//         */
//        int rowsDeleted = context.getContentResolver().delete(
//                MovieContract.ReviewEntry.CONTENT_URI, null, null);
//        Log.e("in deleteAllRecords", rowsDeleted + " reviews deleted");
//        rowsDeleted = context.getContentResolver().delete(
//                MovieContract.TrailerEntry.CONTENT_URI, null, null);
//        Log.e("in deleteAllRecords", rowsDeleted + " trailers deleted");
//        rowsDeleted = context.getContentResolver().delete(
//                MovieContract.MovieEntry.CONTENT_URI, null, null);
//        Log.e("in deleteAllRecords", rowsDeleted + " movies deleted");
//    }

}
