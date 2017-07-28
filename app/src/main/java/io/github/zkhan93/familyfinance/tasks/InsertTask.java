package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.AbstractDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.events.InsertEvent;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.MemberDao;
import io.github.zkhan93.familyfinance.models.Otp;

/**
 * Inserted a given object of type T into database and triggers a EventBus event woth the
 * inserted object
 * Created by zeeshan on 12/7/17.
 */

public class InsertTask<D extends AbstractDao, T> extends AsyncTask<T, Void, List<T>> {
    public static final String TAG = InsertTask.class.getSimpleName();

    private WeakReference<D> daoWeakReference;
    private WeakReference<Listener<T>> listenerWeakReference;
    private boolean clean;

    public InsertTask(D dao) {
        daoWeakReference = new WeakReference<>(dao);
    }

    public InsertTask(D dao, Listener<T> listener) {
        daoWeakReference = new WeakReference<>(dao);
        listenerWeakReference = new WeakReference<>(listener);
    }

    public InsertTask(D dao, Listener<T> listener, boolean cleanBeforeInsert) {
        this(dao, listener);
        clean = cleanBeforeInsert;
    }

    @Override
    protected List<T> doInBackground(T[] params) {
        D dao = daoWeakReference.get();
        if (dao == null)
            return null;
        if (clean) {
            dao.deleteAll();
        }
        List<T> insertedItems = new ArrayList<>();
        for (T item : params) {
            if (item != null) {
                dao.insertOrReplace(item);
                insertedItems.add(item);
            }
        }
        return insertedItems;
    }

    @Override
    protected void onPostExecute(List<T> items) {
        if(items==null)
            return;
        Log.d(TAG, String.format("item inserted", items.toString()));
        if (listenerWeakReference == null)
            EventBus.getDefault().post(new InsertEvent<T>(items));
        else {
            Listener<T> listener = listenerWeakReference.get();
            if (listener != null)
                listener.onInsertTaskComplete(items);
        }
    }

    public interface Listener<T> {
        void onInsertTaskComplete(List<T> items);
    }
}
