package io.github.zkhan93.familyfinance;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PinActivity extends AppCompatActivity implements TextView.OnEditorActionListener {

    public static final String TAG = PinActivity.class.getSimpleName();
    private final int REQUEST_CHECK_PIN = 100;
    private final int REQUEST_CONFIRM_PIN = 101;

    Button cancel;
    EditText pin;
    TextView msg;
    TextView title;

    private HashFunction hf = Hashing.sha256();
    private String action;
    private SharedPreferences sharedPreferences;

    public interface ACTIONS {
        String SET_PIN = "setpin";
        String CONFIRM_PIN = "confirmpin";
        String CHECK_PIN = "checkpin";
    }

    @Override
    @TargetApi(21)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        Button cancel = findViewById(R.id.cancel);
        EditText pin = findViewById(R.id.pin);
        TextView msg = findViewById(R.id.msg);
        TextView title = findViewById(R.id.title);

        pin.requestFocus();
        pin.setOnEditorActionListener(this);
        if (getIntent() == null)
            throw new RuntimeException("PinActivity not called with no intent");
        action = getIntent().getAction();
        Log.d(TAG, "action:" + action);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String actualPin = sharedPreferences.getString(getString(R.string.pref_key_pin_value),
                null);
        if (action == null ||
                !(action.equals(ACTIONS.SET_PIN) ||
                        action.equals(ACTIONS.CONFIRM_PIN) ||
                        action.equals(ACTIONS.CHECK_PIN)
                )) {
            Log.d(TAG, "not called with proper action");
            throw new RuntimeException("PinActivity not called with proper action");
        }
        switch (action) {
            case ACTIONS.CONFIRM_PIN:
                if (actualPin == null) {
                    setResult(RESULT_OK);
                    finish();
                }
                title.setText(R.string.confirm_pin);
                break;
            case ACTIONS.CHECK_PIN:
                if (actualPin == null) {
                    setResult(RESULT_OK);
                    finish();
                }
                title.setText(R.string.enter_pin);
                break;
            case ACTIONS.SET_PIN:
                if (actualPin == null) {
                    action = ACTIONS.SET_PIN;
                    title.setText(R.string.set_pin);
                } else {
                    //launch this activity(on top of itself) to check the pin and if result is
                    // positive then set pin
                    Intent intent = new Intent(ACTIONS.CHECK_PIN, null, this, PinActivity.class);
                    startActivityForResult(intent, REQUEST_CHECK_PIN);
                }
                break;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R
                    .transition.slide));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        performAction();
        return true;
    }

    @OnClick({R.id.cancel})
    public void onViewClick(View view) {
        String str = pin.getText().toString();
        switch (view.getId()) {
            case R.id.delete:
                if (str.length() > 0)
                    pin.setText(str.substring(0, str.length() - 1));
                break;
            case R.id.cancel:
                sharedPreferences.edit().putString(getString(R.string.pref_key_pin_value), null)
                        .apply();
                break;
        }
    }

    private void performAction() {
        switch (action) {
            case ACTIONS.CHECK_PIN:
                checkPin();
                break;
            case ACTIONS.CONFIRM_PIN:
                confirmPin();
                break;
            case ACTIONS.SET_PIN:
                setPin();
                break;
        }
    }

    /**
     * save the entered pin and start the same activity with CONFIRM_PIN Action
     */
    private void setPin() {
        String newPin = pin.getText().toString();
        HashCode hc = hf.newHasher()
                .putString(newPin, Charsets.UTF_8)
                .hash();
        if (sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putString(getString(R.string.pref_key_pin_value), hc.toString())
                .apply();
        Intent intent = new Intent(ACTIONS.CONFIRM_PIN, null, this, PinActivity.class);
        startActivityForResult(intent, REQUEST_CONFIRM_PIN);
    }

    /**
     * confirm the pin entered is same as saved; if not clear the saved pin and launch this
     * activity with SET_PIN action
     */
    private void confirmPin() {
        String enteredPin = pin.getText().toString();
        HashCode hc = hf.newHasher()
                .putString(enteredPin, Charsets.UTF_8)
                .hash();
        if (sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String actualPin = sharedPreferences.getString(getString(R.string.pref_key_pin_value),
                null);
        if (hc.toString().equals(actualPin)) {
            // all set
            setResult(RESULT_OK);
            finish();
        } else {
            //
            msg.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
            msg.setText("Wrong PIN try again!");
        }
    }

    /**
     * check the user entered pin against the saved one in preferences
     */
    private void checkPin() {
        msg.setVisibility(View.GONE);
        String enteredPin = pin.getText().toString();
        String actualPin = sharedPreferences.getString(getString(R.string.pref_key_pin_value),
                null);
        if (actualPin == null) {
            // No pin set call finish() on this activity returning positive result
            setResult(RESULT_OK);
            finish();
        }
        HashCode hc = hf.newHasher()
                .putString(enteredPin, Charsets.UTF_8)
                .hash();
        if (hc.toString().equals(actualPin)) {
            setResult(RESULT_OK);
            finish();
        } else {
            msg.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
            msg.setText("Wrong PIN try again!");
            pin.setText("");
            msg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_PIN) {
            if (resultCode == RESULT_OK) {
                action = ACTIONS.SET_PIN;
                title.setText(R.string.set_pin);
            } else finish();
        }
        if (requestCode == REQUEST_CONFIRM_PIN) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish(); //pin set successful nothing else to do
            } else {
                //clear pin from preference
                sharedPreferences.edit().putString(getString(R.string.pref_key_pin_value), null)
                        .apply();
                setResult(RESULT_CANCELED);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
