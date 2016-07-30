package com.example.android.popmovies;

import com.example.android.popmovies.data.DataModule;
import com.example.android.popmovies.utilities.glide.GlideSetup;
import com.example.android.popmovies.data.sync.PopMoviesSyncAdapter;
import com.example.android.popmovies.ui.fragment.BaseFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by sheshloksamal on 28/03/16.
 *
 */

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DataModule.class}
)
public interface ApplicationComponent {

    void inject(BaseFragment baseFragment);
    void inject(GlideSetup glideSetup);
    void inject(PopMoviesSyncAdapter popMoviesSyncAdapter);
}
