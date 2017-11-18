package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;

import com.google.firebase.database.DatabaseReference;

import org.greenrobot.greendao.AbstractDao;

/**
 * Created by zeeshan on 15/7/17.
 */

public class UpdateTask<D extends AbstractDao, T> extends AsyncTask<T, Void, Void> {

    DatabaseReference itemNodeRef;
    D dao;

    public UpdateTask(D dao, DatabaseReference ItemNodeRef) {
        this.itemNodeRef = itemNodeRef;
        this.dao = dao;
    }

    @Override
    protected Void doInBackground(T[] params) {
        if (dao == null)
            return null;
        for (T item : params) {
            dao.insertOrReplace(item);

        }
        return null;
    }
}
