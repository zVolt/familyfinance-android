package io.github.zkhan93.familyfinance.viewholders;

import android.content.res.Resources;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Constants;

/**
 * Created by zeeshan on 7/7/17.
 */

public class AccountVH extends RecyclerView.ViewHolder implements PopupMenu
        .OnMenuItemClickListener {
    public static final String TAG = AccountVH.class.getSimpleName();
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
    @BindView(R.id.menu)
    ImageButton menu;

    private Resources resources;
    private ItemInteractionListener itemInteractionListener;
    private PopupMenu popup;
    private Account account;

    public AccountVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        resources = itemView.getResources();
        ButterKnife.bind(this, itemView);
        this.itemInteractionListener = itemInteractionListener;

        popup = new PopupMenu(itemView.getContext(), menu);
        MenuInflater inflater = popup.getMenuInflater();
        popup.setOnMenuItemClickListener(this);
        inflater.inflate(R.menu.ccard_item_menu, popup.getMenu());
    }

    public void setAccount(Account account) {
        Log.d(TAG, "item: " + account.toString());
        this.account = account;
        name.setText(account.getName());
        accountNumber.setText(account.getAccountNumber());
        bank.setText(account.getBank());
        ifsc.setText(account.getIfsc());
        balance.setText(NumberFormat.getCurrencyInstance().format(account.getBalance()));
        updatedBy.setText(account.getUpdatedBy().getName());
        updatedOn.setText(Constants.DATE_FORMAT.format(account.getUpdatedOn()));
    }

    @OnClick(R.id.menu)
    void OnClick(View button) {
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_copy:
                itemInteractionListener.copy(account);
                return true;
            case R.id.action_delete:
                itemInteractionListener.delete(account);
                return true;
            case R.id.action_edit:
                itemInteractionListener.edit(account);
                return true;
            case R.id.action_share:
                itemInteractionListener.share(account);
                return true;
            default:
                return false;
        }
    }

    public interface ItemInteractionListener {
        void copy(Account account);

        void delete(Account account);

        void share(Account account);

        void edit(Account account);

    }
}
