package io.github.zkhan93.familyfinance.listeners;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.github.zkhan93.familyfinance.util.Util;


/**
 * Created by zeeshan on 12/10/17.
 */

public class ClearNotificationRecevier extends BroadcastReceiver {
    public static final String TAG = ClearNotificationRecevier.class.getSimpleName();
    public static int REQUEST_CLEAR_NOTIFICATION = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.cancel(Util.COPY_NOTIFICATION_ID);
    }
}
