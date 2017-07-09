package io.github.zkhan93.familyfinance.adapters;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.AccountDao;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.tasks.AccountFetchTask;
import io.github.zkhan93.familyfinance.viewholders.AccountVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class AccountListAdapter extends RecyclerView.Adapter<AccountVH> implements
        AccountFetchTask.Listener {
    public static final String TAG = AccountListAdapter.class.getSimpleName();
    ArrayList<Account> accounts;
    AccountVH.ItemInteractionListener itemInteractionListener;

    public AccountListAdapter(App app, ArrayList<Account> accounts, AccountVH
            .ItemInteractionListener
            itemInteractionListener) {
        this.accounts = accounts == null ? new ArrayList<Account>() : accounts;
        this.itemInteractionListener = itemInteractionListener;

        new AccountFetchTask(app, this).execute();
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
    public void onAccountFetchComplete(List<Account> _accounts) {
        accounts.clear();
        accounts.addAll(_accounts);
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
