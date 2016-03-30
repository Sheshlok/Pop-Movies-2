package com.example.android.popmovies.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.provider.MovieContract;
import com.example.android.popmovies.data.sync.FetchMovieData;
import com.example.android.popmovies.ui.activity.DetailActivity;
import com.example.android.popmovies.ui.adapter.MovieAdapter;
import com.example.android.popmovies.ui.listener.EndlessScrollListener;

import butterknife.Bind;
import butterknife.OnItemClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class SortedByMoviesFragment extends BaseFragment implements
        EndlessScrollListener.OnLoadMoreCallback, LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = SortedByMoviesFragment.class.getSimpleName();
    private MovieAdapter mMovieAdapter;
    private String mQueryPage;

    @Bind(R.id.grid_view) GridView gridView;
    @OnItemClick(R.id.grid_view) void onItemClick(int position) {

        Cursor cursor = (Cursor) mMovieAdapter.getItem(position);
        if (cursor != null) {
            String movieId = cursor.getString
                    (cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
            Uri movieUri = MovieContract.MovieEntry.buildMovieUriWithMovieId(movieId);
            Intent intent = new Intent(getActivity(), DetailActivity.class)
                    .setData(movieUri);

            // Added to retain the instance state of this fragment
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            startActivity(intent);
        } else {
            Log.e(LOG_TAG, "No cursor");
        }
    }

    // A single activity/fragment can use multiple loaders and they are differentiated by these IDs
    private static final int MOVIE_LOADER = 0;

    // All concrete subclasses of Fragment must have a public/no-argument constructor to help the
    // framework re-instantiate this during state restore

    public SortedByMoviesFragment() {}

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.e(LOG_TAG, "in onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(LOG_TAG, "in onCreate");

        if(savedInstanceState == null || !savedInstanceState.containsKey("lastQueryPage")) {
            mQueryPage = "1";
        } else {
            mQueryPage = savedInstanceState.getString("lastQueryPage");
        }
        // Add this line in order for this fragment to handle menu events
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Log.e(LOG_TAG, "in onCreateOptionsMenu");
        menuInflater.inflate(R.menu.menu_movie_list_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.action_refresh) {
            FetchMovieData.subscribeToMovies(getActivity(), mQueryPage);
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take items from a source file and use it to populate the
        // GridView it is attached to

        mMovieAdapter = new MovieAdapter(getContext(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Attach adapter to the gridView
        gridView.setAdapter(mMovieAdapter);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Log.e(LOG_TAG, "in onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
    }


    @Override
    public void onStart() {
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
    public void onStop() {
        super.onStop();
        Log.e(LOG_TAG, "in onStop");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.e(LOG_TAG, "in onSaveInstanceState");
        outState.putString("lastQueryPage", mQueryPage);
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        Log.e(LOG_TAG, "in onDestroyView");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        Log.e(LOG_TAG, "in onDetach");
    }

    private void reAddOnScrollListener(GridLayoutManager gridLayoutManager, int startPage) {
        if (mEndlessScollListener != null) mRecyclerView.removeO
    }

    // --------------------------------Loaders-----------------------------------------------------

    /*
        A CursorLoader is a Loader that queries a ContentResolver and returns a Cursor. Loaders,
        in particular, CursorLoaders, are expected to keep their data across the Activity/Fragment's
        'onStop' or 'onStart' methods, so that when users return to the application, they don't have
        to wait for the data to reload. In addition, a loader owns its data and takes care of it.
        So, we never call cursor.close() ourselves.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Log.e(LOG_TAG, "in onCreateLoader");
        // This fragment only has 1 loader, so we don't care about the id
        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    /*
        Called when the previously created loader has finished its load
     */
    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor data) {
        Log.e(LOG_TAG, "in onLoadFinished");
        mMovieAdapter.swapCursor(data);
    }

    /*
        Called when the previously created loader is being reset, thus making its data unavailable.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        Log.e(LOG_TAG, "in onLoaderReset");
        mMovieAdapter.swapCursor(null);

    }
}
