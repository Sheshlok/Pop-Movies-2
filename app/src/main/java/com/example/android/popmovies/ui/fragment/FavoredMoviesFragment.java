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

package com.example.android.popmovies.ui.fragment;

import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 01/04/16.
 *
 */

public class FavoredMoviesFragment extends MoviesFragment {

    @Override
    public void onRefresh() {
        subscribeToFavoriteMovies();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscribeToFavoriteMovies();
    }

    private void subscribeToFavoriteMovies() {
        mCompositeSubscriptions.add(mMoviesRepository.getSavedMoviesWithGenreNames()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteMovies -> {
                            Timber.d("Favorite movies loaded, %d new items", favoriteMovies.size());
                            mSwipeRefreshLayout.setRefreshing(false);
                            mMoviesAdapter.set(favoriteMovies);
                            if (favoriteMovies.size() == 0)
                                showToast("No favorite movies selected yet !!!");
                        }, throwable -> {
                            Timber.e("Favorite movies loading failed");
                            Timber.e("Error: %s", throwable);
                        }, () -> Timber.i("Favorite movies loaded")
                ));
    }

    //--------------------------------------To see life-cycles -------------------------------------

    @Override
    public void onPause(){
        Timber.e("in onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Timber.e("in onStop");
        super.onStop();
    }

    @Override
    public void onDestroyView(){
        Timber.e("in onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDetach(){
        Timber.e("in onDetach");
        super.onDetach();
    }


}
