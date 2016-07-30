package com.example.android.popmovies.ui.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.model.MovieTrailer;
import com.example.android.popmovies.data.repository.MoviesRepository;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 30/03/16.
 * A Helper class for base fragment and its children to save/delete movies, play trailers,
 * share trailerLinks and emit observables when user likes/dislikes a particular movie.
 */
public class MoviesHelper {

    private static final PublishSubject<FavoredEvent> FAVORED_EVENT_PUBLISH_SUBJECT = PublishSubject.create();

    private final Activity mActivity;
    private final MoviesRepository mMoviesRepository;

    public MoviesHelper(Activity activity, MoviesRepository moviesRepository) {
        this.mActivity = activity;
        this.mMoviesRepository = moviesRepository;
    }

    @RxLogObservable
    public Observable<FavoredEvent> getFavoredObservable() {
        return FAVORED_EVENT_PUBLISH_SUBJECT.asObservable();
    }

    public void setMovieFavored(MovieItem movieItem, boolean favored) {
        movieItem.setFavored(favored);
        if (favored) {
            mMoviesRepository.saveMovie(movieItem);
        } else {
            mMoviesRepository.deleteMovie(movieItem);
        }
        FAVORED_EVENT_PUBLISH_SUBJECT.onNext(new FavoredEvent(Long.parseLong(movieItem.getMovieId()), favored));
    }

    public void playTrailer(MovieTrailer movieTrailer) {
        if (movieTrailer.getSite().equals(MovieTrailer.SITE_YOUTUBE)) {
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/watch?v=" + movieTrailer.getKey())));
        } else {
            Timber.w("Unsupported video format");
        }
    }

    public void shareTrailer(int messageTemplateResId, MovieItem movieItem, MovieTrailer movieTrailer) {
        mActivity.startActivity(Intent.createChooser(
                        createShareIntent(messageTemplateResId, movieItem.getTitle(), movieTrailer.getKey()),
                        mActivity.getString(R.string.title_share_trailer)));
    }

    public Intent createShareIntent(int messageTemplateResId, String title, String key) {
        ShareCompat.IntentBuilder intentBuilder = ShareCompat.IntentBuilder.from(mActivity)
                .setType("text/plain")
                .setText(mActivity.getString(messageTemplateResId, title, " https://www.youtube.com/watch?v=" + key));
        return intentBuilder.getIntent();

    }

    public static class FavoredEvent {
        public long movieId;
        public boolean isFavored;

        private FavoredEvent(long movieId, boolean isFavored) {
            this.movieId = movieId;
            this.isFavored = isFavored;
        }
    }
}
