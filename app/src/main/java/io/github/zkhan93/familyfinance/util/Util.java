package io.github.zkhan93.familyfinance.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.net.InetAddress;

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
}
