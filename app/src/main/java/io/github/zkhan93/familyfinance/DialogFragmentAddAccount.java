package io.github.zkhan93.familyfinance;

import static io.github.zkhan93.familyfinance.adapters.BankSpinnerAdapter.OTHER_BANK;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

import io.github.zkhan93.familyfinance.adapters.BankSpinnerAdapter;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.util.TextWatcherProxy;


/**
 * Created by zeeshan on 12/7/17.
 */

public class DialogFragmentAddAccount extends DialogFragment implements DialogInterface
        .OnClickListener, InsertTask.Listener<Account>, AdapterView.OnItemSelectedListener, View
        .OnClickListener {

    public static final String TAG = DialogFragmentAddAccount.class.getSimpleName();
    public static final String ARG_FAMILY_ID = "familyId";
    public static final String ARG_ACCOUNT = "account";

    Spinner bank;
    TextInputLayout otherBankTil;
    TextInputEditText otherBank;
    TextInputEditText number;
    TextInputEditText accountHolder;

    TextInputEditText ifsc;
    TextInputEditText userid;
    TextInputEditText password;
    TextInputEditText email;
    TextInputEditText phoneNumber;

    ImageButton moreButton;
    TextView moreTitle;
    View moreFields;

    private String familyId;
    private Account account;
    private BankSpinnerAdapter bankSpinnerAdapter;
    private String selectedBankId;
    private View rootView;
    private final TextWatcherProxy numberChangeWatcher;

    public DialogFragmentAddAccount() {
        super();
        numberChangeWatcher = new TextWatcherProxy() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((AlertDialog) getDialog()).getButton(DialogInterface.BUTTON_POSITIVE).setEnabled
                        (charSequence != null && !charSequence.toString().isEmpty());
            }
        };
    }

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
        } else {
            account = null;
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

        rootView = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_add_account, null);
        bank = rootView.findViewById(R.id.bank);
        otherBankTil = rootView.findViewById(R.id.other_bank_til);
        otherBank = rootView.findViewById(R.id.other_bank);
        number = rootView.findViewById(R.id.number);
        accountHolder = rootView.findViewById(R.id.account_holder);

        ifsc = rootView.findViewById(R.id.ifsc);
        userid = rootView.findViewById(R.id.userid);
        password = rootView.findViewById(R.id.password);
        email = rootView.findViewById(R.id.email);
        phoneNumber = rootView.findViewById(R.id.phone_number);

        moreButton = rootView.findViewById(R.id.more_btn);
        moreTitle = rootView.findViewById(R.id.more_title);
        moreFields = rootView.findViewById(R.id.more_fields);
        moreButton.setOnClickListener(this);
        bank.setAdapter(bankSpinnerAdapter);
        bank.setOnItemSelectedListener(this);
        if (account != null) {
            number.setText(account.getAccountNumber());
            accountHolder.setText(account.getAccountHolder());
            ifsc.setText(account.getIfsc());
            selectedBankId = account.getBank();
            bankSpinnerAdapter.setOnLoadCompleteListener(() -> {
                int position = bankSpinnerAdapter.getPosition(selectedBankId);
                if (position == -1) {
                    bank.setSelection(bankSpinnerAdapter.getPosition(OTHER_BANK));
                    otherBank.setText(selectedBankId);
                } else
                    bank.setSelection(position);
            });
            userid.setText(account.getUserid());
            email.setText(account.getEmail());
            password.setText(account.getPassword());
            phoneNumber.setText(account.getPhoneNumber());
            number.setVisibility(View.GONE);
            builder.setPositiveButton(R.string.update, this);
        } else {
            bank.postDelayed(() -> bank.setSelection(0), 100);
        }
        number.addTextChangedListener(numberChangeWatcher);
        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        //trigger the text watcher once
        number.setText(number.getText());
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                String number, name, bank;
                name = accountHolder.getText().toString();
                bank = selectedBankId.equals(OTHER_BANK) ? otherBank.getText()
                        .toString() : selectedBankId;
                number = this.number.getText().toString();
                //skip creating new account if accout number is not present
                //although this is not likely to be in any case adding a textwatcher
                //on number edit fields and disabling the create button
                //or if you try to change the account number
                if (number.isEmpty() || (account != null && !account.getAccountNumber().equals
                        (number)))
                    return;
                Account account = new Account();
                account.setAccountHolder(name);
                account.setBank(bank);
                account.setIfsc(ifsc.getText().toString());
                if (this.account != null) {
                    account.setAccountNumber(this.account.getAccountNumber());
                    account.setBalance(this.account.getBalance());
                } else {
                    account.setAccountNumber(number);
                }
                account.setUserid(userid.getText().toString());
                account.setPassword(password.getText().toString());
                account.setEmail(email.getText().toString());
                account.setPhoneNumber(phoneNumber.getText().toString());
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser != null)
                    account.setUpdatedByMemberId(fbUser.getUid());
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
        if (selectedBankId.equals(OTHER_BANK)) {
            otherBankTil.setVisibility(View.VISIBLE);
        } else {
            otherBankTil.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onClick(final View view) {
        view.postDelayed(() -> performAction(view), 200);
    }

    private void performAction(View view) {
        switch (view.getId()) {
            case R.id.more_btn:
                boolean isVisible = moreFields.getVisibility() == View.VISIBLE;
                moreTitle.setText(isVisible ? R.string.more : R.string.less);
                moreFields.setVisibility(isVisible ? View.GONE : View.VISIBLE);
                moreButton.setImageDrawable(
                        ContextCompat.getDrawable(getActivity(),
                                isVisible ?
                                        R.drawable.ic_keyboard_arrow_down_grey_500_24dp :
                                        R.drawable.ic_keyboard_arrow_up_grey_500_24dp
                        ));
                rootView.postDelayed(() -> ((ScrollView) rootView).smoothScrollTo(0, ((ScrollView) rootView)
                        .getChildAt(0)
                        .getHeight()), 50);
                break;
        }
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
