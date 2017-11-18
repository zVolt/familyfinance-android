package io.github.zkhan93.familyfinance.listeners;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import io.github.zkhan93.familyfinance.util.Util;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class CopyOtpListener extends BroadcastReceiver {
    public static final String TAG = CopyOtpListener.class.getSimpleName();
    public static final String ACTION_COPY_OTP = CopyOtpListener.class.getSimpleName() +
            ".COPY_OTP";
    private String otp;

    @Override
    public void onReceive(Context context, Intent intent) {
        String otp = intent.getStringExtra("OTP");
        String toastMessage = Util.copyToClipboard(context, (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE), otp);
        if (toastMessage == null || toastMessage.isEmpty()) return;
        Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
    }
}
