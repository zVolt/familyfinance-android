package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.helpers.MemberItemActionClbk;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.viewholders.AccountVH;
import io.github.zkhan93.familyfinance.viewholders.MemberVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class AccountListAdapter extends RecyclerView.Adapter<AccountVH> {
    public static final String TAG = AccountListAdapter.class.getSimpleName();
    ArrayList<Account> accounts;

    public AccountListAdapter(ArrayList<Account> accounts) {
        this.accounts = accounts == null ? new ArrayList<Account>() : accounts;
    }

    @Override
    public AccountVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccountVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_account, parent, false));
    }

    @Override
    public void onBindViewHolder(AccountVH holder, int position) {
        holder.setAccount(accounts.get(position));
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }

}
