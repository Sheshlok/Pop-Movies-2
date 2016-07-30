package com.example.android.popmovies.data.repository;

import android.content.ContentResolver;

import com.example.android.popmovies.data.NetworkingAPI.TmdbApi;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.model.MovieReview;
import com.example.android.popmovies.data.model.MovieTrailer;
import com.example.android.popmovies.data.provider.MovieContract;
import com.example.android.popmovies.utilities.Lists;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.squareup.sqlbrite.BriteContentResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 20/03/16.
 *
 */
public class MoviesRepositoryImpl implements MoviesRepository {

    private final TmdbApi mTmdbApi;
    private final GenresRepository mGenresRepository;
    private final BriteContentResolver mBriteContentResolver;
    private final ContentResolver mContentResolver;

    public MoviesRepositoryImpl(TmdbApi tmdbApi, GenresRepository genresRepository,
                                BriteContentResolver briteContentResolver, ContentResolver contentResolver) {
        this.mTmdbApi = tmdbApi;
        this.mGenresRepository = genresRepository;
        this.mBriteContentResolver = briteContentResolver;
        this.mContentResolver = contentResolver;
    }

    @RxLogObservable
    private Observable<List<MovieItem>> getMoviesFromApi(String sortPreference, int page) {
        Timber.e("in getMoviesFromApi");
        return mTmdbApi.getMovies(sortPreference, page)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .onErrorResumeNext(Observable.<MovieItem.Response>empty())
                .map(response -> response.movieResults)
                .subscribeOn(Schedulers.io());
    }

    @Override @RxLogObservable
    public Observable<List<MovieItem>> getMoviesFromApiWithSavedDataAndGenreNames(String sortPreference, int page) {
        return getMoviesFromApi(sortPreference, page)
                .zipWith(getSavedMovieIds(), (movieItems, savedMovieIds) -> {
                    for (MovieItem movieItem : movieItems) {
                        movieItem.setFavored(savedMovieIds.contains(movieItem.getMovieId()));
                    }
                    return movieItems;
                })
                .zipWith(mGenresRepository.getGenresMap(), GENRES_MAPPER)
                .subscribeOn(Schedulers.io());
    }


    @Override @RxLogObservable
    public Observable<List<MovieReview>> getMovieReviews(String movieId) {
        Timber.e("in getMovieReviews");
        return mTmdbApi.getMovieReviews(movieId)
                .timeout(5, TimeUnit.SECONDS)
                .retry(2)
                .onErrorResumeNext(Observable.<MovieReview.Response>empty())
                .map(response -> response.movieReviews)
                .subscribeOn(Schedulers.io());
    }

    @Override @RxLogObservable
    public Observable<List<MovieTrailer>> getMovieTrailers(String movieId) {
        Timber.e("in getMovieTrailers");
        return mTmdbApi.getMovieTrailers(movieId)
                .timeout(3, TimeUnit.SECONDS)
                .retry(2)
                .onErrorResumeNext(Observable.<MovieTrailer.Response>empty())
                .map(response -> response.movieTrailers)
                .subscribeOn(Schedulers.io());

    }

    /*
        1. Need to make sure that the most recent savedMovie is displayed first, hence descending sort.
        2. 'withLatestFrom/combineLATEST' enables the processed query to be re-emitted to its subscriber in
        FavoredMoviesFragment, thereby enabling the auto-refresh on selecting/de-selecting the
        favorite button as each time, 'notifyDataSetChanged()' is called from mMoviesAdapter.set(..)
     */

    @RxLogObservable
    private Observable<List<MovieItem>> getSavedMoviesFromDB() {
        return mBriteContentResolver.createQuery(
                MovieContract.MovieEntry.CONTENT_URI, MovieItem.PROJECTION, null, null,
                MovieContract.MovieEntry.DEFAULT_SORT, true)
                .map(MovieItem.PROJECTION_MAP);
        // The only caveat of using withLatestFrom is if the sourceObservable completes before the
        // otherObservable, nothing is emitted. Thus, we break it into two with '.combineLatest'.
//                .withLatestFrom(mGenresRepository.getGenresMap(), GENRES_MAPPER);
    }

    @Override @RxLogObservable
    public Observable<List<MovieItem>> getSavedMoviesWithGenreNames() {
        return Observable.combineLatest(getSavedMoviesFromDB(), mGenresRepository.getGenresMap(), GENRES_MAPPER);

    }
    @RxLogObservable
    private Observable<Set<String>> getSavedMovieIds() {
        return mBriteContentResolver.createQuery(
                MovieContract.MovieEntry.CONTENT_URI, MovieItem.MOVIE_ID_PROJECTION, null, null, null, true)
                .map(MovieItem.MOVIE_ID_PROJECTION_MAP);
    }

    @Override
    public void saveMovie(MovieItem movieItem) {
        /* Use observable for async insertion instead of AsyncQueryHandler to avoid leaks warning */

        Observable.just(movieItem)
                .subscribeOn(Schedulers.io())
                .subscribe(movieItemToSave -> mContentResolver.insert(
                                MovieContract.MovieEntry.CONTENT_URI,
                                new MovieItem.Builder().movieItem(movieItemToSave).build()),
                        throwable -> Timber.e("Could not insert"),
                        () -> Timber.i("Inserted %s into Database", movieItem.toString()));

//        /*
//            New AsyncQueryHandler(mContentResolver) {} produces warning: Handler Class should be
//            static or leaks might occur.
//         */
//        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(mContentResolver) {};
//        asyncQueryHandler.startInsert(-1, null, MovieContract.MovieEntry.CONTENT_URI,
//                new MovieItem.Builder().movieItem(movieItem).build());
    }

    @Override
    public void deleteMovie(MovieItem movieItem) {
        String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?";
        String[] selectionArgs = new String[] {movieItem.getMovieId()};

        /* Use observable for async deletion instead of AsyncQueryHandler to avoid leaks warning */

        Observable.just(movieItem)
                .subscribeOn(Schedulers.io())
                .subscribe(movieItemToDelete -> mContentResolver.delete(
                                MovieContract.MovieEntry.CONTENT_URI, selection, selectionArgs),
                        throwable -> Timber.e("Could not delete"),
                        () -> Timber.i("Deleted %s from Database", movieItem.toString()));

//        /*
//            New AsyncQueryHandler(mContentResolver) {} produces warning: Handler Class should be
//            static or leaks might occur.
//         */
//        AsyncQueryHandler asyncQueryHandler = new AsyncQueryHandler(mContentResolver) {};
//        asyncQueryHandler.startDelete(-1, null, MovieContract.MovieEntry.CONTENT_URI, selection, selectionArgs);
    }


    private Func2<List<MovieItem>, Map<Integer, String>, List<MovieItem>> GENRES_MAPPER = (movieItems, genresMap) -> {
        for(MovieItem movieItem: movieItems) {
            List<Integer> genreIds = movieItem.getGenreIds();
            if(Lists.isEmpty(genreIds)) continue;

            List<String> genreNames = new ArrayList<>(genreIds.size());
            for (Integer genreId: genreIds) {
                genreNames.add(genresMap.get(genreId));
            }
            movieItem.setGenreNames(genreNames);
        }
        return movieItems;
    };
}
