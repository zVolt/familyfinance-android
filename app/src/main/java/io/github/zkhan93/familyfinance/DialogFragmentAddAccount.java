package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.AccountDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;

/**
 * Created by zeeshan on 12/7/17.
 */

public class DialogFragmentAddAccount extends DialogFragment implements DialogInterface
        .OnClickListener {

    public static final String TAG = DialogFragmentAddAccount.class.getSimpleName();
    public static final String ARGS_FAMILY_ID = "familyID";

    @BindView(R.id.account_holder)
    TextInputEditText accountHolder;
    @BindView(R.id.number)
    TextInputEditText number;
    @BindView(R.id.userid)
    TextInputEditText userid;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.bank)
    TextInputEditText bank;
    @BindView(R.id.ifsc)
    TextInputEditText ifsc;
    @BindView(R.id.balance)
    TextInputEditText balance;

    private TextWatcher accountNumberWatcher;
    private String familyId;

    {
        accountNumberWatcher = new TextWatcher() {
            String[] segs;
            StringBuilder strb = new StringBuilder(19);

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                str = str.trim().replaceAll("[- ]", "");
                strb.setLength(0);
                int i = 1;
                for (char ch : str.toCharArray()) {
                    strb.append(ch);
                    if (i % 4 == 0)
                        strb.append('-');
                    i++;
                }
                if (strb.length() > 19)
                    strb.delete(19, strb.length());
                number.removeTextChangedListener(this);
                number.setText(strb.toString());
                number.setSelection(strb.length());
                number.addTextChangedListener(this);
                Log.d(TAG, strb.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public static DialogFragmentAddAccount newInstance(String familyId) {
        DialogFragmentAddAccount dialogFragmentAddAccount = new DialogFragmentAddAccount();
        Bundle args = new Bundle();
        args.putString(ARGS_FAMILY_ID, familyId);
        dialogFragmentAddAccount.setArguments(args);
        return dialogFragmentAddAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            familyId = bundle.getString(ARGS_FAMILY_ID);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_new_account);
        builder.setPositiveButton(R.string.create, this)
                .setNegativeButton(android.R.string.cancel, this);

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_account,
                null);
        ButterKnife.bind(this, rootView);
        builder.setView(rootView);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                //TODO: validate values

                Account account = new Account(accountHolder.getText().toString(), bank.getText()
                        .toString
                                (), ifsc.getText().toString(), number.getText().toString(), Float
                        .parseFloat(balance.getText().toString()), Calendar.getInstance()
                        .getTimeInMillis(), null);
                account.setUpdatedByMemberId(FirebaseAuth.getInstance
                        ().getCurrentUser().getUid());
                new InsertTask<AccountDao, Account>(((App) getActivity().getApplication())
                        .getDaoSession()
                        .getAccountDao()).execute(account);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                //TODO: teardown the view
                break;
            default:
                Log.d(TAG, "action not implemented/invalid action");
        }
    }
}
