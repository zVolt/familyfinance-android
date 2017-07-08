package io.github.zkhan93.familyfinance.viewholders;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Constants;

/**
 * Created by zeeshan on 7/7/17.
 */

public class CCardVH extends RecyclerView.ViewHolder {
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

    private Resources resources;

    public CCardVH(View itemView) {
        super(itemView);
        resources = itemView.getResources();
        ButterKnife.bind(this, itemView);
        limit.setIndeterminate(false);
    }

    public void setCCard(CCard cCard) {
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

        remainingLimit.setText(String.format(Locale.US, "%.2f %s", cCard.getMaxLimit() - cCard
                .getConsumedLimit(), resources.getString(R.string.rs)));
        updatedBy.setText(cCard.getUpdatedBy().getName());
        updatedOn.setText(Constants.DATE_FORMAT.format(cCard.getUpdatedOn()));
    }
}
