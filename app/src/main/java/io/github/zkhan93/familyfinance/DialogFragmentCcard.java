package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.BankSpinnerAdapter;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.tasks.InsertTask;

import static io.github.zkhan93.familyfinance.models.CCard.EXPIRE_ON;

/**
 * Created by zeeshan on 19/7/17.
 */

public class DialogFragmentCcard extends DialogFragment implements InsertTask.Listener<CCard>,
        DialogInterface.OnClickListener {
    public static final String TAG = DialogFragmentCcard.class.getSimpleName();
    public static final String ARG_FAMILY_ID = "familyID";
    public static final String ARG_CARD = "ccard";
    @BindView(R.id.name)
    TextInputEditText cardName;
    @BindView(R.id.card_holder)
    TextInputEditText cardHolder;
    @BindView(R.id.number)
    TextInputEditText number;
    @BindView(R.id.userid)
    TextInputEditText userid;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.bank)
    Spinner bank;
    @BindView(R.id.max_limit)
    TextInputEditText maxLimit;
    @BindView(R.id.consumed_cc_limit)
    TextInputEditText consumedLimit;
    @BindView(R.id.billing_day)
    NumberPicker billingDay;
    @BindView(R.id.payment_day)
    NumberPicker paymentDay;
    @BindView(R.id.cvv)
    EditText cvv;
    @BindView(R.id.expires_on)
    EditText expiresOn;
    @BindView(R.id.phone_number)
    TextInputEditText phoneNumber;

    private String familyId, selectedBankId;
    private CCard cCard;
    private TextWatcher expiresOnTextWatcher;
    private BankSpinnerAdapter bankSpinnerAdapter;

    {
        expiresOnTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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
                expiresOn.removeTextChangedListener(this);
                expiresOn.setText(value);
                expiresOn.setSelection(value.length());
                expiresOn.addTextChangedListener(this);
            }
        };
    }

    public static DialogFragmentCcard newInstance(String familyId) {
        DialogFragmentCcard dialogFragmentAddAccount = new DialogFragmentCcard();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        dialogFragmentAddAccount.setArguments(args);
        return dialogFragmentAddAccount;
    }

    public static DialogFragmentCcard newInstance(String familyId, CCard cCard) {
        DialogFragmentCcard dialogFragmentAddAccount = new DialogFragmentCcard();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        args.putParcelable(ARG_CARD, cCard);
        dialogFragmentAddAccount.setArguments(args);
        return dialogFragmentAddAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            familyId = bundle.getString(ARG_FAMILY_ID);
            cCard = bundle.getParcelable(ARG_CARD);
        }
        if (bankSpinnerAdapter == null)
            bankSpinnerAdapter = new BankSpinnerAdapter(getActivity().getApplicationContext());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_new_card);
        builder.setPositiveButton(R.string.create, this)
                .setNegativeButton(android.R.string.cancel, this);

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_ccard,
                null);
        ButterKnife.bind(this, rootView);
        billingDay.setMinValue(1);
        billingDay.setMaxValue(31);
        paymentDay.setMinValue(1);
        paymentDay.setMaxValue(31);
        expiresOn.addTextChangedListener(expiresOnTextWatcher);
        bank.setAdapter(bankSpinnerAdapter);
        if (cCard != null) {
            cardName.setText(cCard.getName());
            selectedBankId = cCard.getBank();
            bankSpinnerAdapter.setOnLoadCompleteListener(new BankSpinnerAdapter
                    .OnLoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    bank.setSelection(bankSpinnerAdapter.getPosition(selectedBankId));
                }
            });
            number.setText(cCard.getNumber());
            cardHolder.setText(cCard.getCardholder());
            maxLimit.setText(String.valueOf(cCard.getMaxLimit()));
            consumedLimit.setText(String.valueOf(cCard.getConsumedLimit()));
            paymentDay.setValue(cCard.getPaymentDay());
            billingDay.setValue(cCard.getBillingDay());
            expiresOn.setText(EXPIRE_ON.format(new Date(cCard.getExpireOn())));
            cvv.setText(cCard.getCvv());
            userid.setText(cCard.getUserid());
            password.setText(cCard.getPassword());
            phoneNumber.setText(cCard.getPhoneNumber());
            builder.setPositiveButton(R.string.update, this);
        }
        builder.setView(rootView);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_FAMILY_ID, familyId);
        //todo: save the card edited content
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String amount;
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                //TODO: validate values
                CCard newCcard = new CCard();
                newCcard.setUpdatedByMemberId(FirebaseAuth.getInstance
                        ().getCurrentUser().getUid());
                newCcard.setUpdatedOn(Calendar.getInstance().getTimeInMillis());
                newCcard.setBank(selectedBankId);
                newCcard.setName(cardName.getText().toString());
                newCcard.setNumber(number.getText().toString());
                newCcard.setCardholder(cardHolder.getText().toString());
                amount = maxLimit.getText().toString().trim();
                if (amount.length() == 0) amount = "0";
                newCcard.setMaxLimit(Float.parseFloat(amount));
                amount = consumedLimit.getText().toString().trim();
                if (amount.length() == 0) amount = "0";
                newCcard.setConsumedLimit(Float.parseFloat(amount));
                newCcard.setPaymentDay(paymentDay.getValue());
                newCcard.setBillingDay(billingDay.getValue());
                newCcard.setCvv(cvv.getText().toString());
                newCcard.setPhoneNumber(phoneNumber.getText().toString());
                try {
                    newCcard.setExpireOn(EXPIRE_ON.parse(expiresOn.getText().toString()).getTime());
                } catch (ParseException ex) {
                    newCcard.setExpireOn(-1);
                }
                newCcard.setUserid(userid.getText().toString());
                newCcard.setPassword(password.getText().toString());
                new InsertTask<>(((App) getActivity().getApplication())
                        .getDaoSession()
                        .getCCardDao(), this).execute(newCcard);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                Log.d(TAG, "action not implemented/invalid action");
        }
    }

    @Override
    public void onInsertTaskComplete(List<CCard> items) {
        if (items == null || items.size() == 0)
            return;
        CCard newCcard = items.get(0);
        if (cCard != null && !newCcard.getNumber().trim().equals(cCard.getNumber().trim())) {
            //cards id changed delete previous from firebase , different node
            Map<String, Object> updates = new HashMap<>();
            updates.put(newCcard.getNumber(), newCcard);
            updates.put(cCard.getNumber(), null);//delete old card
            FirebaseDatabase.getInstance().getReference("ccards").child(familyId).updateChildren
                    (updates);
        } else
            FirebaseDatabase.getInstance().getReference("ccards").child(familyId).child
                    (items.get(0).getNumber()).setValue(newCcard);
    }


}
