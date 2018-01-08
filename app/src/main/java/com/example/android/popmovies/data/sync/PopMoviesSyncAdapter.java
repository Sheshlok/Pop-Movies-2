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

package com.example.android.popmovies.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

import com.example.android.popmovies.PopularMoviesApplication;
import com.example.android.popmovies.R;
import com.example.android.popmovies.data.model.MovieItem;
import com.example.android.popmovies.data.repository.GenresRepository;
import com.example.android.popmovies.data.repository.MoviesRepository;
import com.example.android.popmovies.ui.activity.BrowseMoviesActivity;
import com.example.android.popmovies.utilities.OtherUtils;
import com.example.android.popmovies.utilities.PrefUtils;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 17/03/16.
 * The SyncAdapter component in our app encapsulates the code for the tasks that transfer data b/w
 * the device storage associated with an account and a server that requires login access. Based on
 * the scheduling and triggers provided in the app, the SyncAdapter framework (SyncManager - a
 * System Resource) runs the code in the SyncAdapter component. To add the SyncAdapter component
 * in our app, we do the following:
 * 1. SyncAdapter class: A class that wraps the data transfer code in an interface compatible with
 * SyncAdapter framework (AbstractThreadedSyncAdapter here).
 * 2. Bound Service: A component that allows the SyncAdapter framework to run the code in the Sync
 * Adapter class.
 * 3. SyncAdapter XML Metadata file: A file containing information about SyncAdapter. The framework
 * reads this file (config. setup) to find out how to load and schedule data transfer.
 * 4. Declarations in the AppManifest: XML that declares the bound service and points to SyncAdapter
 * specific meta-data.
 * <p>
 * =================================Note the following methods ====================================
 * <p>
 * 1. onPerformSync: This is WHAT happens when a SYNC occurs.
 * 2. getSyncAccount(): This is a helper method. "Whenever you request a sync, you need a sync
 * account." In this case, it gives us a dummy account.
 * 3. syncImmediately: This is a helper method as well. It tells the system to perform a sync with
 * our SyncAdapter immediately.
 */

public class PopMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    @Inject GenresRepository mGenresRepository;
    @Inject MoviesRepository mMoviesRepository;

    private static final String FAVORITES_MODE = "favorites";
    private static final String POPULARITY_MODE = "popular";
    private static final int MOVIES_NOTIFICATION_ID = 1123;

    public static int SYNC_INTERVAL; // For versions lower than KitKat
    public static int SYNC_FLEXTIME = SYNC_INTERVAL / 3; // For versions higher than Kitkat

    public PopMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        ((PopularMoviesApplication) context.getApplicationContext()).getComponent().inject(this);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public PopMoviesSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        ((PopularMoviesApplication) context.getApplicationContext()).getComponent().inject(this);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {

        Timber.tag("Pop Movies").e("on PerformSync called");
        Timber.e("The sync interval is: %d min", PrefUtils.getSyncInterval(getContext()));
        Timber.e("Sync result full sync= %s", syncResult.fullSyncRequested);
        Timber.e("Sync result %1s, %2s, %3s",
                syncResult.toDebugString(), syncResult.stats.describeContents(), syncResult.stats.numEntries);
        Timber.e("Bundle %s", extras.toString());

        /*
            Update the list of Genres so that the most updated genres list is always stored in
            the database. We need to do this separately since getMovies can get its genresMap
            from DB directly if and when it returns earlier.
         */
        Observable.just(mGenresRepository.getGenresFromApiWithSave()
                .subscribeOn(Schedulers.immediate())
                .subscribe(genres -> Timber.tag("Pop Movies Sync Adapter")
                                .d("%d genres inserted into database", genres.size()),
                        throwable1 -> Timber.tag("Pop Movies Sync Adapter")
                                .e("Could not update genres in the database"),
                        () -> Timber.tag("Pop Movies Sync Adapter")
                                .i("Genres insertion complete")));

        /*
            Get the current sort preference and update the observable so that the user always
            sees the most updated screen from last preference. Use default if sort preference is
            set to Favorites.
            Send a notification if notification is enabled and we haven't sent notification for a day
        */
        String sortPreference = PrefUtils.getSortPreference(getContext());
        final String polledSortPref = sortPreference.equals(FAVORITES_MODE) ? POPULARITY_MODE : sortPreference;

        Observable.just(mMoviesRepository.getMoviesFromApiWithSavedDataAndGenreNames(polledSortPref, 1)
                .subscribeOn(Schedulers.immediate())
                .subscribe(movieItems -> {
                            Timber.tag("Pop Movies Sync Adapter")
                                    .d("%d movies downloaded as per preference", movieItems.size());
                            if (PrefUtils.isNotificationEnabled(getContext()) &&
                                OtherUtils.isOneDayLater(PrefUtils.getLastSyncTimeStamp(getContext())))
                                    sendMoviesNotification(movieItems, polledSortPref);
                        },
                        throwable -> Timber.tag("Pop Movies Sync Adapter")
                                .e("Could not update the movies observable"),
                        () -> Timber.tag("Pop Movies Sync Adapter")
                                .i("Movie observable update complete")));

    }

    public static void initializeSyncAdapter(Context context) {
        Timber.e("in initializeSyncAdapter");
        getSyncAccount(context);
    }

    /**
     * Helper method to get the SyncAccount. This will create a new account if no 'poMovies.example.com'
     * account exists. If this is the case, 'onAccountCreated' will be called.
     *
     * @param context The context used to access the account service.
     * @return An account associated with the application
     */
    public static Account getSyncAccount(Context context) {
        Timber.e("in getSyncAccount");

        // Get an instance of the Android AccountManager
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password does not exist, the account does not exist
        if (null == accountManager.getPassword(newAccount)) {
            // Add the account and accountType, no password("") or userDefaults(null). If successful,
            // return the account object, else report an error.
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                // can throw an IllegalArgumentException on the main thread
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    /**
     * onAccountCreated is called when the account is freshly minted and it
     * configures the PeriodicSync and calls for an immediate sync.
     *
     * @param account new Account created in getSyncAccount(). A dummy one here.
     * @param context The context used to configure periodic sync, get ContentAuthority, set
     *                SyncImmediately.
     */
    public static void onAccountCreated(Account account, Context context) {
        Timber.e("in onAccountCreated");

        // Get the SYNC_INTERVAL from Pref_Utils. This is for when the SyncAdapter is initialized
        // and run by the App
        SYNC_INTERVAL = 60 * PrefUtils.getSyncInterval(context); //minutes

        /*
            Since we have created a syncAccount, lets ask ContentResolver to configure periodic syncs
            with the created account, desired contentAuthority (provider), desired Interval, and
            extras bundle, if any.
         */

        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
            Without calling, setSyncAutomatically, our periodic sync will not be enabled. Downside:
            The URL call may happen twice when the app is first installed and started. This informs
            the system that this account/content-authority tuple is eligible for 'auto-sync' when
            it receives a network tickle.

         */
        ContentResolver.setSyncAutomatically(account, context.getString(R.string.content_authority), true);

        /* Finally lets do a sync immediately to get things started */
        syncImmediately(context);
    }

    /**
     * Helper method to configure syncs at regular intervals
     *
     * @param context      The context used to configure periodic syncs
     * @param syncInterval Amount of time in seconds that you wish to elapse b/w periodic syncs
     * @param flexTime     Amount of flex time in seconds before pollFrequency that you permit for sync.
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Timber.e("in configurePeriodicSync");
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            SyncRequest syncRequest = new SyncRequest.Builder()
                    .setSyncAdapter(account, authority)
                    .setExtras(Bundle.EMPTY)
                    .syncPeriodic(syncInterval, flexTime)
                    .build();
            // Register a sync with the SyncManager
            ContentResolver.requestSync(syncRequest);

        } else {
            // If there is another periodic sync scheduled with the account, authority, and extras,
            // then a new periodic sync won't be added, instead the frequency of previous one will
            // be updated
            ContentResolver.addPeriodicSync(account, authority, Bundle.EMPTY, syncInterval);
        }
    }

    public static void syncImmediately(Context context) {
        Timber.e("in syncImmediately");
        Bundle bundle = new Bundle();

        /*
            Now lets pack some extras (booleans) in the bundle which effectively say:
            1. Put the sync request ahead of the queue: SYNC_EXTRAS_EXPEDITED
            2. Ignore the setting for now which says sync should happen when a network tickle
               is received. Sync now: SYNC_EXTRAS_IGNORE_SETTINGS
            3. Stay at it even in the face of backoffs: SYNC_EXTRAS_IGNORE_BACKOFF
            Setting the 2nd and 3rd extras to true is equivalent to setting 'SYNC_EXTRAS_MANUAL'
            to true.
         */
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority),
                bundle);
    }


    /*
        Shows a notification of top 20 movies as obtained from observable update
     */

    private void sendMoviesNotification(List<MovieItem> movies, String sortPreference) {
        Timber.e("in NotifyMovies");

        Context context = getContext();

        Uri ringtoneUri = PrefUtils.getRingtoneUri(context);
        Timber.e("Ringtone uri: %s", ringtoneUri);

        Bitmap largeNotificationIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);

        StringBuilder sb = new StringBuilder();
        sb.append("1. ").append(movies.get(0).getTitle());
        for (int i = 1; i < movies.size(); i++) {
            sb.append(", ").append(i).append(". ").append(movies.get(i).getTitle());
        }

        if (sortPreference.equals("popularity.desc")) {
            sortPreference = "Most Popular";
        } else {
            sortPreference = "Highest Rated";
        }

        String contentText = String.format(context.getString(R.string.format_notification_short), sortPreference);
        String notificationText = String.format(context.getString(R.string.format_notification_long),
                sortPreference, sb.toString());
        /*
            Build the notification. A notification must contain the following: A small Icon,
            a title, and detail text. //Todo It is common practice to use the app icon as the small Icon
            and something relevant as the largeIcon
        */

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_movie_24dp)
                .setColor(context.getResources().getColor(R.color.colorPrimary))
                .setLargeIcon(largeNotificationIcon)
                .setContentTitle(context.getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(ringtoneUri)
                .setTicker(notificationText)
                .setContentText(contentText)
                .setPriority(0);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(contentText);
        inboxStyle.setSummaryText(notificationText);

        builder.setStyle(inboxStyle);


        // Create an explicit intent for an Activity in the app
        Intent resultIntent = new Intent(context, BrowseMoviesActivity.class);

        // Create an artificial backStack for correct back navigation
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(BrowseMoviesActivity.class)
                .addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        notificationManagerCompat.notify(MOVIES_NOTIFICATION_ID, builder.build());

        // Set last sync timeStamp
        PrefUtils.setLastSyncTimeStamp(context);

    }


}
