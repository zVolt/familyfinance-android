package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.AbstractDao;

import java.lang.ref.WeakReference;

import io.github.zkhan93.familyfinance.events.InsertEvent;

/**
 * Inserted a given object of type T into database and triggers a EventBus event woth the inserted object
 * Created by zeeshan on 12/7/17.
 */

public class InsertTask<D extends AbstractDao, T> extends AsyncTask<T, Void, T> {
    public static final String TAG = InsertTask.class.getSimpleName();

    private WeakReference<D> daoWeakReference;

    public InsertTask(D dao) {
        daoWeakReference = new WeakReference<>(dao);
    }

    @Override
    protected T doInBackground(T[] params) {
        D dao = daoWeakReference.get();
        if (dao == null)
            return null;
        T item = params[0];
        if (item != null) {
            dao.insert(item);
        }
        return item;
    }

    @Override
    protected void onPostExecute(T item) {
        Log.d(TAG, String.format("item inserted", item.toString()));
        EventBus.getDefault().post(new InsertEvent<T>(item));
    }

}
