package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.BuildConfig;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Constants;

/**
 * Created by zeeshan on 7/7/17.
 */

public class CCardVH extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

    public static final String TAG = CCardVH.class.getSimpleName();

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.number)
    TextView number;
    @BindView(R.id.date)
    TextView date;
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
    @BindView(R.id.expires_on)
    TextView expiresOn;

    private Context context;
    private PopupMenu popup;
    private ItemInteractionListener itemInteractionListener;
    private CCard cCard;

    public CCardVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        this.itemInteractionListener = itemInteractionListener;
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
        if (cCard.getName() == null || cCard.getName().trim().length() == 0)
            name.setVisibility(View.GONE);
        else
            name.setText(cCard.getName());
        NumberFormat cardNumberFormat = new DecimalFormat("");
        number.setText(cCard.getFormattedNumber(' '));

        /*
        calculate next payment day ie., suppose 15th is the payment day so if current day is less
         than of equal to 15 then next payment date is 15th of current month and if current day
         is greater than 15th then next payment date is 15th of next month.
        **/
        Calendar today = Calendar.getInstance();

        Calendar paymentDate = Calendar.getInstance();
        paymentDate.set(Calendar.DAY_OF_MONTH, cCard.getPaymentDay());

        Calendar billingDate = Calendar.getInstance();
        billingDate.set(Calendar.DAY_OF_MONTH, cCard.getBillingDay());

        if (today.get(Calendar.DAY_OF_MONTH) > cCard.getPaymentDay())
            paymentDate.add(Calendar.MONTH, 1);

        if (cCard.getBillingDay() < paymentDate.get(Calendar.DAY_OF_MONTH))
            billingDate.set(Calendar.MONTH, paymentDate.get(Calendar.MONTH));
        else {
            billingDate.set(Calendar.MONTH, paymentDate.get(Calendar.MONTH));
            billingDate.add(Calendar.MONTH, -1);
        }

        date.setText(String.format("%s - %s", Constants.PAYMENT_DATE.format(billingDate
                .getTime()), Constants.PAYMENT_DATE.format(paymentDate.getTime())));

        cardholder.setText(cCard.getCardholder());
        bank.setText(cCard.getBank());

        limit.setMax((int) cCard.getMaxLimit());
        limit.setProgress((int) cCard.getConsumedLimit());
        //set progress color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if (cCard.getRemainingLimit() <= cCard.getMaxLimit() * 0.25f)
                limit.setProgressTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R
                        .color.md_red_500)));
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

        expiresOn.setText(CCard.EXPIRE_ON.format(new Date(cCard.getExpireOn())));
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
