package com.example.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheshloksamal on 12/03/16.
 * Implementing Parcelable as a good practice. We will be persisting data later in SQLite DB and
 * retrieving it, both via our repository
 */
public class MovieReview implements Parcelable {

    @SerializedName("id") private String mReviewId;
    @SerializedName("author") private String mAuthor;
    @SerializedName("content") private String mContent;
    @SerializedName("url") private String mReviewUrl;

    public MovieReview() {}

    public String getId(){
        return this.mReviewId;
    }

    public MovieReview setId(String id){
        this.mReviewId = id;
        return this;
    }

    public String getAuthor(){
        return this.mAuthor;
    }

    public MovieReview setAuthor(String author){
        this.mAuthor = author;
        return this;
    }

    public String getContent(){
        return this.mContent;
    }

    public MovieReview setContent(String content){
        this.mContent = content;
        return this;
    }

    public String getUrl(){
        return this.mReviewUrl;
    }

    public MovieReview setUrl(String url){
        this.mReviewUrl = url;
        return this;
    }

    @Override
    public String toString(){
        return "Review{Author: " + this.mAuthor + "}";
    }


    protected MovieReview(Parcel in) {
        this.mReviewId = in.readString();
        this.mAuthor = in.readString();
        this.mContent = in.readString();
        this.mReviewUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i){
        dest.writeString(this.mReviewId);
        dest.writeString(this.mAuthor);
        dest.writeString(this.mContent);
        dest.writeString(this.mReviewUrl);
    }

    public static final Parcelable.Creator<MovieReview> CREATOR = new Parcelable.Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int i) {
            return new MovieReview[i];
        }

    };

    // ---------------------Wrapper Class for Retrofit to parse API response ----------------------

    public static class Response {
        @SerializedName("results") public List<MovieReview> movieReviews = new ArrayList<>();
    }
}
