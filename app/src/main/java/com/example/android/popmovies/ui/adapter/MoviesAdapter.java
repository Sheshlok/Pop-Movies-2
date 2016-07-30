/*
 *  Copyright (C) 2016 Sheshlok Samal
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package com.example.android.popmovies.ui.adapter;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popmovies.R;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.utilities.Lists;
import com.github.florent37.glidepalette.GlidePalette;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 6/11/15.
 *
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieHolder> {

    public interface OnMovieClickListener{
        void onContentClicked(@NonNull MovieItem movieitem, int position);
        void onFavoredClicked(@NonNull MovieItem movieItem, int position);
    }

    @NonNull private final Context mContext;
    @NonNull private final LayoutInflater mInflater;
    @NonNull private List<MovieItem> mMovies;
    @NonNull private OnMovieClickListener mOnMovieClickListener;

    public void setOnMovieClickListener(@NonNull OnMovieClickListener onMovieClickListener) {
        this.mOnMovieClickListener = onMovieClickListener;
    }

    public MoviesAdapter(@NonNull Context context, List<MovieItem> movies) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMovies = (movies != null) ? movies : new ArrayList<>();

        // Each item in the data set has a unique identifier, namely the movieId
        setHasStableIds(true);
    }

    @Override
    public MovieHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Timber.d("in onCreateViewHolder");
        return new MovieHolder(mInflater.inflate(R.layout.item_movie, parent, false));
    }

    @Override
    public void onBindViewHolder(MovieHolder holder, int position) {
        //Timber.d("in onBindViewHolder");
        // holder.bind(mMovies.get(position));
        holder.bind(this.getItem(position));

    }

    class MovieHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.movie_item_container) View mMovieItemContainer;
        @BindView(R.id.movie_item_poster) ImageView mImageView;
        @BindView(R.id.movie_item_footer) ViewGroup mFooterView;
        @BindView(R.id.movie_item_title) TextView mTitleView;
        @BindView(R.id.movie_item_genres) TextView mGenresView;
        @BindView(R.id.movie_item_button_favorite) ImageButton mFavoriteButton;

        public MovieHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull MovieItem movieItem) {

            mMovieItemContainer.setOnClickListener(view ->
                    mOnMovieClickListener.onContentClicked(movieItem, getAdapterPosition()));

            mTitleView.setText(movieItem.getTitle());

            if (!Lists.isEmpty(movieItem.getGenreNames())) {
                mGenresView.setText(TextUtils.join(",", movieItem.getGenreNames()));
            } else {
                mGenresView.setText("");
            }

            //noinspection unchecked
            Glide.with(mContext)
                    .load(movieItem.getPosterPath())
                    .crossFade()
                    .placeholder(R.color.cardview_light_background)
                    .listener(GlidePalette.with(movieItem.getPosterPath())
                            .intoCallBack(palette -> applyColors(palette.getVibrantSwatch())))
                    .into(mImageView);


            // FavoriteButton
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mFavoriteButton.setStateListAnimator(
                        AnimatorInflater.loadStateListAnimator(mContext, R.animator.favorite_button_raised));
            }

            ViewCompat.setElevation(mFavoriteButton, R.dimen.favorite_button_elevation);

            mFavoriteButton.setSelected(movieItem.isFavored());

            mFavoriteButton.setOnClickListener(view -> {
                mFavoriteButton.setSelected(!movieItem.isFavored());
                mOnMovieClickListener.onFavoredClicked(movieItem, getAdapterPosition());
            });

        }

        private void applyColors(Palette.Swatch swatch) {
            if (swatch != null) {
                mFooterView.setBackgroundColor(swatch.getRgb());
                mTitleView.setTextColor(swatch.getTitleTextColor());
                mGenresView.setTextColor(swatch.getBodyTextColor());
                mFavoriteButton.setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
            }
        }

    }

    /*----------------------- Helper functions to add or remove items in the list---------------- */

    public void clear() {
        if (!mMovies.isEmpty()) {
            mMovies.clear();
            /*
                Structural Change hence notifyDataSetChanged. RecyclerView will synthesize visible
                structural change events for ths adapter since it reports it has stable IDs when
                this method is used. This can help for the purposes of animation and visual object
                persistence but individual items will still need to be relaid and rebound.
            */
            notifyDataSetChanged();
        }
    }
    public void set(@NonNull List<MovieItem> movies) {
        mMovies = movies;
        // A complete structural change
        notifyDataSetChanged();
    }

    public void add(@NonNull List<MovieItem> newMovies) {
        if (!newMovies.isEmpty()){
            int currentSize = mMovies.size();
            int amountInserted = newMovies.size();

            mMovies.addAll(newMovies);
            /* Can use the more efficient specific change event here */
            notifyItemRangeInserted(currentSize, amountInserted);
            Timber.e("Size of moviesList: %d", mMovies.size());
        }
    }

    /* ------------Getter functions for itemCount, item(s), itemId -------------------------------*/

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    @NonNull public List<MovieItem> getItems() {
        return mMovies;
    }

    /* This gets called in onBindViewHolder to set (unique) itemIds */
    @Override
    public long getItemId(int position) {
        return Long.parseLong(mMovies.get(position).getMovieId());
    }

    public MovieItem getItem(int position) {
        return mMovies.get(position);
    }

}
