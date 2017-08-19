package io.github.zkhan93.familyfinance.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.NumberFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.util.Constants;

/**
 * Created by zeeshan on 7/7/17.
 */

public class AccountVH extends RecyclerView.ViewHolder implements PopupMenu
        .OnMenuItemClickListener {
    public static final String TAG = AccountVH.class.getSimpleName();
    @BindView(R.id.account_holder)
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
    ImageView updatedBy;
    @BindView(R.id.updated_on)
    TextView updatedOn;
    @BindView(R.id.menu)
    ImageButton menu;

    private Context context;
    private ItemInteractionListener itemInteractionListener;
    private PopupMenu popup;
    private Account account;

    public AccountVH(View itemView, ItemInteractionListener itemInteractionListener) {
        super(itemView);
        context = itemView.getContext();
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
        name.setText(account.getAccountHolder());
        accountNumber.setText(account.getAccountNumber());
        bank.setText(account.getBank());
        ifsc.setText(account.getIfsc());
        balance.setText(NumberFormat.getCurrencyInstance().format(account.getBalance()));

        Member _updatedBy = account.getUpdatedBy();

        if (_updatedBy != null && _updatedBy.getProfilePic() != null && !_updatedBy.getProfilePic
                ().isEmpty())
            Glide.with(context).load(_updatedBy.getProfilePic()).apply(RequestOptions
                    .circleCropTransform()).into(updatedBy);

        Date _updatedOn = account.getUpdatedOn() == -1 ? null : new Date(account.getUpdatedOn());
        updatedOn.setText(_updatedOn == null ? "Never" : DateUtils.getRelativeTimeSpanString
                (_updatedOn.getTime()));
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
