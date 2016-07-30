package com.example.android.popmovies.data.provider;

/**
 * Created by sheshloksamal on 12/03/16.
 *
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

    /* Possible paths. We will have 2 tables - movies, genres */
    public static final String PATH_MOVIE = "movies";
    public static final String PATH_GENRES = "genres";

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

        // Default "ORDER BY" clause for favorites page should be descending
        public static final String DEFAULT_SORT = BaseColumns._ID + " DESC";

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
        public static final String COLUMN_MOVIE_BACKDROP_PATH = "backdrop_path";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_USER_RATING = "user_rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_FAVORED = "favored";
        public static final String COLUMN_MOVIE_GENRE_IDS = "genre_ids"; // Comma-separated list of genreIds

        /*
            Helper URIs for movieIDs
         */

        // content://com.example.android.popmovies/movies/movie_id
        public static Uri buildMovieUriWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        // will return movieId
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }


    /* Inner Class that defines the table contents of the 'genres' table */
    public static final class GenreEntry implements BaseColumns {

        // content://com.example.android.popmovies/genres
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRES).build();

        // vnd.android.cursor.dir/com.example.android.popmovies/genres
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_GENRES;

        // vnd.android.cursor.item/com.example.android.popmovies/genres
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_GENRES;

        // content://com.example.android.popmovies/genres/{id}
        public static Uri buildGenreWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        /*
            TABLE DETAILS
         */

        public static final String TABLE_NAME = "genres";

        // Table Columns
        public static final String COLUMN_GENRE_ID = "genre_id";
        public static final String COLUMN_GENRE_NAME = "genre_name";

        /*
            Helper URIs for GenreIDs
         */

        // content://com.example.android.popmovies/genres/genre_id
        public static Uri buildGenreUriWithGenreId(int genreId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(genreId)).build();
        }

        // will return genreId
        public static int getGenreIdFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(1));
        }

    }


}