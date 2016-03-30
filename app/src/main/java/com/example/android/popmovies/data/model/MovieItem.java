package com.example.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sheshloksamal on 6/11/15.
 * 
 */
public class MovieItem implements Parcelable {
    @SerializedName("id")
    private String mMovieId;
    @SerializedName("original_title")
    private String mTitle;
    @SerializedName("poster_path")
    private String mPosterPath;
    @SerializedName("overview")
    private String mSynopsis;
    @SerializedName("vote_average")
    private String mUserRating;
    @SerializedName("release_date")
    private String mReleaseDate;

    public String getMovieId() {
        return this.mMovieId;
    }

    public void setMovieId(String movieId){
        this.mMovieId = movieId;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getPosterPath() {
        return this.mPosterPath;
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public String getSynopsis() {
        return this.mSynopsis;
    }

    public void setSynopsis(String synopsis) {
        this.mSynopsis = synopsis;
    }

    public String getUserRating() {
        return this.mUserRating;
    }

    public void setUserRating(String userRating) {
        this.mUserRating = userRating;
    }

    public String getReleaseDate() {
        return this.mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public String toString() {
        return (this.mMovieId + " --" + this.mTitle + " -- " + this.mPosterPath + " -- " +
                this.mSynopsis + " -- " + this.mUserRating + " -- " + this.mReleaseDate);
    }


    //---------------------------------------------------------------------------------------------

    private MovieItem(Parcel in) {
        this.mMovieId = in.readString();
        this.mTitle = in.readString();
        this.mPosterPath = in.readString();
        this.mSynopsis = in.readString();
        this.mUserRating = in.readString();
        this.mReleaseDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.mMovieId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mPosterPath);
        dest.writeString(this.mSynopsis);
        dest.writeString(this.mUserRating);
        dest.writeString(this.mReleaseDate);
    }

    public static final Parcelable.Creator<MovieItem> CREATOR = new Parcelable.Creator<MovieItem>() {
        @Override
        public MovieItem createFromParcel(Parcel in) {
            return new MovieItem(in);
        }

        @Override
        public MovieItem[] newArray(int i) {
            return new MovieItem[i];
        }
    };

}
