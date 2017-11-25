package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.viewholders.BalanceByBankVH;

/**
 * Created by zeeshan on 11/26/17.
 */

public class BalanceByBankAdapter extends RecyclerView.Adapter<BalanceByBankVH> implements
        ValueEventListener {

    private List<BalanceByBank> balanceByBankList;

    public BalanceByBankAdapter(String familyId) {
        this.balanceByBankList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("accounts").child(familyId)
                .addValueEventListener(this);
    }

    @Override
    public BalanceByBankVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_balance, parent, false);
        return new BalanceByBankVH(view);
    }

    @Override
    public void onBindViewHolder(BalanceByBankVH holder, int position) {
        BalanceByBank memberBalance = balanceByBankList.get(position);
        holder.setItem(memberBalance.bankId, memberBalance.balance);
    }

    @Override
    public int getItemCount() {
        return balanceByBankList.size();
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        Account account;
        Map<String, Float> map = new HashMap<>();
        Float tmpAmount;
        String bankId;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            account = ds.getValue(Account.class);
            if (account == null) continue;
            tmpAmount = map.get(account.getBank());
            if (tmpAmount == null)
                tmpAmount = 0f;
            map.put(account.getBank(), tmpAmount + account.getBalance());
        }
        balanceByBankList.clear();
        for (Map.Entry<String, Float> me : map.entrySet()) {
            balanceByBankList.add(new BalanceByBank(me.getKey(), me.getValue()));
        }
        notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private static class BalanceByBank {
        public BalanceByBank(String bankId, float balance) {
            this.bankId = bankId;
            this.balance = balance;
        }

        String bankId;
        float balance;
    }
}
