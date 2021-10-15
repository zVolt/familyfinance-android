package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.util.Util;

import static io.github.zkhan93.familyfinance.models.DCard.EXPIRE_ON;

/**
 * Created by zeeshan on 11/11/17.
 */

public class DialogFragmentViewDCard extends DialogFragment implements DialogInterface
        .OnClickListener {
    public static final String TAG = DialogFragmentViewDCard.class.getSimpleName();
    public static final String ARG_CARD = "card";
    public static final String ARG_FAMILY_ID = "familyId";

    private DCard dCard;
    private String familyId;
    private DatabaseReference cardRef;
    private Map<String, Object> updateMap;
    private ValueEventListener bankImageLinkListener;

    @BindView(R.id.bank)
    ImageView bank;
    @BindView(R.id.card_holder)
    TextView cardHolder;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.pin)
    TextView pin;
    @BindView(R.id.card_number)
    TextView cardNumber;
    @BindView(R.id.expires_on)
    TextView expiresOn;
    @BindView(R.id.cvv)
    TextView cvv;
    @BindView(R.id.phone_number)
    TextView phoneNumber;
    @BindView(R.id.updated_by)
    ImageView updateBy;
    @BindView(R.id.updated_on)
    TextView updatedOn;

    {
        bankImageLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url != null)
                    Glide.with(bank.getContext()).load(url).into(bank);
                else
                    Glide.with(bank.getContext()).load("https://via.placeholder" +
                            ".com/200x200/f0f0f0/2c2c2c?text=" + dataSnapshot.getKey()).into(bank);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "bank image loading cancelled");
            }
        };
    }

    public static DialogFragmentViewDCard newInstance(DCard dCard, String familyId) {
        DialogFragmentViewDCard dialogFragmentAddAccount = new DialogFragmentViewDCard();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD, dCard);
        args.putString(ARG_FAMILY_ID, familyId);
        dialogFragmentAddAccount.setArguments(args);
        return dialogFragmentAddAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            dCard = bundle.getParcelable(ARG_CARD);
            familyId = bundle.getString(ARG_FAMILY_ID);
        }
        cardRef = FirebaseDatabase.getInstance().getReference("ccards").child(familyId).child(dCard
                .getNumber());
        if (updateMap == null)
            updateMap = new HashMap<>();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setPositiveButton(R
                .string.action_share, this).setNegativeButton("copy", this)
                .setNeutralButton(android.R.string.ok, this);

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_view_dcard,
                null);
        ButterKnife.bind(this, rootView);
        if (dCard != null) {
            FirebaseDatabase.getInstance().getReference("images").child("banks").child(dCard
                    .getBank().toUpperCase()).addListenerForSingleValueEvent(bankImageLinkListener);
            cardHolder.setText(getDefaultNotSet(dCard.getCardholder()));
            cardNumber.setText(dCard.getFormattedNumber(' '));
            email.setText(dCard.getEmail());
            expiresOn.setText(getDefaultNotSet(EXPIRE_ON.format(new Date(dCard.getExpireOn()))));
            cvv.setText(getDefaultNotSet(dCard.getCvv()));
            username.setText(getDefaultNotSet(dCard.getUsername()));
            pin.setText(getDefaultNotSet(dCard.getPin()));
            password.setText(getDefaultNotSet(dCard.getPassword()));
            password.setText(getDefaultNotSet(dCard.getPassword()));
            phoneNumber.setText(getDefaultNotSet(dCard.getPhoneNumber()));
            Glide.with(getContext()).load(dCard.getUpdatedBy().getProfilePic()).apply(RequestOptions
                    .circleCropTransform()).into(updateBy);
            updatedOn.setText(DateUtils.getRelativeTimeSpanString(getContext(), dCard
                    .getUpdatedOn(), true));
        }

        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_CARD, dCard);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_NEGATIVE:
                //copy
                Util.copyToClipboard(getActivity().getApplicationContext(), (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE), dCard
                        .getReadableContent());
                Toast.makeText(getActivity().getApplicationContext(), "Card details copied to " +
                        "clipboard!", Toast.LENGTH_SHORT).show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                //share
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, dCard.getReadableContent());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string
                        .action_share)));
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                break;

        }
    }

    private String getDefaultNotSet(String value) {
        if (value == null || value.isEmpty()) {
            return "Not Set";
        }
        return value;
    }
}
