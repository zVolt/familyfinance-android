package io.github.zkhan93.familyfinance.viewholders;

import static io.github.zkhan93.familyfinance.LoginActivity.TAG;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.util.ItemInteractionListener;
import io.github.zkhan93.familyfinance.util.Util;


public class DCardVH extends BaseVH<DCard> implements PopupMenu
        .OnMenuItemClickListener, View.OnClickListener, View.OnLongClickListener {

    ImageView bank;
    ImageView cardType;
    TextView bankName;
    TextView number;
    TextView cardholder;
    TextView expiresOn;
    TextView cvv;

    private final Context context;
    private ValueEventListener bankImageLinkListener;
    private ValueEventListener bankNameListener;
    private ValueEventListener cardTypeImageLinkListener;

    private void init() {
        bankImageLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url == null)
                    url = String.format("https://via.placeholder" +
                            ".com/200x200/f0f0f0/2c2c2c?text=%s", dataSnapshot.getKey());
                Glide.with(context)
                        .load(url)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_bank_grey_600_18dp))
                        .into(bank);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "bank image loading cancelled");
            }
        };
        bankNameListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                bankName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "bank name fetch cancelled");
            }
        };
        cardTypeImageLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Log.d(TAG, String.format("cardType url: %s", url));
                if (url == null) {
                    cardType.setVisibility(View.GONE);
                } else {
                    cardType.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(url)
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_bank_grey_600_18dp))
                            .into(cardType);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "card type fetch cancelled");
            }
        };
    }

    public DCardVH(View itemView, @NonNull ItemInteractionListener
            itemInteractionListener) {
        super(itemView, itemInteractionListener);
        this.context = itemView.getContext();
        bank = itemView.findViewById(R.id.bank_icon);
        cardType = itemView.findViewById(R.id.card_type);
        bankName = itemView.findViewById(R.id.bank_name);
        number = itemView.findViewById(R.id.number);
        cardholder = itemView.findViewById(R.id.cardholder);
        expiresOn = itemView.findViewById(R.id.expires_on);
        cvv = itemView.findViewById(R.id.cvv);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        this.init();
    }

    public void updateView() {
        String cardType = Util.getCardBrand(item.getNumber());
        Log.d(TAG, String.format("card type: %s", cardType));
        if (cardType != null) {
            FirebaseDatabase.getInstance().getReference("images")
                    .child("card_types")
                    .child(cardType.toUpperCase())
                    .addListenerForSingleValueEvent(cardTypeImageLinkListener);
        }
        FirebaseDatabase.getInstance().getReference("images")
                .child("banks")
                .child(item.getBank().toUpperCase())
                .addListenerForSingleValueEvent(bankImageLinkListener);
        FirebaseDatabase.getInstance().getReference("banks")
                .child(item.getBank().toUpperCase())
                .addListenerForSingleValueEvent(bankNameListener);

        Log.d(TAG, item.toString());
        number.setText(item.getFormattedNumber(' ', true));
        cardholder.setText(item.getCardholder());
        expiresOn.setText(DCard.EXPIRE_ON.format(new Date(item.getExpireOn())));
        cvv.setText(item.getCvv());
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        ItemInteractionListener<DCard> listener = itemInteractionListenerRef.get();
        if (listener == null) return false;
        if (menuItem.getItemId() == R.id.action_delete) {
            listener.delete(item);
            return true;
        } else if (menuItem.getItemId() == R.id.action_edit) {
            listener.edit(item);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        ItemInteractionListener<DCard> listener = itemInteractionListenerRef.get();
        if (listener == null) return;
        listener.view(item);
    }

    @Override
    public boolean onLongClick(View view) {
        ItemInteractionListener<DCard> listener = itemInteractionListenerRef.get();
        if (listener == null) return false;
        listener.edit(item);
        return true;
    }
}
