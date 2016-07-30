package com.example.android.popmovies.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.android.popmovies.R;
import com.example.android.popmovies.data.sync.PopMoviesSyncAdapter;
import com.example.android.popmovies.utilities.PrefUtils;

import timber.log.Timber;

/**
 * Created by sheshloksamal on 7/11/15.
 *
 */
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AppBarLayout appBarLayout =  createPreferenceScreenWithSupportV21Toolbar();
        Toolbar toolbar = (Toolbar) appBarLayout.getChildAt(0);

        ViewCompat.setElevation(toolbar, R.dimen.toolbar_elevation);

        toolbar.setTitle(getString(R.string.title_activity_settings));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view -> NavUtils.navigateUpTo(this,
                NavUtils.getParentActivityIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)));

        setUpSimplePreferencesScreen();
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we are running pre-L, we need to inject our tint aware Views in place of the
            // standard framework versions.
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
            }
        }
        return null;
    }

    @Override
    public void onResume() {
        Timber.e("in onResume");
        super.onResume();
        PrefUtils.registerOnSharedPreferenceChangeListener(this, this);

    }

    @Override
    public void onPause() {
        Timber.e("in onPause");
        super.onPause();
        PrefUtils.unregisterOnSharedPreferenceChangeListener(this, this);
    }

    private void setUpSimplePreferencesScreen(){
        // Add general preferences defined in the xml file
        // Although addPreferencesFromResource(R.xml.id) is deprecated it is the current best practice
        // given that we are targeting GingerBread (API=10)
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_by_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sync_frequency_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_notification_ringtone_key)));

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
     *
     * @param preference Preference object that is bound to an onPreferenceChangeListener.
     * @param value      Value of the preference object {K:V}
     * @return True after setting the summary appropriately
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        Timber.e("in onPreferenceChange");
        setPreferenceSummary(preference, value);
        return true;
    }

    private void setPreferenceSummary(Preference preference, Object value) {
        Timber.e("in setPreferenceSummary");
        String stringValue = value.toString();

        if (preference instanceof ListPreference) {
            // For ListPreference, look up the correct display value in the
            // preference's entries list, since they have separate labels/values
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else if (preference instanceof RingtonePreference) {
            // For Ringtone preference, look up the correct display value using RingtoneManager
            if (TextUtils.isEmpty(stringValue)) {
                // Empty values correspond to silent, i.e. no ringtone
                preference.setSummary(R.string.pref_ringtone_silent);
            } else {
                Ringtone ringtone = RingtoneManager.getRingtone(preference.getContext(), Uri.parse(stringValue));
                if (ringtone == null) {
                    // Clear the summary if there was a look-up error
                    preference.setSummary(null);
                } else {
                    // Set the summary to display the new ringtone name
                    preference.setSummary(ringtone.getTitle(preference.getContext()));
                }
            }

        } else {
            // For other preferences, set the summary to the value's simple string representation
            preference.setSummary(stringValue);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Timber.e("in onSharedPreferenceChanged");

        if (key.equals(getString(R.string.pref_sort_by_key))) {
            // We have changed sortOrder, re-query the ApiService
            Timber.d("Sort key Changed");
            /*
                No need to get ContentResolver and notify change to CONTENT_URIs (or their
                descendents) since BriteContentResolver sets an observable query which is processed
                and then sent to FavoredMoviesFragment via 'withLatestFrom' operator.
                Doing this very step here causes the toast "No favored movies yet...." to be
                displayed on the settings screen when there are no favored movies and we select
                a different sortPreference other than favorites.
             */
        } else if (key.equals(getString(R.string.pref_sync_frequency_key))) {
            // Reconfigure the periodic sync interval
            int syncInterval = 60 * PrefUtils.getSyncInterval(this);
            if (syncInterval != PopMoviesSyncAdapter.SYNC_INTERVAL) {
                PopMoviesSyncAdapter.configurePeriodicSync(this, syncInterval, syncInterval/3); //FLEX_TIME = SYNC_INTERVAL / 3;
            }
        }
    }

    /** Create Support(V21)toolbar - AppBarLayout here to allow elevation and shadowing on toolbar-
     * for Gingerbread devices. Later we cast it back into toolbar with 'getChildAt(0)'
     * @see <a href="https://github.com/davcpas1234/MaterialSettings"> GitHub Repo Material Settings
     * </a> */

    private AppBarLayout createPreferenceScreenWithSupportV21Toolbar() {
        AppBarLayout appBarLayout;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
            appBarLayout = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);
            root.addView(appBarLayout, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            appBarLayout = (AppBarLayout) LayoutInflater.from(this).inflate(R.layout.settings_toolbar, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelOffset(tv.data, getResources().getDisplayMetrics());
            } else {
                height = appBarLayout.getHeight();
            }

            content.setPadding(0, height, 0, 0);
            root.addView(content);
            root.addView(appBarLayout);
        }

        return appBarLayout;

    }

}
