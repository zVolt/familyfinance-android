package io.github.zkhan93.familyfinance.listeners;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import io.github.zkhan93.familyfinance.util.Util;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class NotificationActionsListener extends BroadcastReceiver {
    public static final String TAG = NotificationActionsListener.class.getSimpleName();
    public static final String ACTION_COPY_OTP = NotificationActionsListener.class.getSimpleName() +
            ".COPY_OTP";
    public static final String ACTION_CLAIM_OTP = NotificationActionsListener.class.getSimpleName
            () +
            ".CLAIM_OTP";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Util.Log.d(TAG, "action: %s", action);
        if (action == null) return;
        if (action.equals(ACTION_COPY_OTP)) {
            String otp = intent.getStringExtra("OTP");
            String toastMessage = Util.copyToClipboard(context, (ClipboardManager) context
                    .getSystemService(Context.CLIPBOARD_SERVICE), otp);
            if (toastMessage == null || toastMessage.isEmpty()) return;
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
        } else if (action.equals(ACTION_CLAIM_OTP)) {
            Bundle bundle = intent.getExtras();
            Util.Log.d(TAG, "bundle %s", bundle);
            if (bundle == null) return;
            String otpId = bundle.getString("otpId");
            String familyId = bundle.getString("familyId");
            String meId = bundle.getString("meId");
            Util.Log.d(TAG, "%s | %s | %s", otpId, familyId, meId);
            if (familyId != null && otpId != null && meId != null)
                FirebaseDatabase.getInstance().getReference("otps")
                        .child(familyId)
                        .child(otpId)
                        .child("claimedByMemberId")
                        .setValue(meId);
        }
    }
}
