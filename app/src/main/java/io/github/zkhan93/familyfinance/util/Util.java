package io.github.zkhan93.familyfinance.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;
import java.util.Calendar;

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

    public static class Log {
        public static void d(String TAG, String msg, Object... args) {
            android.util.Log.d(TAG, String.format(msg, args));
        }

        public static void i(String TAG, String msg, Object... args) {
            android.util.Log.i(TAG, String.format(msg, args));
        }

        public static void e(String TAG, String msg, Object... args) {
            android.util.Log.e(TAG, String.format(msg, args));
        }
    }

    /**
     * calculate next payment day ie., suppose 15th is the payment day so if current day is less
     * than of equal to 15 then next payment date is 15th of current month and if current day
     * is greater than 15th then next payment date is 15th of next month.
     **/
    public static String getBillingCycleString(int billingDay, int paymentDay, @NonNull String
            format) {

        Calendar today = Calendar.getInstance();

        Calendar paymentDate = Calendar.getInstance();
        paymentDate.set(Calendar.DAY_OF_MONTH, paymentDay);

        Calendar billingDate = Calendar.getInstance();
        billingDate.set(Calendar.DAY_OF_MONTH, billingDay);

        if (today.get(Calendar.DAY_OF_MONTH) > paymentDay)
            paymentDate.add(Calendar.MONTH, 1);

        if (billingDay < paymentDate.get(Calendar.DAY_OF_MONTH))
            billingDate.set(Calendar.MONTH, paymentDate.get(Calendar.MONTH));
        else {
            billingDate.set(Calendar.MONTH, paymentDate.get(Calendar.MONTH));
            billingDate.add(Calendar.MONTH, -1);
        }

        return String.format(format, Constants.PAYMENT_DATE.format(billingDate
                .getTime()), Constants.PAYMENT_DATE.format(paymentDate.getTime()));
    }
}

