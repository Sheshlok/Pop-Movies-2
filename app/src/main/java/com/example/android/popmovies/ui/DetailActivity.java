package com.example.android.popmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popmovies.R;
import com.example.android.popmovies.SettingsActivity;
import com.example.android.popmovies.ui.DetailActivityFragment;

public class DetailActivity extends AppCompatActivity {

    DetailActivityFragment detailActivityFragment = new DetailActivityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /* Critical to start the Fragment */
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, detailActivityFragment)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        // Return true to display Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically
        // handle clicks on the Home/Up button, so long as you specify a parent Activity
        // in AndroidManifest.xml

        int id = item.getItemId();

        // no Inspection Simplifiable If-Statement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
