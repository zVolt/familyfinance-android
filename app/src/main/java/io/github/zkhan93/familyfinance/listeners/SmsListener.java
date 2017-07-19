package io.github.zkhan93.familyfinance.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.appcompat.BuildConfig;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.tasks.InsertTask;

/**
 * Created by zeeshan on 17/7/17.
 */

public class SmsListener extends BroadcastReceiver implements OnCompleteListener<Void>{
    public static final String TAG = SmsListener.class.getSimpleName();
    private WeakReference<Context> contextWeakReference;
    private List<Otp> otps;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive");
        String activeFamilyId = PreferenceManager.getDefaultSharedPreferences(context).getString
                ("activeFamilyId", null);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null && activeFamilyId != null && intent.getAction().equals("android" +
                ".provider.Telephony" +
                ".SMS_RECEIVED")) {
            contextWeakReference = new WeakReference<>(context);
            otps = new ArrayList<>();
            String mePk = fbUser.getUid();
            Member me = ((App) context.getApplicationContext()).getDaoSession().getMemberDao()
                    .load(mePk);
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
//            int phone = bundle.getInt("phone", -1);
//            int subscription = bundle.getInt("subscription", -1);
//            int slot = bundle.getInt("slot", -1);
            SmsMessage[] msgs = null;
            String smsFrom, smsBody;
            Otp otp;
            Object[] pdus;
//            SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
//            String numbersubs = subscriptionManager.getActiveSubscriptionInfo(subscription)
//                    .getNumber();
//
//            String numberslot = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex
//                    (slot).getNumber();

            if (bundle != null) {
                //---retrieve the SMS message received---
                String format = bundle.getString("format", null);
                try {
                    pdus = (Object[]) bundle.get("pdus");

                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        else
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsFrom = msgs[i].getOriginatingAddress();
                        smsBody = msgs[i].getMessageBody();
                        //only if sms contains string "OTP"
                        if (smsBody.contains("OTP")) {
                            DatabaseReference otpRef = FirebaseDatabase.getInstance().getReference
                                    ("otps").child(activeFamilyId).push();
                            otp = new Otp();
                            otp.setFrom(me);
                            otp.setFromMemberId(mePk);
                            otp.setContent(smsBody);
                            otp.setNumber(smsFrom);
                            otp.setTimestamp(Calendar.getInstance().getTimeInMillis());
                            otp.setId(otpRef.getKey());
                            otps.add(otp);
                            otpRef.setValue(otp).addOnCompleteListener(this);
                        }
                    }
                } catch (Exception e) {
                    Log.d("Exception caught", e.getMessage());
                }
            }
        }
    }

    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            Context context = contextWeakReference.get();
            if (context == null)
                return;
            //because this may be called multiple times
            new InsertTask<>(((App) context.getApplicationContext()).getDaoSession().getOtpDao())
                    .execute(otps.toArray(new Otp[otps.size()]));
        }
    }
}
