package io.github.zkhan93.familyfinance;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.greendao.database.Database;

import java.util.concurrent.Executor;

import io.github.zkhan93.familyfinance.models.DaoMaster;
import io.github.zkhan93.familyfinance.models.DaoSession;

/**
 * Created by zeeshan on 9/7/17.
 */

public class App extends Application {
    public static final boolean ENCRYPTED = false;
    private DaoSession daoSession;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private SharedPreferences spf;
    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        spf = PreferenceManager.getDefaultSharedPreferences(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this,
                ENCRYPTED ? "ff-db-encrypted" : "ff-db");
        Database db = ENCRYPTED ?
                helper.getEncryptedWritableDb("super-secret") :
                helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void setBiometricAuthValidFromTimeToNow() {
        spf.edit().putLong(getString(R.string.pref_success_biometric_auth_at),
                System.currentTimeMillis()).apply();
    }

    public void requestBiometricAuth(FragmentActivity activity) {
        long biometricAuthThreshold = 10 * 1000;
        long lastAuthAt = spf.getLong(getString(R.string.pref_success_biometric_auth_at), 0);
        if (lastAuthAt > System.currentTimeMillis() - biometricAuthThreshold) {
            setBiometricAuthValidFromTimeToNow();
            return;
        }
        //only if its not very recent
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(activity,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication is required to use the app!", Toast.LENGTH_SHORT)
                        .show();
                Log.d(TAG, errString.toString());
                activity.finish();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                setBiometricAuthValidFromTimeToNow();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        BiometricPrompt.PromptInfo.Builder builder = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential");

        if (Build.VERSION.SDK_INT > 29) {
            builder = builder.setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
        }else{
            builder = builder.setNegativeButtonText(getString(R.string.cancel));
        }
        promptInfo = builder.build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.
        biometricPrompt.authenticate(promptInfo);
    }

}
