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
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.android.popmovies.data.model.Genre;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.provider.MovieContract;
import com.example.android.popmovies.utilities.DbUtils;
import com.example.android.popmovies.utils.PollingCheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sheshloksamal on 13/03/16.
 *
 */
public class TestUtilities extends AndroidTestCase {

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        /*
            Set is a data structure that does not allow duplicate elements.
            Map.Entry is a K:V pair mapping contained in a Map.
            ContentValues.valueSet() returns a Set of all keys and values 'Set<Map.Entry<String, Object>>'
         */

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry: valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertTrue("Column " + columnName + " Not Found" + error, index != -1);
            String expectedValue = entry.getValue().toString();
            if (!columnName.equals(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORED)) {
                assertEquals("The expected Value " + expectedValue + " does" +
                        "not match the actual returned Value " + DbUtils.getString(valueCursor, columnName)
                        + " " + error, expectedValue, DbUtils.getString(valueCursor, columnName));
            } else {
                assertEquals("The expected Value " + expectedValue + " does" +
                        "not match the actual returned Value " + DbUtils.getBoolean(valueCursor, columnName)
                        + " " + error, expectedValue, String.valueOf(DbUtils.getBoolean(valueCursor, columnName)));
            }
        }


    }

    static ContentValues createMovieEntryValues() {
        // Not setting favored here since the default value is false both in model and in db schema
        MovieItem testMovie = new MovieItem()
                .setMovieId("293660")
                .setTitle("Deadpool")
                .setPosterPath("/inVq3FRqcYIRl2la8iZikYYxFNR.jpg")
                .setBackdropPath("/n1y094tVDFATSzkTnFxoGZ1qNsG.jpg")
                .setSynopsis("Based upon Marvel Comics’ most unconventional anti-hero, DEADPOOL " +
                        "tells the origin story of former Special Forces operative turned " +
                        "mercenary Wade Wilson, who after being subjected to a rogue experiment " +
                        "that leaves him with accelerated healing powers, adopts the alter ego " +
                        "Deadpool. Armed with his new abilities and a dark, twisted sense of " +
                        "humor, Deadpool hunts down the man who nearly destroyed his life.")
                .setUserRating("7.23")
                .setReleaseDate("2016-02-09")
                .setGenreIds(Arrays.asList(28, 12, 35));

        return new MovieItem.Builder().movieItem(testMovie).build();
    }



    static ContentValues createGenreEntryValues() {
        Genre testGenre = new Genre()
                .setId(28)
                .setName("Action");

        return new Genre.Builder().genre(testGenre).build();

    }

    /* Putting the current genre list in testBulkInsertGenreValues */
    static ContentValues[] createBulkInsertGenreValues() {
        List<ContentValues> testBulkInsertGenreValues = new ArrayList<>();
        testBulkInsertGenreValues.add(new Genre.Builder().id(28).name("Action").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(12).name("Adventure").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(16).name("Animation").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(35).name("Comedy").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(80).name("Crime").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(99).name("Documentary").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(18).name("Drama").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(10751).name("Family").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(14).name("Fantasy").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(10769).name("Foreign").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(36).name("History").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(27).name("Horror").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(10402).name("Music").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(9648).name("Mystery").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(10749).name("Romance").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(878).name("Science Fiction").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(10770).name("TV Movie").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(53).name("Thriller").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(10752).name("War").build());
        testBulkInsertGenreValues.add(new Genre.Builder().id(37).name("Western").build());

        return testBulkInsertGenreValues.toArray(new ContentValues[testBulkInsertGenreValues.size()]);

    }

    /*
        The functions we provider inside of TestProvider, such as testInsert etc. use this utility
        class to test the ContentObserver Callbacks using the PollingCheck class that we grabbed
        from the Android CTS tests.
        Note that This only checks whether the onChange function is called, NOT whether the correct
        Uri is returned or not (that check is in the corresponding TestProvider method).
     */

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            /*
                The PollingCheck Class is taken from the Android CTS(Compatibility Test Suite).
                The reason that PollingCheck works is that, BY DEFAULT, the jUnit framework is not
                running on the main Android Application thread

                Basically, this keeps on checking for boolean at regular intervals, 50 milliseconds
                for a total time of 5 seconds. When the onChange callback is received, the boolean
                changes to true and we come out of this block, and quit the handler thread.
             */
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver(){
        return TestContentObserver.getTestContentObserver();
    }
}
