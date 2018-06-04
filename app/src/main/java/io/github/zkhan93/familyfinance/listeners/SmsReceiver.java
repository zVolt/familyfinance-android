package io.github.zkhan93.familyfinance.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.services.MessagingService;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 17/7/17.
 */

public class SmsReceiver extends BroadcastReceiver {
    public static final String TAG = SmsReceiver.class.getSimpleName();
    private String extractedOtp;
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
        if (fbUser != null &&
                activeFamilyId != null &&
                intent.getAction() != null &&
                intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            //you have 10 sec to finish the job
            handleWork(context, intent);
        } else {
            Log.d(TAG, "one of NOT_LOGGED_IN, NOT_IN_ANY_FAMILY, INVALID_ACTION");
        }
    }

    public void handleWork(Context context, @NonNull Intent intent) {
        Bundle bundle = intent.getExtras();
        //if there is nothing in bundle (no sms data)
        if (bundle == null) return;
        List<Otp> otps = new ArrayList<>();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        //user not logged in
        if (fbUser == null)
            return;
        String mePk = fbUser.getUid();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context.getApplicationContext());
        String activeFamilyId = sharedPreferences.getString("activeFamilyId", null);
        Set<String> keywords = sharedPreferences.getStringSet("keywords", new HashSet<String>());
        //if not in any family
        if (activeFamilyId == null)
            return;
        boolean sendAllSms = sharedPreferences.getBoolean(context.getString(R.string
                        .pref_key_allsms),
                true);
        SmsMessage[] msgs;
        String smsFrom = "", smsBody = "";
        StringBuilder smsBodyBuilder = new StringBuilder();
        Otp otp;
        Object[] pdus;

        //---retrieve the SMS message received---
        String format = bundle.getString("format", null);
        String id;

        pdus = (Object[]) bundle.get("pdus");
        if (pdus == null)
            return;
        msgs = new SmsMessage[pdus.length];
        for (int i = 0; i < msgs.length; i++) {
            if (format != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
            else
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            smsFrom = msgs[i].getOriginatingAddress();
            smsBodyBuilder.append(msgs[i].getMessageBody());
        }
        smsBody = smsBodyBuilder.toString();
        //only if sms contains string "OTP"
        boolean hasKeyword = Util.hasKeywords(smsBody, keywords);
        if (!smsFrom.isEmpty() && (sendAllSms || hasKeyword)) {
            id = FirebaseDatabase.getInstance().getReference
                    ("otps").child(activeFamilyId).push().getKey();
            otp = new Otp();
            otp.setFromMemberId(mePk);
            otp.setContent(smsBody);
            otp.setNumber(smsFrom);
            otp.setTimestamp(Calendar.getInstance().getTimeInMillis());
            otp.setId(id);
            if (hasKeyword) {
                extractedOtp = Util.extractOTPFromString(context, otp.getContent());
                if (extractedOtp != null)
                    otp.setContent(String.format("%s: %s", extractedOtp, otp.getContent()));
            }
            otps.add(otp);
        } else {
            Log.d(TAG, "Not sharing an SMS");
        }
        Calendar calendar = Calendar.getInstance();
        String month = String.valueOf(calendar.get(Calendar.MONTH));
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        DatabaseReference otpRef = FirebaseDatabase.getInstance().getReference
                ("otps").child(activeFamilyId).child(year).child(month);
        DatabaseReference newOtpRef;
        Map<String, String> data = new HashMap<>();
        data.put(MessagingService.KEYS.FROM_NAME, fbUser.getDisplayName());
        data.put(MessagingService.KEYS.FAMILY_ID, activeFamilyId);
        data.put(MessagingService.KEYS.ME_ID, mePk);
        for (Otp tmpOtp : otps) {
            newOtpRef = otpRef.push();
            tmpOtp.setId(newOtpRef.getKey());
            extractedOtp = Util.extractOTPFromString(context, tmpOtp.getContent());
            newOtpRef.setValue(tmpOtp);
            data.put(MessagingService.KEYS.NUMBER, tmpOtp.getNumber());
            data.put(MessagingService.KEYS.ID, newOtpRef.getKey());
            data.put(MessagingService.KEYS.CONTENT, tmpOtp.getContent());
        }
        //TODO: Show internet not there notification
        String strOtp = MessagingService.showNotification(context.getApplicationContext(), data);
        MessagingService.copyToClipboard(context.getApplicationContext(), strOtp);
    }
}
