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

import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.popmovies.data.provider.MovieContract;

/**
 * Created by sheshloksamal on 12/03/16.
 * We want to check the URIs in both the tables
 */
public class TestMovieContract extends AndroidTestCase {

    private static final String movieId = "293660"; // Deadpool
    private static final int genreId = 12;

    /** Since we want to start all tests with a clean slate */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testBuildMovieWithId(){
        Uri movieWithMovieIdUri = MovieContract.MovieEntry.buildMovieUriWithMovieId(movieId);
        assertNotNull("Error: Null Uri returned. You must correct buildMovieUriWithMovieId in" +
                "MovieContract", movieWithMovieIdUri);
        assertEquals("Error: movieWithMovieIdUri does not match our expected result",
                movieWithMovieIdUri.toString(), "content://com.example.android.popmovies/movies/293660");

        String fetchedMovieId = MovieContract.MovieEntry.getMovieIdFromUri(movieWithMovieIdUri);
        assertEquals("Error: MovieId not properly appended to the end of Uri",
                movieId, fetchedMovieId);
    }

    public void testBuildGenreWithId(){
        Uri genreWithGenreIdUri = MovieContract.GenreEntry.buildGenreUriWithGenreId(genreId);
        assertNotNull("Error: Null Uri returned. You must correct buildGenreUriWithGenreId in" +
                "MovieContract", genreWithGenreIdUri);
        assertEquals("Error: genreWithGenreIdUri does not match our expected result",
                genreWithGenreIdUri.toString(), "content://com.example.android.popmovies/genres/12");

        int fetchedGenreId = MovieContract.GenreEntry.getGenreIdFromUri(genreWithGenreIdUri);
        assertEquals("Error: GenreId not properly appended to the end of Uri",
                genreId, fetchedGenreId);
    }

    /* We want to end all tests with a clean slate */
    @Override
    public void tearDown() throws Exception{
        super.tearDown();
    }

}
