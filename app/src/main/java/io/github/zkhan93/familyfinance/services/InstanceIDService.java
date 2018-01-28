package io.github.zkhan93.familyfinance.services;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by zeeshan on 13/7/17.
 *
 */

public class InstanceIDService extends FirebaseInstanceIdService {
    public static final String TAG = InstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid())
                    .child("token").setValue(refreshedToken);
    }
}
