package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.AccountDao;

/**
 * Created by zeeshan on 9/7/17.
 */

public class AccountFetchTask extends AsyncTask<Void, Void, List<Account>> {
    public static final String TAG = AccountFetchTask.class.getSimpleName();
    private WeakReference<AccountDao> accountDaoWeakReference;
    private WeakReference<Listener> listenerWeakReference;

    public AccountFetchTask(App app, Listener listener) {
        accountDaoWeakReference = new WeakReference<>(app.getDaoSession().getAccountDao());
        listenerWeakReference = new WeakReference<Listener>(listener);
    }

    @Override
    protected List<Account> doInBackground(Void... params) {
        List<Account> data = new ArrayList<>();
        AccountDao accountDao = accountDaoWeakReference.get();
        if (accountDao == null)
            return data;
        data = accountDao.loadAll();
        return data;
    }

    @Override
    protected void onPostExecute(List<Account> accounts) {
        Listener listener = listenerWeakReference.get();
        if (listener == null) {
            Log.d(TAG,"listener is detached");
            return;
        }
        listener.onAccountFetchComplete(accounts);
    }

    public interface Listener {
        void onAccountFetchComplete(List<Account> accounts);
    }
}
