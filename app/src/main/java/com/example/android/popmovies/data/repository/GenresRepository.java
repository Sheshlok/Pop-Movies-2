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
