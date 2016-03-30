package com.example.android.popmovies.data.provider;

/**
 * Created by sheshloksamal on 12/03/16.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Define table and column names for the Movie Database
 */
public class MovieContract {

    /* Content Authority for the entire 'Content Provider' */
    public static final String CONTENT_AUTHORITY = "com.example.android.popmovies";

    /* Base of all URIs which app will use to contact the content provider */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* Possible paths. We will have 3 tables - movies, reviews, trailers */
    public static final String PATH_MOVIE = "movies";
    public static final String PATH_REVIEW = "reviews";
    public static final String PATH_TRAILER = "trailers";

    /* Inner Class that defines the table contents of 'movie' table */
    public static final class MovieEntry implements BaseColumns {

        // content://com.example.android.popmovies/movies
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        // vnd.android.cursor.dir/com.example.android.popmovies/movies
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // vnd.android.cursor.item/com.example.android.popmovies/movies
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // content://com.example.android.popmovies/movies/{id}
        public static Uri buildmovieUriWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        /*
            TABLE DETAILS
         */

        // Table Name
        public static final String TABLE_NAME = "movies";

        // Table Columns
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER_PATH = "poster_path";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_USER_RATING = "user_rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";

        /*
            Helper URIs for movieIDs
         */

        // content://com.example.android.popmovies/movie_id
        public static Uri buildMovieUriWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        // will return movieId
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    /* Inner Class that defines the table contents of the 'reviews' table */
    public static final class ReviewEntry implements BaseColumns {

        // content://com.example.android.popmovies/reviews
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        // vnd.android.cursor.dir/com.example.android.popmovies/reviews
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // vnd.android.cursor.item/com.example.android.popmovies/reviews
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        // content://com.example.android.popmovies/reviews/{id}
        public static Uri buildReviewWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        /*
            TABLE DETAILS
         */

        public static final String TABLE_NAME = "reviews";

        // Table Columns
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "content";
        public static final String COLUMN_REVIEW_URL = "review_url";
        // Column with foreign key from the movie table for INNER JOIN later
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        /*
            Helper URIs for movieIDs
         */
        // content://com.example.android.popmovies/reviews/movie_id:
        public static Uri buildReviewUriWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        // will return movie_id
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /* Inner Class that defines the table contents of the 'trailers' table */
    public static final class TrailerEntry implements BaseColumns {

        // content://com.example.android.popmovies/trailers
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        // vnd.android.cursor.dir/com.example.android.popmovies/trailers
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        // vnd.android.cursor.item/com.example.android.popmovies/trailers
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        // content://com.example.android.popmovies/trailers/{id}
        public static Uri buildTrailerWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /*
            TABLE DETAILS
         */
        public static final String TABLE_NAME = "trailers";

        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_TRAILER_KEY = "youtube_key";
        public static final String COLUMN_TRAILER_NAME = "trailer_name";
        public static final String COLUMN_TRAILER_SITE = "site";
        public static final String COLUMN_TRAILER_SIZE = "size";
        // Column with foreign key from movie table for INNER JOIN later
        public static final String COLUMN_MOVIE_KEY = "movie_id";

        /*
            Helper URIs for movieIds
         */

        // content://com.example.android.popmovies/trailers/movie_id
        public static Uri buildTrailerUriWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        // will return movie_id
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}