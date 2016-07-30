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
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.example.android.popmovies.R;

/**
 * Created by sheshloksamal on 19/03/16.
 *
 */
public class PrefUtils {

    public static String getSortPreference(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(
                context.getString(R.string.pref_sort_by_key), context.getString(R.string.pref_sort_by_default));
    }

    public static void registerOnSharedPreferenceChangeListener(
            Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(
            Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    // Notifications
    public static boolean isNotificationEnabled(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_enable_notifications_key),
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));
    }

    // Put lastSync timeStamp
    public static void setLastSyncTimeStamp(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit()
                .putLong(context.getString(R.string.pref_last_notification), System.currentTimeMillis())
                .commit(); //Using it from a background thread
    }

    // Get lastSync timeStamp
    public static long getLastSyncTimeStamp(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getLong(context.getString(R.string.pref_last_notification), 0);
    }

    // Get sync interval
    public static int getSyncInterval(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(sp.getString(context.getString(R.string.pref_sync_frequency_key),
                context.getString(R.string.pref_sync_frequency_default)));
    }

    // Get ringtone uri
    public static Uri getRingtoneUri(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return Uri.parse(sp.getString(context.getString(R.string.pref_notification_ringtone_key),
                context.getString(R.string.pref_notification_ringtone_default)));
    }

}
