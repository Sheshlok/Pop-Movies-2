package com.example.android.popmovies.data.repository;

import com.example.android.popmovies.data.model.Genre;

import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * Created by sheshloksamal on 30/03/16.
 * An interface which activities and fragments can use to get data as needed without
 * understanding how the data is retrieved. Storing data is taken care of in the implementation
 * itself
 *
 * @see GenresRepositoryImpl
 */
public interface GenresRepository {

    Observable<Map<Integer, String>> getGenresMap();

    /* For syncAdapter */
    Observable<List<Genre>> getGenresFromApiWithSave();

    /* Database operations - No database operations will be needed. Lets keep it for now */
    Observable<List<Genre>> getSavedGenres();

}
