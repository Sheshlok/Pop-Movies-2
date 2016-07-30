package com.example.android.popmovies.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.ui.adapter.MoviesAdapter;
import com.example.android.popmovies.ui.helper.MoviesHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 21/03/16.
 *
 * This is a parent fragment for movie items to be displayed in grid (recycler view).
 *
 * SwipeRefreshLayout implementation here doesn't show its full potential, as the TMDB data does not
 * have time-stamps (updated_time or created_time). For the feeds which are embellished with time-
 * stamps, such as Facebook API JSON feeds, only the changed items can be retrieved on
 * SwipeRefreshLayout. So, in a broader sense, it is just a dummy implementation, except for the
 * aesthetic reasons of course (loading spinners in the transient times of response-to-request).
 */
public abstract class MoviesFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, MoviesAdapter.OnMovieClickListener {

    public interface OnMovieSelectedListener {
        void onMovieSelected(MovieItem movieItem);
    }

    // For preserving state during rotation or any event when the app gets killed by the system
    private static final String SELECTED_POSITION_STATE = "selected_position_state";
    private static final String MOVIES_LIST_STATE = "movies_state";

    @BindView(R.id.movies_recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.movies_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;

    protected MoviesHelper mMoviesHelper;
    protected OnMovieSelectedListener mOnMovieSelectedListener;
    protected MoviesAdapter mMoviesAdapter;
    protected GridLayoutManager mGridLayoutManager;
    protected CompositeSubscription mCompositeSubscriptions;
    protected int mSelectedPosition = RecyclerView.NO_POSITION;

    @CallSuper @Override
    public void onAttach(Context context) {
        Timber.e("in onAttach");
        super.onAttach(context);
        try{
            mOnMovieSelectedListener = (OnMovieSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMovieSelectedListener");
        }

        mMoviesHelper = new MoviesHelper((Activity) context, mMoviesRepository);
    }

    @CallSuper @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @CallSuper @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        mCompositeSubscriptions = new CompositeSubscription();

        mSelectedPosition = (savedInstanceState != null)
                ? savedInstanceState.getInt(SELECTED_POSITION_STATE, -1)
                : -1;
        Timber.e("Selected position %d", mSelectedPosition);
        List<MovieItem> restoredMoviesList = (savedInstanceState != null)
                ? savedInstanceState.getParcelableArrayList(MOVIES_LIST_STATE)
                : new ArrayList<>();

        mMoviesAdapter = new MoviesAdapter(getActivity(), restoredMoviesList);
        mMoviesAdapter.setOnMovieClickListener(this);

        initializeSwipeRefreshLayout();
        initializeRecyclerView();

    }

    @CallSuper @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_POSITION_STATE, mSelectedPosition);
        outState.putParcelableArrayList(MOVIES_LIST_STATE, new ArrayList<>(mMoviesAdapter.getItems()));
        super.onSaveInstanceState(outState);
    }

    @CallSuper @Override
    public void onDestroyView() {
        mCompositeSubscriptions.unsubscribe();
        super.onDestroyView();
    }

    private void initializeSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @CallSuper
    protected void initializeRecyclerView(){
        mGridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.movies_columns));
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mMoviesAdapter);

        if (mSelectedPosition != -1) mRecyclerView.scrollToPosition(mSelectedPosition);
    }

    // For SwipeRefreshLayout
    @Override
    public abstract void onRefresh();


    @Override
    public void onContentClicked(@NonNull MovieItem movieItem, int position){
        mSelectedPosition = position;
        mOnMovieSelectedListener.onMovieSelected(movieItem);
    }

    @Override
    public void onFavoredClicked(@NonNull MovieItem movieItem, int position) {
        boolean favored = !movieItem.isFavored();
        Timber.v("onFavoredClicked: favored %s", favored);
        mMoviesHelper.setMovieFavored(movieItem, favored);
        if (favored) showToast("Saved to favorites");
        else showToast("Removed from favorites");

    }

}
