package io.github.zkhan93.familyfinance;

import static io.github.zkhan93.familyfinance.models.CCard.EXPIRE_ON;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import io.github.zkhan93.familyfinance.adapters.BankSpinnerAdapter;
import io.github.zkhan93.familyfinance.events.ConfirmDeleteEvent;
import io.github.zkhan93.familyfinance.events.CreateEvent;
import io.github.zkhan93.familyfinance.events.UpdateEvent;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.ExpiryTextWatcher;
import io.github.zkhan93.familyfinance.util.TextWatcherProxy;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 19/7/17.
 */

public class DialogFragmentCcard extends DialogFragment implements DialogInterface
        .OnClickListener, AdapterView.OnItemSelectedListener, View.OnClickListener,
        DialogFragmentBillingCycle.OnBillingCycleSelectListener {
    public static final String TAG = DialogFragmentCcard.class.getSimpleName();
    public static final String ARG_FAMILY_ID = "familyId";
    public static final String ARG_CARD = "ccard";
    TextInputEditText cardName;
    TextInputEditText cardHolder;
    TextInputEditText number;
    TextInputEditText userid;
    TextInputEditText password;
    Spinner bank;
    TextInputEditText maxLimit;
    TextInputEditText consumedLimit;
    TextView billingCycle;
    EditText cvv;
    EditText expiresOn;
    TextInputEditText phoneNumber;

    TextInputLayout otherBankTil;
    TextInputEditText otherBank;

    ImageButton moreButton;
    TextView moreTitle;
    View moreFields;

    private String familyId, selectedBankId;
    private int billingDay, paymentDay;
    private String checkCardNumber;
    private CCard cCard;
    private ExpiryTextWatcher expiresOnTextWatcher;
    private final TextWatcherProxy cardNumberTextWatcher;
    private BankSpinnerAdapter bankSpinnerAdapter;
    private View rootView;
    private final ValueEventListener cardNumberChecker;

    {
        cardNumberTextWatcher = new TextWatcherProxy() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence == null || charSequence.toString().isEmpty()) {
                    ((AlertDialog) getDialog())
                            .getButton(DialogInterface.BUTTON_POSITIVE)
                            .setEnabled(false);
                } else {
                    //card number check if the card already exists
                    checkCardNumber = charSequence.toString();
                    FirebaseDatabase.getInstance()
                            .getReference("ccards")
                            .child(familyId)
                            .child(checkCardNumber).addListenerForSingleValueEvent(cardNumberChecker);
                }
            }
        };

        cardNumberChecker = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;
                if (!dataSnapshot.getKey().equals(checkCardNumber)) return;
                if (dataSnapshot.exists())
                    number.setError("Card already exists!");
                ((AlertDialog) getDialog())
                        .getButton(DialogInterface.BUTTON_POSITIVE)
                        .setEnabled(!dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
               .setNegativeButton(android.R.string.cancel, this)
               .setNeutralButton(R.string.delete, this);

        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_ccard,
                null);
        cardName = rootView.findViewById(R.id.name);
        cardHolder = rootView.findViewById(R.id.card_holder);
        number = rootView.findViewById(R.id.number);
        userid = rootView.findViewById(R.id.userid);
        password = rootView.findViewById(R.id.password);
        bank = rootView.findViewById(R.id.bank);
        maxLimit = rootView.findViewById(R.id.max_limit);
        consumedLimit = rootView.findViewById(R.id.consumed_cc_limit);
        billingCycle = rootView.findViewById(R.id.billing_cycle);
        cvv = rootView.findViewById(R.id.cvv);
        expiresOn = rootView.findViewById(R.id.expires_on);
        phoneNumber = rootView.findViewById(R.id.phone_number);

        otherBankTil = rootView.findViewById(R.id.other_bank_til);
        otherBank = rootView.findViewById(R.id.other_bank);

        moreButton = rootView.findViewById(R.id.more_btn);
        moreTitle = rootView.findViewById(R.id.more_title);
        moreFields = rootView.findViewById(R.id.more_fields);

        expiresOnTextWatcher = new ExpiryTextWatcher(expiresOn);
        expiresOn.addTextChangedListener(expiresOnTextWatcher);
        bank.setAdapter(bankSpinnerAdapter);
        bank.setOnItemSelectedListener(this);
        moreButton.setOnClickListener(this);
        billingCycle.setOnClickListener(this);
        if (cCard != null) {
            cardName.setText(cCard.getName());
            selectedBankId = cCard.getBank();
            bankSpinnerAdapter.setOnLoadCompleteListener(new BankSpinnerAdapter
                    .OnLoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    int position = bankSpinnerAdapter.getPosition(selectedBankId);
                    if (position == -1) {
                        bank.setSelection(bankSpinnerAdapter.getPosition(BankSpinnerAdapter
                                .OTHER_BANK));
                        otherBank.setText(selectedBankId);
                    } else
                        bank.setSelection(position);
                }
            });
            number.setText(cCard.getNumber());
            number.setVisibility(View.GONE);
            cardHolder.setText(cCard.getCardholder());
            maxLimit.setText(String.valueOf(cCard.getMaxLimit()));
            consumedLimit.setText(String.valueOf(cCard.getConsumedLimit()));
            expiresOn.setText(EXPIRE_ON.format(new Date(cCard.getExpireOn())));
            cvv.setText(cCard.getCvv());
            userid.setText(cCard.getUserid());
            password.setText(cCard.getPassword());
            phoneNumber.setText(cCard.getPhoneNumber());
            billingDay = cCard.getBillingDay();
            paymentDay = cCard.getPaymentDay();
            resetBillingCycleText();
            builder.setPositiveButton(R.string.update, this);
        } else {
            number.addTextChangedListener(cardNumberTextWatcher);
            number.setVisibility(View.VISIBLE);
            bankSpinnerAdapter.setOnLoadCompleteListener(() -> bank.setSelection(0));
        }
        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (cCard == null)
            ((AlertDialog) getDialog())
                    .getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_FAMILY_ID, familyId);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                CCard newCard = buildUpdatedCard();
                if (cCard == null)
                    EventBus.getDefault().post(new CreateEvent<>(newCard));
                else{
                    newCard.setNumber(cCard.getNumber());
                    EventBus.getDefault().post(new UpdateEvent<>(newCard));
                }
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                dialog.cancel();
                EventBus.getDefault().post(new ConfirmDeleteEvent<>(cCard));
                break;
            default:
                Log.d(TAG, "action not implemented/invalid action");
        }
    }
    private CCard buildUpdatedCard(){
        String amount;
        String number = this.number.getText().toString().trim();
        //no card can be created without a valid number
        if (number.isEmpty()) return null;
        CCard newCcard = new CCard();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null)
            newCcard.setUpdatedByMemberId(fbUser.getUid());
        newCcard.setUpdatedOn(Calendar.getInstance().getTimeInMillis());
        selectedBankId = selectedBankId.equals(BankSpinnerAdapter.OTHER_BANK) ?
                otherBank.getText().toString() : selectedBankId;
        newCcard.setBank(selectedBankId);
        newCcard.setName(cardName.getText().toString());
        newCcard.setNumber(number);
        newCcard.setBillingDay(billingDay);
        newCcard.setPaymentDay(paymentDay);
        newCcard.setCardholder(cardHolder.getText().toString());
        amount = maxLimit.getText().toString().trim();
        if (amount.length() == 0) amount = "0";
        newCcard.setMaxLimit(Float.parseFloat(amount));
        amount = consumedLimit.getText().toString().trim();
        if (amount.length() == 0) amount = "0";
        newCcard.setConsumedLimit(Float.parseFloat(amount));
        newCcard.setCvv(cvv.getText().toString());
        newCcard.setPhoneNumber(phoneNumber.getText().toString());
        try {
            newCcard.setExpireOn(EXPIRE_ON.
                    parse(expiresOn.getText().toString())
                    .getTime());
        } catch (ParseException ex) {
            newCcard.setExpireOn(-1);
        }
        newCcard.setUserid(userid.getText().toString());
        newCcard.setPassword(password.getText().toString());
        return newCcard;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        selectedBankId = bankSpinnerAdapter.getBankId(position);
        if (selectedBankId.equals(BankSpinnerAdapter.OTHER_BANK)) {
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
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                clickActions(view);
            }
        }, 200);
    }

    private void clickActions(View view) {
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
                rootView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((ScrollView) rootView).smoothScrollTo(0, ((ScrollView) rootView)
                                .getChildAt(0)
                                .getHeight());
                    }
                }, 50);
                break;
            case R.id.billing_cycle:
                DialogFragmentBillingCycle.getInstance(this, billingDay, paymentDay).show
                        (getActivity()
                                .getSupportFragmentManager(), DialogFragmentBillingCycle.TAG);
                break;
        }
    }

    @Override
    public void onBillingCycleSelect(int billingDay, int paymentDay) {
        this.billingDay = billingDay;
        this.paymentDay = paymentDay;
        resetBillingCycleText();
    }

    private void resetBillingCycleText() {
        billingCycle.setText(Util.getBillingCycleString(billingDay, paymentDay, "%s - %s"));
    }
}
