package com.example.android.popmovies.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;



/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    /*
        1. If you change the database schema, you must update the DB version. We will start at
         DATABASE_VERSION 1. This must be manually incremented each time we release an updated
         APK with new database schema.
        2. 30th Mar: Updating dbVersion due to a) updated schema for movies table, b) insertion of
         genres tables, and c) a complete overhaul of project as per reactive programming
         paradigms.
     */
    private static final int DATABASE_VERSION = 2;

    public static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
       /-------------------------------------------------------------------------------------------/
       30th Mar: This was used before when we had an earlier schema, i.e. ReviewEntry and
       TrailerEntry tables with movie_id as the FK. Since we no longer fetch Reviews and Trailers
       at start but only when a certain movieItem is clicked (and store all the retrieved ones in
       the custom cache of the OkHttpClient, we need 2 independent tables. Nevertheless, lets leave
       this one as it is as it does not impact the database anyhow.
       /-------------------------------------------------------------------------------------------/
       By default, foreign key constraints are not enforced by database. This method
       allows the application to enforce foreign key constraints. It must be called each time the
       database is opened to ensure that foreign key constraints are enabled for the session.
       When foreign key constraints are disabled the database does not check whether changes to
       database will violate foreign key constraints. As a result, it is possible for the
       database state to become inconsistent.
    */
    @Override
    public void onConfigure(SQLiteDatabase sqLiteDatabase) {
        if (Build.VERSION.SDK_INT >= 16) {
            sqLiteDatabase.setForeignKeyConstraintsEnabled(true);
        } else {
            sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /* No AUTOINCREMENT on primary key since there is no field which needs to be sorted
        by design */

        /** MOVIE_ID column should be unique */
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME
                + "("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_FAVORED + " INTEGER NOT NULL DEFAULT 0, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_GENRE_IDS + " TEXT, "
                + "UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE"
                + ");";

        /** GENRE_ID column should be unique */
        final String SQL_CREATE_GENRE_TABLE = "CREATE TABLE " + MovieContract.GenreEntry.TABLE_NAME
                + "("
                + MovieContract.GenreEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.GenreEntry.COLUMN_GENRE_ID + " INTEGER NOT NULL, "
                + MovieContract.GenreEntry.COLUMN_GENRE_NAME + " TEXT NOT NULL, "
                + "UNIQUE (" + MovieContract.GenreEntry.COLUMN_GENRE_ID + ") ON CONFLICT REPLACE"
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_GENRE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        /*
            This database is a cache for online data, so its upgrade policy is to simply
            discard the data and start over.

            This only fires when database version number is changed. It DOES NOT depend on
            the version number of the application.

            If you want to modify the schema without wiping data (in case where data contains
            user-generated information), use 'ALTER TABLE' to add new tables
         */
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.GenreEntry.TABLE_NAME);

    }
}
