package com.example.android.popmovies.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.example.android.popmovies.data.provider.MovieContract.MovieEntry;
import com.example.android.popmovies.data.provider.MovieContract.ReviewEntry;
import com.example.android.popmovies.data.provider.MovieContract.TrailerEntry;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    /*
        If you change the database schema, you must update the DB version. We will start at
        DATABASE_VERSION 1. This must be manually incremented each time we release an updated
        APK with new database schema
     */
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
       By default, foreign key constraints are not enforced by database. This method
       allows the application to enforce foreign key constraints. It must be called each time the
       database is open to ensure that foreign key constraints are enabled for the session.
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
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME +
                " ( " +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_USER_RATING + " TEXT NOT NULL, " +
                "UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE );";

        /** REVIEW_ID column should be unique */
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME +
                " ( " +
                ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_REVIEW_URL + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID +
                "), " + "UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ") ON CONFLICT REPLACE );";

        /** TRAILER_ID column should be unique */
        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME +
                " ( " +
                TrailerEntry._ID + " INTEGER PRIMARY KEY, " +
                TrailerEntry.COLUMN_TRAILER_ID + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_KEY + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_NAME + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_SITE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_TRAILER_SIZE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_MOVIE_KEY + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                MovieEntry.TABLE_NAME + "(" + MovieEntry.COLUMN_MOVIE_ID +
                "), UNIQUE (" + TrailerEntry.COLUMN_TRAILER_ID + ") ON CONFLICT REPLACE );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);
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
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);

    }
}
