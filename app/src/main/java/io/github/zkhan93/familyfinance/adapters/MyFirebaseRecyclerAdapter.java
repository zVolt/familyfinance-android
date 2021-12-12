package io.github.zkhan93.familyfinance.adapters;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.models.BaseModel;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.util.ItemInteractionListener;
import io.github.zkhan93.familyfinance.viewholders.BaseVH;

public abstract class MyFirebaseRecyclerAdapter<T extends BaseModel, V extends BaseVH<T>> extends FirebaseRecyclerAdapter<T, V> {
    public static final String TAG = DCardListAdapter.class.getSimpleName();

    protected ItemInteractionListener<T> itemInteractionListener;
    private final AdapterInteraction adapterInteraction;
    private final DaoSession daoSession;

    public MyFirebaseRecyclerAdapter(App app, ItemInteractionListener<T>
            itemInteractionListener, FirebaseRecyclerOptions<T> options, AdapterInteraction adapterInteraction) {
        super(options);
        this.itemInteractionListener = itemInteractionListener;
        this.adapterInteraction = adapterInteraction;
        daoSession = app.getDaoSession();
    }

    @Override
    protected void onBindViewHolder(@NonNull V holder, int position,
                                    @NonNull T item) {
        holder.setItem(item);
    }

    @Override
    public void onDataChanged() {
        adapterInteraction.dataChanged();
    }

    @Override
    public void onError(DatabaseError e) {
        adapterInteraction.dataChanged();
    }

    public interface AdapterInteraction {
        void dataChanged();
    }

}
