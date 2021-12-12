package io.github.zkhan93.familyfinance.util;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.listeners.ClearNotificationRecevier;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;

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
        String message;
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

    public static void quickCopy(@NonNull Context context, @NonNull Account account) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        StringBuilder strb = new StringBuilder();
        if (clipboardManager != null) {
            ClipData clip = ClipData.newPlainText("account number", account.getAccountNumber());
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(context, "Account number copied to clipboard", Toast.LENGTH_SHORT)
                    .show();
        } else {
            strb.append(account.getAccountNumber());
            strb.append('\n');
        }
        strb.append("Name: ");
        strb.append(account.getAccountHolder());
        strb.append('\n');
        strb.append("IFSC: ");
        strb.append(account.getIfsc());
        showSimpleNotification(context, "Account details", strb.toString());
    }

    public static void quickCopy(@NonNull Context context, @NonNull AddonCard addonCard) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        StringBuilder strb = new StringBuilder();
        if (clipboardManager != null) {
            ClipData clip = ClipData.newPlainText("account number", addonCard.getNumber());
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(context, "Card number copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            strb.append(addonCard.getNumber());
            strb.append('\n');
        }
        strb.append("Expiry: ");
        strb.append(CCard.EXPIRE_ON.format(new Date(addonCard.getExpiresOn())));
        strb.append('\n');
        strb.append("CVV: ");
        strb.append(addonCard.getCvv());
        showSimpleNotification(context, "Card details", strb.toString());
    }

    public static void quickCopy(@NonNull Context context, @NonNull CCard cCard) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context
                .CLIPBOARD_SERVICE);
        StringBuilder strb = new StringBuilder();
        if (clipboardManager != null) {
            ClipData clip = ClipData.newPlainText("account number", cCard.getNumber());
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(context, "Card number copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            strb.append(cCard.getNumber());
            strb.append('\n');
        }
        strb.append("Expiry: ");
        strb.append(CCard.EXPIRE_ON.format(new Date(cCard.getExpireOn())));
        strb.append('\n');
        strb.append("CVV: ");
        strb.append(cCard.getCvv());
        showSimpleNotification(context, "Card details", strb.toString());
    }

    private static void showSimpleNotification(@NonNull Context context, String title, String
            content) {
        String summary = buildSummary(content);
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat
                .BigTextStyle()
                .setBigContentTitle(title)
                .setSummaryText(summary)
                .bigText(content);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, TAG)
                        .setSmallIcon(R.drawable.ic_stat_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setVibrate(new long[]{500})
                        .setStyle(bigTextStyle)
                        .setPriority(Notification.PRIORITY_HIGH);
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        if (mNotifyMgr != null)
            mNotifyMgr.notify(COPY_NOTIFICATION_ID, mBuilder.build());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent intent = new Intent(context, ClearNotificationRecevier.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    ClearNotificationRecevier.REQUEST_CLEAR_NOTIFICATION,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 30000, pendingIntent);
        }
    }

    public static int COPY_NOTIFICATION_ID = 1005;

    private static String buildSummary(String content) {
        if (content == null) return null;
        StringBuilder strb = new StringBuilder();
        String[] segs = content.split("[:\\n]");
        if (segs.length > 1)
            for (int i = 1; i < segs.length; i += 2) {
                if (!segs[i].trim().isEmpty()) {
                    strb.append(segs[i]);
                    strb.append(", ");
                }
            }
        else
            strb.append(content);
        if (strb.length() >= 2 && strb.charAt(strb.length() - 1) == ' ')
            strb.delete(strb.length() - 2, strb.length() - 1);
        return strb.toString();
    }

    public static String getCardBrand(String number) {
        //TODO: move this json to firebase
        String jsonString = "{\"american_express\":{\"34\":[\"15\"],\"37\":[\"15\"]},\"diners_club\":{\"36\":[\"14-19\"],\"300-305\":[\"16-19\"],\"3095\":[\"16-19\"],\"38-39\":[\"16-19\"]},\"rupay\":{\"60\":[\"16\"],\"6521\":[\"16\"],\"6522\":[\"16\"],\"6528\":[\"16\"]},\"jcb\":{\"3528-3589\":[\"16-19\"]},\"discover\":{\"6011\":[\"16-19\"],\"622126-622925\":[\"16-19\"],\"624000-626999\":[\"16-19\"],\"628200-628899\":[\"16-19\"],\"64\":[\"16-19\"],\"65\":[\"16-19\"]},\"dankort\":{\"5019\":[\"16\"]},\"maestro\":{\"6759\":[\"12-19\"],\"676770\":[\"12-19\"],\"676774\":[\"12-19\"],\"50\":[\"12-19\"],\"56-69\":[\"12-19\"]},\"mastercard\":{\"2221-2720\":[\"16\"],\"51-55\":[\"16\"]},\"unionpay\":{\"81\":[\"16\"]},\"visa\":{\"4\":[\"13-19\"]}}";
        JSONObject brandMap = null;
        try {
            brandMap = new JSONObject(jsonString);
        } catch (JSONException ex) {
            Log.d(TAG, "could not parse string to JSON");
            return null;
        } finally {
            if (brandMap == null) {
                Log.d(TAG, "brandMap is None");
                return null;
            }
        }
        number = number.replaceAll("[- .]", "");
        char[] numArray = number.toCharArray();
        if (Character.isDigit(numArray[0])) {
            long firstSix = Long.parseLong(number.substring(0, 6));
            int cardNumberLength = number.length();
            Iterator<String> brandNames = brandMap.keys();
            while (brandNames.hasNext()) {
                String brandName = brandNames.next();
                JSONObject branRegex = brandMap.optJSONObject(brandName);
                Iterator<String> startingDigits = branRegex.keys();
                while (startingDigits.hasNext()) {
                    String startingDigit = startingDigits.next();
                    long rangeStart, rangeEnd;
                    if (startingDigit.contains("-")) {
                        String[] segs = startingDigit.split("-");
                        segs[0] = String.format("%1$-6s", segs[0]).replace(' ', '0');
                        segs[1] = String.format("%1$-6s", segs[1]).replace(' ', '9');
                        rangeStart = Long.parseLong(segs[0]);
                        rangeEnd = Long.parseLong(segs[1]);
                    } else {
                        rangeStart = Long.parseLong(String.format("%1$-6s", startingDigit).replace(' ', '0'));
                        rangeEnd = Long.parseLong(String.format("%1$-6s", startingDigit).replace(' ', '9'));
                    }
                    boolean validPrefix = firstSix >= rangeStart && firstSix <= rangeEnd;
                    if (validPrefix) {

                        JSONArray lengths = branRegex.optJSONArray(startingDigit);
                        for (int i = 0; i < lengths.length(); i++) {
                            String length = lengths.optString(i);
                            int lenRangeStart;
                            int lenRangeEnd;
                            if (length.contains("-")) {
                                String[] segs = length.split("-");
                                lenRangeStart = Integer.parseInt(segs[0]);
                                lenRangeEnd = Integer.parseInt(segs[1]);
                            } else {
                                lenRangeStart = lenRangeEnd = Integer.parseInt(length);
                            }
                            boolean validLength = cardNumberLength >= lenRangeStart && cardNumberLength <= lenRangeEnd;
                            if (validLength) {
                                return brandName;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }
}

