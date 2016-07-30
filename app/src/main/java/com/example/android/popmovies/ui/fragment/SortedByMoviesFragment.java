package com.example.android.popmovies.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.ui.listener.EndlessScrollListener;
import com.example.android.popmovies.utilities.PrefUtils;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class SortedByMoviesFragment extends MoviesFragment implements
        EndlessScrollListener.OnLoadMoreCallback  {

    private static final int VISIBLE_THRESHOLD = 10;
    private static final String STATE_QUERY_PAGE = "state_query_page";

    private EndlessScrollListener mEndlessScrollListener;

    /*
        We need a Behavior Subject here which can attach to the observable and consume the items
        it emits (List<MovieItem>), primarily because of 2 reasons:
        1. It is an Observer as well as an Observable. This helps us subscribe to movies on Activity
        creation. Later when it starts seeing items from 'discoverMovies' Observable, it emits them
        as we return it as an observable.
        2. It acts as an intermediary which passes the page information (from EndlessScrollListener,
        SwipeOnRefreshListener, when the view is created) to the API, and gets the appropriate
        Observable.
        3. It changes its 'behavior' (coincides with name, what say) depending on the situation, i.e.
        when the screen is rotated, it is passed on to 'subscribeToMovies' as an empty observable and
        subscribeToMovies method just completes (//Todo: Check if it completes/terminates).

     */
    private BehaviorSubject<Observable<List<MovieItem>>> mMoviesBehaviorSubject = BehaviorSubject.create();

    private int mQueryPage;

    // All concrete subclasses of Fragment must have a public/no-argument constructor to help the
    // framework re-instantiate this during state restore
    public SortedByMoviesFragment() {}

    @Override
    public void onAttach(Context context){
        Timber.e("in onAttach");
        super.onAttach(context);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Timber.e("in onViewCreated");

        mQueryPage = (savedInstanceState != null)
                ? savedInstanceState.getInt(STATE_QUERY_PAGE, 0)
                : 0;

        // Calling super at last, since initialization of RecyclerView requires 'currentPage' as a
        // must-have state information so that it can pass on this information (modified if needed
        // in case of rotation, i.e. currentPage - 1) to its pal 'EndlessScrollListener' which in
        // turn would be re-instantiated (on rotation) with all its past memories intact.

        // Note: 'initializeRecyclerView' of parent abstract class is overridden here
        // Note: 'GridlayoutManager' is passed so it can convey the item count/visibility states for
        // on-scrolling during re-instantiation.
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Timber.e("in onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        // Add a subscription to global saved stream of movies to reflect the latest changes in UI
        mCompositeSubscriptions.add(mMoviesHelper.getFavoredObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoredEvent -> {
                    int count = mMoviesAdapter.getItemCount();
                    for (int position = 0; position < count; position++) {
                        if (mMoviesAdapter.getItemId(position) == favoredEvent.movieId) {
                            mMoviesAdapter.getItem(position).setFavored(favoredEvent.isFavored);
                            mMoviesAdapter.notifyItemChanged(position);
                            Timber.d("Adapter to update item at position %d", position);
                        }
                    }
                }));

        // Now get movies
        subscribeToMovies();

        if (savedInstanceState == null) reloadContent();

    }

    private void reloadContent() {
        mSelectedPosition = -1;
        reAddOnScrollListener(mGridLayoutManager, mQueryPage = 0);
        getPage(1);
    }

    @Override
    public void onStart() {
        Timber.e("in onStart");
        super.onStart();
    }

    @Override
    public void onResume(){
        Timber.e("in onResume");
        super.onResume();
    }

    @Override
    public void onRefresh() {
        // For swipe to refresh
        if (PrefUtils.getSortPreference(getActivity()) != null) reloadContent();
    }

    @Override
    protected void initializeRecyclerView(){
        Timber.e("in initializeRecyclerView %d", mQueryPage);
        super.initializeRecyclerView();
        reAddOnScrollListener(mGridLayoutManager, mQueryPage);
    }

    private void reAddOnScrollListener(GridLayoutManager gridLayoutManager, int startPage) {
        if (mEndlessScrollListener != null) mRecyclerView.removeOnScrollListener(mEndlessScrollListener);

        mEndlessScrollListener = EndlessScrollListener.fromGridLayoutManager(
                gridLayoutManager, VISIBLE_THRESHOLD, startPage == 0 ? startPage : startPage - 1).setOnLoadMoreCallback(this);
        mRecyclerView.addOnScrollListener(mEndlessScrollListener);
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        getPage(page);
    }

    private void getPage(int page) {
        Timber.d("Page %d is loading.", page);
        Timber.e("Sort Preference is: %s", PrefUtils.getSortPreference(getActivity()));
        mMoviesBehaviorSubject.onNext(mMoviesRepository.getMoviesFromApiWithSavedDataAndGenreNames(
                PrefUtils.getSortPreference(getActivity()), page));

    }

    private void subscribeToMovies() {
        Timber.d("Subscribing to Movies");
        /* Observable.concat returns the Observable<T> from Observable<Observable<T>> here */
        mCompositeSubscriptions.add(Observable.concat(mMoviesBehaviorSubject)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mQueryPage++;

                    Timber.d(String.format("Page %d is loaded, %d new items ", mQueryPage, movies.size()));
                    if (mQueryPage == 1) mMoviesAdapter.clear();
                    mMoviesAdapter.add(movies);
                }, throwable -> {
                    Timber.e("Movies Loading failed.");
                    mSwipeRefreshLayout.setRefreshing(false);
                }, () -> {
                    Timber.i("Movies Loading completed.");
                    mSwipeRefreshLayout.setRefreshing(false);
                }));
    }

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
    public void onSaveInstanceState(Bundle outState) {
        Timber.d("in onSaveInstanceState");
        outState.putInt(STATE_QUERY_PAGE, mQueryPage);
        super.onSaveInstanceState(outState);

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
