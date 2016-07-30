package com.example.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheshloksamal on 12/03/16.
 *
 */
public class MovieTrailer implements Parcelable{

    public static final String TYPE_TRAILER = "Trailer";
    public static final String SITE_YOUTUBE = "YouTube";

    @SerializedName("id") private String mTrailerId;
    @SerializedName("key") private String mKey;
    @SerializedName("name") private String mTrailerName;
    @SerializedName("site") private String mSite;
    @SerializedName("size") private String mSize;
    @SerializedName("type") private String mType;

    public MovieTrailer(){}

    public String getId() {
        return this.mTrailerId;
    }

    public MovieTrailer setId(String id) {
        this.mTrailerId = id;
        return this;
    }

    public String getKey() {
        return this.mKey;
    }

    public MovieTrailer setKey(String key) {
        this.mKey = key;
        return this;
    }

    public String getName() {
        return this.mTrailerName;
    }

    public MovieTrailer setName(String name) {
        this.mTrailerName = name;
        return this;
    }

    public String getSite() {
        return this.mSite;
    }

    public MovieTrailer setSite(String site) {
        this.mSite = site;
        return this;
    }

    public String getSize() {
        return this.mSize;
    }

    public MovieTrailer setSize(String size) {
        this.mSize = size;
        return this;
    }

    public String getType() {
        return this.mType;
    }

    public MovieTrailer setType(String type) {
        this.mType = type;
        return this;
    }

    @Override
    public String toString(){
        return "Video{" +
                "key='" + this.mKey + "\'" +
                ", name='" + this.mTrailerName + "\'" +
                ", site='" + this.mSite + "\'" +
                ", type='" + this.mType + '\'' +
                "}";
    }

    //--------------------------------Parcelable Implementation-------------------------------------

    protected MovieTrailer(Parcel in) {
        this.mTrailerId = in.readString();
        this.mKey = in.readString();
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
        dest.writeString(this.mKey);
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

    //-----------------------------Wrapper class for Retrofit to parse API response----------------

    public static class Response {
        @SerializedName("results") public List<MovieTrailer> movieTrailers = new ArrayList<>();
    }

}
