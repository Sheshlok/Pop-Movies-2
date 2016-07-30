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

package com.example.android.popmovies.ui.fragment;

import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.popmovies.R;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.model.MovieReview;
import com.example.android.popmovies.data.model.MovieTrailer;
import com.example.android.popmovies.ui.activity.MovieDetailsActivity;
import com.example.android.popmovies.ui.helper.MoviesHelper;
import com.example.android.popmovies.utilities.Lists;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends BaseFragment implements ObservableScrollViewCallbacks,
        SwipeRefreshLayout.OnRefreshListener{

    public static final String ARG_MOVIE_DETAILS = "movieDetailArguments";

    // For preserving state on rotation or events when the app gets killed by the framework
    private static final String MOVIE_REVIEWS_STATE = "movie_reviews";
    private static final String MOVIE_TRAILERS_STATE = "movie_trailers";

    private CompositeSubscription mCompositeSubscription;
    private MovieItem mMovieItem;
    private List<MovieReview> mMovieReviews;
    private List<MovieTrailer> mMovieTrailers;
    private MovieTrailer mMovieTrailer;
    private MenuItem mMenuItemShare;
    private List<Runnable> mDeferredUiOperations = new ArrayList<>();
    private Toolbar mToolbar;
    private MoviesHelper mMoviesHelper;

    @BindView(R.id.movie_details_swipe_refresh_layout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.observable_movie_scroll_view) ObservableScrollView mObservableScrollView;

    @BindView(R.id.movie_backdrop_container) FrameLayout mBackdropContainer;
    @BindView(R.id.movie_backdrop) ImageView mBackdropView;
    @BindView(R.id.backdrop_play_main_trailer) ImageView mBackdropPlayMainTrailerView;

    @BindView(R.id.movie_poster_image_thumbnail) ImageView mMoviePosterView;
    @BindView(R.id.movie_title) TextView mMovieTitleView;
    @BindView(R.id.movie_release_year) TextView mMovieReleaseYearView;
    @BindView(R.id.movie_rating) TextView mMovieRatingView;
    @BindView(R.id.movie_favorite_button) ImageButton mFavoriteButton;
    @BindView(R.id.movie_plot_synopsis) TextView mMoviePlotSynopsisView;
    @BindView(R.id.movie_reviews_container) ViewGroup mMovieReviewsGroup;
    @BindView(R.id.movie_trailers_container) ViewGroup mMovieTrailersGroup;

    @BindColor(R.color.colorPrimary) int mColorPrimary;
    @BindColor(R.color.textAndIcons) int mColorTitleText;

    // All concrete subclasses of Fragment must have a public/no-argument constructor to help the
    // framework re-instantiate this during state restore
    public MovieDetailsFragment() {}

    @Override
    public void onAttach(Context context) {
        Timber.e("in onAttach");
        super.onAttach(context);
        setHasOptionsMenu(true);
        mMoviesHelper = new MoviesHelper((Activity) context, mMoviesRepository);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.e("in onCreateView");
        return inflater.inflate(R.layout.fragment_movie_details, container, false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Timber.e("in onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() instanceof MovieDetailsActivity) {
            mToolbar = ((MovieDetailsActivity) getActivity()).getToolbar();
            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, R.color.colorPrimary));
        }

        mObservableScrollView.setScrollViewCallbacks(this);

        if (savedInstanceState != null) {
            mMovieReviews = savedInstanceState.getParcelableArrayList(MOVIE_REVIEWS_STATE);
            mMovieTrailers = savedInstanceState.getParcelableArrayList(MOVIE_TRAILERS_STATE);
        }

        initializeSwipeRefreshLayout();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        Timber.e("in onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();

        onScrollChanged(mObservableScrollView.getCurrentScrollY(), false, false);

        Bundle args = getArguments();
        if (args != null ) {
            onMovieItemLoaded(args.getParcelable(ARG_MOVIE_DETAILS));
        }
        loadReviewsAndTrailers();

        /*
            Add a subscription to global saved stream of movies to reflect the latest changes in UI
            (especially for 2-pane case so that selecting a movieItem as favorite (or otherwise) in
            BrowseMovieActivity's fragments is reflected in MovieDetailsFragment when the same
            movie is present on both sides
        */
        mCompositeSubscription.add(mMoviesHelper.getFavoredObservable()
                .filter(favoredEvent -> mMovieItem != null &&
                        Long.parseLong(mMovieItem.getMovieId()) == favoredEvent.movieId)
                .subscribe(movie -> {
                    mMovieItem.setFavored(movie.isFavored);
                    mFavoriteButton.setSelected(movie.isFavored);
                }));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        Timber.e("in onCreateOptionsMenu");
        menuInflater.inflate(R.menu.menu_movie_details_fragment, menu);
        mMenuItemShare = menu.findItem(R.id.share_movie_details);
        tryExecuteDeferredUiOperations();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Timber.e("in onOptionsItemSelected");
        if (menuItem.getItemId() == R.id.share_movie_details){
            if (mMovieTrailer != null) {
                mMoviesHelper.shareTrailer(R.string.share_movie_template, mMovieItem, mMovieTrailer);
            }
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMovieReviews != null) outState.putParcelableArrayList(MOVIE_REVIEWS_STATE, new ArrayList<>(mMovieReviews));
        if (mMovieTrailers != null) outState.putParcelableArrayList(MOVIE_TRAILERS_STATE, new ArrayList<>(mMovieTrailers));
        super.onSaveInstanceState(outState);

    }

    private void loadReviewsAndTrailers() {
        if (mMovieReviews != null) onReviewsLoaded(mMovieReviews);
        else loadReviews();
        if (mMovieTrailers != null) onTrailersLoaded(mMovieTrailers);
        else loadTrailers();

    }

    private void onMovieItemLoaded(MovieItem movieItem) {
        mMovieItem = movieItem;

        if (mToolbar != null) { // 1-pane mode
            mToolbar.setTitle(movieItem.getTitle());
        }
        mMovieTitleView.setText(movieItem.getTitle());
        mMovieReleaseYearView.setText(movieItem.getReleaseDate());
        mMoviePlotSynopsisView.setText(movieItem.getSynopsis());
        mMovieRatingView.setText(movieItem.getUserRating());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFavoriteButton.setStateListAnimator(
                    AnimatorInflater.loadStateListAnimator(getContext(), R.animator.favorite_button_raised));
        }

        ViewCompat.setElevation(mFavoriteButton, R.dimen.favorite_button_elevation);

        mFavoriteButton.setSelected(movieItem.isFavored());

        // Poster Image
        Glide.with(this)
                .load(movieItem.getPosterPath())
                .placeholder(R.color.cardview_light_background)
                .crossFade()
                .into(mMoviePosterView);

        // Backdrop Image
        Glide.with(this)
                .load(movieItem.getBackdropPath())
                .placeholder(R.color.cardview_light_background)
                .crossFade()
                .into(mBackdropView);
    }

    private void loadReviews(){
        mCompositeSubscription.add(mMoviesRepository.getMovieReviews(mMovieItem.getMovieId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(movieReviews -> {
                            Timber.d("Reviews loaded, %d items", movieReviews.size());
                            mSwipeRefreshLayout.setRefreshing(false);
                            onReviewsLoaded(movieReviews);
                        }, throwable -> {
                            Timber.e(throwable, "Reviews loading failed");
                            mSwipeRefreshLayout.setRefreshing(false);
                            onReviewsLoaded(null);
                        }, () -> {
                            Timber.d("Reviews loading completed.");
                            mSwipeRefreshLayout.setRefreshing(false);
                        })
        );
    }

    private void onReviewsLoaded(List<MovieReview> movieReviews){
        if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);

        mMovieReviews = movieReviews;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        boolean hasReviews = false;

        if (!Lists.isEmpty(movieReviews)){
            for (MovieReview movieReview: movieReviews){
                if (TextUtils.isEmpty(movieReview.getAuthor())){
                    continue;
                }

                View reviewView = inflater.inflate(R.layout.item_movie_review, mMovieReviewsGroup, false);
                TextView reviewAuthorView = ButterKnife.findById(reviewView, R.id.review_author);
                TextView reviewContentView = ButterKnife.findById(reviewView, R.id.review_content);

                reviewAuthorView.setText(movieReview.getAuthor());
                reviewContentView.setText(movieReview.getContent());

                mMovieReviewsGroup.addView(reviewView);
                hasReviews = true;
            }
        }

        mMovieReviewsGroup.setVisibility(hasReviews ? View.VISIBLE : View.GONE);

    }

    private void loadTrailers(){
        mCompositeSubscription.add(mMoviesRepository.getMovieTrailers(mMovieItem.getMovieId())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(movieTrailers -> {
                            Timber.d("Trailers Loaded, %d items.", movieTrailers.size());
                            mSwipeRefreshLayout.setRefreshing(false);
                            onTrailersLoaded(movieTrailers);
                        }, throwable -> {
                            Timber.e("Trailers loading failed.");
                            mSwipeRefreshLayout.setRefreshing(false);
                            onTrailersLoaded(null);
                        }, () -> {
                            Timber.d("Trailers loading completed.");
                            mSwipeRefreshLayout.setRefreshing(false);
                        })
        );

    }

    private void onTrailersLoaded(List<MovieTrailer> movieTrailers){
        if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);

        mMovieTrailers = movieTrailers;

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        boolean hasTrailers = false;

        if (!Lists.isEmpty(movieTrailers)) {
            for (MovieTrailer movieTrailer: movieTrailers){
                if (movieTrailer.getType().equals(MovieTrailer.TYPE_TRAILER)){
                    Timber.d("Trailer found.");
                    mMovieTrailer = movieTrailer;
                    mBackdropContainer.setTag(mMovieTrailer);
                    mBackdropContainer.setOnClickListener(view -> mMoviesHelper.playTrailer(
                            (MovieTrailer) mBackdropContainer.getTag()));
                    break;
                }
            }

            for (MovieTrailer movieTrailer: movieTrailers) {
                View trailerView = inflater.inflate(R.layout.item_movie_trailer, mMovieTrailersGroup, false);
                TextView trailerNameView = ButterKnife.findById(trailerView, R.id.movie_trailer_name);

                trailerNameView.setText(String.format("%s: %s", movieTrailer.getSite(), movieTrailer.getName()));

                // Add on click listeners
                trailerView.setTag(movieTrailer);
                trailerView.setOnClickListener(view -> mMoviesHelper.playTrailer((MovieTrailer) trailerView.getTag()));

                mMovieTrailersGroup.addView(trailerView);
                hasTrailers = true;
            }
        }

        showShareMenuItemDeferred(mMovieTrailer != null);
        mBackdropContainer.setClickable(mMovieTrailer != null);
        mBackdropPlayMainTrailerView.setVisibility(mMovieTrailer != null ? View.VISIBLE : View.GONE);
        mMovieTrailersGroup.setVisibility(hasTrailers ? View.VISIBLE : View.GONE);
    }

    private void showShareMenuItemDeferred(boolean visible) {
        mDeferredUiOperations.add(() -> mMenuItemShare.setVisible(visible));
        tryExecuteDeferredUiOperations();
    }

    private void tryExecuteDeferredUiOperations() {
        if (mMenuItemShare != null) {
            for (Runnable r: mDeferredUiOperations) {
                r.run();
            }
            mDeferredUiOperations.clear();
        }

    }

    @OnClick(R.id.movie_favorite_button)
    public void onFavored(ImageButton imageButton) {
        if (mMovieItem == null) return;

        boolean favored = !mMovieItem.isFavored();
        imageButton.setSelected(favored);
        mMoviesHelper.setMovieFavored(mMovieItem, favored);
        if (favored) showToast("Saved to favorites");
        else showToast("Removed from favorites");
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.e("in onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.e("in onResume");
    }

    @Override
    public void onPause(){
        super.onPause();
        Timber.e("in onPause");
    }

    @Override
    public void onStop(){
        super.onStop();
        Timber.e("in onStop");
    }

    @Override
    public void onDestroyView(){
        Timber.e("in onDestroyView");
        mCompositeSubscription.unsubscribe();
        super.onDestroyView();

    }

    @Override
    public void onDetach(){
        Timber.e("in onDetach");
        super.onDetach();
    }

    //----- See GitHub sample: ParallaxToolbarScrollViewActivity.java for ObservableScrollView lib--
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewCompat.setTranslationY(mBackdropContainer, scrollY / 2);
        int parallaxImageHeight = mBackdropContainer.getMeasuredHeight();
        float alpha = Math.min(1, (float) scrollY / parallaxImageHeight);
        if (mToolbar != null) { // For 1-pane mode
            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, mColorPrimary));
            mToolbar.setTitleTextColor(ScrollUtils.getColorWithAlpha(alpha, mColorTitleText));
        }
    }

    @Override
    public void onDownMotionEvent() {
        /** ignore */
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        // If Observable scrollview is not null, is shown, and cannot scroll upwards, then
        // activate the SwipeRefreshLayout, else de-activate it.
        if (mObservableScrollView != null && mObservableScrollView.isShown() &&
                !ViewCompat.canScrollVertically(mObservableScrollView, -1)) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }

    }

    //------------ Implementing SwipeRefreshLayout & .OnRefreshListener--------------------------------

    private void initializeSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_progress_colors));
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        loadReviewsAndTrailers();
    }
}
