package com.example.android.popmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popmovies.R;
import com.facebook.stetho.Stetho;

public class BrowseMoviesActivity extends AppCompatActivity {

    private static final String LOG_TAG = BrowseMoviesActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Todo: Remove later. Only for debug purposes
        Stetho.initializeWithDefaults(this);

        Log.e(LOG_TAG, "in onCreate");
        setContentView(R.layout.activity_browse_movies);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.e(LOG_TAG, "in onStart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.e(LOG_TAG, "in onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.e(LOG_TAG, "in onResume");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.e(LOG_TAG, "in onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.e(LOG_TAG, "in onDestroy");
    }
}
