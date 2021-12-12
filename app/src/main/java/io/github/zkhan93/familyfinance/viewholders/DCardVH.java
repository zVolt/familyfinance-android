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

    private DCard dCard;
    private final Context context;
    private ItemInteractionListener itemInteractionListener;
    private final ValueEventListener bankImageLinkListener;
    private final ValueEventListener bankNameListener;
    private final ValueEventListener cardTypeImageLinkListener;

    {
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
    }

    public void setItem(DCard dCard) {
        String cardType = Util.getCardBrand(dCard.getNumber());
        Log.d(TAG, String.format("card type: %s", cardType));
        if (cardType != null) {
            FirebaseDatabase.getInstance().getReference("images")
                    .child("card_types")
                    .child(cardType.toUpperCase())
                    .addListenerForSingleValueEvent(cardTypeImageLinkListener);
        }
        FirebaseDatabase.getInstance().getReference("images")
                .child("banks")
                .child(dCard.getBank().toUpperCase())
                .addListenerForSingleValueEvent(bankImageLinkListener);
        FirebaseDatabase.getInstance().getReference("banks")
                .child(dCard.getBank().toUpperCase())
                .addListenerForSingleValueEvent(bankNameListener);
        this.dCard = dCard;
        Log.d(TAG, dCard.toString());
        number.setText(dCard.getFormattedNumber(' ', true));
        cardholder.setText(dCard.getCardholder());
        expiresOn.setText(DCard.EXPIRE_ON.format(new Date(dCard.getExpireOn())));
        cvv.setText(dCard.getCvv());
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                itemInteractionListener.delete(dCard);
                return true;
            case R.id.action_edit:
                itemInteractionListener.edit(dCard);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        itemInteractionListener.view(dCard);
    }

    @Override
    public boolean onLongClick(View view) {
        if (itemInteractionListener != null) {
            itemInteractionListener.edit(dCard);
            return true;
        }
        return false;
    }
}
