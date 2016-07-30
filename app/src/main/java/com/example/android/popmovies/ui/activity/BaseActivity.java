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

package com.example.android.popmovies.ui.activity;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.popmovies.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by sheshloksamal on 27/03/16.
 * Base class for all activities. Binds views, sets up toolbar. Memory leaks in activity are watched
 * by application
 *
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Nullable @BindView(R.id.toolbar) Toolbar mToolbar;

    @CallSuper @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @CallSuper @Override
    public void setContentView(int layoutResId){
        super.setContentView(layoutResId);
        //Get a reference to all views in the contentView
        ButterKnife.bind(this);
        setUpToolBar();

    }

    @CallSuper @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private void setUpToolBar() {
        if (mToolbar == null) {
            Timber.w("Didn't find a toolbar");
            return;
        }
        // Set elevation of 4dp as per material design specs
        ViewCompat.setElevation(mToolbar, R.dimen.toolbar_elevation);

        setSupportActionBar(mToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        actionBar.setDisplayHomeAsUpEnabled(false);

    }

    public Toolbar getToolbar() {
        return this.mToolbar;
    }

}
