package io.github.zkhan93.familyfinance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        OnCompleteListener<AuthResult> {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1005;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private GoogleSignInAccount account;
    private OnCompleteListener<Void> saveUserDataListener;
    @BindView(R.id.sign_in_button)
    Button btnLogin;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.progress_message)
    TextView progressMsg;

    {
        saveUserDataListener = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                progressMsg.setText(getString(R.string.progress_success));
                if (task.isSuccessful()) {
                    startMainActivity();
                } else {
                    //writing to firebase failed for some reason
                    if (task.getException() != null)
                        Log.d(TAG, "failed write operation" + task.getException()
                                .getLocalizedMessage());
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        btnLogin.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions
                .DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .requestServerAuthCode(getString(R.string.web_client_id), false)
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        progressBar.setVisibility(View.GONE);
        progressMsg.setTextColor(ContextCompat.getColor(this, R.color.md_grey_50));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // already signed in
            String userPic = null;
            if (user.getPhotoUrl() != null) {
                userPic = user.getPhotoUrl().toString();
            }
            Member member = new Member(user.getUid(),
                    user.getDisplayName(),
                    user.getEmail(),
                    Calendar.getInstance().getTimeInMillis(),
                    false,
                    userPic);
            ((App) getApplication()).getDaoSession().getMemberDao().insertOrReplace(member);
            Log.d(TAG, "already logged in");
            startMainActivity();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            progressMsg.setText(getString(R.string.progress_select_account));
            progressBar.setVisibility(View.VISIBLE);
            startSignIn();
        }
    }

    private void startSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void startMainActivity() {
        if (PreferenceManager.getDefaultSharedPreferences
                (getApplicationContext()).contains(getString(R.string.pref_family_id))) {
            //TODO: check if I'm a member of this family if not then delete familyId from
            // preference and show Select Family Activity
            startActivity(new Intent(LoginActivity.this,
                    HomeActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this,
                    SelectFamilyActivity.class));
        }
        finish();
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        Util.Log.d(TAG, "onComplete: task called");
        if (task.isSuccessful()) {
            // Sign in success, update UI with the signed-in user's information
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                progressMsg.setText(getString(R.string.progress_saving_details));
                String userPic = null;
                if (user.getPhotoUrl() != null)
                    userPic = user.getPhotoUrl().toString();
                Member member = new Member(user.getUid(),
                        user.getDisplayName(),
                        user.getEmail(),
                        Calendar.getInstance().getTimeInMillis(),
                        false,
                        userPic);
                ((App) getApplication()).getDaoSession().getMemberDao().insertOrReplace(member);

                Map<String, Object> updates = new HashMap<>();
                String prefix = "users/" + user.getUid() + "/";
                updates.put(prefix + "name", member.getName());
                updates.put(prefix + "id", member.getId());
                updates.put(prefix + "email", member.getEmail());
                updates.put(prefix + "profilePic", member.getProfilePic());
                updates.put(prefix + "smsEnabled", member.getSmsEnabled());
                updates.put(prefix + "token", FirebaseInstanceId.getInstance().getToken());
                updates.put(prefix + "serverAuth", account.getServerAuthCode());
                progressMsg.setText(getString(R.string.progress_updating_details));
                FirebaseDatabase.getInstance()
                        .getReference()
                        .updateChildren(updates)
                        .addOnCompleteListener(saveUserDataListener);
            } else {
                progressBar.setVisibility(View.GONE);
                if (task.getException() == null)
                    progressMsg.setText(getString(R.string.progress_failed_signin));
                else
                    progressMsg.setText(task.getException().getLocalizedMessage());
            }
        } else {
            // cannot get user's data
            progressBar.setVisibility(View.GONE);
            progressMsg.setText(getString(R.string.progress_failed_signin));
            Util.Log.d(TAG, "onComplete: task failed");
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when
        // starting the sign in flow.
        Util.Log.i(TAG, "onActivityResult: requestCode: %b resultCode: %b", requestCode == RC_SIGN_IN,
                resultCode==Activity.RESULT_OK);
        if (requestCode == RC_SIGN_IN) {
//            IdpResponse response = IdpResponse.fromResultIntent(data);
            GoogleSignInResult response = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    account = task.getResult(ApiException.class);
                    if (account!=null) {

                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken()
                                , null);
                        mAuth.signInWithCredential(credential).addOnCompleteListener(this);
                    }else{
                        Util.Log.d(TAG, "sign in attempt failed");
                        progressMsg.setText(getString(R.string.progress_failed_signin));
                    }
                } catch (ApiException e) {
                    Util.Log.d(TAG, "sign in attempt failed");
                    progressMsg.setText(getString(R.string.progress_failed_signin));
                }
            } else {
                // Sign in failed

                progressBar.setVisibility(View.GONE);
                if (response == null) {
                    Log.d(TAG, "null response object");
                    progressMsg.setText(getString(R.string.progress_null_response));
                    return;
                }
                int gStatusCode = response.getStatus().getStatusCode();
                Log.d(TAG, String.format("failed sign-in in response:(%d) %s",gStatusCode, response.toString()));
                switch (gStatusCode) {
                    case CommonStatusCodes.NETWORK_ERROR:
                    case CommonStatusCodes.TIMEOUT:
                        progressMsg.setText(getString(R.string.progress_no_internet));
                        break;
                    default:
                        progressMsg.setText(GoogleSignInStatusCodes
                                .getStatusCodeString(gStatusCode));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

