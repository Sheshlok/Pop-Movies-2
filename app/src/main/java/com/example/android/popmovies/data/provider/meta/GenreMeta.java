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

import com.example.android.popmovies.data.model.Genre;
import com.example.android.popmovies.data.provider.MovieContract;
import com.example.android.popmovies.utilities.DbUtils;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Func1;

/**
 * Created by sheshloksamal on 30/03/16.
 * * @see  <a href="https://github.com/square/sqlbrite/blob/master/sample%2Fsrc%2Fmain%2Fjava%2Fcom%2Fexample%2Fsqlbrite%2Ftodo%2Fdb%2FTodoItem.java">
 *     SqlBrite Sample db>TodoItem.java</a>
 */
public interface GenreMeta {

    /* Makes ContentValues from a 'Genre' object for insertion into the database */

    final class Builder {
        private final ContentValues contentValues = new ContentValues();

        public Builder id(int id) {
            contentValues.put(MovieContract.GenreEntry.COLUMN_GENRE_ID, id);
            return this;
        }

        public Builder name(String name) {
            contentValues.put(MovieContract.GenreEntry.COLUMN_GENRE_NAME, name);
            return this;
        }

        public Builder genre(Genre genre) {
            return id(genre.getId())
                    .name(genre.getName());
        }

        public ContentValues build() {
            return contentValues;
        }
    }

    /* Projection for 'genres' table */
    String[] PROJECTION = {
            MovieContract.GenreEntry.COLUMN_GENRE_ID,
            MovieContract.GenreEntry.COLUMN_GENRE_NAME
    };

    Func1<SqlBrite.Query, List<Genre>> CURSOR_TO_GENRE_LIST_PROJECTION_MAP = query -> {
        Cursor cursor = query.run();
        try {
            List<Genre> genres = new ArrayList<>(cursor.getCount());
            while (cursor.moveToNext()) {
                genres.add(new Genre()
                        .setId(DbUtils.getInt(cursor, MovieContract.GenreEntry.COLUMN_GENRE_ID))
                        .setName(DbUtils.getString(cursor, MovieContract.GenreEntry.COLUMN_GENRE_NAME)));
            }
            return genres;
        } finally {
            cursor.close();
        }

    };

    Func1<List<Genre>, Map<Integer, String>> PROJECTION_MAP = genreList -> {
        Map<Integer, String> genresMap = new HashMap<>(genreList.size());
        for (Genre genre: genreList) {
            int id = genre.getId();
            String name = genre.getName();
            genresMap.put(id, name);
        }
        return genresMap;
    };
}
