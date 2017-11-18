package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.Query;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.models.OtpDao;

/**
 * Created by zeeshan on 10/7/17.
 */

public class LoadFromDbTask<D extends AbstractDao<T, ?>, T>
        extends AsyncTask<Void, Void, List<T>> {

    private WeakReference<D> cCardDaoWeakReference;
    private WeakReference<Listener<T>> callbacksWeakReference;
    private Query<T> query;

    /**
     *
     * @param dao
     * @param listener
     * @deprecated use {@link #LoadFromDbTask(Query, Listener)} instead.
     */
    @Deprecated
    public LoadFromDbTask(D dao, Listener<T> listener) {
        callbacksWeakReference = new WeakReference<>(listener);
        cCardDaoWeakReference = new WeakReference<>(dao);
    }

    public LoadFromDbTask(Query<T> query, Listener<T> listener) {
        callbacksWeakReference = new WeakReference<>(listener);
        this.query = query;
    }

    @Override
    protected List<T> doInBackground(Void... params) {
        if (query != null) return query.forCurrentThread().list();
        D dao = cCardDaoWeakReference.get();
        List<T> data = new ArrayList<>();
        if (dao == null)
            return data;
        if (dao instanceof OtpDao)
            data = dao.queryBuilder().orderDesc(OtpDao.Properties.Timestamp).list();
        else
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
