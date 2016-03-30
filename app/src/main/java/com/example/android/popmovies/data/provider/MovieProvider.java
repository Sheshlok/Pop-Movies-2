package com.example.android.popmovies.data.provider;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class MovieProvider extends ContentProvider{

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    // Get a MovieDbHelper instance
    private MovieDbHelper mOpenHelper;

    // URI Matcher used by this Content Provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Constants for matching URI types
    static final int MOVIES = 100;
    static final int MOVIES_WITH_MOVIE_ID = 101;
    static final int REVIEWS = 200;
    static final int REVIEWS_WITH_MOVIE_ID = 201;
    static final int TRAILERS = 300;
    static final int TRAILERS_WITH_MOVIE_ID = 301;



    static UriMatcher buildUriMatcher(){
        // The code passed into the constructor represents the code to return for the root URI.
        // Its common to use NO_MATCH as the code for this case.
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        /* Match each URI type with the constants declared above */

        // content://com.example.android.popmovies/movies = 100
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIES);

        // content://com.example.android.popmovies/movies/* = 101
        uriMatcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIES_WITH_MOVIE_ID);

        // content://com.example.android.popmovies/reviews = 200
        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEWS);

        // content://com.example.android.popmovies/reviews/* = 201
        uriMatcher.addURI(authority, MovieContract.PATH_REVIEW + "/*", REVIEWS_WITH_MOVIE_ID);

        // content://com.example.android.popmovies/trailers = 300
        uriMatcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILERS);

        // content://com.example.android.popmovies/trailers/* = 301
        uriMatcher.addURI(authority, MovieContract.PATH_TRAILER + "/*", TRAILERS_WITH_MOVIE_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate(){
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {

        // Use the uriMatcher to determine what kind of URI it is
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case MOVIES: //100 = content://com.example.android.popmovies/movies
                //vnd.android.cursor.dir/com.example.android.popmovies/movies
                return MovieContract.MovieEntry.CONTENT_TYPE;

            case MOVIES_WITH_MOVIE_ID: //101 = content://com.example.android.popmovies/movies/*
                //vnd.android.cursor.item/com.example.android.popmovies/movies
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

            case REVIEWS: //200 = content://com.example.android.popmovies/reviews
                //vnd.android.cursor.dir/com.example.android.popmovies/reviews
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            case REVIEWS_WITH_MOVIE_ID: //201 = content://com.example.android.popmovies/reviews/*
                //vnd.android.cursor.dir/com.example.android.popmovies/reviews/
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            case TRAILERS: //300 = content://com.example.android.popmovies/trailers
                //vnd.android.cursor.dir/com.example.android.popmovies/trailers
                return MovieContract.TrailerEntry.CONTENT_TYPE;

            case TRAILERS_WITH_MOVIE_ID: //301 = content://com.example.android.popmovies/trailers/*
                //vnd.android.cursor.dir/com.example.android.popmovies/trailers/
                return MovieContract.TrailerEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);

        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor returnCursor;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES: //100 = content://com.example.android.popmovies/movies
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, //no grouping of rows
                        null, //Since no grouping, no HAVING clause either
                        sortOrder
                );
                break;
            case MOVIES_WITH_MOVIE_ID: //101 = content://com.example.android.popmovies/movies/*
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{MovieContract.MovieEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEWS: //200 = content://com.example.android.popmovies/reviews
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEWS_WITH_MOVIE_ID: //201 = content://com.example.android.popmovies/reviews/*
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ",
                        new String[] {MovieContract.ReviewEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILERS: //300 = content://com.example.android.popmovies/trailers
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILERS_WITH_MOVIE_ID: //301 = content://com.example.android.popmovies/trailers/*
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ",
                        new String[] {MovieContract.TrailerEntry.getMovieIdFromUri(uri)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        /*
            Register a NotificationUri for the cursor returned. This causes the cursor to register
            a ContentObserver to watch for changes that happen to the URI - that was passed into
            the function - and any of its descendents. This allows the ContentProvider to easily
            tell the UI when the cursor changes on operations like database inserts and updates.
          */
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES: //100 = content://com.example.android.popmovies/movies
                long movieRowId = mOpenHelper.getWritableDatabase().insert(
                        MovieContract.MovieEntry.TABLE_NAME, null, contentValues
                );
                if (movieRowId > 0) {
                    returnUri = MovieContract.MovieEntry.buildmovieUriWithId(movieRowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case REVIEWS: //200 = content://com.example.android.popmovies/reviews
                long reviewRowId = mOpenHelper.getWritableDatabase().insert(
                        MovieContract.ReviewEntry.TABLE_NAME, null, contentValues
                );
                if (reviewRowId > 0) {
                    returnUri = MovieContract.ReviewEntry.buildReviewWithId(reviewRowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            case TRAILERS: //300 = content://com.example.android.popmovies/trailers
                long trailerRowId = mOpenHelper.getWritableDatabase().insert(
                        MovieContract.TrailerEntry.TABLE_NAME, null, contentValues
                );
                if (trailerRowId > 0) {
                    returnUri = MovieContract.TrailerEntry.buildTrailerWithId(trailerRowId);
                } else {
                    throw new android.database.SQLException("Failed to insert a row into" + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        // Notify registered observers that a row was changed and attempt to sync changes. By
        // default, CursorAdapter objects will receive this notification
        getContext().getContentResolver().notifyChange(uri, null);
        return  returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs){
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match){
            case MOVIES: //100 = content://com.example.android.popmovies/movies
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case MOVIES_WITH_MOVIE_ID: //101 = content://com.example.android.popmovies/movies/*
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[] {MovieContract.MovieEntry.getMovieIdFromUri(uri)}
                );
                break;
            case REVIEWS: //200 = content://com.example.android.popmovies/reviews
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case REVIEWS_WITH_MOVIE_ID: //201 = content://com.exmple.android.popmovies/reviews/*
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ",
                        new String[] {MovieContract.ReviewEntry.getMovieIdFromUri(uri)}
                );
                break;
            case TRAILERS: //300 = content://com.example.android.popmovies/trailers
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case TRAILERS_WITH_MOVIE_ID: //301 = content://com.example.android.popmovies/trailers/*
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ",
                        new String[] {MovieContract.TrailerEntry.getMovieIdFromUri(uri)}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        /**
            With MOVIE_ID cases are for convenience functions. They should be used at discretion.
            For e.g., they should not be used for updating:
             1. MOVIE_ID/COLUMN_MOVIE_KEY since then the selection statement breaks down.
             2. the unique fields esp.REVIEW_ID since there can be multiple reviews for the same
                MOVIE_IDs and only 1 will remain. Same goes for TRAILER_IDs if there is more than
                1 trailer
            In these scenarios, use the more general selection.
         */

        switch (match){
            case MOVIES: //100 = content://com.example.android.popmovies/movies
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            case MOVIES_WITH_MOVIE_ID: //101 = content://com.example.android.popmovies/movies/*
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.MovieEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[] {MovieContract.MovieEntry.getMovieIdFromUri(uri)}
                );
                break;
            case REVIEWS: //200 = content://com.example.android.popmovies/reviews
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            case REVIEWS_WITH_MOVIE_ID: //201 = content://com.exmple.android.popmovies/reviews/*
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_KEY + " = ? ",
                        new String[] {MovieContract.ReviewEntry.getMovieIdFromUri(uri)}
                );
                break;
            case TRAILERS: //300 = content://com.example.android.popmovies/trailers
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            case TRAILERS_WITH_MOVIE_ID: //301 = content://com.example.android.popmovies/trailers/*
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_KEY + " = ? ",
                        new String[] {MovieContract.TrailerEntry.getMovieIdFromUri(uri)}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;

        switch (match){
            case MOVIES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues cv: contentValues) {
                        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, cv);
                        if (movieRowId != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case REVIEWS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues cv: contentValues) {
                        long reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, cv);
                        if (reviewRowId != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case TRAILERS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues cv: contentValues) {
                        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, cv);
                        if (trailerRowId != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            default:
                returnCount = super.bulkInsert(uri, contentValues);
        }
        if (returnCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnCount;
    }
   /*
        No need to call this method. This makes sure that the testing framework is running smoothly.
        Link: http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    */
    @Override
    @TargetApi(11)
    public void shutdown(){
        mOpenHelper.close();
        super.shutdown();
    }

}