package io.github.zkhan93.familyfinance;

import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.adapters.AddonCardListAdapter;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.AddonCardVH;

import static io.github.zkhan93.familyfinance.models.CCard.EXPIRE_ON;

/**
 * Created by zeeshan on 11/11/17.
 */

public class DialogFragmentViewCard extends DialogFragment implements DialogInterface
        .OnClickListener, SeekBar.OnSeekBarChangeListener, AddonCardVH.ItemInteractionListener {
    public static final String TAG = DialogFragmentViewCard.class.getSimpleName();
    public static final String ARG_CARD = "card";
    public static final String ARG_FAMILY_ID = "familyId";

    private CCard cCard;
    private String familyId;
    private DatabaseReference cardRef;
    private Map<String, Object> updateMap;
    private final ValueEventListener bankImageLinkListener;

    ImageView bank;
    TextView cardHolder;
    TextView cardNumber;
    TextView expiresOn;
    TextView cvv;
    TextView billingCycle;
    TextView phoneNumber;
    SeekBar limit;
    TextView cardLimit;
    ImageView updateBy;
    TextView updatedOn;
    TextView userid;
    TextView password;
    RecyclerView cards;

    public DialogFragmentViewCard() {
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

        bank = rootView.findViewById(R.id.bank);
        cardHolder = rootView.findViewById(R.id.card_holder);
        cardNumber = rootView.findViewById(R.id.card_number);
        expiresOn = rootView.findViewById(R.id.expires_on);
        cvv = rootView.findViewById(R.id.cvv);
        billingCycle = rootView.findViewById(R.id.billing_cycle);
        phoneNumber = rootView.findViewById(R.id.phone_number);
        limit = rootView.findViewById(R.id.limit);
        cardLimit = rootView.findViewById(R.id.card_limit);
        updateBy = rootView.findViewById(R.id.updated_by);
        updatedOn = rootView.findViewById(R.id.updated_on);
        userid = rootView.findViewById(R.id.userid);
        password = rootView.findViewById(R.id.password);
        cards = rootView.findViewById(R.id.cards);

        if (cCard != null) {
            FirebaseDatabase.getInstance().getReference("images").child("banks").child(cCard
                    .getBank().toUpperCase()).addListenerForSingleValueEvent(bankImageLinkListener);
            cardHolder.setText(getDefaultNotSet(cCard.getCardholder()));
            cardNumber.setText(cCard.getFormattedNumber(' '));
            cardLimit.setText(NumberFormat.getCurrencyInstance().format(cCard
                    .getMaxLimit()));
            limit.setMax((int) cCard.getMaxLimit());
            limit.setProgress((int) cCard.getConsumedLimit());
            billingCycle.setText(Util.getBillingCycleString(cCard.getBillingDay(), cCard
                    .getPaymentDay(), "%s - %s"));
            expiresOn.setText(getDefaultNotSet(EXPIRE_ON.format(new Date(cCard.getExpireOn()))));
            cvv.setText(getDefaultNotSet(cCard.getCvv()));
            userid.setText(getDefaultNotSet(cCard.getUserid()));
            password.setText(getDefaultNotSet(cCard.getPassword()));
            phoneNumber.setText(getDefaultNotSet(cCard.getPhoneNumber()));
            Glide.with(getContext()).load(cCard.getUpdatedBy().getProfilePic()).apply(RequestOptions
                    .circleCropTransform()).into(updateBy);
            updatedOn.setText(DateUtils.getRelativeTimeSpanString(getContext(), cCard
                    .getUpdatedOn(), true));

            cards.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            AddonCardListAdapter addonCardListAdapter = new AddonCardListAdapter(null, cards);
            cards.setAdapter(addonCardListAdapter);
//            addonCardListAdapter.setItems(cCard.getAddonCards());
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
    public void onResume() {
        super.onResume();
        cCard.refresh();

        cards.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        AddonCardListAdapter addonCardListAdapter = new AddonCardListAdapter(null, cards);
        cards.setAdapter(addonCardListAdapter);
//        addonCardListAdapter.setItems(cCard.getAddonCards());

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
        //skip initial trigger

        cardLimit.setText(NumberFormat.getCurrencyInstance().format(cCard.getMaxLimit()));
        updateMap.put("consumedLimit", newValue);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null)
            updateMap.put("updatedByMemberId", fbUser.getUid());
        updateMap.put("updatedOn", Calendar.getInstance().getTimeInMillis());
        cardRef.updateChildren(updateMap);

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

    @Override
    public void delete(AddonCard addonCard) {
        String title = "You want to delete Addon Card " + addonCard.getNumber();
        DialogFragmentConfirm<AddonCard> dialogFragmentConfirm = new DialogFragmentConfirm<>();
        Bundle bundle = new Bundle();
        bundle.putString(DialogFragmentConfirm.ARG_TITLE, title);
        bundle.putParcelable(DialogFragmentConfirm.ARG_ITEM, addonCard);
        dialogFragmentConfirm.setArguments(bundle);
        dialogFragmentConfirm.show(getActivity().getSupportFragmentManager(),
                DialogFragmentConfirm.TAG);
    }

    @Override
    public void edit(AddonCard addonCard) {
        Log.d(TAG, "edit addon");
        DialogFragmentAddonCard.newInstance(familyId, addonCard.getMainCardNumber(), addonCard)
                .show(getFragmentManager(),
                        DialogFragmentAddonCard.TAG);
    }

    @Override
    public void share(AddonCard addonCard) {
        Log.d(TAG, "share addon");
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, addonCard.getReadableContent());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string
                .action_share)));
    }

    @Override
    public void onLongPress(AddonCard addonCard) {
        Util.quickCopy(getActivity().getApplicationContext(), addonCard);
    }
}
