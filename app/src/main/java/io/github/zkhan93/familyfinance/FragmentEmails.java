package io.github.zkhan93.familyfinance;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.WatchRequest;
import com.google.api.services.gmail.model.WatchResponse;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.EmailListAdapter;
import io.github.zkhan93.familyfinance.callbacks.SubscribeEmailCallback;
import io.github.zkhan93.familyfinance.util.Util;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

/**
 * Created by zeeshan on 10/29/17.
 */

public class FragmentEmails extends Fragment implements SubscribeEmailCallback,
        EasyPermissions.PermissionCallbacks {
    public static final String TAG = FragmentEmails.class.getSimpleName();
    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static final String PREF_ACCOUNT_NAME = "accountName";
    private static final String[] SCOPES = {GmailScopes.GMAIL_READONLY};

    private static final String ARG_FAMILY_ID = "familyId";


    private String familyId;
    private EmailListAdapter emailListAdapter;
    GoogleAccountCredential mCredential;

    @BindView(R.id.list)
    RecyclerView emailList;

    public FragmentEmails() {

    }

    public static FragmentEmails newInstance(String familyId) {
        FragmentEmails fragment = new FragmentEmails();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            familyId = bundle.getString(ARG_FAMILY_ID, null);
        }

        mCredential = GoogleAccountCredential.usingOAuth2(
                getActivity().getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_emails, container, false);
        ButterKnife.bind(this, rootView);
        emailListAdapter = new EmailListAdapter(getActivity().getApplication(), familyId,
                this);
        emailList.setLayoutManager(new LinearLayoutManager(getActivity()
                .getApplicationContext
                        ()));
        emailList.setAdapter(emailListAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        emailListAdapter.registerPreferenceChange();
    }

    @Override
    public void onStop() {
        super.onStop();
        emailListAdapter.unregisterPreferenceChange();
    }

    @Override
    public void onSubscribeEmail() {
        Log.d(TAG, "subscribe Email clicked");
        getResultsFromApi();

    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            Log.d(TAG, "No network connection available.");
        } else {
            android.accounts.Account account = mCredential.getSelectedAccount();
            if (account != null)
                new GetTokenTask(getActivity().getApplicationContext(), account).execute();
            else
                Log.d(TAG, "no selected account");
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity());
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                getActivity(),
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                getActivity(), android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null)
            networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private static class EmailSubscribeTask extends AsyncTask<Void, Void, WatchResponse> {
        private final String TAG = io.github.zkhan93.familyfinance.tasks.EmailSubscribeTask.class
                .getSimpleName();
        private SharedPreferences sharedPreferences;
        private Gmail mService = null;

        EmailSubscribeTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.gmail.Gmail.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Family Finance")
                    .build();
            Log.d(TAG, "selectedAccountName: " + credential.getSelectedAccountName());
        }

        @Override
        protected WatchResponse doInBackground(Void... voids) {

            try {
                String user = "me";
                WatchRequest watchRequest = new WatchRequest();
                watchRequest.setTopicName("projects/familyfinance-e8098/topics/push-mail");
                watchRequest.setLabelIds(Collections.singletonList("INBOX"));
                return mService.users().watch(user, watchRequest).execute();
            } catch (Exception e) {
                cancel(true);
                Log.d(TAG, "exception: " + e.getMessage());
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(WatchResponse watchResponse) {
            if (watchResponse != null) {
                Log.d(TAG, "watchResponse: " + watchResponse.getExpiration());
                Log.d(TAG, "watchResponse: " + watchResponse.getHistoryId());
//                sharedPreferences.edit().putString("historyId", String.valueOf(watchResponse
//                        .getHistoryId
//                                ())).putLong
//                        ("expiration", watchResponse.getExpiration()).putBoolean("subscribed",
// true)
//                        .apply();
            } else {
                Log.d(TAG, "watchResponse is null");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    /**
     * Callback for when a permission is granted using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    /**
     * Callback for when a permission is denied using the EasyPermissions
     * library.
     *
     * @param requestCode The request code associated with the requested
     *                    permission
     * @param list        The requested permission list. Never null.
     */
    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Log.d(TAG,
                            "This app requires Google Play Services. Please install " +
                                    "Google Play Services on your device and relaunch this app.");
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

    static class GetTokenTask extends AsyncTask<Void, Void, Void> {
        WeakReference<Context> contextWeakReference;
        private android.accounts.Account account;

        GetTokenTask(Context context, android.accounts.Account account) {
            contextWeakReference = new WeakReference<>(context);
            this.account = account;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String token = GoogleAuthUtil.getToken(contextWeakReference.get(), account,
                        SCOPES[0]);
                Util.Log.d(TAG, "token: %s", token);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }
}
