package io.github.zkhan93.familyfinance.services;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by zeeshan on 13/7/17.
 */

public class MessagingService extends FirebaseMessagingService {
    public static final String TAG = MessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "fcm message:" + remoteMessage.toString());
    }
}
