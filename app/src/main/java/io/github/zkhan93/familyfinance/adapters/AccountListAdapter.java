package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.AccountVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class AccountListAdapter extends RecyclerView.Adapter<AccountVH> implements
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
    public AccountVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccountVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_account, parent, false), itemInteractionListener);
    }

    @Override
    public void onBindViewHolder(AccountVH holder, int position) {
        holder.setAccount(accounts.get(position));
    }

    @Override
    public int getItemCount() {
        return accounts.size();
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
}
