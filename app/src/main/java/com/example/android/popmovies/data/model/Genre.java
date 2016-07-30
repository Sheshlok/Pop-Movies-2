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
