package io.github.zkhan93.familyfinance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.firebase.ui.database.FirebaseRecyclerOptions;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.util.ItemInteractionListener;
import io.github.zkhan93.familyfinance.viewholders.DCardVH;

public class DCardListAdapter extends MyFirebaseRecyclerAdapter<DCard, DCardVH> {
    public static final String TAG = DCardListAdapter.class.getSimpleName();
    private final DaoSession daoSession;

    public DCardListAdapter(App app,
                            ItemInteractionListener<DCard> itemInteractionListener,
                            FirebaseRecyclerOptions<DCard> options,
                            AdapterInteraction adapterInteraction,
                            DaoSession daoSession) {
        super(app, itemInteractionListener, options, adapterInteraction);
        this.daoSession = daoSession;
    }

    @NonNull
    @Override
    public DCardVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_ccard, parent, false);
        return new DCardVH(view, this.itemInteractionListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull DCardVH holder, int position, @NonNull DCard item) {
        item.__setDaoSession(daoSession);
        super.onBindViewHolder(holder, position, item);
    }
}
