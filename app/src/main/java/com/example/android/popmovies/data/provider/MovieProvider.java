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
public class MovieProvider extends ContentProvider {

    // Get a MovieDbHelper instance
    private MovieDbHelper mOpenHelper;

    // URI Matcher used by this Content Provider
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Constants for matching URI types
    static final int MOVIES = 100;
    static final int MOVIES_WITH_MOVIE_ID = 101;
    static final int GENRES = 200;
    static final int GENRES_WITH_GENRES_ID = 201;



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

        // content://com.example.android.popmovies/genres = 200
        uriMatcher.addURI(authority, MovieContract.PATH_GENRES, GENRES);

        // content://com.example.android.popmovies/genres/* = 201
        uriMatcher.addURI(authority, MovieContract.PATH_GENRES + "/*", GENRES_WITH_GENRES_ID);

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

            case GENRES: //200 = content://com.example.android.popmovies/genres
                //vnd.android.cursor.dir/com.example.android.popmovies/genres
                return MovieContract.GenreEntry.CONTENT_TYPE;

            case GENRES_WITH_GENRES_ID: //201 = content://com.example.android.popmovies/genres/*
                //vnd.android.cursor.item/com.example.android.popmovies/genres/
                return MovieContract.GenreEntry.CONTENT_ITEM_TYPE;

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
            case GENRES: //200 = content://com.example.android.popmovies/genres
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.GenreEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case GENRES_WITH_GENRES_ID: //201 = content://com.example.android.popmovies/genres/*
                returnCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.GenreEntry.TABLE_NAME,
                        projection,
                        MovieContract.GenreEntry.COLUMN_GENRE_ID + " = ? ",
                        new String[] {Integer.toString(MovieContract.GenreEntry.getGenreIdFromUri(uri))},
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

            case GENRES: //200 = content://com.example.android.popmovies/genres
                long genreRowId = mOpenHelper.getWritableDatabase().insert(
                        MovieContract.GenreEntry.TABLE_NAME, null, contentValues
                );
                if (genreRowId > 0) {
                    returnUri = MovieContract.GenreEntry.buildGenreWithId(genreRowId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        // Notify registered observers that a row was changed and attempt to sync changes. By
        // default, CursorAdapter objects will receive this notification
        getContext().getContentResolver().notifyChange(uri, null, false);
        return returnUri;
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
            case GENRES: //200 = content://com.example.android.popmovies/genres
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.GenreEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case GENRES_WITH_GENRES_ID: //201 = content://com.example.android.popmovies/genres/*
                rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.GenreEntry.TABLE_NAME,
                        MovieContract.GenreEntry.COLUMN_GENRE_ID + " = ? ",
                        new String[] {Integer.toString(MovieContract.GenreEntry.getGenreIdFromUri(uri))}
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null, false);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

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
            case GENRES: //200 = content://com.example.android.popmovies/genres
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.GenreEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            case GENRES_WITH_GENRES_ID: //201 = content://com.example.android.popmovies/genres/*
                rowsUpdated = mOpenHelper.getWritableDatabase().update(
                        MovieContract.GenreEntry.TABLE_NAME,
                        contentValues,
                        MovieContract.GenreEntry.COLUMN_GENRE_ID + " = ? ",
                        new String[] {Integer.toString(MovieContract.GenreEntry.getGenreIdFromUri(uri))}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null, false);
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
            case GENRES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues cv: contentValues) {
                        long genreRowId = db.insert(MovieContract.GenreEntry.TABLE_NAME, null, cv);
                        if (genreRowId != -1) {
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
            getContext().getContentResolver().notifyChange(uri, null, false);
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