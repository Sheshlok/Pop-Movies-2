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

package com.example.android.popmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.android.popmovies.data.provider.MovieContract;
import com.example.android.popmovies.data.provider.MovieContract.GenreEntry;
import com.example.android.popmovies.data.provider.MovieDbHelper;

import java.util.Arrays;
import java.util.HashSet;

import timber.log.Timber;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class TestMovieDB extends AndroidTestCase{

    /*
        Since we want each test to start with a clean slate
     */
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database. This
        makes sure we always start with a clean test.
     */
    @Override
    protected void setUp() throws Exception{
        super.setUp();
        deleteTheDatabase();
    }

    public void testAllTablesCreated() throws Throwable {

        /*
            A HashSet has NO ORDER, is NON-SYNCHRONIZED, and uses HashTable for storage. Perfect
            for this since we want to be able to add and find columns at random in our collection.
         */
        final HashSet<String> tableNameHashSet = new HashSet<>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(GenreEntry.TABLE_NAME);

        SQLiteDatabase db = new MovieDbHelper(this.mContext).getReadableDatabase();

        /** Check if we get a Readable database */
        assertEquals(true, db.isOpen());

        /** Check if our database has all the tables */
        /* Returns a list of all tables in database. The database column names are:
         * 'type', 'name', 'tbl_name', 'rootpage', 'sql'. 'name' and 'tbl_name' are same */
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: Database has not been created properly", c.moveToFirst());

        /*
            Move the cursor, comparing it to our tempHashSet in a pop-off mode
         */
        do {
            Timber.v(".getString(columnIndex) is: " + c.getString(0));
            Timber.v("Remove operation successful? " + tableNameHashSet.remove(c.getString(0)));
            Timber.v("TableName HashSet now looks like: " + tableNameHashSet);
        } while (c.moveToNext());

        assertTrue("Error: The database was not created with both the Tables: MovieEntry & " +
                        "GenreEntry", tableNameHashSet.isEmpty());

        db.close();
        c.close();
    }

    /** Check if our tables have correct columns */
        /* TABLE 1: Movie */

    public void testMovieTableColumns() throws Throwable {

        SQLiteDatabase db = new MovieDbHelper(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: We were unable to query the database for table information", c.moveToFirst());

        // Build a HashSet of all columns we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORED);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_GENRE_IDS);

        Timber.v("Column HashSet of Movie Table now looks like: " + movieColumnHashSet);

        /* Column Headers: [cid, name, type, notnull, dflt_value, pk] */
        Timber.v("The column names are: " + Arrays.toString(c.getColumnNames()));

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            Timber.v(".getString(columnNameIndex)" + columnName);
            movieColumnHashSet.remove(columnName);
            Timber.v("Column HashSet of Movie Table now looks like: " + movieColumnHashSet);
        } while (c.moveToNext());

        assertTrue("Error: The database does not contain all the required MovieEntry columns",
                movieColumnHashSet.isEmpty());

        db.close();
        c.close();
    }

        /* TABLE 2: Review */

    public void testGenreTableColumns() throws Throwable {

        SQLiteDatabase db = new MovieDbHelper(mContext).getReadableDatabase();
        Cursor c = db.rawQuery("PRAGMA table_info(" + GenreEntry.TABLE_NAME + ")", null);

        assertTrue("Error: We were unable to query the database for table information", c.moveToFirst());

        // Build a HashSet of all columns we want to look for
        final HashSet<String> genreColumnHashSet = new HashSet<>();
        genreColumnHashSet.add(MovieContract.GenreEntry._ID);
        genreColumnHashSet.add(GenreEntry.COLUMN_GENRE_ID);
        genreColumnHashSet.add(GenreEntry.COLUMN_GENRE_NAME);

        Timber.v("Column HashSet of Review Table now looks like: " + genreColumnHashSet);

        /* Column Headers: [cid, name, type, notnull, dflt_value, pk] */
        Timber.v("The column names are: " + Arrays.toString(c.getColumnNames()));

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            Timber.v(".getString(columnNameIndex)" + columnName);
            genreColumnHashSet.remove(columnName);
            Timber.v("Column HashSet of Review Table now looks like: " + genreColumnHashSet);
        } while (c.moveToNext());

        assertTrue("Error: The database does not contain all the required GenreEntry columns",
                genreColumnHashSet.isEmpty());

        db.close();
        c.close();
    }


    public void testMovieTable() {

        // Get a writable database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues movieEntryValues = TestUtilities.createMovieEntryValues();

        // Insert ContentValues into the database and verify the rowId is not -1
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieEntryValues);
        assertTrue(movieRowId != -1);

        // Get the data back and verify that it made the round-trip
        Cursor movieCursor = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue("Error: No records returned from the movie query", movieCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: MovieQuery Validation Failed.",
                movieCursor, movieEntryValues);

        // Finally close the cursor and the database
        movieCursor.close();
        assertTrue("Error: The cursor is not closed", movieCursor.isClosed());

        db.close();
        assertFalse("Error: The database is not closed. The reference to it still exists", db.isOpen());

    }

    public void testGenreTable() {
        // Get a writable database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // Now, Create ContentValues<Genre>
        ContentValues genreEntryValues = TestUtilities.createGenreEntryValues();

        //Insert ContentValues<Review> and genreRowId back
        long genreRowId = db.insert(GenreEntry.TABLE_NAME, null, genreEntryValues);
        assertTrue(genreRowId != -1);

        // Get the data back and verify that it made the roundtrip
        Cursor genreCursor = db.query(GenreEntry.TABLE_NAME, null, null, null, null, null, null);

        assertTrue("Error: No records returned from the genre query", genreCursor.moveToFirst());

        TestUtilities.validateCurrentRecord("Error: ReviewQuery Validation Failed.",
                genreCursor, genreEntryValues);

        // Finally close the cursor and the database
        genreCursor.close();
        assertTrue("Error: The cursor is not closed", genreCursor.isClosed());

        db.close();
        assertFalse("Error: The database is not closed. The reference to it still exists", db.isOpen());

    }

    /* We want to end all tests with a clean slate */
    @Override
    public void tearDown() throws Exception{
        deleteTheDatabase();
        super.tearDown();
    }

}
