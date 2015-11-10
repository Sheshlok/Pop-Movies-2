package com.example.android.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie_item_details")) {
            MovieItem movieItem = intent.getParcelableExtra("movie_item_details");
            //Toast.makeText(getActivity(), movieItem.toString(), Toast.LENGTH_LONG).show();

            TextView movieNameView = (TextView) rootView.findViewById(R.id.movie_name_header);
            if (!(movieItem.originalTitle.equals("null"))) {
                movieNameView.setText(movieItem.originalTitle);
            } else {
                movieNameView.setText("Movie Name N/A");
            }
            ImageView moviePosterView = (ImageView) rootView.findViewById(R.id.movie_poster_image_thumbnail);
            Glide.with(getContext())
                    .load(movieItem.moviePoster)
                    .error(R.mipmap.ic_launcher) // Handles the null case
                    .crossFade()
                    .thumbnail(0.1f)
                    .into(moviePosterView);

            TextView movieReleaseYearView = (TextView) rootView.findViewById(R.id.movie_release_year);
            if (!(movieItem.releaseDate.equals("null"))) {
                movieReleaseYearView.setText(movieItem.releaseDate.substring(0, 4));
            } else {
                movieReleaseYearView.setText("Year of Release N/A");
            }

            TextView movieRatingView = (TextView) rootView.findViewById(R.id.movie_rating);
            if (!(movieItem.userRating.equals("null"))) {
                movieRatingView.setText(movieItem.userRating + "/10");
            } else {
                movieRatingView.setText("Rating N/A");
            }

            TextView moviePlotSynopsisView = (TextView) rootView.findViewById(R.id.movie_plot_synopsis);
            if (!(movieItem.aPlotSynopsis.equals("null"))) {
                moviePlotSynopsisView.setText(movieItem.aPlotSynopsis);
            } else {
                moviePlotSynopsisView.setText("Synopsis N/A");
            }
        }
        return rootView;
    }
}
