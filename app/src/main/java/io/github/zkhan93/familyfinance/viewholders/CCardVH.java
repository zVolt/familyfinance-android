package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Constants;

/**
 * Created by zeeshan on 7/7/17.
 */

public class CCardVH extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.payment_date)
    TextView paymentDate;
    @BindView(R.id.cardholder)
    TextView cardholder;
    @BindView(R.id.bank)
    TextView bank;
    @BindView(R.id.limit)
    ProgressBar limit;
    @BindView(R.id.remaining_limit)
    TextView remainingLimit;
    @BindView(R.id.updated_by)
    TextView updatedBy;
    @BindView(R.id.updated_on)
    TextView updatedOn;
    @BindView(R.id.menu)
    ImageButton menu;
    @BindView(R.id.max_limit)
    TextView maxLimit;

    private Resources resources;
    private Context context;
    private PopupMenu popup;
    private ItemInteractionListener itemInteractionListener;
    private CCard cCard;

    public CCardVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        this.itemInteractionListener = itemInteractionListener;
        resources = itemView.getResources();
        context = itemView.getContext();
        ButterKnife.bind(this, itemView);
        limit.setIndeterminate(false);

        popup = new PopupMenu(itemView.getContext(), menu);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.ccard_item_menu, popup.getMenu());
    }

    public void setCCard(CCard cCard) {
        this.cCard = cCard;
        name.setText(cCard.getName());
        number.setText(cCard.getNumber());

        /*
        calculate next payment day ie., suppose 15th is the payment day so if current day is less
         than of equal to 15 then next payment date is 15th of current month and if current day
         is greater than 15th then next payment date is 15th of next month.
        **/
        Calendar nextPaymentDate = Calendar.getInstance();
        int currentDayOfMonth = nextPaymentDate.get(Calendar.DAY_OF_MONTH);
        nextPaymentDate.set(Calendar.DAY_OF_MONTH, cCard.getPaymentDay());
        if (currentDayOfMonth > nextPaymentDate.get(Calendar.DAY_OF_MONTH))
            nextPaymentDate.add(Calendar.MONTH, 1);

        paymentDate.setText(Constants.PAYMENT_DATE.format(nextPaymentDate.getTime()));

        cardholder.setText(cCard.getCardholder());
        bank.setText(cCard.getBank());

        limit.setMax((int) cCard.getMaxLimit());
        limit.setProgress((int) cCard.getConsumedLimit());
        //set progress color
        if (cCard.getRemainingLimit() <= cCard.getMaxLimit() * 0.25f)
            limit.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.md_red_500)));
        else if (cCard.getRemainingLimit() <= cCard.getMaxLimit() * 0.5f)
            limit.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R
                    .color.md_orange_500)));
        else
            limit.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R
                    .color.md_green_500)));

        maxLimit.setText(NumberFormat.getCurrencyInstance().format(cCard.getMaxLimit()));
        remainingLimit.setText(NumberFormat.getCurrencyInstance().format(cCard.getMaxLimit() - cCard
                .getConsumedLimit()));

        updatedBy.setText(cCard.getUpdatedBy().getName());
        updatedOn.setText(Constants.DATE_FORMAT.format(cCard.getUpdatedOn()));
    }

    @OnClick(R.id.menu)
    void onClick(View button) {
        popup.show();
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
            case R.id.action_share:
                itemInteractionListener.share(cCard);
                return true;
            default:
                return false;
        }
    }

    public interface ItemInteractionListener {
        void copy(CCard cCard);

        void delete(CCard cCard);

        void share(CCard cCard);

        void edit(CCard cCard);

    }
}
