package io.github.zkhan93.familyfinance.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

import io.github.zkhan93.familyfinance.MainActivity;
import io.github.zkhan93.familyfinance.R;

/**
 * Created by zeeshan on 13/7/17.
 */

public class MessagingService extends FirebaseMessagingService {
    public static final String TAG = MessagingService.class.getSimpleName();
    public static final int mNotificationId = 001;

    interface KEYS {
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
        Log.d(TAG, "fcm message:" + strb.toString());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "user not logged in");
            return;
        }

        if (data.get(KEYS.TYPE).equals(TYPE.OTP))
            showNotification(data);
        else if (data.get(KEYS.TYPE).equals(TYPE.PRESENCE))
            updatePresence(user);
    }

    private void updatePresence(FirebaseUser user) {
        String familyId = PreferenceManager.getDefaultSharedPreferences(this).getString
                ("activeFamilyId", null);
        if (familyId == null) {
            Log.d(TAG, "active family not selected SmsListener will not be able to push the otp " +
                    "to " +
                    "cloud");
            return;
        }
        FirebaseDatabase.getInstance().getReference("members").child(familyId).child(user.getUid
                ()).child("wasPresentOn").setValue(Calendar.getInstance().getTimeInMillis());
    }

    private void showNotification(Map<String, String> data) {
        Intent resultIntent = new Intent(this, MainActivity.class);
        // Because clicking the notification opens a new activity, there's
        // no need to create an artificial back stack.
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String title = String.format("%s | %s", data.get(KEYS.FROM_NAME), data
                .get(KEYS.NUMBER));

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat
                .BigTextStyle()
                .setBigContentTitle(data.get(KEYS.NUMBER))
                .setSummaryText(data.get(KEYS.FROM_NAME))
                .bigText(data.get(KEYS.CONTENT));

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(data.get(KEYS.CONTENT))
                        .setStyle(bigTextStyle);

        mBuilder.setContentIntent(resultPendingIntent);
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
