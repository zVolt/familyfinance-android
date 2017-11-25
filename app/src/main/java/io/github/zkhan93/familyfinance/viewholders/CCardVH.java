package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.adapters.AddonCardListAdapter;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 7/7/17.
 */

public class CCardVH extends RecyclerView.ViewHolder implements PopupMenu
        .OnMenuItemClickListener, View.OnClickListener {

    public static final String TAG = CCardVH.class.getSimpleName();

    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.date)
    TextView date;
    @BindView(R.id.cardholder)
    TextView cardholder;
    @BindView(R.id.bank)
    ImageView bank;
    @BindView(R.id.limit)
    ProgressBar limit;
    @BindView(R.id.remaining_limit)
    TextView remainingLimit;
    @BindView(R.id.updated_by)
    ImageView updatedBy;
    @BindView(R.id.updated_on)
    TextView updatedOn;
    @BindView(R.id.menu)
    ImageButton menu;
    @BindView(R.id.consumed_limit)
    TextView consumedLimit;
    @BindView(R.id.expires_on)
    TextView expiresOn;
    @BindView(R.id.addon_cards)
    RecyclerView addonCards;
    @BindView(R.id.addons_title)
    TextView addonTitle;


    private Context context;
    private PopupMenu popup;
    private ItemInteractionListener itemInteractionListener;
    private CCard cCard;
    private AddonCardListAdapter addonCardListAdapter;
    private ValueEventListener bankImageLinkListener;

    {
        bankImageLinkListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url == null)
                    url = String.format("http://via.placeholder" +
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
    }

    public CCardVH(View itemView, ItemInteractionListener itemInteractionListener, AddonCardVH
            .ItemInteractionListener addonCardInteractionListener) {
        super(itemView);
        this.itemInteractionListener = itemInteractionListener;
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
        limit.setIndeterminate(false);

        popup = new PopupMenu(itemView.getContext(), menu);
        itemView.setOnClickListener(this);
        menu.setOnClickListener(this);
        addonTitle.setOnClickListener(this);

        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.ccard_item, popup.getMenu());

        addonCardListAdapter = new AddonCardListAdapter(addonCardInteractionListener);
        addonCards.setAdapter(addonCardListAdapter);
        addonCards.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager
                .HORIZONTAL, true));
    }


    public void setCCard(CCard cCard) {
        this.cCard = cCard;

        number.setText(cCard.getFormattedNumber('-', true));

        date.setText(Util.getBillingCycleString(cCard.getBillingDay(),
                cCard.getPaymentDay(), "%s - %s"));

        cardholder.setText(cCard.getCardholder());
        FirebaseDatabase.getInstance().getReference("images")
                .child("banks")
                .child(cCard.getBank().toUpperCase())
                .addListenerForSingleValueEvent(bankImageLinkListener);

        limit.setMax((int) cCard.getMaxLimit());
        limit.setProgress((int) cCard.getConsumedLimit());
        //set progress color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (cCard.getRemainingLimit() <= cCard.getMaxLimit() * 0.25f)
                limit.setProgressTintList(ColorStateList
                        .valueOf(ContextCompat.getColor(context, R.color.md_red_500)));
            else if (cCard.getRemainingLimit() <= cCard.getMaxLimit() * 0.5f)
                limit.setProgressTintList(ColorStateList
                        .valueOf(ContextCompat.getColor(context, R.color.md_orange_500)));
            else
                limit.setProgressTintList(ColorStateList
                        .valueOf(ContextCompat.getColor(context, R.color.md_green_500)));
        } else {
            int color;
            if (cCard.getRemainingLimit() <= cCard.getMaxLimit() * 0.25f)
                color = ContextCompat.getColor(context, R.color.md_red_500);
            else if (cCard.getRemainingLimit() <= cCard.getMaxLimit() * 0.5f)
                color = ContextCompat.getColor(context, R.color.md_orange_500);
            else
                color = ContextCompat.getColor(context, R.color.md_green_500);
            limit.getProgressDrawable()
                    .setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        }
        consumedLimit.setText(NumberFormat.getCurrencyInstance()
                .format(cCard.getConsumedLimit()));
        remainingLimit.setText(NumberFormat.getCurrencyInstance()
                .format(cCard.getRemainingLimit()));

        Member _updatedBy = cCard.getUpdatedBy();

        if (_updatedBy != null &&
                _updatedBy.getProfilePic() != null &&
                !_updatedBy.getProfilePic().isEmpty())
            Glide.with(context)
                    .load(_updatedBy.getProfilePic())
                    .apply(RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_person_grey_600_24dp))
                    .into(updatedBy);

        if (cCard.getAddonCards() != null && cCard.getAddonCards().size() > 0) {
            addonCardListAdapter.setItems(cCard.getAddonCards());
            addonTitle.setVisibility(View.VISIBLE);
            addonTitle.setText(String
                    .format(Locale.ENGLISH, "%d Addon Cards", cCard.getAddonCards().size()));
        } else {
            addonTitle.setVisibility(View.GONE);
        }
        updatedOn.setText(DateUtils.getRelativeTimeSpanString(context, cCard.getUpdatedOn(), true));
        //Constants.DATE_FORMAT.format(cCard.getUpdatedOn())

        expiresOn.setText(CCard.EXPIRE_ON.format(new Date(cCard.getExpireOn())));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu:
                popup.show();
                break;
            case R.id.addons_title:
                addonCards.setVisibility(
                        addonCards.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
                );
                break;
            default:
                itemInteractionListener.onView(cCard);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy:
                itemInteractionListener.copy(cCard);
                return true;
            case R.id.action_delete:
                itemInteractionListener.delete(cCard);
                return true;
            case R.id.action_edit:
                itemInteractionListener.edit(cCard);
                return true;
            case R.id.action_add_addoncard:
                itemInteractionListener.addAddonCard(cCard);
            default:
                return false;
        }
    }

    public interface ItemInteractionListener {
        void copy(CCard cCard);

        void delete(CCard cCard);

        void share(CCard cCard);

        void edit(CCard cCard);

        void addAddonCard(CCard cCard);

        void onView(CCard cCard);

    }
}
