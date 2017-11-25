package io.github.zkhan93.familyfinance.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.zkhan93.familyfinance.R;

/**
 * Created by zeeshan on 15/7/17.
 */

public class Util {
    public static final String TAG = Util.class.getSimpleName();

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context
                .CONNECTIVITY_SERVICE);
        return cm != null && cm.getActiveNetworkInfo() != null;
    }

    public static String extractOTPFromString(@NonNull Context context, @NonNull String messages) {
        if (pattern == null)
            readOtpRegexValuesAndCompilePattern(context);
        String otp = null;
        String tmp;
        Matcher match = pattern.matcher(messages);
        while (match.find()) {
            tmp = match.group(1);
            if (tmp != null && !tmp.endsWith(".") && (otp == null || otp.length() <= tmp.length()))
                otp = tmp;
        }
        return otp;
    }

    private static Pattern pattern;

    public static void readOtpRegexValuesAndCompilePattern(Context context) {
        if (context == null) return;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        String otpChars = sharedPreferences.getString("otpChars", null);
        if (otpChars == null || otpChars.length() == 0) return;
        String otpLengths = sharedPreferences.getString("otpLengths", null);
        if (otpLengths == null || otpLengths.length() == 0) return;
        String[] lengths = otpLengths.split(",");
        Util.compilePattern(otpChars, lengths);
    }

    private static void compilePattern(String acceptedChars, String[] lengths) {
        // \b([0-9]{4}\.?|[0-9]{8}\.?|[0-9]{6}\.?)\b
        StringBuilder strb = new StringBuilder();
        strb.append("\\b(");
        for (String len : lengths) {
            strb.append("[%1$s]{");
            strb.append(len);
            strb.append("}\\.?|");
        }
        strb.deleteCharAt(strb.length() - 1);
        strb.append(")\\b");
        pattern = Pattern.compile(String.format(strb.toString(), acceptedChars));
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

    public static boolean hasKeywords(String content, @NonNull Set<String> keywords) {
        if (content == null || content.isEmpty()) return false;
        content = content.replace(" ", "").toLowerCase();
        for (String keyword : keywords)
            if (content.contains(keyword))
                return true;
        return false;
    }
    public String getBankLogonUrl(String bankId){
        return String.format("https://firebasestorage.googleapis.com/v0/b/familyfinance-e8098.appspot.com/o/bank%2Fic_bank_%s.png?alt=media&token=fda916a0-bd18-4981-a263-578c6083fc74",bankId);
    }
}

