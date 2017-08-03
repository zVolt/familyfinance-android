package io.github.zkhan93.familyfinance.viewholders;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;

/**
 * Created by zeeshan on 28/7/17.
 */

public class AddonCardVH extends RecyclerView.ViewHolder implements PopupMenu
        .OnMenuItemClickListener {

    public static String TAG = AddonCard.class.getSimpleName();

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.card_number)
    TextView number;
    @BindView(R.id.mob_number)
    TextView mobNumber;
    @BindView(R.id.expires_on)
    TextView expiresOn;
    @BindView(R.id.cvv)
    TextView cvv;
    @BindView(R.id.updated_by)
    TextView updatedBy;
    @BindView(R.id.updated_on)
    TextView updatedOn;
    @BindView(R.id.menu)
    ImageButton menu;

    private PopupMenu popup;
    private AddonCard addonCard;
    private ItemInteractionListener itemInteractionListener;

    public AddonCardVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.itemInteractionListener = itemInteractionListener;
        popup = new PopupMenu(itemView.getContext(), menu);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.ccard_item_menu, popup.getMenu());
        popup.getMenu().findItem(R.id.action_add_addoncard).setVisible(false);
    }

    @OnClick(R.id.menu)
    void onClick(View view) {
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                itemInteractionListener.delete(addonCard);
                return true;
            case R.id.action_edit:
                itemInteractionListener.edit(addonCard);
                return true;
            case R.id.action_share:
                itemInteractionListener.share(addonCard);
                return true;
            default:
                return false;
        }

    }

    public void setAddonCard(AddonCard addonCard) {
        this.addonCard = addonCard;
        name.setText(addonCard.getName());
        number.setText(addonCard.getNumber());
        mobNumber.setText(addonCard.getPhoneNumber());
        expiresOn.setText(CCard.EXPIRE_ON.format(new Date(addonCard.getExpiresOn())));
        cvv.setText(String.valueOf(addonCard.getCvv()));
        updatedBy.setText(addonCard.getUpdatedBy().getName());
        updatedOn.setText(DateUtils.getRelativeTimeSpanString(addonCard.getUpdatedOn()));
    }

    public interface ItemInteractionListener {
        void delete(AddonCard addonCard);

        void edit(AddonCard addonCard);

        void share(AddonCard addonCard);
    }
}