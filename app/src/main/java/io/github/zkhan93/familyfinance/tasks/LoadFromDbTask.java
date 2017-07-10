package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;

import org.greenrobot.greendao.AbstractDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.models.Account;

/**
 * Created by zeeshan on 10/7/17.
 */

public class LoadFromDbTask<D extends AbstractDao<T, ?>, T>
        extends AsyncTask<Void, Void, List<T>> {

    private WeakReference<D> cCardDaoWeakReference;
    private WeakReference<Callbacks<T>> callbacksWeakReference;

    public LoadFromDbTask(D dao, Callbacks<T> callbacks) {
        callbacksWeakReference = new WeakReference<>(callbacks);
        cCardDaoWeakReference = new WeakReference<>(dao);
    }

    @Override
    protected List<T> doInBackground(Void... params) {
        D dao = cCardDaoWeakReference.get();
        List<T> data = new ArrayList<>();
        if (dao == null)
            return data;
        data = dao.loadAll();
        return data;
    }

    @Override
    protected void onPostExecute(List<T> data) {
        Callbacks<T> callbacks = callbacksWeakReference.get();
        if (callbacks == null)
            return;
        callbacks.onTaskComplete(data);
    }

    public interface Callbacks<T> {
        void onTaskComplete(List<T> data);
    }
}
