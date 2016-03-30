package com.example.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sheshloksamal on 12/03/16.
 * Implementing Parcelable as a good practice. We will be persisting data later in SQLite DB and
 * retrieving it later through Loaders
 */
public class MovieReview implements Parcelable {

    @SerializedName("id") private String mReviewId;
    @SerializedName("author") private String mAuthor;
    @SerializedName("content") private String mContent;
    @SerializedName("url") private String mReviewUrl;

    public String getId(){
        return this.mReviewId;
    }

    public void setId(String id){
        this.mReviewId = id;
    }

    public String getAuthor(){
        return this.mAuthor;
    }

    public void setAuthor(String author){
        this.mAuthor = author;
    }

    public String getContent(){
        return this.mContent;
    }

    public void setContent(String content){
        this.mContent = content;
    }

    public String getUrl(){
        return this.mReviewUrl;
    }

    public void setUrl(String url){
        this.mReviewUrl = url;
    }

    public String toString() {
        return (this.mReviewId + " -- " + this.mAuthor + " -- " + this.mContent + " -- " + this.mReviewUrl);
    }

    // --------------------------------------------------------------------------------------------

    private MovieReview(Parcel in) {
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
}
