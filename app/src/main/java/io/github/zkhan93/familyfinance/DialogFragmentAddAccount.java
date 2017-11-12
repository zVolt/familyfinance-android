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
import android.widget.AdapterView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.BankSpinnerAdapter;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.AccountDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;

/**
 * Created by zeeshan on 12/7/17.
 */

public class DialogFragmentAddAccount extends DialogFragment implements DialogInterface
        .OnClickListener, InsertTask.Listener<Account>, AdapterView.OnItemSelectedListener {

    public static final String TAG = DialogFragmentAddAccount.class.getSimpleName();
    public static final String ARG_FAMILY_ID = "familyID";
    public static final String ARG_ACCOUNT = "account";

    @BindView(R.id.account_holder)
    TextInputEditText accountHolder;
    @BindView(R.id.number)
    TextInputEditText number;
    @BindView(R.id.userid)
    TextInputEditText userid;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.bank)
    Spinner bank;
    @BindView(R.id.ifsc)
    TextInputEditText ifsc;

    private String familyId;
    private Account account;
    private BankSpinnerAdapter bankSpinnerAdapter;
    private String selectedBankId;

    public static DialogFragmentAddAccount newInstance(String familyId) {
        DialogFragmentAddAccount dialogFragmentAddAccount = new DialogFragmentAddAccount();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        dialogFragmentAddAccount.setArguments(args);
        return dialogFragmentAddAccount;
    }

    public static DialogFragmentAddAccount newInstance(String familyId, Account account) {
        DialogFragmentAddAccount dialogFragmentAddAccount = new DialogFragmentAddAccount();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        args.putParcelable(ARG_ACCOUNT, account);
        dialogFragmentAddAccount.setArguments(args);
        return dialogFragmentAddAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            familyId = bundle.getString(ARG_FAMILY_ID);
            account = bundle.getParcelable(ARG_ACCOUNT);
        }
        if (bankSpinnerAdapter == null)
            bankSpinnerAdapter = new BankSpinnerAdapter(getActivity().getApplicationContext());
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
        bank.setAdapter(bankSpinnerAdapter);
        bank.setOnItemSelectedListener(this);
        if (account != null) {
            number.setText(account.getAccountNumber());
            accountHolder.setText(account.getAccountHolder());
            ifsc.setText(account.getIfsc());
            selectedBankId = account.getBank();
            bankSpinnerAdapter.setOnLoadCompleteListener(new BankSpinnerAdapter
                    .OnLoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    bank.setSelection(bankSpinnerAdapter.getPosition(selectedBankId));
                }
            });
            userid.setText(account.getUserid());
            password.setText(account.getPassword());
            builder.setPositiveButton(R.string.update, this);
        }
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
                String amount;
                Account account = new Account();
                account.setAccountHolder(accountHolder.getText().toString());
                account.setBank(selectedBankId);
                account.setIfsc(ifsc.getText().toString());
                account.setAccountNumber(number.getText().toString());
//                amount = balance.getText().toString().trim();
//                if (amount.length() == 0) amount = "0";
//                account.setBalance(Float.parseFloat(amount));
                account.setUpdatedByMemberId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                account.setUpdatedOn(Calendar.getInstance().getTimeInMillis());
                new InsertTask<>(((App) getActivity().getApplication())
                        .getDaoSession()
                        .getAccountDao(), this).execute(account);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                Log.d(TAG, "action not implemented/invalid action");
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        selectedBankId = bankSpinnerAdapter.getBankId(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onInsertTaskComplete(List<Account> items) {
        if (items == null || items.size() == 0)
            return;
        Account account = items.get(0);
        FirebaseDatabase.getInstance().getReference("accounts").child(familyId).child
                (items.get(0).getAccountNumber()).setValue(account);
    }
}
