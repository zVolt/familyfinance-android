package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;

import org.greenrobot.greendao.AbstractDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zeeshan on 10/7/17.
 */

public class LoadFromDbTask<D extends AbstractDao<T, ?>, T>
        extends AsyncTask<Void, Void, List<T>> {

    private WeakReference<D> cCardDaoWeakReference;
    private WeakReference<Listener<T>> callbacksWeakReference;

    public LoadFromDbTask(D dao, Listener<T> listener) {
        callbacksWeakReference = new WeakReference<>(listener);
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
        Listener<T> listener = callbacksWeakReference.get();
        if (listener == null)
            return;
        listener.onLoadTaskComplete(data);
    }

    public interface Listener<T> {
        void onLoadTaskComplete(List<T> data);
    }
}
