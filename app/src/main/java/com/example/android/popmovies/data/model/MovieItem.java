/*
 *  Copyright (C) 2016 Sheshlok Samal
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package com.example.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.example.android.popmovies.data.provider.meta.MovieItemMeta;
import com.example.android.popmovies.utilities.Lists;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheshloksamal on 6/11/15.
 * 
 */
public class MovieItem implements Parcelable, MovieItemMeta {
    @SerializedName("id") private String mMovieId;
    @SerializedName("original_title") private String mTitle;
    @SerializedName("poster_path") private String mPosterPath;
    @SerializedName("backdrop_path") private String mBackdropPath;
    @SerializedName("overview") private String mSynopsis;
    @SerializedName("vote_average") private String mUserRating;
    @SerializedName("release_date") private String mReleaseDate;
    @SerializedName("genre_ids") private List<Integer> mGenreIds = new ArrayList<>();

    boolean mFavored = false;

    List<String> mGenreNames;

    public MovieItem(){}

    public String getMovieId() {
        return this.mMovieId;
    }

    public MovieItem setMovieId(String movieId){
        this.mMovieId = movieId;
        return this;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public MovieItem setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public String getPosterPath() {
        return this.mPosterPath;
    }

    public MovieItem setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
        return this;
    }

    public String getBackdropPath(){
        return this.mBackdropPath;
    }

    public MovieItem setBackdropPath(String backdropPath){
        this.mBackdropPath = backdropPath;
        return this;
    }

    public String getSynopsis() {
        return this.mSynopsis;
    }

    public MovieItem setSynopsis(String synopsis) {
        this.mSynopsis = synopsis;
        return this;
    }

    public String getUserRating() {
        return this.mUserRating;
    }

    public MovieItem setUserRating(String userRating) {
        this.mUserRating = userRating;
        return this;
    }

    public String getReleaseDate() {
        return this.mReleaseDate;
    }

    public MovieItem setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
        return this;
    }

    public boolean isFavored(){
        return this.mFavored;
    }

    public MovieItem setFavored(boolean mFavored){
        this.mFavored = mFavored;
        return this;
    }

    public List<Integer> getGenreIds() {
        return this.mGenreIds;
    }

    public MovieItem setGenreIds(List<Integer> genreIds) {
        this.mGenreIds = genreIds;
        return this;
    }

    public List<String> getGenreNames() {
        return this.mGenreNames;
    }

    public MovieItem setGenreNames(List<String> genreNames) {
        this.mGenreNames = genreNames;
        return this;
    }

    /* Makes a string - comma-separated list -  from List<Integer> genreIds for putting into
    ContentValues */
    public String makeGenreIdsList() {
        if (Lists.isEmpty(this.mGenreIds)) return "";

        StringBuilder sb = new StringBuilder();
        sb.append(this.mGenreIds.get(0));
        for (int i = 1; i < this.mGenreIds.size(); i++) {
            sb.append(",").append(this.mGenreIds.get(i));
        }

        return sb.toString();
    }

    /* Set genreIds from a comma-separated list in a cursor for a movieItem object*/
    public MovieItem putGenreIdsList(String ids){
        if (!TextUtils.isEmpty(ids)){
            this.mGenreIds = new ArrayList<>();
            String[] strs = ids.split(",");
            for (String s: strs){
                this.mGenreIds.add(Integer.parseInt(s));
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return ("Movie { Title: " + this.mTitle + "}");
    }


    //-------------------------------------Parcelable Implementation--------------------------------

    protected MovieItem(Parcel in) {
        this.mMovieId = in.readString();
        this.mTitle = in.readString();
        this.mPosterPath = in.readString();
        this.mBackdropPath = in.readString();
        this.mSynopsis = in.readString();
        this.mUserRating = in.readString();
        this.mReleaseDate = in.readString();
        this.mFavored = in.readByte() != 0;
        this.mGenreIds = new ArrayList<>();
        in.readList(this.mGenreIds, List.class.getClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMovieId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mPosterPath);
        dest.writeString(this.mBackdropPath);
        dest.writeString(this.mSynopsis);
        dest.writeString(this.mUserRating);
        dest.writeString(this.mReleaseDate);
        dest.writeByte(mFavored ? (byte) 1: (byte) 0);
        dest.writeList(this.mGenreIds);
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

    //-------------Wrapper Class for Retrofit to parse API response -------------------------------

    public static class Response {

        @SerializedName("page") public int page;
        @SerializedName("results") public List<MovieItem> movieResults = new ArrayList<>();
    }

}
