package io.github.zkhan93.familyfinance.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.services.SMSUploadService;

/**
 * Created by zeeshan on 17/7/17.
 */

public class SmsReceiver extends BroadcastReceiver {
    public static final String TAG = SmsReceiver.class.getSimpleName();
    public static final String JOB_TAG = SMSUploadService.class.getSimpleName() +
            "_JOB_TAG";
    private List<Otp> otps;

    /**
     * accourding to docs you have 10 sec to perform your job and the object will no longer be
     * active after it returns from this function
     * its runs on main thread no network or IO operations
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        String activeFamilyId = PreferenceManager.getDefaultSharedPreferences(context).getString
                ("activeFamilyId", null);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null && activeFamilyId != null && intent.getAction().equals("android" +
                ".provider.Telephony.SMS_RECEIVED")) {
            SMSUploadService.enqueueWork(context, intent);
        } else {
            Log.d(TAG, "one of NOT_LOGGED_IN, NOT_IN_ANY_FAMILY, INVALID_ACTION");
        }
    }
}
