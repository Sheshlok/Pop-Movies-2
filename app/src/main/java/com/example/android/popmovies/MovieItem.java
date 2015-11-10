package com.example.android.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sheshloksamal on 6/11/15.
 */
public class MovieItem implements Parcelable {
    String originalTitle;
    String moviePoster; //Thumbnail Image of the movie poster
    String aPlotSynopsis; //called overview in the api
    String userRating; //called vote_average in the api
    String releaseDate;

    public MovieItem(String originalTitle, String moviePoster, String aPlotSynopsis,
                     String userRating, String releaseDate) {
        this.originalTitle = originalTitle;
        this.moviePoster = moviePoster;
        this.aPlotSynopsis = aPlotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    private MovieItem(Parcel in) {
        this.originalTitle = in.readString();
        this.moviePoster = in.readString();
        this.aPlotSynopsis = in.readString();
        this.userRating = in.readString();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return (this.originalTitle + " -- " + this.moviePoster + " -- " + this.aPlotSynopsis +
                " -- " + this.userRating + " -- " + this.releaseDate);
    }

    /* public String getMoviePoster() {
        return this.moviePoster;
    }*/

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.originalTitle);
        parcel.writeString(this.moviePoster);
        parcel.writeString(this.aPlotSynopsis);
        parcel.writeString(this.userRating);
        parcel.writeString(this.releaseDate);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel parcel) {
            return new MovieItem(parcel);
        }

        @Override
        public MovieItem[] newArray(int i) {
            return new MovieItem[i];
        }
    };

}
