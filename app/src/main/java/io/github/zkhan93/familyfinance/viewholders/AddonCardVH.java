package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.Date;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.Member;

/**
 * Created by zeeshan on 28/7/17.
 */

public class AddonCardVH extends RecyclerView.ViewHolder implements PopupMenu
        .OnMenuItemClickListener, View.OnLongClickListener {

    public static String TAG = AddonCard.class.getSimpleName();

    TextView name;
    TextView number;
    TextView mobNumber;
    TextView expiresOn;
    TextView cvv;
    ImageView updatedBy;
    TextView updatedOn;
    ImageButton menu;

    private PopupMenu popup;
    private AddonCard addonCard;
    private final ItemInteractionListener itemInteractionListener;
    private final Context context;

    public AddonCardVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        context = itemView.getContext();
        name = itemView.findViewById(R.id.name);
        number = itemView.findViewById(R.id.card_number);
        mobNumber = itemView.findViewById(R.id.phone_number);
        expiresOn = itemView.findViewById(R.id.expires_on);
        cvv = itemView.findViewById(R.id.cvv);
        updatedBy = itemView.findViewById(R.id.updated_by);
        updatedOn = itemView.findViewById(R.id.updated_on);
        menu = itemView.findViewById(R.id.menu);
        itemView.setOnLongClickListener(this);
        itemView.findViewById(R.id.menu).setOnClickListener(view -> popup.show());
        this.itemInteractionListener = itemInteractionListener;
        popup = new PopupMenu(itemView.getContext(), menu);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.addon_card, popup.getMenu());
        updatedBy.setVisibility(View.GONE);
        updatedOn.setVisibility(View.GONE);

    }

    @Override
    public boolean onLongClick(View view) {
        if (itemInteractionListener != null) {
            itemInteractionListener.onLongPress(addonCard);
            return true;
        }
        return false;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (itemInteractionListener != null)
                    itemInteractionListener.delete(addonCard);
                return true;
            case R.id.action_edit:
                if (itemInteractionListener != null)
                    itemInteractionListener.edit(addonCard);
                return true;
            case R.id.action_share:
                if (itemInteractionListener != null)
                    itemInteractionListener.share(addonCard);
                return true;
            default:
                return false;
        }

    }

    public void setAddonCard(AddonCard addonCard) {
        this.addonCard = addonCard;
        name.setText(addonCard.getName());
        number.setText(addonCard.getFormattedNumber(' '));
        mobNumber.setText(addonCard.getPhoneNumber());
        expiresOn.setText(CCard.EXPIRE_ON.format(new Date(addonCard.getExpiresOn())));
        cvv.setText(String.valueOf(addonCard.getCvv()));
        Member _updatedBy = addonCard.getUpdatedBy();

        if (_updatedBy != null && _updatedBy.getProfilePic() != null && !_updatedBy.getProfilePic
                ().isEmpty())
            Glide.with(context)
                    .load(_updatedBy.getProfilePic())
                    .apply(RequestOptions
                            .circleCropTransform()
                            .placeholder(R.drawable.ic_person_grey_600_24dp))
                    .into(updatedBy);

        updatedOn.setText(DateUtils.getRelativeTimeSpanString(addonCard.getUpdatedOn()));
    }

    public interface ItemInteractionListener {
        void delete(AddonCard addonCard);

        void edit(AddonCard addonCard);

        void share(AddonCard addonCard);

        void onLongPress(AddonCard addonCard);
    }
}
