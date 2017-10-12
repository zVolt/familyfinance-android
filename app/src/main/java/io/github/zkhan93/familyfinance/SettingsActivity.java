package io.github.zkhan93.familyfinance;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
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
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {
    public static final String TAG = SettingsActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private static int REQUEST_CODE_SET_PIN = 102;
    private static int REQUEST_CODE_CHECK_PIN = 103;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                                index >= 0
                                        ? listPreference.getEntries()[index]
                                        : null);

                    } else if (preference instanceof RingtonePreference) {
                        // For ringtone preferences, look up the correct display value
                        // using RingtoneManager.
                        if (TextUtils.isEmpty(stringValue)) {
                            // Empty values correspond to 'silent' (no ringtone).
                            preference.setSummary(R.string.pref_ringtone_silent);

                        } else {
                            Ringtone ringtone = RingtoneManager.getRingtone(
                                    preference.getContext(), Uri.parse(stringValue));

                            if (ringtone == null) {
                                // Clear the summary if there was a lookup error.
                                preference.setSummary(null);
                            } else {
                                // Set the summary to reflect the new ringtone display
                                // name.
                                String name = ringtone.getTitle(preference.getContext());
                                preference.setSummary(name);
                            }
                        }

                    } else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setupActionBar();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(GeneralPreferenceFragment.TAG);
        if (fragment == null)
            fragment = new GeneralPreferenceFragment();
        fm.beginTransaction().replace(R.id.container, fragment).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment implements
            Preference.OnPreferenceClickListener, SharedPreferences
            .OnSharedPreferenceChangeListener {
        public static final String TAG = GeneralPreferenceFragment.class.getSimpleName();
        private SharedPreferences sharedPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
//            bindPreferenceSummaryToValue(findPreference("example_list"));
//            enable_pin
            Preference enablePin = findPreference("enable_pin");
            enablePin.setOnPreferenceClickListener(this);
            ((SwitchPreference) enablePin).setChecked(sharedPreferences.getBoolean("enable_pin",
                    false));
            Log.d(TAG, "setting all set");
        }

        @Override
        public void onStart() {
            super.onStart();
            PreferenceManager.getDefaultSharedPreferences(getActivity())
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceClick(Preference preference) {
            Log.d(TAG, "preferenceClick" + preference.getKey());
            if (preference.getKey().equals("enable_pin")) {
                if (((SwitchPreference) preference).isChecked()) {
                    Log.d(TAG, "set new PIN");
                    startActivityForResult(new Intent(PinActivity.ACTIONS.SET_PIN, null,
                            getActivity(), PinActivity.class), REQUEST_CODE_SET_PIN);
                } else {
                    Log.d(TAG, "check PIN then disable");
                    startActivityForResult(new Intent(PinActivity.ACTIONS.CHECK_PIN, null,
                            getActivity(), PinActivity.class), REQUEST_CODE_CHECK_PIN);
                }
                return true;
            }
            return false;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("enable_pin"))
                ((SwitchPreference) findPreference("enable_pin")).setChecked(sharedPreferences
                        .getBoolean(key, false));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Log.d(TAG, "onActivityResult req:" + requestCode + " res:" + resultCode);
            //response for pin set
            if (requestCode == REQUEST_CODE_SET_PIN) {
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "set pin success");
                    sharedPreferences.edit().putBoolean
                            ("enable_pin", true).apply();
                } else {
                    //cancelled
                    Log.d(TAG, "set pin failed");
                    sharedPreferences.edit().putBoolean
                            ("enable_pin", false).remove("PIN").apply();
                    ((SwitchPreference) findPreference("enable_pin")).setChecked(false);
                }
            }
            //response for PIn check
            if (requestCode == REQUEST_CODE_CHECK_PIN) {

                if (resultCode == RESULT_OK) {
                    //pin checked now disable the pin
                    Log.d(TAG, "check pin success");
                    sharedPreferences.edit().putBoolean
                            ("enable_pin", false).putString("PIN", null).remove("PIN").apply();
                } else {
                    //pin verification failed keep the pin on
                    Log.d(TAG, "check pin failed");
                    sharedPreferences.edit().putBoolean
                            ("enable_pin", true).apply();
                    ((SwitchPreference) findPreference("enable_pin")).setChecked(true);
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
