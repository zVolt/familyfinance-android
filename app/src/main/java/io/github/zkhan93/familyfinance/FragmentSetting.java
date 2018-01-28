package io.github.zkhan93.familyfinance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zeeshan on 21/10/17.
 */

public class FragmentSetting extends PreferenceFragment implements Preference
        .OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String TAG = FragmentSetting.class.getSimpleName();
    private SharedPreferences sharedPreferences;
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
                    Log.d(TAG, preference.getKey() + " preference summary with value " +
                            stringValue);
                    switch (preference.getKey()) {
                        case "pref_key_notification":
                            preference.setSummary((boolean) value ? R.string
                                    .pref_notification_enable : R.string.pref_notification_disable);
                            return true;
                        case "pref_key_ringtone":
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
                            return true;
                        case "pref_key_vibrate":
                            //no summary for vibrate preference
                            return true;
                        case "pref_key_copy":
                            preference.setSummary((boolean) value ? R.string.pref_copy_enable : R
                                    .string.pref_copy_disable);
                            return true;
                        case "pref_key_pin":
                            preference.setSummary((boolean) value ? R.string.pref_pin_enable : R
                                    .string.pref_pin_disable);
                            return true;
                        case "pref_key_autolock":
                            int index = ((ListPreference) preference).findIndexOfValue(stringValue);
                            preference.setSummary(preference.getContext().getString(R.string
                                    .pref_autolock, ((ListPreference) preference).getEntries()
                                    [index]));
                            return true;
                        case "pref_key_allsms":
                            preference.setSummary((boolean) value ? R.string.pref_allsms_enable : R
                                    .string.pref_allsms_disable);
                            return true;
                        case "pref_key_notification_only_otp":
                            preference.setSummary((boolean) value ? R.string
                                    .pref_notificationOnlyOtp_enable : R
                                    .string.pref_notificationOnlyOtp_disable);
                            return true;
                        case "pref_key_no_of_sms":
                            preference.setSummary(preference.getContext().getString(R.string
                                    .pref_no_of_sms, value));
                            return true;
                        default:
                            return false;
                    }
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
        if (preference instanceof SwitchPreference)
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        else if (preference instanceof RingtonePreference || preference instanceof ListPreference)
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
    }

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


        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_copy)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_notification)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string
                .pref_key_notification_only_otp)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_ringtone)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_vibrate)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_autolock)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_allsms)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_no_of_sms)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_pin)));

        Preference pinPreference = findPreference(getString(R.string.pref_key_pin));
        pinPreference.setOnPreferenceChangeListener(this);
        ((SwitchPreference) pinPreference).setChecked(sharedPreferences.getBoolean(getString
                        (R.string.pref_key_pin),
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
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .unregisterOnSharedPreferenceChangeListener(this);
        findPreference(getString(R.string.pref_key_pin)).setOnPreferenceClickListener(null);
        Log.d(TAG, "listener removed");
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
    public boolean onPreferenceChange(Preference preference, Object o) {
        Log.d(TAG, "this is value: " + o.toString());
        return false;
    }


    public boolean onPreferenceChanged(Preference preference) {
        Log.d(TAG, "preferenceClick" + preference.getKey());
        if (preference.getKey().equals(getString(R.string.pref_key_pin))) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
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
        if (isAdded() && key.equals(getString(R.string.pref_key_pin)))
            ((SwitchPreference) findPreference(getString(R.string.pref_key_pin))).setChecked
                    (sharedPreferences
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
                        (getString(R.string.pref_key_pin), true).apply();
            } else {
                //cancelled
                Log.d(TAG, "set pin failed");
                sharedPreferences.edit().putBoolean
                        (getString(R.string.pref_key_pin), false).remove(getString(R.string
                        .pref_key_pin_value)).apply();
                ((SwitchPreference) findPreference(getString(R.string.pref_key_pin)))
                        .setChecked(false);
            }
        }
        //response for PIN check
        if (requestCode == REQUEST_CODE_CHECK_PIN) {

            if (resultCode == RESULT_OK) {
                //pin checked now disable the pin
                Log.d(TAG, "check pin success");
                sharedPreferences.edit().putBoolean
                        (getString(R.string.pref_key_pin), false).putString(getString(R.string
                        .pref_key_pin_value), null)
                        .remove(getString(R.string.pref_key_pin_value)).apply();
            } else {
                //pin verification failed keep the pin on
                Log.d(TAG, "check pin failed");
                sharedPreferences.edit().putBoolean
                        (getString(R.string.pref_key_pin), true).apply();
                ((SwitchPreference) findPreference(getString(R.string.pref_key_pin)))
                        .setChecked(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
