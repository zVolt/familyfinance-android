package io.github.zkhan93.familyfinance.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;

import io.github.zkhan93.familyfinance.R;

/**
 * Created by zeeshan on 15/7/17.
 */

public class Util {
    public static final String TAG = Util.class.getSimpleName();

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static String extractOTPFromString(String message) {
        if (message == null || message.isEmpty())
            return null;
        String otp = null;
        otp = null;
        String[] segments = message.split("\\s+");
        for (String segment : segments) {
            if (segment.startsWith("."))
                segment = segment.substring(1);
            if (segment.endsWith("."))
                segment = segment.substring(0, segment.length() - 1);
            if ((segment.length() == 8 || segment.length() == 4 || segment.length() == 6) &&
                    segment.matches("\\d+")) {
                Log.d(TAG, "OTP is:" + segment);
                if (otp == null || otp.length() < segment.length())
                    otp = segment;
            }
        }
        return otp;
    }

    public static String copyToClipboard(Context context, ClipboardManager clipboard, String otp) {
        String message = null;
        if (otp == null)
            message = context.getString(R.string.no_otp_found);
        else {
            message = context.getString(R.string.copy_message, otp);
            if (clipboard == null) {
                Log.d(TAG, "cannot get clipboard");
                message = context.getString(R.string.error_clipboard, otp);
            } else {
                ClipData clip = ClipData.newPlainText("OTP", otp);
                clipboard.setPrimaryClip(clip);
            }
        }
        return message;
    }
}
