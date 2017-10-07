package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.events.InsertEvent;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.AccountDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.AccountVH;
import io.github.zkhan93.familyfinance.viewholders.FooterVH;

/**
 *
 * Created by zeeshan on 8/7/17.
 */

public class AccountListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        InsertTask.Listener<Account>, LoadFromDbTask.Listener<Account>,
        ChildEventListener, ValueEventListener {
    public static final String TAG = AccountListAdapter.class.getSimpleName();
    private ArrayList<Account> accounts;
    private AccountVH.ItemInteractionListener itemInteractionListener;
    private DatabaseReference accountsRef;
    private String familyId;
    private AccountDao accountDao;
    /**
     * Ignore OnChildAdded() calls if its value is true else add/update the new item received
     * from firebase,
     * used to fetch all the accounts in one go inside ValueEventListener.onDataChanged()
     */
    private boolean ignoreChildEvents;

    public AccountListAdapter(App app, String familyId, AccountVH.ItemInteractionListener
            itemInteractionListener) {
        accountDao = app.getDaoSession().getAccountDao();
        this.accounts = new ArrayList<>();
        ignoreChildEvents = true;
        this.familyId = familyId;
        accountsRef = FirebaseDatabase.getInstance().getReference("accounts").child(familyId);
        Query<Account> query = accountDao.queryBuilder().orderDesc(AccountDao.Properties
                .UpdatedOn).build();
        new LoadFromDbTask<>(query, this).execute();
        this.itemInteractionListener = itemInteractionListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
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

//    public void reloadFromDisk(App app) {
//        new LoadFromDbTask<>(app.getDaoSession().getAccountDao(), this).execute();
//    }

    public void registerForEvents() {
        EventBus.getDefault().register(this);
    }

    public void unregisterForEvents() {
        EventBus.getDefault().unregister(this);
    }

    /**
     * return true if this was an update else false
     *
     * @param newAccount
     * @return
     */
    private boolean addOrUpdate(Account newAccount) {
        Account oldAccount;
        ListIterator<Account> itr = accounts.listIterator();
        int position = 0;
        boolean found = false;
        while (itr.hasNext()) {
            oldAccount = itr.next();
            if (oldAccount.getAccountNumber().trim().equals(newAccount.getAccountNumber().trim())) {
                oldAccount.updateFrom(newAccount); //copy value from new object to maintain dao
                // connection
                accountDao.insertOrReplace(oldAccount);
                oldAccount = accountDao.load(oldAccount.getAccountNumber());
                itr.set(oldAccount);
                notifyItemChanged(position);
                found = true;
                break;
            }
            position++;
        }
        if (found) {
            oldAccount = accounts.remove(position);
            accounts.add(0, oldAccount);
            notifyItemMoved(position, 0);
        } else {
            accountDao.insertOrReplace(newAccount);
            newAccount = accountDao.load(newAccount.getAccountNumber());
            accounts.add(0, newAccount);
            notifyItemInserted(0);
        }
        return found;
    }

    public void deleteAccount(String accountNumber) {
        ListIterator<Account> itr = accounts.listIterator();
        int position = 0;
        while (itr.hasNext()) {
            if (itr.next().getAccountNumber().trim().equals(accountNumber.trim())) {
                itr.remove();
                notifyItemRemoved(position);
                accountsRef.child(accountNumber).setValue(null);
            }
            position++;
        }
    }

    /**
     * check if the item item already exist then update else insert
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountAdded(InsertEvent<Account> event) {
        if (ignoreChildEvents) return;
        Log.d(TAG, "adapter notified about the account insertion");
        for (final Account newAccount : event.getItems()) {
            addOrUpdate(newAccount);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvents)
            return;
        Account newAccount = dataSnapshot.getValue(Account.class);
        if (newAccount == null)
            return;
        addOrUpdate(newAccount);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvents)
            return;
        Account newAccount = dataSnapshot.getValue(Account.class);
        if (newAccount == null)
            return;
        addOrUpdate(newAccount);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (ignoreChildEvents)
            return;
        Account newAccount = dataSnapshot.getValue(Account.class);
        if (newAccount == null) return;
        Account oldAccount;
        int position = 0;
        ListIterator<Account> itr = accounts.listIterator();
        while (itr.hasNext()) {
            oldAccount = itr.next();
            if (oldAccount.getAccountNumber().trim().equals(newAccount.getAccountNumber().trim())) {
                itr.remove();
                notifyItemRemoved(position);
                break;
            }
            position++;
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //don't give a fuck
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        //hmm.. not sure what should I do
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        //write all the data in database and listen to the InsertEvent<Account>
        Account account;
        List<Account> accounts = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            account = ds.getValue(Account.class);
            if (account != null) {
                accounts.add(account);
            }
        }
        new InsertTask<>(accountDao, this)
                .execute(accounts.toArray(new Account[accounts.size()]));

    }

    //attah this listener for the loading the initial data
    @Override
    public void onInsertTaskComplete(List<Account> items) {
        accounts.clear();
        accounts.addAll(items);
        Collections.sort(accounts, Account.BY_UPDATED_ON);
        notifyDataSetChanged();
        ignoreChildEvents = false;
    }

    @Override
    public void onLoadTaskComplete(List<Account> data) {
        if (data != null) {
            accounts.addAll(data);
            Collections.sort(accounts, Account.BY_UPDATED_ON);
            notifyDataSetChanged();
        }

        accountsRef.addListenerForSingleValueEvent(this);
        accountsRef.addChildEventListener(this);
    }
}
