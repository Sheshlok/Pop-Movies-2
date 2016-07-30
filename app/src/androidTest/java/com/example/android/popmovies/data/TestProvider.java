package com.example.android.popmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;

import com.example.android.popmovies.data.provider.MovieContract;
import com.example.android.popmovies.data.provider.MovieDbHelper;
import com.example.android.popmovies.data.provider.MovieProvider;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class TestProvider extends AndroidTestCase {

   /*
        This helper function deletes all records from both the database tables using the database
        functions only. This is designed to be used to reset the state of the database until the
        delete and query functions are available in Content Provider
    */

    public void deleteAllRecordsFromDB(){
        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        db.delete(MovieContract.GenreEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);

        db.close();
        assertFalse("Error: Database Not Closed", db.isOpen());

    }

    public void deleteAllRecords(){
        //deleteAllRecordsFromDB();
        deleteAllRecordsFromProvider();
    }

    /* We want to start each test with a clean slate */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /*
        This test checks to see whether the Content Provider is registered correctly
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // ComponentName is PackageName + Class Name. So the componentName for our ContentProvider
        // would be: com.example.android.popmovies/com.example.android.popmovies.data.provider.MovieProvider
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());

        try {
            // Fetch the providerInfo using the componentName, from the PackageManager
            // This throws an exception if the provider is not registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            // Log.d("ProviderInfo: ", providerInfo.toString());

            // Check to see if the content authorities match
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY, providerInfo.authority,
                    MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // MovieProvider is not registered correctly
            assertTrue("Error: MovieProvider is not registered correctly at " +
                    mContext.getPackageName(), false);
        }

    }

    public void testGetType(){
        // URI to be passed: content://com.example.android.popmovies/movies
        // Type to be returned: vnd.android.cursor.dir/com.example.android.popmovies/movies
        String type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals("Error: The MovieEntry CONTENT_URI should return" +
                "MovieContract.MovieEntry.CONTENT_TYPE", type, MovieContract.MovieEntry.CONTENT_TYPE);

        // URI to be passed: content://com.example.android.popmovies/genres
        // Type to be returned: vnd.android.cursor.dir/com.example.android.popmovies/genres
        type = mContext.getContentResolver().getType(MovieContract.GenreEntry.CONTENT_URI);
        assertEquals("Error: The GenreEntry CONTENT_URI should return" +
                "MovieContract.GenreEntry.CONTENT_TYPE", type, MovieContract.GenreEntry.CONTENT_TYPE);

        String movieId = "293660";

        // URL to be passed: content://com.example.android.popmovies/movies/movieId
        // Type to be returned: vnd.android.cursor.item/com.example.android.popmovies
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUriWithMovieId(movieId));
        assertEquals("Error: The MovieEntry CONTENT_URI with movieId should return" +
                "MovieContract.MovieEntry.CONTENT_ITEM_TYPE", type, MovieContract.MovieEntry.CONTENT_ITEM_TYPE);

        int genreId = 28;
        // URL to be passed: content://com.example.android.popmovies/genres/genreId
        // Type to be returned: vnd.android.cursor.item/com.example.android.popmovies.genres
        type = mContext.getContentResolver().getType(MovieContract.GenreEntry.buildGenreUriWithGenreId(genreId));
        assertEquals("Error: The GenreEntry CONTENT_URI with genreID should return" +
                "MovieContract.GenreEntry.CONTENT_ITEM_TYPE", type, MovieContract.GenreEntry.CONTENT_ITEM_TYPE);

    }

    /*
        Tests Movie Queries. This test uses the database directly to insert and then uses the
        ContentProvider to read out the data.
     */

    public void testMovieQueries() {
        // Get a writable database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        // Create contentValues of what you want to insert
        ContentValues movieEntryValues = TestUtilities.createMovieEntryValues();

        // Insert ContentValues into the database and verify that the rowId is not -1
        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, movieEntryValues);
        assertTrue("Error: Failed to insert movie entries", movieRowId != -1);

        // Get the data back through two types of queries and verify that it made the round trip

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records returned from the movie query ", movieCursor.moveToFirst());
        // Make sure we get the correct Cursor out of the database
        TestUtilities.validateCurrentRecord("Test MovieQueries", movieCursor, movieEntryValues);

        // Has the NotificationUri been set on the cursor correctly
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Content Observer not registered on the uri passed in query ",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
        }

        Uri movieUriWithMovieId = MovieContract.MovieEntry.buildMovieUriWithMovieId(
                movieEntryValues.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
        movieCursor = mContext.getContentResolver().query(
                movieUriWithMovieId,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: No records returned from the movie query with movieId",
                movieCursor.moveToFirst());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCurrentRecord("Test Movie Query with MovieId", movieCursor, movieEntryValues);

        // Has the NotificationUri been set on the cursor correctly
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Content Observer not registered on the uri passed in query ",
                    movieCursor.getNotificationUri(), movieUriWithMovieId);
        }

        movieCursor.close();
        assertTrue("Error: Cursor not closed", movieCursor.isClosed());

        db.close();
        assertFalse("Error: Database Not Closed", db.isOpen());

    }

    /*
        Tests Genre Queries. This test uses the database directly to insert and then uses the
        ContentProvider to read out the data.
     */

    public void testGenreQueries(){
        // Get a writable database
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        
        // Create ContentValues<Genre>
        ContentValues genreEntryValues = TestUtilities.createGenreEntryValues();

        // Insert ContentValues<Genre> to get a genreRowId back and verify it is not -1
        long genreRowId = db.insert(MovieContract.GenreEntry.TABLE_NAME, null, genreEntryValues);
        assertTrue("Error: Failed to insert genre entries", genreRowId != -1);

        // Get the data back through two types of queries and verify that it made the round trip
        Cursor genreCursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records returned from the genres query", genreCursor.moveToFirst());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCurrentRecord("Test Genre Query", genreCursor, genreEntryValues);

        // Has the NotificationUri been set correctly
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Content Observer not registered on the URI passed in the query",
                    genreCursor.getNotificationUri(), MovieContract.GenreEntry.CONTENT_URI);
        }

        Uri genreUriWithGenreId = MovieContract.GenreEntry.buildGenreUriWithGenreId(
                genreEntryValues.getAsInteger(MovieContract.GenreEntry.COLUMN_GENRE_ID));
        genreCursor = mContext.getContentResolver().query(
                genreUriWithGenreId,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records returned from the genres query with genreId",
                genreCursor.moveToFirst());
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCurrentRecord("Test Genres Query with GenreId", genreCursor, genreEntryValues);

        // Has the NotificationUri been set correctly
        if (Build.VERSION.SDK_INT >= 19) {
            assertEquals("Error: Content Observer not registered on the URI passed in the query",
                    genreCursor.getNotificationUri(), genreUriWithGenreId);
        }

        genreCursor.close();
        assertTrue("Error: Cursor Not Closed", genreCursor.isClosed());

        db.close();
        assertFalse("Error: Database Not Closed", db.isOpen());

    }

    /*
        This tests the insert functionality of ContentProvider. We use query functionality here too.
     */
    public void testInsert(){

        ContentValues movieEntryValues = TestUtilities.createMovieEntryValues();

        // Register a ContentObserver for our insert, directly with the ContentResolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver
                (MovieContract.MovieEntry.CONTENT_URI, true, tco);

        // Insert the ContentValues<Movie> and see if the ContentObserver gets called
        Uri movieInsertUri = mContext.getContentResolver().insert(
                MovieContract.MovieEntry.CONTENT_URI, movieEntryValues);

        // If this fails, i.e. we get Unexpected Timeout message it means the
        // uri returned by inserting movies is not calling the registered ContentObservers.
        tco.waitForNotificationOrFail();

        // Unregister the ContentObserver
        mContext.getContentResolver().unregisterContentObserver(tco);

        // Check if the uri returned is non-null and double check if the movieRowId is not -1
        assertTrue("Error: Insertion of MovieEntry failed", movieInsertUri != null);
        assertTrue("Error: Insertion of MovieEntry failed", ContentUris.parseId(movieInsertUri) != -1);

        // Query the ContentProvider and verify that the data made the round trip
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records returned from the database", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("testInsert: Error validating movieEntry", cursor, movieEntryValues);

        // Now, lets add genres

        // Register a ContentObserver for our insert<Genre> directly with the ContentProvider.
        // Get the ContentObserver again, since it is a one-shot class, as we have already quit
        // the handler thread before, when we got notification
        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver
                (MovieContract.GenreEntry.CONTENT_URI, true, tco);

        // Create ContentValues<Genre> and insert to see if the content observer gets called
        ContentValues genreEntryValues = TestUtilities.createGenreEntryValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(
                MovieContract.GenreEntry.CONTENT_URI, genreEntryValues);

        // If this fails, the uri returned by insert genres is not calling the registered COs.
        tco.waitForNotificationOrFail();

        // Unregister the ContentObserver
        mContext.getContentResolver().unregisterContentObserver(tco);

        // Check to see if the genreInsertUri is not null and double check if the row Id is not -1
        assertTrue("Error: Insertion of Genre Failed", genreInsertUri != null);
        assertTrue("Error: Insertion of Genre Failed", ContentUris.parseId(genreInsertUri) != -1);

        // Now, query the Content Provider to verify that the data made the round-trip
        // We use genreWithGenreId Uri here
        cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.buildGenreUriWithGenreId(
                        genreEntryValues.getAsInteger(MovieContract.GenreEntry.COLUMN_GENRE_ID)
                ),
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records returned from the database", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("testInsert: Error validating genreEntry", cursor, genreEntryValues);

        cursor.close();
        assertTrue("Error: Cursor Not Closed", cursor.isClosed());

    }


    public void testUpdate() {
        testInsert();

        /* Updating movie entries */

        // Get updated ContentValues
        ContentValues updatedMovieEntryValues = new ContentValues(TestUtilities.createMovieEntryValues());
        updatedMovieEntryValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, "DeadPool: A Musketeer");
        updatedMovieEntryValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING, "8.0");

        // Register a Content Observer on the main CONTENT_URI
        // This will watch for changes to the descendents too, i.e. for WITH_MOVIES_ID type URI as well
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver
                (MovieContract.MovieEntry.CONTENT_URI, true, tco);

        String movieId = updatedMovieEntryValues.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        int rowsUpdated = mContext.getContentResolver().update(
                MovieContract.MovieEntry.buildMovieUriWithMovieId(movieId),
                updatedMovieEntryValues,
                null,
                null
        );

        assertEquals("Error: Failure in Updating Movie Entry", rowsUpdated, 1);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // Now verify that the data made the round trip
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUriWithMovieId(movieId),
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records returned from the movie query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("testUpdate: Error movie entry update",
                cursor, updatedMovieEntryValues);

        /*
            Updating Genre Entries: For e.g. lets say the genre Id is updated
         */

        ContentValues updatedGenreEntryValues = new ContentValues
                (TestUtilities.createGenreEntryValues());
        updatedGenreEntryValues.put(MovieContract.GenreEntry.COLUMN_GENRE_ID, 108);

        // Registering a Content Observer on the main CONTENT_URI. This will look for changes in
        // descendents too, for e.g. in WITH_GENRE_ID type URI as well
        tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver
                (MovieContract.GenreEntry.CONTENT_URI, true, tco);

        rowsUpdated = mContext.getContentResolver().update(
                MovieContract.GenreEntry.CONTENT_URI,
                updatedGenreEntryValues,
                MovieContract.GenreEntry.COLUMN_GENRE_ID + " = ? ",
                new String[] {"28"} //old value
        );

        assertEquals("Error: Failure in updating genre entry", rowsUpdated, 1);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // Now verify that the data made the round trip
        cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertTrue("Error: No records returned from the genres query", cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("testUpdate: Error genre entry update",
                cursor, updatedGenreEntryValues);

        cursor.close();
        assertTrue("Error: Cursor Not Closed", cursor.isClosed());

    }

    /*
        Will test only 1 type of BulkInsert, say for Genres, since the logic is same for both tables.
        We will insert current 19 records and see.
     */

    public void testBulkInsert() {

        /* Creating genreEntryContentValues array */

        ContentValues[] testBulkInsertGenreEntryValues = TestUtilities.createBulkInsertGenreValues();
        int BULK_INSERT_GENRE_RECORDS = testBulkInsertGenreEntryValues.length;

        // Register a ContentObserver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver
                (MovieContract.GenreEntry.CONTENT_URI, true, tco);

        // Bulk Insert
        int insertCount = mContext.getContentResolver().bulkInsert
                (MovieContract.GenreEntry.CONTENT_URI, testBulkInsertGenreEntryValues);
        assertTrue("Error: Inserting genre entries in bulk", insertCount >= 0);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // Verify that the data made the round-trip
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No records inserted", cursor.moveToFirst());
        assertTrue("Error: Mismatch in number of records inserted", cursor.getCount() == BULK_INSERT_GENRE_RECORDS);
        for (int i=0; i < BULK_INSERT_GENRE_RECORDS; i++, cursor.moveToNext()){
            TestUtilities.validateCurrentRecord("testBulkInsert: Error validating genre entry" + i,
                    cursor, testBulkInsertGenreEntryValues[i]);
        }

        cursor.close();
        assertTrue("Error: Cursor Not Closed", cursor.isClosed());

    }

    /*
        This tests the delete functionality. This uses the insert & query functions too.
        So, they must be written and tested before doing this,
     */
    public void testDelete(){
        testInsert();

        // Register a ContentObserver for our movies delete
        TestUtilities.TestContentObserver moviesContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver
                (MovieContract.MovieEntry.CONTENT_URI, true, moviesContentObserver);

        // Register a ContentObserver for our genres delete
        TestUtilities.TestContentObserver genresContentObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver
                (MovieContract.GenreEntry.CONTENT_URI, true, genresContentObserver);

        // delete All Records using the provider
        deleteAllRecordsFromProvider();

        // If either of these fail, i.e. we get an unexpected timeout message, it means that
        // delete operation is not notifying the registered ContentObservers
        moviesContentObserver.waitForNotificationOrFail();
        genresContentObserver.waitForNotificationOrFail();

        // Unregister all the content observers
        mContext.getContentResolver().unregisterContentObserver(moviesContentObserver);
        mContext.getContentResolver().unregisterContentObserver(genresContentObserver);
    }

    /*
        This helper function deletes all records from the database tables using the ContentProvider.
        It also queries the ContentProvider to make sure all records are successfully deleted.
        Note, to do so, we should have already implemented the delete and query functions of
        Content Provider. Once done replace deleteRecordsFromDB() with this.
     */
    public void deleteAllRecordsFromProvider() {

        int rowsDeleted;

        rowsDeleted = mContext.getContentResolver().delete(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null
        );
        assertTrue("Error: Records not deleted from the 'genres' table during delete", rowsDeleted >= 0);

        rowsDeleted = mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        assertTrue("Error: Records not deleted from the 'movies' table during delete", rowsDeleted >= 0);

        /* Query the tables to verify that records are deleted */

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from the 'genres' table during delete", 0, cursor.getCount());

        cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from the 'movies' table during delete", 0, cursor.getCount());

        cursor.close();
        assertTrue("Error: Cursor Not Closed", cursor.isClosed());
    }

    /* We want to end all tests with a clean slate */
    @Override
    public void tearDown() throws Exception {
        deleteAllRecords();
        super.tearDown();
    }

}