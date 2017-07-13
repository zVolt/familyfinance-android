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

    @BindView(R.id.signin)
    Button SignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
            Log.d(TAG, "already logged in");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else
            Log.d(TAG, "not already logged in");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick(R.id.signin)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signin:
                startActivityForResult(
                        // Get an instance of AuthUI based on the default app
                        AuthUI.getInstance().createSignInIntentBuilder().build(), RC_SIGN_IN);
                break;
        }
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
                            .getEmail(), false, FirebaseInstanceId.getInstance().getToken(), user
                            .getPhotoUrl().toString());
                    FirebaseDatabase.getInstance().getReference().child("users").child(user
                            .getUid()).setValue(member)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        if (PreferenceManager.getDefaultSharedPreferences
                                                (getApplicationContext()).contains("familyId")) {
                                            startActivity(new Intent(LoginActivity.this,
                                                    SelectFamilyActivity
                                                    .class));
                                        } else {
                                            startActivity(new Intent(LoginActivity.this,
                                                    MainActivity
                                                    .class));
                                        }
                                        finish();

                                    } else {
                                        //wtinting to firebase failed for some reason
                                    }
                                }
                            });
                } else {
                    // cannot get user's data
                }
                return;
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
//                    showSnackbar(R.string.sign_in_cancelled);
                    Log.d(TAG, "User pressed back button");
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

