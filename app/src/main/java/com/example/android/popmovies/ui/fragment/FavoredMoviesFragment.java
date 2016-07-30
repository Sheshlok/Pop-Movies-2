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
