package com.example.android.popmovies.data.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by sheshloksamal on 17/03/16.
 * A bound service that instantiates the authenticator when started.
 * This (bound) service provides an Android IBinder object that allows the SyncAdapter framework to
 * acccess the authenticator and exchange data
 */
public class PopMoviesAuthenticatorService extends Service {
    private PopMoviesAuthenticator mPopMoviesAuthenticator;

    @Override
    public void onCreate() {
        mPopMoviesAuthenticator = new PopMoviesAuthenticator(this);
    }

    /*
        When the system binds to the service for RPC, return the authenticator's IBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mPopMoviesAuthenticator.getIBinder();
    }
}
