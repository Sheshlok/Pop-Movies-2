package com.example.android.popmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.android.popmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by sheshloksamal on 13/03/16.
 */
public class TestUtilities extends AndroidTestCase {


    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        /*
            Set is a data structure that does not allow duplicate elements.
            Map.Entry is a K:V pair mapping contained in a Map.
            ContentValues.valueSet() returns a Set of all keys and values Set<Map.Entry<String, Object>>
         */

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry: valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);
            assertTrue("Column" + columnName + "Not Found" + error, index != -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("The expected Value" + expectedValue + "does" +
                    "not match the actual returned Value" + valueCursor.getString(index) + error,
                   expectedValue, valueCursor.getString(index));
        }

    }


    static ContentValues createMovieEntryValues() {

        ContentValues movieEntryValues = new ContentValues();
        movieEntryValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, "293660");
        movieEntryValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "Deadpool");
        movieEntryValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
                "/inVq3FRqcYIRl2la8iZikYYxFNR.jpg");
        movieEntryValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS,
                "Based upon Marvel Comics’ most unconventional anti-hero, DEADPOOL tells the origin" +
                        " story of former Special Forces operative turned mercenary Wade Wilson, " +
                        "who after being subjected to a rogue experiment that leaves him with " +
                        "accelerated healing powers, adopts the alter ego Deadpool. Armed with his " +
                        "new abilities and a dark, twisted sense of humor, Deadpool hunts down the " +
                        "man who nearly destroyed his life.");
        movieEntryValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING, "7.23");
        movieEntryValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, "2016-02-09");
        return movieEntryValues;
    }



    static ContentValues createReviewEntryValues(String movieId) {

        ContentValues reviewEntryValues = new ContentValues();
        reviewEntryValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_KEY, movieId);
        reviewEntryValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, "56c146cac3a36817f900d5f0");
        reviewEntryValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "huy.duc.eastagile");
        reviewEntryValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "A funny movie with a " +
                "romantic love story. Wade Wilson (Ryan Reynolds) is a former Special Forces " +
                "operative who now works as a mercenary. His world comes crashing down when " +
                "evil scientist Ajax (Ed Skrein) tortures, disfigures and transforms him into" +
                " Deadpool. The rogue experiment leaves Deadpool with accelerated healing powers" +
                " and a twisted sense of humor. With help from mutant allies Colossus and" +
                " Negasonic Teenage Warhead (Brianna Hildebrand), Deadpool uses his new skills" +
                " to hunt down the man who nearly destroyed his life.");
        reviewEntryValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_URL,
                "https://www.themoviedb.org/review/56c146cac3a36817f900d5f0");

        return reviewEntryValues;

    }

    static ContentValues createTrailerEntryValues(String movieId) {

        ContentValues trailerEntryValues = new ContentValues();
        trailerEntryValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_KEY, movieId);
        trailerEntryValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, "56c4cb4bc3a3680d57000560");
        trailerEntryValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_KEY, "7jIBCiYg58k");
        trailerEntryValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_NAME, "Trailer");
        trailerEntryValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SITE, "YouTube");
        trailerEntryValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_SIZE, "1080");

        return trailerEntryValues;
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
