package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 11/12/17.
 */

public class DialogFragmentViewAccount extends DialogFragment implements DialogInterface
        .OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final String TAG = DialogFragmentAddAccount.class.getSimpleName();
    public static final String ARG_ACCOUNT = "account";
    public static final String ARG_FAMILY_ID = "familyId";

    private Account account;
    private String familyId;
    private DatabaseReference accountRef;
    private FirebaseUser firebaseUser;
    private Map<String, Object> updateMap;
    private final ValueEventListener bankImageLinkListener;

    ImageView bank;
    TextView accountNumber;
    TextView accountHolder;
    TextView balance;
    SeekBar limit;
    TextView ifsc;
    TextView email;
    TextView phoneNumber;
    TextView userid;
    TextView password;
    ImageView updatedBy;
    TextView updatedOn;

    public DialogFragmentViewAccount() {
        super();
        bankImageLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url == null)
                    url = String.format("https://via.placeholder.com/200x200/f0f0f0/2c2c2c?text=%s",
                            dataSnapshot.getKey());
                Glide.with(bank.getContext())
                        .load(url)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_bank_grey_600_18dp))
                        .into(bank);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "bank image loading cancelled");
            }
        };
    }

    public static DialogFragmentViewAccount newInstance(Account account, String familyId) {
        DialogFragmentViewAccount dialogFragmentAddAccount = new DialogFragmentViewAccount();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ACCOUNT, account);
        args.putString(ARG_FAMILY_ID, familyId);
        dialogFragmentAddAccount.setArguments(args);
        return dialogFragmentAddAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            account = bundle.getParcelable(ARG_ACCOUNT);
            familyId = bundle.getString(ARG_FAMILY_ID);
        }
        accountRef = FirebaseDatabase.getInstance().getReference("accounts").child(familyId).child
                (account.getAccountNumber());
        if (updateMap == null)
            updateMap = new HashMap<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setPositiveButton(R
                .string.action_share, this).setNegativeButton("copy", this)
                .setNeutralButton(android.R.string.ok, this);

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_view_account,
                null);
        bank = rootView.findViewById(R.id.bank);
        accountNumber = rootView.findViewById(R.id.account_number);
        accountHolder = rootView.findViewById(R.id.account_holder);
        balance = rootView.findViewById(R.id.balance);
        limit = rootView.findViewById(R.id.limit);
        ifsc = rootView.findViewById(R.id.ifsc);
        email = rootView.findViewById(R.id.email);
        phoneNumber = rootView.findViewById(R.id.phone_number);
        userid = rootView.findViewById(R.id.userid);
        password = rootView.findViewById(R.id.password);
        updatedBy = rootView.findViewById(R.id.updated_by);
        updatedOn = rootView.findViewById(R.id.updated_on);
        if (account != null) {
            FirebaseDatabase.getInstance().getReference("images").child("banks").child(account
                    .getBank
                            ().toUpperCase()).addListenerForSingleValueEvent(bankImageLinkListener);

            accountHolder.setText(getDefaultNotSet(account.getAccountHolder()));
            accountNumber.setText(account.getAccountNumber());
            balance.setText(NumberFormat.getCurrencyInstance().format(account
                    .getBalance()));

            limit.setMax((int) Math.max(100000, account.getBalance() * 1.5f));
            limit.setProgress((int) account.getBalance());

            ifsc.setText(getDefaultNotSet(account.getIfsc()));
            email.setText(getDefaultNotSet(account.getEmail()));
            userid.setText(getDefaultNotSet(account.getUserid()));
            password.setText(getDefaultNotSet(account.getPassword()));
            phoneNumber.setText(getDefaultNotSet(account.getPhoneNumber()));
            Glide.with(getContext()).load(account.getUpdatedBy().getProfilePic()).apply
                    (RequestOptions
                            .circleCropTransform()).into(updatedBy);
            updatedOn.setText(DateUtils.getRelativeTimeSpanString(getContext(), account
                    .getUpdatedOn(), true));
        }
        limit.setOnSeekBarChangeListener(this);
        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_ACCOUNT, account);
        outState.putString(ARG_FAMILY_ID, familyId);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_NEGATIVE:
                //copy
                Util.copyToClipboard(getActivity().getApplicationContext(), (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE), account
                        .getReadableContent());
                Toast.makeText(getActivity().getApplicationContext(), "Account details copied to " +
                        "clipboard!", Toast.LENGTH_SHORT).show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                //share
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, account.getReadableContent());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string
                        .action_share)));
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int newValue, boolean b) {
        newValue = (newValue/100)*100;
        balance.setText(NumberFormat.getCurrencyInstance().format(newValue));
        updateMap.put("balance", newValue);
        updateMap.put("updatedByMemberId", firebaseUser.getUid());
        updateMap.put("updatedOn", Calendar.getInstance().getTimeInMillis());
        accountRef.updateChildren(updateMap);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private String getDefaultNotSet(String value) {
        if (value == null || value.isEmpty()) {
            return "Not Set";
        }
        return value;
    }
}
