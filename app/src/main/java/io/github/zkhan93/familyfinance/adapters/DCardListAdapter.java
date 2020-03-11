package io.github.zkhan93.familyfinance.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.models.DCardDao;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.viewholders.DCardVH;
import io.github.zkhan93.familyfinance.viewholders.EmptyVH;

public class DCardListAdapter extends FirebaseRecyclerAdapter<DCard, RecyclerView.ViewHolder> {
    public static final String TAG = DCardListAdapter.class.getSimpleName();

    private DCardVH.ItemInteractionListener itemInteractionListener;
    private boolean ignoreChildEvent;
    private DCardDao dCardDao;

    public DCardListAdapter(App app, DCardVH.ItemInteractionListener
            itemInteractionListener, FirebaseRecyclerOptions<DCard> options) {
        super(options);
        this.dCardDao = app.getDaoSession().getDCardDao();
        this.itemInteractionListener = itemInteractionListener;
        ignoreChildEvent = true;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.EMPTY)
            return new EmptyVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_empty, parent, false), "blankCCard");
        else
            return new DCardVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_dcard, parent, false), itemInteractionListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                    @NonNull DCard dCard) {
        DCardVH dCardHolder = (DCardVH) holder;

        dCard.__setDaoSession((DaoSession) dCardDao.getSession());
        dCardHolder.setDCard(dCard);
    }

    @Override
    public void onDataChanged() {
        // Called each time there is a new data snapshot. You may want to use this method
        // to hide a loading spinner or check for the "no documents" state and update your UI.
        // ...
        Log.d(TAG, "data changes");

    }

    @Override
    public void onError(DatabaseError e) {
        // Called when there is an error getting data. You may want to update
        // your UI to display an error message to the user.
        // ...
        Log.d(TAG, "data changes" + e);
    }


    public interface ITEM_TYPE {
        int NORMAL = 0;
        int EMPTY = 1;
    }

}
