package io.github.zkhan93.familyfinance.viewholders;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.util.Constants;

/**
 * Created by zeeshan on 7/7/17.
 */

public class AccountVH extends RecyclerView.ViewHolder {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.account_number)
    TextView accountNumber;
    @BindView(R.id.bank)
    TextView bank;
    @BindView(R.id.ifsc)
    TextView ifsc;
    @BindView(R.id.balance)
    TextView balance;
    @BindView(R.id.updated_by)
    TextView updatedBy;
    @BindView(R.id.updated_on)
    TextView updatedOn;

    private Resources resources;

    public AccountVH(View itemView) {
        super(itemView);
        resources = itemView.getResources();
        ButterKnife.bind(this, itemView);
    }

    public void setAccount(Account account) {
        name.setText(account.getName());
        accountNumber.setText(account.getAccountNumber());
        bank.setText(account.getBank());
        ifsc.setText(account.getIfsc());
        balance.setText(String.format(Locale.US, "%.2f %s", account.getBalance(), resources
                .getString(R.string.rs)));
        updatedBy.setText(account.getUpdatedBy().getName());
        updatedOn.setText(Constants.DATE_FORMAT.format(account.getUpdatedOn()));
    }
}
