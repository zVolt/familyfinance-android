package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.AccountVH;
import io.github.zkhan93.familyfinance.viewholders.FooterVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class AccountListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        LoadFromDbTask.Callbacks<Account> {
    public static final String TAG = AccountListAdapter.class.getSimpleName();
    private ArrayList<Account> accounts;
    private AccountVH.ItemInteractionListener itemInteractionListener;

    public AccountListAdapter(App app, AccountVH.ItemInteractionListener itemInteractionListener) {
        this.accounts = new ArrayList<>();
        this.itemInteractionListener = itemInteractionListener;

        new LoadFromDbTask<>(app.getDaoSession().getAccountDao(), this).execute();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == ITEM_TYPE.NORMAL) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_account, parent, false);
            return new AccountVH(view, itemInteractionListener);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_footer, parent, false);
            return new FooterVH(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AccountVH)
            ((AccountVH) holder).setAccount(accounts.get(position));
    }

    @Override
    public int getItemCount() {
        return accounts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == accounts.size() ? ITEM_TYPE.FOOTER : ITEM_TYPE.NORMAL;
    }

    @Override
    public void onTaskComplete(List<Account> data) {
        accounts.clear();
        accounts.addAll(data);
        notifyDataSetChanged();
    }

    public void notifyItemChanged(Account _account) {
        int position = 0;
        boolean found = false;
        for (Account acc : accounts) {
            if (acc.getAccountNumber().trim().equals(_account.getAccountNumber().trim())) {
                found = true;
                acc.update(); // fetch the item from database again
                break;
            }
            position++;
        }
        if (found)
            notifyItemChanged(position);
    }

    public interface ITEM_TYPE {
        int NORMAL = 0;
        int FOOTER = 1;
    }
}
