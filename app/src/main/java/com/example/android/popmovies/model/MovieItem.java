package com.example.android.popmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sheshloksamal on 6/11/15.
 */
public class MovieItem implements Parcelable {
    @SerializedName("original_title")
    private String title;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("overview")
    private String synopsis;
    @SerializedName("vote_average")
    private String userRating;
    @SerializedName("release_date")
    private String releaseDate;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return this.posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getUserRating() {
        return this.userRating;
    }

    public void setUserRating(String userRating) {
        this.userRating = userRating;
    }

    public String getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public MovieItem(String originalTitle, String moviePoster, String synopsis,
                     String userRating, String releaseDate) {
        this.title = originalTitle;
        this.posterPath = moviePoster;
        this.synopsis = synopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    private MovieItem(Parcel in) {
        this.title = in.readString();
        this.posterPath = in.readString();
        this.synopsis = in.readString();
        this.userRating = in.readString();
        this.releaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return (this.title + " -- " + this.posterPath + " -- " + this.synopsis +
                " -- " + this.userRating + " -- " + this.releaseDate);
    }

    /* public String getMoviePoster() {
        return this.posterPath;
    }*/

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.title);
        parcel.writeString(this.posterPath);
        parcel.writeString(this.synopsis);
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
