package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.BankSpinnerAdapter;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Util;

import static io.github.zkhan93.familyfinance.models.CCard.EXPIRE_ON;

/**
 * Created by zeeshan on 19/7/17.
 */

public class DialogFragmentCcard extends DialogFragment implements DialogInterface
        .OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, View.OnClickListener,
        DialogFragmentBillingCycle.OnBillingCycleSelectListener {
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
    @BindView(R.id.billing_cycle)
    TextView billingCycle;
    @BindView(R.id.cvv)
    EditText cvv;
    @BindView(R.id.expires_on)
    EditText expiresOn;
    @BindView(R.id.phone_number)
    TextInputEditText phoneNumber;

    @BindView(R.id.other_bank_til)
    TextInputLayout otherBankTil;
    @BindView(R.id.other_bank)
    TextInputEditText otherBank;

    @BindView(R.id.more_btn)
    ImageButton moreButton;
    @BindView(R.id.more_title)
    TextView moreTitle;
    @BindView(R.id.more_fields)
    View moreFields;

    private String familyId, selectedBankId;
    private int billingDay, paymentDay;
    private String checkCardNumber;
    private CCard cCard;
    private TextWatcher expiresOnTextWatcher;
    private BankSpinnerAdapter bankSpinnerAdapter;
    private View rootView;
    private ValueEventListener cardNumberChecker;

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
                .setNegativeButton(android.R.string.cancel, this);

        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_ccard,
                null);
        ButterKnife.bind(this, rootView);
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
            number.addTextChangedListener(this);
            number.setVisibility(View.VISIBLE);
            bankSpinnerAdapter.setOnLoadCompleteListener(new BankSpinnerAdapter
                    .OnLoadCompleteListener() {
                @Override
                public void onLoadComplete() {
                    bank.setSelection(0);
                }
            });
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
        String amount;
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                String number = this.number.getText().toString().trim();
                //no card can be created without a valid number
                if (number.isEmpty()) return;
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
                createCard(newCcard);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            default:
                Log.d(TAG, "action not implemented/invalid action");
        }
    }

    /**
     * can have 2 case
     * case I : Create new card - noting to worry about, just make sure you do not override an
     * existing card
     * case II: Update a exsisting card details, make sure that the update does not include
     * change in card number because that will cause a new card to get created
     *
     * @param newCcard
     */
    public void createCard(CCard newCcard) {
        if (newCcard == null)
            return;
        if (cCard == null || newCcard.getNumber().trim().equals(cCard.getNumber().trim()))
            FirebaseDatabase.getInstance()
                    .getReference("ccards")
                    .child(familyId)
                    .child(newCcard.getNumber())
                    .setValue(newCcard);
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
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

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

    @Override
    public void afterTextChanged(Editable editable) {

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
