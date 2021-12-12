package io.github.zkhan93.familyfinance.util;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CardNumberWatcher {
    public static final String TAG = CardNumberWatcher.class.getSimpleName();
    private final ValueEventListener cardNumberExistsValueListener;
    private final TextWatcherProxy cardNumberTextWatcher;

    public interface Listener {
        void callback(boolean exists);

        void empty();
    }

    public CardNumberWatcher(@NonNull String familyId, @NonNull TextInputEditText editText, @NonNull Listener listener) {
        cardNumberExistsValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Util.Log.d(TAG, "callback recieved" + dataSnapshot.toString());
                listener.callback(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Util.Log.d(TAG, "error checking card number");
            }
        };
        cardNumberTextWatcher = new TextWatcherProxy() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence == null || charSequence.toString().isEmpty()) {
                    listener.empty();
                } else {
                    String number = charSequence.toString();
                    FirebaseDatabase.getInstance()
                            .getReference("ccards")
                            .child(familyId)
                            .child(number).addListenerForSingleValueEvent(cardNumberExistsValueListener);
                }
            }
        };
        editText.addTextChangedListener(cardNumberTextWatcher);
    }
}
