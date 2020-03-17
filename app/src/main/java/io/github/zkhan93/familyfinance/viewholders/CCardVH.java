package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CCard;

/**
 * Created by zeeshan on 7/7/17.
 */

public class CCardVH extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    public static final String TAG = CCardVH.class.getSimpleName();

    @BindView(R.id.card_type)
    ImageView cardType;

    @BindView(R.id.bank_name)
    TextView bankName;

    @BindView(R.id.cardholder)
    TextView cardholder;

    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.expires_on)
    TextView expiresOn;
    @BindView(R.id.tv_expires_on)
    TextView tvExpiresOn;

    @BindView(R.id.cvv)
    TextView cvv;
    @BindView(R.id.tv_cvv)
    TextView tvCvv;
    @BindView(R.id.container)
    ConstraintLayout container;


    private Context context;

    private ItemInteractionListener itemInteractionListener;
    private CCard cCard;
    private MyValueEventListener bankImageLinkListener, cardTypeImageLinkListener, bankNameListener;

    {
        bankImageLinkListener = new MyValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url == null)
                    url = String.format("https://via.placeholder" +
                            ".com/200x200/f0f0f0/2c2c2c?text=%s", dataSnapshot.getKey());
                Glide.with(context)
                        .asBitmap()
                        .load(url)
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_bank_grey_600_18dp))
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                createPaletteAsync(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {

                            }
                        });
            }
        };
        cardTypeImageLinkListener = new MyValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url == null)
                    cardType.setVisibility(View.GONE);
                else {
                    cardType.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(url)
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_bank_grey_600_18dp))
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

    public CCardVH(View itemView, @NonNull ItemInteractionListener itemInteractionListener) {
        super(itemView);
        this.itemInteractionListener = itemInteractionListener;
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
                Palette.Swatch swatch = p.getVibrantSwatch();
                if (swatch != null) {
                    Log.d(TAG, "swatch is NOT NULL");
                    container.setBackgroundColor(swatch.getRgb());
                }
            }
        });
    }

    public void setCCard(CCard cCard) {
        this.cCard = cCard;
        number.setText(cCard.getFormattedNumber(' ', true));
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

        String type = cCard.getType();
        if (type != null)
            FirebaseDatabase.getInstance().getReference("images")
                    .child("card_types")
                    .child(type)
                    .addListenerForSingleValueEvent(cardTypeImageLinkListener);

        if (cCard.getExpireOn() == -1) {
            expiresOn.setVisibility(View.GONE);
            tvExpiresOn.setVisibility(View.GONE);
        } else {
            expiresOn.setVisibility(View.VISIBLE);
            tvExpiresOn.setVisibility(View.VISIBLE);
            expiresOn.setText(CCard.EXPIRE_ON.format(new Date(cCard.getExpireOn())));
        }

        if (cCard.getCvv() == null || cCard.getCvv().equals("")) {
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
        itemInteractionListener.onView(cCard);
    }

    @Override
    public boolean onLongClick(View view) {
        itemInteractionListener.onLongPress(cCard);
        return true;
    }

    public interface ItemInteractionListener {

        @Deprecated
        void delete(CCard cCard);

        @Deprecated
        void edit(CCard cCard);

        @Deprecated
        void addAddonCard(CCard cCard);

        void onView(CCard cCard);

        void onLongPress(CCard cCard);

    }

    public abstract class MyValueEventListener implements ValueEventListener {

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.d(TAG, "loading cancelled");
        }
    }
}
