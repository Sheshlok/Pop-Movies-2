package com.example.android.popmovies.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import timber.log.Timber;

/**
 * Created by sheshloksamal on 17/03/16.
 * We have our data transfer code for the home screen (get latest genres and get movies as per the
 * stored preference) encapsulated in syncAdapter. But, we need to provide the framework (SyncManager)
 * access to this code. This bound service passes an Android IBinder object for the SyncAdapter
 * component to the framework. With this binder object, the framework can invoke the 'onPerformSync'
 * method.
 * In a nutshell, this defines a Service that returns an IBinder for the SyncAdapter class, allowing
 * the SyncManager (system framework for sync) to call onPerformSync(..)
 */
public class PopMoviesSyncService extends Service {

    // Storage for an instance of SyncAdapter
    private static PopMoviesSyncAdapter sPopMoviesSyncAdapter = null;

    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    /*
        Instantiate the SyncAdapter object.
        The system calls this method when the service is first created, to perform one-time setup
        procedures(before it calls either onStartCommand() or onBind(). If the service is already
        running this method is not called.
     */
    @Override
    public void onCreate() {
        Timber.e("in onCreate");
        synchronized (sSyncAdapterLock) {
            if (sPopMoviesSyncAdapter == null) {
                Timber.e("PopMoviesSyncAdapter not null");
                sPopMoviesSyncAdapter = new PopMoviesSyncAdapter(getApplicationContext(), true);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPopMoviesSyncAdapter.getSyncAdapterBinder();
    }
}
