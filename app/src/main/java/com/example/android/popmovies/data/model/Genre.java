package com.example.android.popmovies.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.android.popmovies.data.provider.meta.GenreMeta;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sheshloksamal on 30/03/16.
 *
 */
public class Genre implements Parcelable, GenreMeta {

    @SerializedName("id") private int mId;
    @SerializedName("name") private String mName;

    public Genre() {}

    public int getId() {
        return this.mId;
    }

    public Genre setId(int id) {
        this.mId = id;
        return this;
    }

    public String getName() {
        return this.mName;
    }

    public Genre setName(String name) {
        this.mName = name;
        return this;
    }

    @Override
    public String toString() {
        return "Genre{ Name: " + this.mName + " }";
    }


    //-------------------------------------Parcelable Implementation--------------------------------
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
    }

    protected Genre(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
    }

    public static Creator<Genre> CREATOR = new Creator<Genre>() {
        @Override
        public Genre createFromParcel(Parcel source) {
            return new Genre(source);
        }

        @Override
        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    //-------------Wrapper Class for Retrofit to parse API response -------------------------------

    public static class Response {

        @SerializedName("genres") public List<Genre> genres = new ArrayList<>();
    }
}
