package io.github.zkhan93.familyfinance;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.util.ExpiryTextWatcher;

import static io.github.zkhan93.familyfinance.models.CCard.EXPIRE_ON;

/**
 * Created by zeeshan on 28/7/17.
 */

public class DialogFragmentAddonCard extends DialogFragment implements DialogInterface
        .OnClickListener, InsertTask.Listener<AddonCard> {
    public static final String TAG = DialogFragmentAddonCard.class.getSimpleName();
    public static final String ARG_FAMILY_ID = "familyId";
    public static final String ARG_MAIN_CARD_NUMBER = "mainCardNumber";
    public static final String ARG_ADDONCARD = "addonCard";

    TextInputEditText name;
    TextInputEditText number;
    EditText expiresOn;
    EditText cvv;
    TextInputEditText phoneNumber;

    private String familyId, mainCardNumber;
    private AddonCard addonCard;
    private TextWatcher expiresOnTextWatcher;

    public static DialogFragmentAddonCard newInstance(String familyId, String mainCardNumber) {
        DialogFragmentAddonCard dialogFragmentAddonCard = new DialogFragmentAddonCard();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        args.putString(ARG_MAIN_CARD_NUMBER, mainCardNumber);
        dialogFragmentAddonCard.setArguments(args);
        return dialogFragmentAddonCard;
    }

    public static DialogFragmentAddonCard newInstance(String familyId, String mainCardNumber,
                                                      AddonCard addonCard) {
        DialogFragmentAddonCard dialogFragmentAddonCard = new DialogFragmentAddonCard();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        args.putString(ARG_MAIN_CARD_NUMBER, mainCardNumber);
        args.putParcelable(ARG_ADDONCARD, addonCard);
        dialogFragmentAddonCard.setArguments(args);
        return dialogFragmentAddonCard;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            familyId = bundle.getString(ARG_FAMILY_ID);
            mainCardNumber = bundle.getString(ARG_MAIN_CARD_NUMBER);
            addonCard = bundle.getParcelable(ARG_ADDONCARD);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.title_new_card);
        builder.setPositiveButton(R.string.create, this)
                .setNegativeButton(android.R.string.cancel, this);

        View rootView = LayoutInflater
                .from(getActivity())
                .inflate(R.layout.dialog_add_addoncard,null);
        name = rootView.findViewById(R.id.name);
        number = rootView.findViewById(R.id.number);
        expiresOn = rootView.findViewById(R.id.expires_on);
        cvv = rootView.findViewById(R.id.cvv);
        phoneNumber = rootView.findViewById(R.id.phone_number);
        expiresOnTextWatcher = new ExpiryTextWatcher(expiresOn);
        expiresOn.addTextChangedListener(expiresOnTextWatcher);
        if (addonCard != null) {
            name.setText(addonCard.getName());
            number.setText(addonCard.getNumber());
            expiresOn.setText(CCard.EXPIRE_ON.format(new Date(addonCard.getExpiresOn())));
            cvv.setText(String.valueOf(addonCard.getCvv()));
            phoneNumber.setText(addonCard.getPhoneNumber());
            builder.setPositiveButton(R.string.update, this);
        }
        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                //TODO: validate values
                AddonCard newAddonCard = new AddonCard();
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                if (fbUser != null)
                newAddonCard.setUpdatedByMemberId(fbUser.getUid());
                newAddonCard.setMainCardNumber(mainCardNumber);
                newAddonCard.setUpdatedOn(Calendar.getInstance().getTimeInMillis());

                newAddonCard.setName(name.getText().toString());
                newAddonCard.setNumber(number.getText().toString());
                newAddonCard.setCvv(Integer.parseInt(cvv.getText().toString()));
                newAddonCard.setPhoneNumber(phoneNumber.getText().toString());
                try {
                    newAddonCard.setExpiresOn(EXPIRE_ON
                            .parse(expiresOn.getText().toString())
                            .getTime());
                } catch (ParseException ex) {
                    newAddonCard.setExpiresOn(-1);
                }
                new InsertTask<>(((App) getActivity().getApplication())
                        .getDaoSession()
                        .getAddonCardDao(), this)
                        .execute(newAddonCard);
                break;
            default:
                Log.d(TAG, "action not implemented/invalid action");
        }
    }

    @Override
    public void onInsertTaskComplete(List<AddonCard> items) {
        if (items == null || items.size() == 0)
            return;
        AddonCard newAddonCcard = items.get(0);
        //if this is an update
        if (addonCard != null && !newAddonCcard.getNumber().trim().equals(addonCard.getNumber()
                .trim())) {
            //cards id changed delete previous from firebase , different node
            Map<String, Object> updates = new HashMap<>();
            updates.put(newAddonCcard.getNumber(), newAddonCcard);
            updates.put(addonCard.getNumber(), null);//delete old card
            FirebaseDatabase.getInstance()
                    .getReference("ccards")
                    .child(familyId)
                    .child(mainCardNumber)
                    .child("addonCards")
                    .updateChildren(updates);
        } else
            FirebaseDatabase.getInstance()
                    .getReference("ccards")
                    .child(familyId)
                    .child(mainCardNumber)
                    .child("addonCards")
                    .child(newAddonCcard.getNumber())
                    .setValue(newAddonCcard);
    }
}
