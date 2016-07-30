package com.example.android.popmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.example.android.popmovies.R;
import com.example.android.popmovies.ui.fragment.MovieDetailsFragment;

import timber.log.Timber;

public class MovieDetailsActivity extends BaseActivity {

    public static final String MOVIE_ITEM = "movie_item";
    private static final String MOVIE_DETAILS_FRAGMENT_TAG = "fragment_movie_details";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.e("in onCreate");
        Timber.e("savedInstanceState is null? %s", savedInstanceState == null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        /* Critical to start the Fragment */
        if (savedInstanceState == null) {

            // Get a detail fragment in the container through a fragment transaction
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailsFragment.ARG_MOVIE_DETAILS,
                    getIntent().getParcelableExtra(MOVIE_ITEM));
            MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
            movieDetailsFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, movieDetailsFragment, MOVIE_DETAILS_FRAGMENT_TAG)
                    .commit();
        }

        if (mToolbar != null) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                mToolbar.setNavigationOnClickListener(view -> finishAfterTransition());
//            } else {
//                mToolbar.setNavigationOnClickListener(view -> finish());
//            }

            // Get the support Action Bar
            ActionBar actionBar = getSupportActionBar();

            if (actionBar != null) {
                actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    /* Provides up navigation to the same instance of the Parent Activity, with its state intact */
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch(menuItem.getItemId()){
            // Respond to action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpTo(this,
                        NavUtils.getParentActivityIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
