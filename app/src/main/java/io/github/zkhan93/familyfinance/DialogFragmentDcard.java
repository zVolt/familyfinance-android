package io.github.zkhan93.familyfinance;

import static io.github.zkhan93.familyfinance.models.DCard.EXPIRE_ON;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import io.github.zkhan93.familyfinance.events.CreateEvent;
import io.github.zkhan93.familyfinance.events.DeleteEvent;
import io.github.zkhan93.familyfinance.events.UpdateEvent;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.util.SimpleTextWatcher;

/**
 * Created by zeeshan on 19/7/17.
 */

public class DialogFragmentDcard extends DialogFragment implements DialogInterface
        .OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, View.OnClickListener {
    public static final String TAG = DialogFragmentDcard.class.getSimpleName();
    public static final String ARG_FAMILY_ID = "familyId";
    public static final String ARG_CARD = "ccard";

    TextInputEditText cardName;
    TextInputEditText cardHolder;
    TextInputEditText number;
    TextInputEditText email;
    TextInputEditText username;
    TextInputEditText password;
    TextInputEditText pin;
    Spinner bank;
    EditText cvv;
    EditText expiresOn;
    TextInputEditText phoneNumber;

    TextInputLayout otherBankTil;
    TextInputEditText otherBank;

    ImageButton moreButton;
    TextView moreTitle;
    View moreFields;

    private String familyId, selectedBankId;
    private String checkCardNumber;
    private DCard dCard;
    private TextWatcher expiresOnTextWatcher;
    private BankSpinnerAdapter bankSpinnerAdapter;
    private View rootView;
    private ValueEventListener cardNumberChecker;

    {
        expiresOnTextWatcher = new SimpleTextWatcher() {
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

    public static DialogFragmentDcard newInstance(String familyId) {
        DialogFragmentDcard dialog = new DialogFragmentDcard();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        dialog.setArguments(args);
        return dialog;
    }

    public static DialogFragmentDcard newInstance(String familyId, DCard dCard) {
        DialogFragmentDcard dialog = new DialogFragmentDcard();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        args.putParcelable(ARG_CARD, dCard);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            familyId = bundle.getString(ARG_FAMILY_ID);
            dCard = bundle.getParcelable(ARG_CARD);
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

        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_dcard,
                null);
        cardName = rootView.findViewById(R.id.name);
        cardHolder = rootView.findViewById(R.id.card_holder);
        number = rootView.findViewById(R.id.number);
        email = rootView.findViewById(R.id.email);
        username = rootView.findViewById(R.id.username);
        password = rootView.findViewById(R.id.password);
        pin = rootView.findViewById(R.id.pin);
        bank = rootView.findViewById(R.id.bank);
        cvv = rootView.findViewById(R.id.cvv);
        expiresOn = rootView.findViewById(R.id.expires_on);
        phoneNumber = rootView.findViewById(R.id.phone_number);

        otherBankTil = rootView.findViewById(R.id.other_bank_til);
        otherBank = rootView.findViewById(R.id.other_bank);

        moreButton = rootView.findViewById(R.id.more_btn);
        moreTitle = rootView.findViewById(R.id.more_title);
        moreFields = rootView.findViewById(R.id.more_fields);
        expiresOn.addTextChangedListener(expiresOnTextWatcher);
        bank.setAdapter(bankSpinnerAdapter);
        bank.setOnItemSelectedListener(this);
        moreButton.setOnClickListener(this);

        if (dCard != null) {
            cardName.setText(dCard.getName());
            selectedBankId = dCard.getBank();
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
            number.setText(dCard.getNumber());
            number.setVisibility(View.GONE);
            cardHolder.setText(dCard.getCardholder());

            expiresOn.setText(EXPIRE_ON.format(new Date(dCard.getExpireOn())));
            cvv.setText(dCard.getCvv());
            username.setText(dCard.getUsername());
            password.setText(dCard.getPassword());
            phoneNumber.setText(dCard.getPhoneNumber());
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
        if (dCard == null)
            ((AlertDialog) getDialog())
                    .getButton(DialogInterface.BUTTON_POSITIVE)
                    .setEnabled(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_FAMILY_ID, familyId);
    }

    private DCard buildUpdatedDCard() {
        String number = this.number.getText().toString().trim();
        //no card can be created without a valid number
        if (number.isEmpty()) return null;
        DCard newDCard = new DCard();
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null)
            newDCard.setUpdatedByMemberId(fbUser.getUid());
        newDCard.setUpdatedOn(Calendar.getInstance().getTimeInMillis());
        selectedBankId = selectedBankId.equals(BankSpinnerAdapter.OTHER_BANK) ?
                otherBank.getText().toString() : selectedBankId;
        newDCard.setBank(selectedBankId);
        newDCard.setName(cardName.getText().toString());
        newDCard.setNumber(number);
        newDCard.setCardholder(cardHolder.getText().toString());

        newDCard.setCvv(cvv.getText().toString());
        newDCard.setPhoneNumber(phoneNumber.getText().toString());
        try {
            newDCard.setExpireOn(EXPIRE_ON.
                    parse(expiresOn.getText().toString())
                    .getTime());
        } catch (ParseException ex) {
            newDCard.setExpireOn(-1);
        }
        newDCard.setUsername(username.getText().toString());
        newDCard.setPassword(password.getText().toString());
        return newDCard;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                DCard card = buildUpdatedDCard();
                EventBus.getDefault().post(new UpdateEvent<>(card));
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                EventBus.getDefault().post(new DeleteEvent<>(dCard));
                break;
            default:
                Log.d(TAG, "action not implemented/invalid action");
        }
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
        }
    }
}
