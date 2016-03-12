package com.example.android.popmovies.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.android.popmovies.EndlessScrollListener;
import com.example.android.popmovies.MovieItem;
import com.example.android.popmovies.MovieItemAdapter;
import com.example.android.popmovies.R;
import com.example.android.popmovies.utilities.Connectivity;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieListFragment extends Fragment {

    private final String LOG_TAG = MovieListFragment.class.getSimpleName();
    private MovieItemAdapter mMovieAdapter;
    private ArrayList<MovieItem> mMovieList;
    private String mQueryPage;
    private String mSortPreference;
    private boolean mSortPreferenceChanged;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            mMovieList = new ArrayList<MovieItem>();
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList("movies");
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        mSortPreference = pref.getString(getString(R.string.pref_sort_by_key),
                        getString(R.string.pref_sort_by_default));
        if(savedInstanceState == null || !savedInstanceState.containsKey("lastQueryPage")) {
            mQueryPage = "1";
        } else {
            mQueryPage = savedInstanceState.getString("lastQueryPage");
        }
    }

    public MovieListFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("movies", mMovieList);
        outState.putString("lastQueryPage", mQueryPage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The ArrayAdapter will take items from a source file and use it to populate the
        // GridView it is attached to

        mMovieAdapter = new MovieItemAdapter(getActivity(), mMovieList);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView and attach this adapter to it
        GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        gridView.setAdapter(mMovieAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("movie_item_details", mMovieAdapter.getItem(position));

                // Added to retain the instance state of this fragment
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // Not good
                startActivity(intent);
            }
        });

        // Attaching the listener to enable Infinite Scrolling
        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the adapter.
                // This needs to send out a network request and append new data items to the adapter.
                // Use the page as a parameter in your API request via updateMovieList()

                // Log.v(LOG_TAG, " The page number is: " + String.valueOf(page));
                // Log.v(LOG_TAG, " The page number is: " + mQueryPage);

                // This piece of code optimizes the number of HTTP requests on device rotation
                // On device rotation, since the activity is destroyed, page starts from 2 again

                // Log.v(LOG_TAG, " The page number is: " + mQueryPage);

                while (page <= Integer.parseInt(mQueryPage)){
                    page++;
                    // Log.v(LOG_TAG, " The page number is: " + String.valueOf(page));
                }

                mQueryPage = String.valueOf(page);
                // Log.v(LOG_TAG, " The page number is: " + mQueryPage);

                updateMovieList();


                return true; //ONLY if more data is actually being loaded, false otherwise.
            }
        });
        return rootView;
    }

    private void updateMovieList() {
        // Before trying to pass on the URL param make sure that there is network connection
        // Use the connectivity utility class
        if (Connectivity.isConnected(getActivity())) {

            // Use of SharedPreferences to get the 'Sort By' preference
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
            mSortPreference = sharedPref.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));
            // Log.d(LOG_TAG, "Sort Preference is: " + sortPreference);

            // Executing network tasks on an Async Thread

        } else {
            Toast.makeText(getActivity(), "No network connection available", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreference = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortPreference = sharedPreference.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default));
        mSortPreferenceChanged = !(sortPreference.equals(mSortPreference));

        // This to minimize the number of "HTTP handshakes". The HTTP requests only go through via
        // updateMovieList() if
        // 1. mMovieList.size() == 0: The activity is started for the first time. In case of
        // orientation change, mMovieList is passed on as parcelable and is attached to the adapter
        // again. We don't need to recreate same HTTP requests again.
        // 2. !(sortPreference.equals(mSortPreference): This is for settings change.
        if (mMovieList.size() == 0) {
            updateMovieList();
        }

        if (mSortPreferenceChanged) {
            mQueryPage = "1";
            updateMovieList();
        }
    }

//    //This Async Thread takes in String as a parameter and returns a MovieItem[]
//    public class FetchMovieList extends AsyncTask<String, Void,ArrayList<MovieItem>> {
//
//        private final String LOG_TAG = FetchMovieList.class.getSimpleName();
//
//        /**
//         * Takes the String representing the complete data in JSON format, parse it to find
//         * attributes of MovieItem objects, and return the array of MovieItems
//         * @param movieListJSONStr String representing the complete data (from API call) in JSON format
//         * @return                 List of MovieItem objects with attributes parsed from the JSON string
//         * @throws JSONException
//         */
//        private ArrayList<MovieItem> getMovieListItems(String movieListJSONStr) throws JSONException {
//
//            // These are the names of the JSON Objects that need to be extracted
//
//            final String TMDB_RESULT = "results";
//            final String TMDB_ORIGINAL_TITLE = "original_title";
//            final String TMDB_MOVIE_POSTER = "poster_path";
//            final String TMDB_PLOT_SYNOPSIS = "overview";
//            final String TMDB_USER_RATING = "vote_average";
//            final String TMDB_RELEASE_DATE = "release_date";
//            final String BASE_POSTER_PATH_SOURCE = "image.tmdb.org";
//            String size;
//
//            // Making the image adaptive to connection speed
//            if(Connectivity.isConnectedFast(getActivity())){
//                size = "w370"; // Works for 2x displays also
//            } else {
//                size = "w185";
//            }
//
//            Uri.Builder builder = new Uri.Builder();
//            builder.scheme("https")
//                    .authority(BASE_POSTER_PATH_SOURCE)
//                    .appendPath("t")
//                    .appendPath("p")
//                    .appendPath(size)
//                    .build();
//
//            final String BASE_POSTER_PATH_URL = builder.toString();
//
//            // Create an empty list of MovieItem objects
//            List<MovieItem> movieItemList = new ArrayList<MovieItem>();
//
//            // Parse the JSON String for JSON Objects
//            JSONObject movieList = new JSONObject(movieListJSONStr);
//            JSONArray movieArray = movieList.getJSONArray(TMDB_RESULT);
//
//            int i = 0;
//            while (i < movieArray.length()) {
//
//                // Create a temporary MovieItem object to fill in the parsed attributes
//                MovieItem tempMovieItem = new MovieItem("", "", "", "", "");
//
//                // Get the JSON Object representing the item
//                JSONObject movieItem = movieArray.getJSONObject(i);
//
//                // parse Data from JSON Object and initialize the tempMovieItem Object attributes
//                tempMovieItem.originalTitle = movieItem.getString(TMDB_ORIGINAL_TITLE);
//                tempMovieItem.moviePoster = BASE_POSTER_PATH_URL + movieItem.getString(TMDB_MOVIE_POSTER);
//                tempMovieItem.aPlotSynopsis = movieItem.getString(TMDB_PLOT_SYNOPSIS);
//                tempMovieItem.userRating = movieItem.getString(TMDB_USER_RATING);
//                tempMovieItem.releaseDate = movieItem.getString(TMDB_RELEASE_DATE);
//
//                movieItemList.add(tempMovieItem);
//                i++;
//            }
//
//            /*
//            for (MovieItem item: movieItemList) {
//                Log.v(LOG_TAG, "MovieItem is: " + item  ); //toString() method is automatically called
//            }
//            */
//
//            return (ArrayList) movieItemList;
//        }
//
//        @Override
//        protected ArrayList<MovieItem> doInBackground(String...params) {
//            // Log.v(LOG_TAG, "AsyncTask called");
//            if (params.length == 0) {
//                return null;
//            }
//            // These two need to be declared outside the try/catch block so that
//            // they can be closed later
//            HttpsURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string
//            String movieListJSONStr = null;
//            String port = "3";
//            String reference = "discover";
//            String format = "movie";
//
//            //API Key
//            //TODO Insert your API key here. Get it from @link: https://www.themoviedb.org/
//            String apiKey = "[YOUR API KEY]";
//
//            try {
//                // Construct the URL for the TMDB query
//                // Possible parameters are available at TMDB's apiary documentation page
//                // http://docs.themoviedb.apiary.io/#reference/discover/discovermovie/get
//                final String API_SOURCE = "api.themoviedb.org";
//                final String QUERY_SORT_PARAM = "sort_by";
//                final String QUERY_PAGE = "page";
//                final String QUERY_API_KEY_PARAM = "api_key";
//
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("https")
//                        .authority(API_SOURCE)
//                        .appendPath(port)
//                        .appendPath(reference)
//                        .appendPath(format)
//                        .appendQueryParameter(QUERY_SORT_PARAM, params[0])
//                        .appendQueryParameter(QUERY_PAGE, params[1])
//                        .appendQueryParameter(QUERY_API_KEY_PARAM,apiKey)
//                        .build();
//
//                // Log.v(LOG_TAG, "Built URL: " + builder.toString());
//
//                URL url = new URL(builder.toString());
//
//                // Create the request to TMDB, and then make the connection
//                urlConnection = (HttpsURLConnection)  url.openConnection();
//                urlConnection.setReadTimeout(10000 /* milliseconds */);
//                urlConnection.setConnectTimeout(15000 /* milliseconds */);
//                urlConnection.setRequestMethod("GET");
//                urlConnection.setDoInput(true);
//                urlConnection.connect();
//
//                /* Log.v(LOG_TAG, "The response is: " +
//                        urlConnection.getResponseMessage() + " " +
//                        urlConnection.getResponseCode()); */
//
//                // Reading the inputStream in a buffered way, i.e. Attaching a BufferedReader to it
//                InputStream inputStream = urlConnection.getInputStream();
//                if (inputStream == null) {
//                    //nothing to do
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                // Reading from Buffered Reader object and storing it in a String Buffer (thread-safe)
//                StringBuffer buffer = new StringBuffer();
//                String line;
//                while((line = reader.readLine()) != null) {
//                    // Since it is a JSON, adding a new line is not necessary (it won't affect
//                    // parsing later). But it does make debugging a lot easier if you print out the
//                    // completed buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty. No point in parsing
//                    return null;
//                }
//
//                movieListJSONStr = buffer.toString();
//                // Log.v(LOG_TAG, "MovieList JSON String: " + movieListJSONStr);
//
//
//            } catch (MalformedURLException e) {
//                Log.e(LOG_TAG, "Error", e);
//                e.printStackTrace();
//                return null;
//
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error", e);
//                e.printStackTrace();
//                return null;
//
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error Closing Stream", e);
//                    }
//                }
//            }
//
//            try {
//                return getMovieListItems(movieListJSONStr);
//            } catch(JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//
//            // This will only happen when there is an error parsing or getting the forecast
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<MovieItem> movieItemList) {
//            if (movieItemList != null) {
//                // Clear the adapter only when the activity starts afresh (device rotation taken
//                // care of by modifying the outState bundle) OR when the sort Preference is changed
//                if (mMovieList.size() == 0) {
//                    mMovieAdapter.clear();
//                }
//
//                if (mSortPreferenceChanged) {
//                    mMovieAdapter.clear();
//                    mSortPreferenceChanged = false;
//                }
//                for (MovieItem item: movieItemList) {
//                    mMovieAdapter.add(item);
//                }
//            }
//
//         }
//    }
}
