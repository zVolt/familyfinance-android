package io.github.zkhan93.familyfinance;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PinActivity extends AppCompatActivity {

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
    @BindView(R.id.delete)
    ImageButton buttonDelete;
    @BindView(R.id.pin)
    TextView pin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);
        ButterKnife.bind(this);
        pin.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @OnClick({R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id
            .button_5, R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9,
            R.id.button_ok, R.id.delete})
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
                checkPin();
                break;
            case R.id.delete:
                if (str.length() > 0)
                    pin.setText(str.substring(0, str.length() - 1));
                break;

        }
    }

    private Toast toast;

    private void checkPin() {
        String str = pin.getText().toString();
        if (str.equals("1234")) {
            if (toast == null)
                toast = Toast.makeText(getApplicationContext(), "pin correct", Toast.LENGTH_SHORT);
            toast.show();
        } else
            pin.setText("");
    }
}
