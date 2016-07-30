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

package com.example.android.popmovies.data.repository;

import android.content.ContentResolver;
import android.content.ContentValues;

import com.example.android.popmovies.data.NetworkingAPI.TmdbApi;
import com.example.android.popmovies.data.model.Genre;
import com.example.android.popmovies.data.provider.MovieContract;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.squareup.sqlbrite.BriteContentResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 30/03/16.
 *
 */
public class GenresRepositoryImpl implements GenresRepository{

    private final TmdbApi mTmdbApi;
    private final BriteContentResolver mBriteContentResolver;
    private final ContentResolver mContentResolver;

    public GenresRepositoryImpl(TmdbApi tmdbApi, BriteContentResolver briteContentResolver,
                                ContentResolver contentResolver) {
        this.mTmdbApi = tmdbApi;
        this.mBriteContentResolver = briteContentResolver;
        this.mContentResolver = contentResolver;
    }

    /*
    No need of calling subscribeOn(Schedulers.io) since items emitted from the observable returned
    from 'createQuery' use the 'Schedulers.io' supplied to 'SqlBrite.wrapContentProvider'. Thus,
    contentResolver triggers are inherently asynchronous.
    */
    @RxLogObservable
    public Observable<List<Genre>> getSavedGenres() {
        Timber.e("in getSavedGenres");
        return mBriteContentResolver.createQuery(
                MovieContract.GenreEntry.CONTENT_URI, Genre.PROJECTION, null, null, null, true)
                .map(Genre.CURSOR_TO_GENRE_LIST_PROJECTION_MAP);
    }

    // This is used by the SyncAdapter as a periodic call
    @Override @RxLogObservable
    public Observable<List<Genre>> getGenresFromApiWithSave() {
        Timber.e("in getGenresFromApiWithSave");
        return mTmdbApi.getGenres()
                .timeout(3, TimeUnit.SECONDS)
                .retry(2)
                .onErrorResumeNext(Observable.<Genre.Response>empty())
                .map(response -> response.genres)
                .doOnNext(this::bulkInsertGenresIntoDB)
                .subscribeOn(Schedulers.io());

    }

    /*
       Merge the DB and API streams and emits the first item (satisfying the criteria) - from
       whichever stream - and then terminate. Order is important since it's the lookup order.
     */

    @RxLogObservable
    private Observable<List<Genre>> getGenres() {
        Timber.e("in getGenres");
        return Observable.merge(getSavedGenres(), getGenresFromApiWithSave())
                .first(genres -> genres != null && genres.size() !=0 );
    }


    @Override @RxLogObservable
    public Observable<Map<Integer, String>> getGenresMap() {
        return getGenres()
                .map(Genre.PROJECTION_MAP);
    }

    private void bulkInsertGenresIntoDB(List<Genre> genres) {
        List<ContentValues> bulkInsertGenreValues = new ArrayList<>();
        for (int i = 0; i < genres.size(); i++) {
            bulkInsertGenreValues.add(new Genre.Builder()
                    .genre(genres.get(i))
                    .build());
        }
        int genresInserted = mContentResolver.bulkInsert(MovieContract.GenreEntry.CONTENT_URI,
                bulkInsertGenreValues.toArray(new ContentValues[bulkInsertGenreValues.size()]));
        if (genresInserted != genres.size()) {
            Timber.e(genresInserted + "/" + genres.size() + "inserted");
        } else {
            Timber.e(genresInserted + " genres inserted into DB");
        }
    }

}
