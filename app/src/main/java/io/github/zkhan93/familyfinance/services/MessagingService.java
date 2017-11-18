package io.github.zkhan93.familyfinance.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.zkhan93.familyfinance.MainActivity;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.util.Util;

import static io.github.zkhan93.familyfinance.listeners.CopyOtpListener.ACTION_COPY_OTP;

/**
 * Created by zeeshan on 13/7/17.
 */

public class MessagingService extends FirebaseMessagingService {
    public static final String TAG = MessagingService.class.getSimpleName();
    public static final int mNotificationId = 1001;

    public interface KEYS {
        String FROM_EMAIL = "from_email";
        String NUMBER = "number";
        String TYPE = "type";
        String FROM_ID = "from_id";
        String TIMESTAMP = "timestamp";
        String FROM_NAME = "from_name";
        String FROM_PROFILE_PIC = "from_profilePic";
        String CONTENT = "content";
        String FROM_MEMBER_ID = "fromMemberId";
    }

    interface TYPE {
        String PRESENCE = "2";
        String OTP = "1";
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        StringBuilder strb = new StringBuilder();
        Map<String, String> data = remoteMessage.getData();

        for (Map.Entry<String, String> me : data.entrySet()) {
            strb.append(me.getKey()).append(": ").append(me.getValue()).append("\n");
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "user not logged in");
            return;
        }

        if (data.get(KEYS.TYPE).equals(TYPE.OTP)) {
            String otp = showNotification(getApplicationContext(), data);
            copyToClipboard(getApplicationContext(), otp);
        } else if (data.get(KEYS.TYPE).equals(TYPE.PRESENCE))
            updatePresence(user);
    }

    private void updatePresence(FirebaseUser user) {
        String familyId = PreferenceManager.getDefaultSharedPreferences(this).getString
                ("activeFamilyId", null);
        if (familyId == null) {
            Log.d(TAG, "active family not selected SmsReceiver will not be able to push the otp " +
                    "to cloud");
            return;
        }
        FirebaseDatabase.getInstance().getReference("members").child(familyId).child(user.getUid
                ()).child("wasPresentOn").setValue(Calendar.getInstance().getTimeInMillis());
    }

    public static String showNotification(Context context, Map<String, String> data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        boolean showNotification = sharedPreferences.getBoolean(context.getString(R.string
                .pref_key_notification), true);
        Set<String> keywords = sharedPreferences.getStringSet("keywords", new HashSet<String>());
        String content = data.get(KEYS.CONTENT);
        if (content == null) return null;
        boolean hasKeyword = Util.hasKeywords(content, keywords);
        String otp = "";
        if (hasKeyword)
            otp = Util.extractOTPFromString(context, content);
        //check notification for me setting
        boolean onlyOtp = sharedPreferences.getBoolean(context.getString(R.string
                .pref_key_notification_only_otp), false);
        if ((!showNotification) || (onlyOtp && !hasKeyword))
            return otp;
        String ringtone = sharedPreferences.getString(context.getString(R.string
                .pref_key_ringtone), null);
        Log.d(TAG, "ringtone setting: " + ringtone);
        boolean vibrate = sharedPreferences.getBoolean(context.getString(R.string
                .pref_key_vibrate), false);
        Log.d(TAG, "vibration setting: " + vibrate);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("FragmentPosition", MainActivity.PAGE_POSITION.SMS);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        String title = String.format("%s | %s | %s", otp, data
                .get(KEYS.NUMBER), data.get(KEYS.FROM_NAME));

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat
                .BigTextStyle()
                .setBigContentTitle(title)
                .setSummaryText(data.get(KEYS.FROM_NAME))
                .bigText(data.get(KEYS.CONTENT));

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_stat_launcher)
                        .setContentTitle(title)
                        .setContentText(content)
                        .setStyle(bigTextStyle)
                        .setPriority(Notification.PRIORITY_HIGH);
        if (otp != null && !otp.isEmpty()) {
            Intent copyIntent = new Intent(ACTION_COPY_OTP);
            copyIntent.putExtra("OTP", otp);
            PendingIntent copyPendingIntent = PendingIntent.getBroadcast(context, 1, copyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.addAction(new NotificationCompat.Action(R.drawable
                    .ic_content_copy_grey_50_24dp, "Copy OTP", copyPendingIntent));
        }
        mBuilder.setContentIntent(resultPendingIntent);
        //set fake vibration to enable heads Up in API 21+
        if (Build.VERSION.SDK_INT >= 21) mBuilder.setVibrate(new long[0]);

        if (vibrate)
            mBuilder.setVibrate(new long[]{100, 300, 300, 100});
        if (ringtone != null && !ringtone.isEmpty())
            mBuilder.setSound(Uri.parse(ringtone));

        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        if (mNotifyMgr != null)
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        return otp;
    }

    public static void copyToClipboard(Context context, String otp) {
        boolean isCopyEnabled = PreferenceManager.getDefaultSharedPreferences(context).getBoolean
                (context.getString(R.string.pref_key_copy), true);
        if (!isCopyEnabled || otp == null) return;
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
        String toastMessage = null;
        if (!otp.isEmpty())
            toastMessage = Util.copyToClipboard(context, (ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE), otp);
        if (toastMessage == null || toastMessage.isEmpty()) toastMessage = "No OTP found";
        Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
    }
}
