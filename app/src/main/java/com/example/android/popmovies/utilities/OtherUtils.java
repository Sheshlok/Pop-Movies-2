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

/**
 * Created by sheshloksamal on 03/04/16.
 *
 */
public class OtherUtils {

    /* Checks if 1 day has passed since the lastTimeStamp checked */

    public static boolean isOneDayLater(long lastTimeStamp) {
        final long ONE_DAY = 24 * 60 * 60 * 1000;
        long now = System.currentTimeMillis();
        long timePassed = now - lastTimeStamp;

        return (timePassed > ONE_DAY);
    }
}
