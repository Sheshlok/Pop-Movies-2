package com.example.android.popmovies.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.android.popmovies.R;
import com.example.android.popmovies.data.provider.MovieContract;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sheshloksamal on 6/11/15.
 * {@link MoviesAdapter} exposes a list of movies from a {@link android.database.Cursor} to a
 * {@link android.widget.GridView}
 */

public class MoviesAdapter extends CursorAdapter {

    /**
     * When we use this constructor, we set c = null, and flags = 0, which means no cursor to
     * draw data from and no auto-requery. Cursor is given in the LoaderManager.LoaderCallback
     * implementation and the auto-requery is enabled through content observers registered in
     * the ContentProvider 'query' function.
     * @param context   The context.
     * @param c         The cursor from which to get the data.
     * @param flags     If true, the adapter will call requery() on the cursor, whenever it
     *                  changes so the most recent data is always displayed. Using true here is
     *                  discouraged.
     */
    public MoviesAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
    }

    final class ViewHolder{
        @Bind(R.id.movie_item_poster) ImageView posterView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * These Views are reused as needed as Adapters work with AbsListView(ListView, GridView etc.)
     * to populate them.
     * @param context    The context
     * @param cursor     The cursor passed in from the CursorLoader
     * @param parent     the parent of this view
     * @return View
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        int layoutId = R.layout.item_movie;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        // After we have inflated the view we set a ViewHolder tag on that view
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    /**
     * This is where we fill in the views with the contents from the cursor. Here, we are binding
     * the values in the cursor to the view.
     * @param view          View passed from newView(...)
     * @param context       The context
     * @param cursor        The cursor passed in from the CursorLoader
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Get back the ViewHolder object. Now, we immediately have references to all the views
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String moviePosterPath = cursor.getString
                (cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH));

        Glide.with(context)
                .load(moviePosterPath)
                .placeholder(R.color.cardview_light_background)
                .crossFade()
                .into(viewHolder.posterView);
    }

}
