package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.WeakReference;
import java.util.Date;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.ItemInteractionListener;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 7/7/17.
 */

public class CCardVH extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnLongClickListener {

    public static final String TAG = CCardVH.class.getSimpleName();

    ImageView bankLogo;
    ImageView cardType;
    TextView bankName;
    TextView cardholder;
    TextView number;
    TextView expiresOn;
    TextView tvExpiresOn;
    TextView cvv;
    TextView tvCvv;
    ConstraintLayout container;

    private Context context;

    private WeakReference<ItemInteractionListener<CCard>> itemInteractionListenerRef;
    private CCard cCard;
    private MyValueEventListener bankImageLinkListener, cardTypeImageLinkListener, bankNameListener;

    {
        bankImageLinkListener = new MyValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url == null)
                    url = String.format("https://via.placeholder" +
                            ".com/200x200/f0f0f0/2c2c2c?text=%s", dataSnapshot.getKey());
//                Glide.with(context)
//                        .load(url)
//                        .apply(RequestOptions.placeholderOf(R.drawable.ic_bank_grey_600_18dp))
//                        .into(bankLogo);
                Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_bank_grey_600_18dp))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource,
                                                        @Nullable Transition<? super Bitmap> transition) {
                                createPaletteAsync(resource);
                                bankLogo.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        };
        cardTypeImageLinkListener = new MyValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url != null) {
                    Glide.with(context)
                            .load(url)
                            .into(cardType);
                }
            }
        };
        bankNameListener = new MyValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                bankName.setText(name);
            }
        };
    }

    public CCardVH(View itemView, @NonNull ItemInteractionListener<CCard> itemInteractionListener) {
        super(itemView);
        this.itemInteractionListenerRef = new WeakReference<>(itemInteractionListener);
        context = itemView.getContext();

        bankLogo = itemView.findViewById(R.id.bank_icon);
        cardType = itemView.findViewById(R.id.card_type);
        bankName = itemView.findViewById(R.id.bank_name);
        cardholder = itemView.findViewById(R.id.cardholder);
        number = itemView.findViewById(R.id.number);
        expiresOn = itemView.findViewById(R.id.expires_on);
        tvExpiresOn = itemView.findViewById(R.id.tv_expires_on);
        cvv = itemView.findViewById(R.id.cvv);
        tvCvv = itemView.findViewById(R.id.tv_cvv);
        container = itemView.findViewById(R.id.container);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    private void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(p -> {
            // Use generated instance
            if (p != null) {
                Palette.Swatch swatch = p.getVibrantSwatch();
                if (swatch != null) {
                    container.setBackgroundColor(Util.manipulateColor(swatch.getRgb(), 0.5f));
                }
            }
        });
    }

    public void setCCard(CCard cCard) {
        setCCard(cCard, true);
    }

    public void setCCard(CCard cCard, boolean secure) {
        this.cCard = cCard;

        number.setText(cCard.getFormattedNumber(' ', secure));
        bankName.setText(cCard.getBank());

        if (cCard.getCardholder() == null || cCard.getCardholder().equals("")) {
            cardholder.setText("UNKNOWN");
        } else {
            cardholder.setText(cCard.getCardholder().toUpperCase());
        }

        FirebaseDatabase.getInstance().getReference("images")
                .child("banks")
                .child(cCard.getBank().toUpperCase())
                .addListenerForSingleValueEvent(bankImageLinkListener);

        FirebaseDatabase.getInstance().getReference("banks")
                .child(cCard.getBank().toUpperCase())
                .addListenerForSingleValueEvent(bankNameListener);

        String cardType = cCard.getType();
        if (cardType == null)
            cardType = Util.getCardBrand(cCard.getNumber());
        if (cardType != null)
            FirebaseDatabase.getInstance().getReference("images")
                    .child("card_types")
                    .child(cardType.toUpperCase())
                    .addListenerForSingleValueEvent(cardTypeImageLinkListener);

        if (cCard.getExpireOn() == -1) {
            expiresOn.setVisibility(View.GONE);
            tvExpiresOn.setVisibility(View.GONE);
        } else {
            expiresOn.setVisibility(View.VISIBLE);
            tvExpiresOn.setVisibility(View.VISIBLE);
            expiresOn.setText(CCard.EXPIRE_ON.format(new Date(cCard.getExpireOn())));
        }
        if (cCard.getCvv() == null || cCard.getCvv().isEmpty()) {
            cvv.setVisibility(View.GONE);
            tvCvv.setVisibility(View.GONE);
        } else {
            cvv.setVisibility(View.VISIBLE);
            tvCvv.setVisibility(View.VISIBLE);
            cvv.setText(cCard.getCvv());
        }
    }


    @Override
    public void onClick(View view) {
        ItemInteractionListener<CCard> itemInteractionListener = itemInteractionListenerRef.get();
        if (itemInteractionListener != null)
            itemInteractionListener.view(cCard);
    }

    @Override
    public boolean onLongClick(View view) {
        ItemInteractionListener<CCard> itemInteractionListener = itemInteractionListenerRef.get();
        if (itemInteractionListener != null)
            itemInteractionListener.edit(cCard);
        return true;
    }

    public abstract static class MyValueEventListener implements ValueEventListener {
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "loading cancelled");
        }
    }
}
