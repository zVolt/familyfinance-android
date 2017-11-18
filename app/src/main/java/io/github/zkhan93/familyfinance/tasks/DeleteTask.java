package io.github.zkhan93.familyfinance.tasks;

import android.os.AsyncTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.query.DeleteQuery;

import io.github.zkhan93.familyfinance.events.DeleteEvent;
import io.github.zkhan93.familyfinance.models.BaseModel;

/**
 * Created by zeeshan on 22/7/17.
 */

public class DeleteTask<T extends BaseModel> extends AsyncTask<Void, Void, Void> {
    private DeleteQuery<T> deleteQuery;
    private Listener listener;

    public DeleteTask(DeleteQuery<T> deleteQuery) {
        this.deleteQuery = deleteQuery;
    }

    public DeleteTask(DeleteQuery<T> deleteQuery, Listener listener) {
        this(deleteQuery);
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        deleteQuery.executeDeleteWithoutDetachingEntities();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (listener == null) {
            EventBus.getDefault().post(new DeleteEvent());
            return;
        }
        listener.onDeleteTaskComplete();
    }

    public interface Listener {
        void onDeleteTaskComplete();
    }
}
