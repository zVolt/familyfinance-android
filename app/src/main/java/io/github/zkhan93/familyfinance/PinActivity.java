package io.github.zkhan93.familyfinance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PinActivity extends AppCompatActivity {

    public static final String TAG = PinActivity.class.getSimpleName();
    private final int REQUEST_CHECK_PIN = 100;
    private final int REQUEST_CONFIRM_PIN = 101;
    @BindView(R.id.button_0)
    Button button0;
    @BindView(R.id.button_1)
    Button button1;
    @BindView(R.id.button_2)
    Button button2;
    @BindView(R.id.button_3)
    Button button3;
    @BindView(R.id.button_4)
    Button button4;
    @BindView(R.id.button_5)
    Button button5;
    @BindView(R.id.button_6)
    Button button6;
    @BindView(R.id.button_7)
    Button button7;
    @BindView(R.id.button_8)
    Button button8;
    @BindView(R.id.button_9)
    Button button9;
    @BindView(R.id.button_ok)
    ImageButton buttonOk;
    @BindView(R.id.button_cancel)
    ImageButton buttonCancel;
    @BindView(R.id.delete)
    ImageButton buttonDelete;
    @BindView(R.id.pin)
    TextView pin;

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.msg)
    TextView msg;

    private HashFunction hf = Hashing.sha256();
    private String action;
    private SharedPreferences sharedPreferences;

    public interface ACTIONS {
        String SET_PIN = "setpin";
        String CONFIRM_PIN = "confirmpin";
        String CHECK_PIN = "checkpin";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        ButterKnife.bind(this);
        pin.setTransformationMethod(PasswordTransformationMethod.getInstance());
        if (getIntent() == null)
            throw new RuntimeException("PinActivity not called with no intent");
        action = getIntent().getAction();
        Log.d(TAG, "action:" + action);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String actualPin = sharedPreferences.getString("PIN", null);
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
                title.setText(R.string.confirm_pin);
                buttonCancel.setVisibility(View.VISIBLE);
                break;
            case ACTIONS.CHECK_PIN:
                title.setText(R.string.enter_pin);
                buttonCancel.setVisibility(View.INVISIBLE);
                break;
            case ACTIONS.SET_PIN:
                buttonCancel.setVisibility(View.INVISIBLE);
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
    }

    @OnLongClick(R.id.delete)
    protected boolean onDeleteLongPress(View view) {
        if (view.getId() != R.id.delete) {
            Log.d(TAG, "action not implemented");
            return false;
        }
        pin.setText("");
        return true;
    }

    @OnClick({R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id
            .button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9,
            R.id.button_ok, R.id.delete, R.id.button_cancel})
    public void onViewClick(View view) {
        String str = pin.getText().toString();
        switch (view.getId()) {
            case R.id.button_0:
                pin.setText(str + "0");
                break;
            case R.id.button_1:
                pin.setText(str + "1");
                break;
            case R.id.button_2:
                pin.setText(str + "2");
                break;
            case R.id.button_3:
                pin.setText(str + "3");
                break;
            case R.id.button_4:
                pin.setText(str + "4");
                break;
            case R.id.button_5:
                pin.setText(str + "5");
                break;
            case R.id.button_6:
                pin.setText(str + "6");
                break;
            case R.id.button_7:
                pin.setText(str + "7");
                break;
            case R.id.button_8:
                pin.setText(str + "8");
                break;
            case R.id.button_9:
                pin.setText(str + "9");
                break;
            case R.id.button_ok:
                performAction();
                break;
            case R.id.delete:
                if (str.length() > 0)
                    pin.setText(str.substring(0, str.length() - 1));
                break;
            case R.id.button_cancel:
                sharedPreferences.edit().putString("PIN", null).apply();
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
        sharedPreferences.edit().putString("PIN", hc.toString()).apply();
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
        String actualPin = sharedPreferences.getString("PIN", null);
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
        String enteredPin = pin.getText().toString();
        String actualPin = sharedPreferences.getString("PIN", null);
        if (actualPin == null) {
            // No pin set call finish() on this activity returning positive result
            setResult(RESULT_OK);
            finish();
        }
        HashCode hc = hf.newHasher()
                .putString(enteredPin, Charsets.UTF_8)
                .hash();
        Log.d(TAG, "" + hc.toString() + "\n actual" + actualPin);
        if (hc.toString().equals(actualPin)) {
            setResult(RESULT_OK);
            finish();
        } else {
            msg.setTextColor(ContextCompat.getColor(this, R.color.errorColor));
            msg.setText("Wrong PIN try again!");
            pin.setText("");
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
                sharedPreferences.edit().putString("PIN", null).apply();
                setResult(RESULT_CANCELED);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
