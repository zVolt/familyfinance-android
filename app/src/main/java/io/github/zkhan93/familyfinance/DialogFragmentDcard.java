package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
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
import io.github.zkhan93.familyfinance.models.DCard;

import static io.github.zkhan93.familyfinance.models.DCard.EXPIRE_ON;

/**
 * Created by zeeshan on 19/7/17.
 */

public class DialogFragmentDcard extends DialogFragment implements DialogInterface
        .OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, View.OnClickListener {
    public static final String TAG = DialogFragmentDcard.class.getSimpleName();
    public static final String ARG_FAMILY_ID = "familyId";
    public static final String ARG_CARD = "ccard";

    @BindView(R.id.name)
    TextInputEditText cardName;
    @BindView(R.id.card_holder)
    TextInputEditText cardHolder;
    @BindView(R.id.number)
    TextInputEditText number;
    @BindView(R.id.email)
    TextInputEditText email;
    @BindView(R.id.username)
    TextInputEditText username;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.pin)
    TextInputEditText pin;
    @BindView(R.id.bank)
    Spinner bank;
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
    private String checkCardNumber;
    private DCard dCard;
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
                .setNegativeButton(android.R.string.cancel, this);

        rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_dcard,
                null);
        ButterKnife.bind(this, rootView);
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        String amount;
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                String number = this.number.getText().toString().trim();
                //no card can be created without a valid number
                if (number.isEmpty()) return;
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
                createCard(newDCard);
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
     * @param newDcard
     */
    public void createCard(DCard newDcard) {
        if (newDcard == null)
            return;
        if (dCard == null || newDcard.getNumber().trim().equals(dCard.getNumber().trim()))
            FirebaseDatabase.getInstance()
                    .getReference("dcards")
                    .child(familyId)
                    .child(newDcard.getNumber())
                    .setValue(newDcard);
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
