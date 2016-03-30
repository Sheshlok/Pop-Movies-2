package com.example.android.popmovies.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.provider.MovieContract;
import com.example.android.popmovies.data.sync.FetchMovieData;
import com.example.android.popmovies.utilities.PrefUtils;

/**
 * Created by sheshloksamal on 7/11/15.
 *
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private final String LOG_TAG = SettingsActivity.class.getSimpleName();

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add general preferences defined in the xml file
        // Although addPreferencesFromResource(R.xml.id) is deprecated it is the current best practice
        // given that we are targeting GingerBread (API=10)
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_by_key)));
    }

    @Override
    public void onResume(){
        Log.e(LOG_TAG, "in onResume");
        super.onResume();
        PrefUtils.registerOnSharedPreferenceChangeListener(this, this);

    }

    @Override
    public void onPause(){
        Log.e(LOG_TAG, "in onPause");
        super.onPause();
        PrefUtils.unregisterOnSharedPreferenceChangeListener(this, this);
    }

    /**
     * Sets the listener to watch for value changes - sets the callback to be invoked
     * (onPreferenceChange) when this Preference is changed by the user (but before the internal
     * state, i.e. DefaultSharedPreferences, is updated). Thus, first onPreferenceChange is called
     * and then onSharedPreferenceChange (when the preference.setSummary(..) executes).
     * Also fires the listener once, to initialize the summary (so it shows up before the value is
     * changed).
     *
     * @param preference 'preference' object to which listener is to be attached
     */

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's current value
        // Get SharedPreferences object containing all key-value pairs that are associated with
        // the Preference objects. Extract the value of the preference object that is passed on
        // as a parameter
        setPreferenceSummary(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    /**
     * This gets called when the preference is changed by the user
     * @param preference    Preference object that is bound to an onPreferenceChangeListener.
     * @param value         Value of the preference object {K:V}
     * @return              True after setting the summary appropriately
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        Log.e(LOG_TAG, "in onPreferenceChange");
        setPreferenceSummary(preference, value);
        return true;
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        Log.e(LOG_TAG, "in setPreferenceSummary");
        String stringValue = value.toString();

        if(preference instanceof ListPreference) {
            // For ListPreference, look up the correct display value in the
            // preference's entries list, since they have separate labels/values
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.e(LOG_TAG, "in onSharedPreferenceChanged");
        if (key.equals(getString(R.string.pref_sort_by_key))) {
            // We have changed sortOrder, re-query the ApiService
            String queryPage = "1";
            FetchMovieData.updateMovieList(this, queryPage);
            /*
                Get the ContentResolver and notify changes to all CONTENT_URIs (This also lets
                the CONTENT_URI descendents be notified). So, the loaders sitting on these
                CONTENT_URIs or their descendents, get this notification and re-query the
                ContentResolver for the updated cursor. Upon getting the updated cursor, the
                onLoadFinished() method is called, and the views get updated (either through
                CursorAdapter or directly, for e.g. MovieListFragment and DetailActivityFragment
                cases)
             */
            getContentResolver().notifyChange(MovieContract.MovieEntry.CONTENT_URI, null);
            getContentResolver().notifyChange(MovieContract.ReviewEntry.CONTENT_URI, null);
            getContentResolver().notifyChange(MovieContract.TrailerEntry.CONTENT_URI, null);
        }
    }

}
