package io.github.zkhan93.familyfinance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zeeshan on 21/10/17.
 */

public class FragmentSetting extends PreferenceFragmentCompat {

    public static final String TAG = FragmentSetting.class.getSimpleName();
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            (preference, value) -> {
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
                    case "pref_key_allsms":
                        preference.setSummary((boolean) value ? R.string.pref_allsms_enable : R
                                .string.pref_allsms_disable);
                        return true;
                    case "pref_key_notification_only_otp":
                        preference.setSummary((boolean) value ? R.string
                                .pref_notificationOnlyOtp_enable : R
                                .string.pref_notificationOnlyOtp_disable);
                        return true;
                    default:
                        return false;
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
        else if (preference instanceof ListPreference)
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity());
        setPreferencesFromResource(R.xml.pref_general, rootKey);
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_copy)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_notification)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string
                .pref_key_notification_only_otp)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_vibrate)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_key_allsms)));
        Log.d(TAG, "setting all set");
    }

}
