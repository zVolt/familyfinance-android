package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.AddonCardListAdapter;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Util;

import static io.github.zkhan93.familyfinance.models.CCard.EXPIRE_ON;

/**
 * Created by zeeshan on 11/11/17.
 */

public class DialogFragmentViewCard extends DialogFragment implements DialogInterface
        .OnClickListener, SeekBar.OnSeekBarChangeListener {
    public static final String TAG = DialogFragmentAddAccount.class.getSimpleName();
    public static final String ARG_CARD = "card";
    public static final String ARG_FAMILY_ID = "familyId";

    private CCard cCard;
    private String familyId;
    private DatabaseReference cardRef;
    private Map<String, Object> updateMap;
    private ValueEventListener bankImageLinkListener;

    @BindView(R.id.bank)
    ImageView bank;
    @BindView(R.id.card_holder)
    TextView cardHolder;
    @BindView(R.id.card_number)
    TextView cardNumber;
    @BindView(R.id.expires_on)
    TextView expiresOn;
    @BindView(R.id.cvv)
    TextView cvv;
    @BindView(R.id.billing_cycle)
    TextView billingCycle;
    @BindView(R.id.phone_number)
    TextView phoneNumber;
    @BindView(R.id.limit)
    SeekBar limit;
    @BindView(R.id.consumed_limit)
    TextView consumedLimit;
    @BindView(R.id.remaining_limit)
    TextView remainingLimit;
    @BindView(R.id.updated_by)
    ImageView updateBy;
    @BindView(R.id.updated_on)
    TextView updatedOn;
    @BindView(R.id.userid)
    TextView userid;
    @BindView(R.id.password)
    TextView password;
    @BindView(R.id.addons_title)
    TextView addonTitle;
    @BindView(R.id.addon_cards)
    RecyclerView addonCards;

    {
        bankImageLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url != null)
                    Glide.with(bank.getContext()).load(url).into(bank);
                else
                    Glide.with(bank.getContext()).load("http://via.placeholder" +
                            ".com/200x200/f0f0f0/2c2c2c?text=" + dataSnapshot.getKey()).into(bank);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "bank image loading cancelled");
            }
        };
    }

    public static DialogFragmentViewCard newInstance(CCard cCard, String familyId) {
        DialogFragmentViewCard dialogFragmentAddAccount = new DialogFragmentViewCard();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD, cCard);
        args.putString(ARG_FAMILY_ID, familyId);
        dialogFragmentAddAccount.setArguments(args);
        return dialogFragmentAddAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            cCard = bundle.getParcelable(ARG_CARD);
            familyId = bundle.getString(ARG_FAMILY_ID);
        }
        cardRef = FirebaseDatabase.getInstance().getReference("ccards").child(familyId).child(cCard
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

        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_view_ccard,
                null);
        ButterKnife.bind(this, rootView);
        if (cCard != null) {
            FirebaseDatabase.getInstance().getReference("images").child("banks").child(cCard
                    .getBank().toUpperCase()).addListenerForSingleValueEvent(bankImageLinkListener);
            cardHolder.setText(getDefaultNotSet(cCard.getCardholder()));
            cardNumber.setText(cCard.getFormattedNumber('-'));
            consumedLimit.setText(NumberFormat.getCurrencyInstance().format(cCard
                    .getConsumedLimit()));
            remainingLimit.setText(NumberFormat.getCurrencyInstance().format(cCard
                    .getRemainingLimit()));
            limit.setMax((int) cCard.getMaxLimit());
            limit.setProgress((int) cCard.getConsumedLimit());
            billingCycle.setText(Util.getBillingCycleString(cCard.getBillingDay(), cCard
                    .getBillingDay(), "%s - %s"));
            expiresOn.setText(getDefaultNotSet(EXPIRE_ON.format(new Date(cCard.getExpireOn()))));
            cvv.setText(getDefaultNotSet(cCard.getCvv()));
            userid.setText(getDefaultNotSet(cCard.getUserid()));
            password.setText(getDefaultNotSet(cCard.getPassword()));
            phoneNumber.setText(getDefaultNotSet(cCard.getPhoneNumber()));
            Glide.with(getContext()).load(cCard.getUpdatedBy().getProfilePic()).apply(RequestOptions
                    .circleCropTransform()).into(updateBy);
            updatedOn.setText(DateUtils.getRelativeTimeSpanString(getContext(), cCard
                    .getUpdatedOn(), true));
            if (cCard.getAddonCards().size() > 0) {
                addonTitle.setVisibility(View.VISIBLE);
                addonCards.setLayoutManager(new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false));
                AddonCardListAdapter addonCardListAdapter = new AddonCardListAdapter(null);
                addonCards.setAdapter(addonCardListAdapter);
                addonCardListAdapter.setItems(cCard.getAddonCards());
            } else {
                addonTitle.setVisibility(View.GONE);
            }
        }
        limit.setOnSeekBarChangeListener(this);

        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_CARD, cCard);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int whichButton) {
        switch (whichButton) {
            case DialogInterface.BUTTON_NEGATIVE:
                //copy
                Util.copyToClipboard(getActivity().getApplicationContext(), (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE), cCard
                        .getReadableContent());
                Toast.makeText(getActivity().getApplicationContext(), "Card details copied to " +
                        "clipboard!", Toast.LENGTH_SHORT).show();
                break;
            case DialogInterface.BUTTON_POSITIVE:
                //share
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, cCard.getReadableContent());
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string
                        .action_share)));
                break;
            case DialogInterface.BUTTON_NEUTRAL:
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int newValue, boolean b) {
        Util.Log.d(TAG, "%d %d %s", seekBar.getProgress(), newValue, String.valueOf(b));
        //skip initial trigger
        if (newValue != cCard.getConsumedLimit()) {
            consumedLimit.setText(NumberFormat.getCurrencyInstance().format(newValue));
            remainingLimit.setText(NumberFormat.getCurrencyInstance().format(cCard.getMaxLimit() -
                    newValue));
            updateMap.put("consumedLimit", newValue);
            FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
            if (fbUser != null)
            updateMap.put("updatedByMemberId", fbUser.getUid());
            updateMap.put("updatedOn", Calendar.getInstance().getTimeInMillis());
            cardRef.updateChildren(updateMap);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private String getDefaultNotSet(String value) {
        if (value == null || value.isEmpty()) {
            return "Not Set";
        }
        return value;
    }
}
