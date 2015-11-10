package com.example.android.popmovies;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by sheshloksamal on 7/11/15.
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {

    // private final String LOG_TAG = SettingsActivity.class.getSimpleName();

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

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value is
     * changed)
     * @param preference 'preference' object to which listener has to be attached
     */

    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's current value
        // Get SharedPreferences object containing all key-value pairs that are associated with
        // the Preference objects. Extract the value of the preference object that is passed on
        // as a parameter
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    /**
     * Must be over-ridden as per the class declaration syntax.
     * Called in the class above to update the summary
     * @param preference    Preference object that is bound to an onPreferenceChangeListener.
     * @param value         Value of the preference object {K:V}
     * @return              True after setting the summary appropriately
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
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
        return true;
    }

}
