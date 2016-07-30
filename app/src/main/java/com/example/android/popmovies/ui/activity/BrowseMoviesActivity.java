package com.example.android.popmovies.ui.activity;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.sync.PopMoviesSyncAdapter;
import com.example.android.popmovies.ui.fragment.FavoredMoviesFragment;
import com.example.android.popmovies.ui.fragment.MovieDetailsFragment;
import com.example.android.popmovies.ui.fragment.MoviesFragment;
import com.example.android.popmovies.ui.fragment.SortedByMoviesFragment;
import com.example.android.popmovies.utilities.PrefUtils;
import com.squareup.seismic.ShakeDetector;

import timber.log.Timber;

public class BrowseMoviesActivity extends BaseActivity implements MoviesFragment.OnMovieSelectedListener,
        ShakeDetector.Listener{

    private static final String MOVIES_FRAGMENT_TAG = "fragment_movies";
    private static final String MOVIE_DETAILS_FRAGMENT_TAG = "fragment_movie_details";
    private static final String FAVORITES_MODE = "favorites";

    private MoviesFragment mMoviesFragment;
    private String mSortPreference;
    private boolean mTwoPane;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.e("in onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_browse_movies);

        AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) mToolbar.getLayoutParams();
        layoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                 AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS | AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP);

        mTwoPane = findViewById(R.id.movie_details_container) != null;
        mSortPreference = PrefUtils.getSortPreference(this);

        // Shake it, shake it, shake it...baby !!! Oh, Lord...have mercy !!
        mShakeDetector = new ShakeDetector(this);
        mShakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));

        // Initialize SyncAdapter
        PopMoviesSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mMoviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentByTag(MOVIES_FRAGMENT_TAG);
        if (mMoviesFragment == null) {
            replaceMoviesFragment(mSortPreference.equals(FAVORITES_MODE)
                    ? new FavoredMoviesFragment()
                    : new SortedByMoviesFragment());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_browse_movies_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                mMoviesFragment.onRefresh();
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(MovieItem movieItem) {
        Timber.d("Movie %s selected", movieItem.getTitle());
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailsFragment.ARG_MOVIE_DETAILS, movieItem);
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(args);

            replaceMovieDetailsFragment(movieDetailsFragment);
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.MOVIE_ITEM, movieItem);
            startActivity(intent);
        }
    }

    private void replaceMovieDetailsFragment(MovieDetailsFragment movieDetailsFragment){
        getSupportFragmentManager().beginTransaction()
                .add(R.id.movie_details_container, movieDetailsFragment, MOVIE_DETAILS_FRAGMENT_TAG)
                .commit();
    }

    private void replaceMoviesFragment(MoviesFragment moviesFragment) {
        mMoviesFragment = moviesFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movies_container, moviesFragment, MOVIES_FRAGMENT_TAG)
                .commit();
    }

    @Override
    protected void onStart() {
        Timber.e("in onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Timber.e("in onResume");
        mShakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        super.onResume();
        String sortPreference = PrefUtils.getSortPreference(this);
        if (sortPreference != null && !sortPreference.equals(mSortPreference)){
            replaceMoviesFragment(sortPreference.equals(FAVORITES_MODE)
                    ? new FavoredMoviesFragment()
                    : new SortedByMoviesFragment());
            mSortPreference = sortPreference;
        }
    }

    @Override
    protected void onPause() {
        Timber.e("in onPause");
        mShakeDetector.stop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Timber.e("in onStop");

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Timber.e("in onDestroy");
        super.onDestroy();
    }

    @Override
    public void hearShake() {
        // Refresh on shaking
        Timber.e("in onhearShake");
        if (mMoviesFragment != null) mMoviesFragment.onRefresh();
    }

}
