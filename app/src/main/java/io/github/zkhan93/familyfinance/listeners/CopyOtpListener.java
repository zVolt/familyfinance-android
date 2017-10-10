package io.github.zkhan93.familyfinance.listeners;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
        otp = null;
        String[] segments = message.split("\\s+");
        for (String segment : segments) {
            if (segment.startsWith("."))
                segment = segment.substring(1);
            if (segment.endsWith("."))
                segment = segment.substring(0, segment.length() - 1);
            if ((segment.length() == 4 || segment.length() == 6) && segment.matches("\\d+")) {

                Log.d(TAG, "OTP is:" + segment);
                if (otp == null || otp.length() < segment.length())
                    otp = segment;
            }
        }
        Toast toast = Toast.makeText(context, otp, Toast.LENGTH_LONG);
        toast.show();
        //copy otp to clipboard
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        if (clipboard == null) {
            Log.d(TAG, "cannot get clipboard");
            return;
        }
        ClipData clip = ClipData.newPlainText("OTP", otp);
        clipboard.setPrimaryClip(clip);
        toast.setText(otp + " copied to clipboard");
        toast.show();
    }
}
