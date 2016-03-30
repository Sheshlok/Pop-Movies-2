package com.example.android.popmovies.ui.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popmovies.R;
import com.example.android.popmovies.data.provider.MovieContract;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    public static final String DETAIL_URI = "MovieURI";
    private Uri mMovieUri;
    // A single activity/fragment can have multiple loaders and they are differentiated by these IDs
    private static final int MOVIE_DETAIL_LOADER = 0;

    @Bind(R.id.movie_name_header) TextView mMovieTitleView;
    @Bind(R.id.movie_poster_image_thumbnail) ImageView mMoviePosterView;
    @Bind(R.id.movie_release_year) TextView mMovieReleaseYearView;
    @Bind(R.id.movie_rating) TextView mMovieRatingView;
    @Bind(R.id.movie_plot_synopsis) TextView mMoviePlotSynopsisView;


    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        // Get a reference to all Views
        ButterKnife.bind(this, rootView);

        Bundle args = getArguments();
        if (args != null ) {
            mMovieUri = args.getParcelable(DETAIL_URI);
            //Toast.makeText(getActivity(), movieItem.toString(), Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Log.e(LOG_TAG, "in onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_DETAIL_LOADER, null, this);
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.e(LOG_TAG, "in onStart");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.e(LOG_TAG, "in onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.e(LOG_TAG, "in onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.e(LOG_TAG, "in onStop");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.e(LOG_TAG, "in onDestroyView");

        // Release reference to Views
        ButterKnife.unbind(this);
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.e(LOG_TAG, "in onDetach");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle){
        Log.e(LOG_TAG, "in onCreateLoader");
        return new CursorLoader(
                getActivity(),
                mMovieUri, // MovieUri with MovieID
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {

        if (data != null & data.moveToFirst()) {
             /* Movie Name */
            String movieName = data.getString(data.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
            Log.e(LOG_TAG, movieName);
            if (!movieName.equals("null")) {
                mMovieTitleView.setText(movieName);
            } else {
                mMovieTitleView.setText("Movie Name N/A");
            }

            /* Movie Poster */
            String moviePosterPath = data.getString(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH));

            Glide.with(getContext())
                    .load(moviePosterPath)
                    .placeholder(R.color.cardview_light_background) // Handles the null case
                    .crossFade()
                    .into(mMoviePosterView);

            /* Movie Release Year */
            String movieReleaseDate = data.getString(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
            if (!(movieReleaseDate.equals("null"))) {
                mMovieReleaseYearView.setText(movieReleaseDate.substring(0, 4));
            } else {
                mMovieReleaseYearView.setText("Year of Release N/A");
            }

            /* Movie Rating */
            String movieRating = data.getString(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING));
            if (!(movieRating.equals("null"))) {
                mMovieRatingView.setText(movieRating + "/10");
            } else {
                mMovieRatingView.setText("Rating N/A");
            }

            /* Movie Synopsis */
            String movieSynopsis = data.getString(data.getColumnIndex(
                    MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS));
            if (!(movieSynopsis.equals("null"))) {
                mMoviePlotSynopsisView.setText(movieSynopsis);
            } else {
                mMoviePlotSynopsisView.setText("Synopsis N/A");
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.e(LOG_TAG, "in onLoaderReset");
    }

}
