package com.example.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class MovieTrailer implements Parcelable{

    @SerializedName("id") private String mTrailerId;
    @SerializedName("key") private String mYoutubeKey;
    @SerializedName("name") private String mTrailerName;
    @SerializedName("site") private String mSite;
    @SerializedName("size") private String mSize;

    public String getId() {
        return this.mTrailerId;
    }

    public void setId(String id) {
        this.mTrailerId = id;
    }

    public String getKey() {
        return this.mYoutubeKey;
    }

    public void setKey(String key) {
        this.mYoutubeKey = key;
    }

    public String getName() {
        return this.mTrailerName;
    }

    public void setName(String name) {
        this.mTrailerName = name;
    }

    public String getSite() {
        return this.mSite;
    }

    public void setSite(String site) {
        this.mSite = site;
    }

    public String getSize() {
        return this.mSize;
    }

    public void setSize(String size) {
        this.mSize = size;
    }

    public String toString() {
        return (this.mTrailerId + " -- " + this.mYoutubeKey + " -- " + this.mTrailerName + " -- " + this.mSite + " -- " +
                this.mSize);
    }

    //--------------------------------------------------------------------------------------------

    private MovieTrailer(Parcel in) {
        this.mTrailerId = in.readString();
        this.mYoutubeKey = in.readString();
        this.mTrailerName = in.readString();
        this.mSite = in.readString();
        this.mSize = in.readString();
    }

    @Override
    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.mTrailerId);
        dest.writeString(this.mYoutubeKey);
        dest.writeString(this.mTrailerName);
        dest.writeString(this.mSite);
        dest.writeString(this.mSite);
    }

    public static final Parcelable.Creator<MovieTrailer> CREATOR = new Parcelable.Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int i) {
            return new MovieTrailer[i];
        }
    };

}
