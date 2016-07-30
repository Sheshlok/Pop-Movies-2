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

package com.example.android.popmovies.utilities;

import android.content.Context;

/**
 * Created by sheshloksamal on 19/03/16.
 *
 */
public class ImageUtils {

    private static final String LOG_TAG = ImageUtils.class.getSimpleName();
    /**
     * Helper method which builds complete posterPath URLs based on the imageView size and the
     * connection speeds (adaptive). We don't deal with transitioning network conditions.
     * @param context       context
     * @param imagePath     relative URL of movie poster
     * @param width         calculated width of the imageView onMeasure
     * @return              Base path URL for right-sized image as a f(connectivity, imageView width)
     *
     *
     */
    public static String getAdaptivePosterPathURL(Context context, String imagePath, int width) {
        String widthPath;

        if (Connectivity.isConnected(context)) { // If we are connected, check for adaptivity
            if (width <= 92)
                widthPath = "/92";
            else if (width <= 154)
                widthPath = "/154";
            else if (width <= 185)
                widthPath = "/185";
            else if (width <= 342 && Connectivity.isConnectedFast(context)) // Start bringing adaptivity in
                widthPath = "/w342";
            else if (width <= 500 && Connectivity.isConnectedFast(context))
                widthPath = "/w500";
            else if (Connectivity.isConnectedFast(context))
                widthPath = "/w780";
            else
                widthPath = "/w185";
        } else {                        // If we are not connected, default back to original values
            if (width <= 92)
                widthPath = "/92";
            else if (width <= 154)
                widthPath = "/154";
            else if (width <= 185)
                widthPath = "/185";
            else if (width <= 342)
                widthPath = "/w342";
            else if (width <= 500)
                widthPath = "/w500";
            else
                widthPath = "/w780";
        }

        return Constants.BASE_POSTER_PATH_URL + widthPath + imagePath;
    }

}
