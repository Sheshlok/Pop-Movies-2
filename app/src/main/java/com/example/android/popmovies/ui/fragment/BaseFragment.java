package com.example.android.popmovies.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.example.android.popmovies.PopularMoviesApplication;
import com.example.android.popmovies.data.repository.GenresRepository;
import com.example.android.popmovies.data.repository.MoviesRepository;
import com.squareup.leakcanary.RefWatcher;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by sheshloksamal on 20/03/16.
 *
 * Base class for all fragments
 * Binds views, performs dependency injections, and watches for memory leaks
 *
 */
public abstract class BaseFragment extends Fragment {

    private Toast mToast;
    private Unbinder mUnbinder;
    @Inject MoviesRepository mMoviesRepository;
    @Inject GenresRepository mGenresRepository;


    @CallSuper @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Perform Dependency Injection when activity is attached to the fragment and is available
        ((PopularMoviesApplication) getActivity().getApplication()).getComponent().inject(this);
    }

    @CallSuper @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        super.onViewCreated(rootView, savedInstanceState);

        // Get a reference to all the views in the rootView
        mUnbinder = ButterKnife.bind(this, rootView);
    }

    @CallSuper @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }


    @CallSuper @Override
    public void onDestroyView() {
        // Release reference to all the views
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @CallSuper @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = PopularMoviesApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    protected void showToast(String message) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
