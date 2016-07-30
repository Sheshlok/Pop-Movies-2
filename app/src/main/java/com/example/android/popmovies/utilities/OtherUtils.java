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
