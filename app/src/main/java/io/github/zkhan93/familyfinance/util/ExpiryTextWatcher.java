package io.github.zkhan93.familyfinance.util;

import android.text.Editable;
import android.widget.EditText;

public class ExpiryTextWatcher extends TextWatcherProxy {
    private EditText editTextToWatch;

    public ExpiryTextWatcher(EditText editTextToWatch) {
        this.editTextToWatch = editTextToWatch;
    }

    @Override
    public void afterTextChanged(Editable s) {
        String value = s.toString();
        value = value.replace("/", "");
        if (value.length() == 1) {
            int num = Integer.parseInt(value);
            if (num > 1)
                value = "1";
        } else if (value.length() == 2) {
            int num = Integer.parseInt(value);
            if (num == 0)
                value = "1";
            else if (num > 12)
                value = "12";
        }
        if (value.length() > 2) {
            value = value.substring(0, 2) + "/" + value.substring(2);
        }
        this.editTextToWatch.removeTextChangedListener(this);
        this.editTextToWatch.setText(value);
        this.editTextToWatch.setSelection(value.length());
        this.editTextToWatch.addTextChangedListener(this);
    }
}
