package io.github.zkhan93.familyfinance.util;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public abstract class SilentValueEventListener implements ValueEventListener {
    public static final String TAG = SilentValueEventListener.class.getSimpleName();

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Util.Log.d(TAG, "value fetch failed!!");
    }
}
