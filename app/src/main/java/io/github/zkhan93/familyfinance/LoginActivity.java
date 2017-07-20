package io.github.zkhan93.familyfinance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.models.Member;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseUser user = auth.getCurrentUser();
            // already signed in
            Member member = new Member(user.getUid(), user.getDisplayName(), user
                    .getEmail(), false, user
                    .getPhotoUrl().toString());
            ((App) getApplication()).getDaoSession().getMemberDao().insertOrReplace(member);
            Log.d(TAG, "already logged in");
            startMainActivity();
            finish();
        } else
            startActivityForResult(
                    // Get an instance of AuthUI based on the default app
                    AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders
                            (Arrays.asList(
                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                            ))
                            .setLogo(R.mipmap.ic_launcher)
                            .setTosUrl("http://google.com")
                            .setTheme(R.style.AppTheme_NoActionBar)
                            .build(), RC_SIGN_IN);
    }

    private void startMainActivity() {
        if (PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext()).contains("familyId")) {
            //TODO: check if I'm a member of this family if not then delete familyId from
            // preference and show Select Family Activity
            startActivity(new Intent(LoginActivity.this,
                    MainActivity
                            .class));
        } else {
            startActivity(new Intent(LoginActivity.this,
                    SelectFamilyActivity
                            .class));
        }
        finish();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when
        // starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == ResultCodes.OK) {
                //TODO: show progress bar during this process
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Member member = new Member(user.getUid(), user.getDisplayName(), user
                            .getEmail(), false, user
                            .getPhotoUrl().toString());
                    ((App) getApplication()).getDaoSession().getMemberDao().insertOrReplace(member);
                    Map<String, Object> updates = new HashMap<>();
                    String prefix = "users/" + user.getUid() + "/";
                    updates.put(prefix + "name", member.getName());
                    updates.put(prefix + "id", member.getId());
                    updates.put(prefix + "email", member.getEmail());
                    updates.put(prefix + "profilePic", member.getProfilePic());
                    updates.put(prefix + "smsEnabled", member.getSmsEnabled());
                    updates.put(prefix + "token", FirebaseInstanceId
                            .getInstance().getToken());
                    FirebaseDatabase.getInstance().getReference().updateChildren(updates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        startMainActivity();
                                    } else {
                                        //writing to firebase failed for some reason
                                        Log.d(TAG, "failed write operation" + task.getException()
                                                .getLocalizedMessage());
                                    }
                                }
                            });
                } else {
                    // cannot get user's data
                    Log.d(TAG, "no data");
                }
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
//                    showSnackbar(R.string.sign_in_cancelled);
                    Log.d(TAG, "User pressed back button");
                    finish();
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
//                    showSnackbar(R.string.no_internet_connection);
                    Log.d(TAG, "No internet");
                    return;
                }

                if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.d(TAG, "Unknown Error");
                    return;
                }
            }

            Log.d(TAG, "Unknown Signin Response");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

