package com.example.android.popmovies.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.android.popmovies.R;
import com.example.android.popmovies.model.MovieItem;
import com.example.android.popmovies.utilities.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sheshloksamal on 6/11/15.
 * {@link MovieAdapter} exposes a list of movies from a {@link android.database.Cursor} to a
 * {@link android.widget.GridView}
 */

public class MovieAdapter extends ArrayAdapter<MovieItem> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    @Bind(R.id.list_item_movie_poster_imageView) ImageView posterThumbNail;

    /**
     * This is our own custom constructor. There is no default constructor available in
     * <a>android.widget.ArrayAdapter</a>.
     * The context is used to inflate the layout file and List is the data we want to populate
     * into the lists.
     *
     * @param context    The current context. Used to inflate the layout file
     * @param movieItems A List of MovieItem objects to display in a list
     */

    public MovieAdapter(Activity context, List<MovieItem> movieItems) {
        // Here we initialize the ArrayAdapter's INTERNAL STORAGE for the context and the List.
        // The second argument is used when the ArrayAdapter is populating a single TextView
        // Because this is a custom adapter for an ImageView, the Adapter is not going to use this
        // second argument. So, it can be any value. Here, we set it to 0.
        super(context, 0, movieItems);
    }

    /**
     * Provides a view for an AdapterView(ListView, GridView etc.)
     * @param position      The AdapterView position that is requesting the View
     * @param convertView   The 'recycled view' to populate
     * @param parent        The parent ViewGroup that is used for inflation
     * @return              The View for the position in the AdapterView
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the MovieItem object from the ArrayAdapter at the appropriate position

        // Log.d(LOG_TAG, "getViewCalled" + "position: "+ position);
        // Why is getView() called multiple times in GridView?
        MovieItem movieItem = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View Object we are getting, then inflate the layout
        // If not, this View already has the layout implemented from a previous call to getView
        // and we modify the View widgets as usual.

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_movie_poster,
                    parent, false);
        }

        // Get a reference to the imageView
        ButterKnife.bind(this, convertView);

        posterThumbNail.setAdjustViewBounds(true);
        posterThumbNail.setPadding(0, 0, 0, 0);

        Glide.with(getContext())
                .load(Utils.getAdaptiveBasePosterPathURL(getContext()) + movieItem.getPosterPath())
                .error(R.mipmap.ic_launcher) // Handles the null case
                .crossFade()
                .thumbnail(0.1f)
                .into(posterThumbNail);

        return convertView;
    }
}
