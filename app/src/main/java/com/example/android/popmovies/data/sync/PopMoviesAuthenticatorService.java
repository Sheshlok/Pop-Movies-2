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
