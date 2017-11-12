package io.github.zkhan93.familyfinance.services;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.models.OtpDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 11/11/17.
 */

public class SMSUploadService extends JobIntentService implements OnCompleteListener<Void> {
    public static final String TAG = SMSUploadService.class.getSimpleName();
    private static String[] KEYWORDS = {"otp", "onetimepassword","pin"};
    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    private int numberOfsms = 0;
    private String extractedOtp;
    final Handler mHandler = new Handler();

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SMSUploadService.class, JOB_ID, work);
    }


    @Override
    public void onHandleWork(@NonNull Intent intent) {
        Bundle bundle = intent.getExtras();
        //if there is nothing in bundle (no sms data)
        if (bundle == null) return;
        List<Otp> otps = new ArrayList<>();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        //user not logged in
        if (fbUser == null)
            return;
        String mePk = fbUser.getUid();
        Member me = ((App) getApplicationContext()).getDaoSession().getMemberDao().load(mePk);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext());
        String activeFamilyId = sharedPreferences.getString("activeFamilyId", null);
        //if not in any family
        if (activeFamilyId == null)
            return;
        boolean sendAllSms = sharedPreferences.getBoolean(getString(R.string.pref_key_allsms),
                false);
        SmsMessage[] msgs;
        String smsFrom, smsBody;
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
            smsBody = msgs[i].getMessageBody();
            //only if sms contains string "OTP"
            if (sendAllSms || hasKeywords(smsBody)) {
                id = FirebaseDatabase.getInstance().getReference
                        ("otps").child(activeFamilyId).push().getKey();
                otp = new Otp();
                otp.setFrom(me);
                otp.setFromMemberId(mePk);
                otp.setContent(smsBody);
                otp.setNumber(smsFrom);
                otp.setTimestamp(Calendar.getInstance().getTimeInMillis());
                otp.setId(id);
                Log.d(TAG, otp.toString());
                otps.add(otp);

            } else {
                Log.d(TAG, "Not sharing an SMS");
            }
        }
        //insert into local database
        DaoSession daoSession = ((App) getApplicationContext()).getDaoSession();
        new InsertTask<OtpDao, Otp>(daoSession.getOtpDao())
                .execute(otps.toArray(new Otp[otps.size()]));
        //push to firebase
        DatabaseReference otpRef = FirebaseDatabase.getInstance().getReference
                ("otps").child(activeFamilyId);
        DatabaseReference newOtpRef;
        numberOfsms = otps.size();
        for (Otp tmpOtp : otps) {
            newOtpRef = otpRef.push();
            tmpOtp.setId(newOtpRef.getKey());
            tmpOtp.__setDaoSession(daoSession);
            newOtpRef.setValue(tmpOtp).addOnCompleteListener(this);
            extractedOtp = Util.extractOTPFromString(tmpOtp.getContent());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (extractedOtp != null && !extractedOtp.isEmpty())
            toast(extractedOtp);
    }

    // Helper for showing tests
    void toast(@NonNull  final CharSequence text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SMSUploadService.this, text, Toast.LENGTH_LONG).show();
                Util.copyToClipboard(SMSUploadService.this, (ClipboardManager) SMSUploadService.this
                        .getSystemService(CLIPBOARD_SERVICE), text.toString());
            }
        });
    }

    private boolean hasKeywords(String content) {
        if (content == null || content.isEmpty()) return false;
        content = content.replace(" ", "");
        for (String keyword : KEYWORDS)
            if (content.toLowerCase().contains(keyword.toLowerCase()))
                return true;
        return false;
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        synchronized (this) {
            numberOfsms -= 1;
        }
        Log.d(TAG, "otp pushed on firebase " + (task.isSuccessful() ? "successfully" :
                "failed"));
        Log.d(TAG, "number of SMS thread remaining" + numberOfsms);
    }
}
