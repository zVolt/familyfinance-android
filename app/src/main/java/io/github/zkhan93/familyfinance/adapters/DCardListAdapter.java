package io.github.zkhan93.familyfinance.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseError;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.viewholders.DCardVH;

public class DCardListAdapter extends FirebaseRecyclerAdapter<DCard, RecyclerView.ViewHolder> {
    public static final String TAG = DCardListAdapter.class.getSimpleName();

    private DCardVH.ItemInteractionListener itemInteractionListener;
    private AdapterInteraction adapterInteraction;

    public DCardListAdapter(App app, DCardVH.ItemInteractionListener
            itemInteractionListener, FirebaseRecyclerOptions<DCard> options, AdapterInteraction adapterInteraction) {
        super(options);
        this.itemInteractionListener = itemInteractionListener;
        this.adapterInteraction = adapterInteraction;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DCardVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_ccard, parent, false), itemInteractionListener);
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position,
                                    @NonNull DCard dCard) {
        DCardVH dCardHolder = (DCardVH) holder;
        dCardHolder.setDCard(dCard);
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
