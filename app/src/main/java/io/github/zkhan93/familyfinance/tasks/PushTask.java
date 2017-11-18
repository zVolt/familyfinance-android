package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.ExecutionException;

import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.Group;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Otp;

/**
 * Created by zeeshan on 14/7/17.
 */

public class PushTask<T> extends AsyncTask<T, Void, Void> {

    public static final String TAG = PushTask.class.getSimpleName();

    private DatabaseReference ref;

    public PushTask(String familyId) {
        ref = FirebaseDatabase.getInstance().getReference("/families").child(familyId);
    }

    @Override
    protected Void doInBackground(T[] params) {
        T data = params[0];
        if (data instanceof Account) {
            DatabaseReference accountRef = ref.child("accounts");
            Account account = (Account) data;
            try {
                Tasks.await(accountRef.child(account.getAccountNumber()).setValue(account));
            } catch (ExecutionException | InterruptedException ex) {
                Log.d(TAG, "error pushing account: " + ex.getLocalizedMessage());
            }
        }
        if (data instanceof Group) {
            ref = ref.child("groups");
        }
        if (data instanceof Member) {
            ref = ref.child("members");
        }
        if (data instanceof Otp) {
            DatabaseReference otpRef = ref.child("opts");
            Otp otp = (Otp) data;
            try {
                Tasks.await(otpRef.child(otp.getId()).setValue(otp));
            } catch (ExecutionException | InterruptedException ex) {
                Log.d(TAG, "error pushing Otp: " + ex.getLocalizedMessage());
            }
        }
        if (data instanceof CCard) {
            DatabaseReference cCardRef = ref.child("ccards");
            CCard ccard = (CCard) data;
            try {
                Tasks.await(cCardRef.child(ccard.getNumber()).setValue(ccard));
            } catch (ExecutionException | InterruptedException ex) {
                Log.d(TAG, "error pushing Ccard: " + ex.getLocalizedMessage());
            }
        }

        return null;
    }
}
