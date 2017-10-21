package io.github.zkhan93.familyfinance.listeners;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class CopyOtpListener extends BroadcastReceiver {
    public static final String TAG = CopyOtpListener.class.getSimpleName();
    public static final String ACTION_COPY_OTP = "io.github.zkhan93.familyfinance.listeners" +
            ".CopyOtpListener.COPY_OTP";
    private String otp;

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("OTP");
        Log.d(TAG, "copy OTP from" + message);
        Util.copyToClipboardAndToast(context, Util.extractOTPFromString(message));
    }
}
