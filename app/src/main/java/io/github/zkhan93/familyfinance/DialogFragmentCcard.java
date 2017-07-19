package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.tasks.InsertTask;

/**
 * Created by zeeshan on 19/7/17.
 */

public class DialogFragmentCcard extends DialogFragment implements InsertTask.Listener<CCard>,
        DialogInterface.OnClickListener {
    public static final String TAG = DialogFragmentCcard.class.getSimpleName();
    public static final String ARG_FAMILY_ID = "familyID";
    public static final String ARG_CARD = "ccard";

    @BindView(R.id.card_holder)
    TextInputEditText cardHolder;
    @BindView(R.id.number)
    TextInputEditText number;
    @BindView(R.id.userid)
    TextInputEditText userid;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.bank)
    TextInputEditText bank;
    @BindView(R.id.max_limit)
    TextInputEditText maxLimit;
    @BindView(R.id.consumed_cc_limit)
    TextInputEditText consumedLimit;

    @BindView(R.id.billing_day)
    NumberPicker billingDay;
    @BindView(R.id.payment_day)
    NumberPicker paymentDay;

    private String familyId;
    private CCard cCard;

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
        if (cCard != null) {
            bank.setText(cCard.getBank());
            number.setText(cCard.getNumber());
            cardHolder.setText(cCard.getCardholder());
            maxLimit.setText(String.valueOf(cCard.getMaxLimit()));
            consumedLimit.setText(String.valueOf(cCard.getConsumedLimit()));
            paymentDay.setValue(cCard.getPaymentDay());
            billingDay.setValue(cCard.getBillingDay());

            userid.setText(cCard.getUserid());
            password.setText(cCard.getPassword());
            builder.setPositiveButton(R.string.update,this);
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
                CCard newCcard = new CCard();
                newCcard.setUpdatedByMemberId(FirebaseAuth.getInstance
                        ().getCurrentUser().getUid());
                newCcard.setUpdatedOn(Calendar.getInstance().getTimeInMillis());
                newCcard.setBank(bank.getText().toString());
                newCcard.setNumber(number.getText().toString());
                newCcard.setCardholder(cardHolder.getText().toString());
                newCcard.setMaxLimit(Float.parseFloat(maxLimit.getText().toString()));
                newCcard.setConsumedLimit(Float.parseFloat(consumedLimit.getText().toString()));
                newCcard.setPaymentDay(paymentDay.getValue());
                newCcard.setBillingDay(billingDay.getValue());
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
        if (!newCcard.getNumber().trim().equals(cCard.getNumber().trim())) {
            //cards id changed delete previous from firebase , different node
            //
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
