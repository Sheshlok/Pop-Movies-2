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

package com.example.android.popmovies.data.provider.meta;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.provider.MovieContract;
import com.example.android.popmovies.utilities.DbUtils;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.functions.Func1;

/**
 * Created by sheshloksamal on 30/03/16.
 * * @see  <a href="https://github.com/square/sqlbrite/blob/master/sample%2Fsrc%2Fmain%2Fjava%2Fcom%2Fexample%2Fsqlbrite%2Ftodo%2Fdb%2FTodoItem.java">
 *     SqlBrite Sample db>TodoItem.java</a>
 */
public interface MovieItemMeta {

    /* Makes ContentValues from a 'MovieItem' object for insertion into the database */
    final class Builder {

        private final ContentValues contentValues = new ContentValues();

        public Builder id(String movieId) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            return this;
        }

        public Builder title(String title) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
            return this;
        }

        public Builder posterPath(String posterPath) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, posterPath);
            return this;
        }

        public Builder backdropPath(String backdropPath) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH, backdropPath);
            return this;
        }

        public Builder userRating(String userRating) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING, userRating);
            return this;
        }

        public Builder releaseDate(String releaseDate) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, releaseDate);
            return this;
        }

        public Builder synopsis(String overview) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, overview);
            return this;
        }

        public Builder favored(boolean favored) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_FAVORED, favored);
            return this;
        }

        public Builder genreIds(String genreIds) {
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_GENRE_IDS, genreIds);
            return this;
        }

        public Builder movieItem(MovieItem movieItem) {
            return id(movieItem.getMovieId())
                    .title(movieItem.getTitle())
                    .posterPath(movieItem.getPosterPath())
                    .backdropPath(movieItem.getBackdropPath())
                    .synopsis(movieItem.getSynopsis())
                    .userRating(movieItem.getUserRating())
                    .releaseDate(movieItem.getReleaseDate())
                    .favored(movieItem.isFavored())
                    .genreIds(movieItem.makeGenreIdsList());
        }

        public ContentValues build() {
            return contentValues;
        }
    }

    /* Projection for 'movies' table */
    String[] PROJECTION = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH,
            MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_FAVORED,
            MovieContract.MovieEntry.COLUMN_MOVIE_GENRE_IDS
    };

    Func1<SqlBrite.Query, List<MovieItem>> PROJECTION_MAP = query -> {
        Cursor cursor = query.run();
        try {
            List<MovieItem> movies = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()){
                movies.add(new MovieItem()
                        .setMovieId(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_ID))
                        .setTitle(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_TITLE))
                        .setPosterPath(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH))
                        .setBackdropPath(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_BACKDROP_PATH))
                        .setSynopsis(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS))
                        .setUserRating(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING))
                        .setReleaseDate(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE))
                        .setFavored(DbUtils.getBoolean(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_FAVORED))
                        .putGenreIdsList(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_GENRE_IDS)));
            }
            return movies;
        } finally {
             cursor.close();
        }
    };


    String[] MOVIE_ID_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID
    };

    Func1<SqlBrite.Query, Set<String>> MOVIE_ID_PROJECTION_MAP = query -> {
        Cursor cursor = query.run();
        try {
            Set<String> movieIds = new HashSet<>(cursor.getCount());
            while (cursor.moveToNext()) {
                movieIds.add(DbUtils.getString(cursor, MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            }
            return movieIds;
        } finally {
            cursor.close();
        }
    };

}
