package io.github.zkhan93.familyfinance.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.WatchRequest;
import com.google.api.services.gmail.model.WatchResponse;

import java.io.IOException;
import java.util.Collections;

/**
 * Created by zeeshan on 10/29/17.
 */

public class EmailSubscribeTask extends AsyncTask<Void, Void, WatchResponse> {
    public static final String TAG = EmailSubscribeTask.class.getSimpleName();
    private SharedPreferences sharedPreferences;

    public EmailSubscribeTask(GoogleAccountCredential credential, Context context) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String accountName = sharedPreferences.getString("account_name", null);
        Log.d(TAG, "account_name: " + accountName);
        credential.setSelectedAccountName(accountName);
        Gmail mService = new Gmail.Builder(transport, jsonFactory, credential)
                .setApplicationName("Family Finance")
                .build();
        credential.setSelectedAccountName(accountName);
        String user = "me";
        WatchRequest watchRequest = new WatchRequest();
        watchRequest.setTopicName("projects/familyfinance-e8098/topics/push-mail");
        watchRequest.setLabelIds(Collections.singletonList("INBOX"));
        Log.d(TAG, "asdasd: " + credential.getSelectedAccountName());
        WatchResponse watchResponse = null;
        try {
            watchResponse = mService.users().watch(user, watchRequest).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (watchResponse != null) {
            Log.d(TAG, "watchResponse: " + watchResponse.getExpiration());
            Log.d(TAG, "watchResponse: " + watchResponse.getHistoryId());
            sharedPreferences.edit().putString("historyId", String.valueOf(watchResponse
                    .getHistoryId
                            ())).putLong
                    ("expiration", watchResponse.getExpiration()).putBoolean("subscribed", true)
                    .apply();
        } else {
            Log.d(TAG, "watchResponse null");
        }
    }

    @Override
    protected WatchResponse doInBackground(Void... voids) {
        return null;
//        try {
//
//        } catch (Exception e) {
//            cancel(true);
//            Log.d(TAG, "exception: " + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
    }

    @Override
    protected void onPostExecute(WatchResponse watchResponse) {
        if (watchResponse != null) {
            Log.d(TAG, "watchResponse: " + watchResponse.getExpiration());
            Log.d(TAG, "watchResponse: " + watchResponse.getHistoryId());
            sharedPreferences.edit().putString("historyId", String.valueOf(watchResponse
                    .getHistoryId
                            ())).putLong
                    ("expiration", watchResponse.getExpiration()).putBoolean("subscribed", true)
                    .apply();
        } else {
            Log.d(TAG, "watchResponse null");
        }
    }
}
