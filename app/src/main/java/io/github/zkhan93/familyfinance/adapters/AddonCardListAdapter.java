package io.github.zkhan93.familyfinance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.viewholders.CCardVH;

/**
 * Created by zeeshan on 28/7/17.
 */

public class AddonCardListAdapter extends RecyclerView.Adapter<CCardVH> {
    public static String TAG = AddonCardListAdapter.class.getSimpleName();
    private List<CCard> addonCards;
    private CCardVH.ItemInteractionListener itemInteractionListener;
    private int recycler_width;
    public AddonCardListAdapter(CCardVH.ItemInteractionListener itemInteractionListener, RecyclerView recyclerView) {
        this.itemInteractionListener = itemInteractionListener;
        this.addonCards = new ArrayList<>();
        this.recycler_width =recyclerView.getWidth();
    }

    public void setItems(List<CCard> addonCards) {
        this.addonCards.clear();
        this.addonCards.addAll(addonCards);
        notifyDataSetChanged();
    }

    @Override
    public CCardVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_ccard_small, parent, false);
        return new CCardVH(view, itemInteractionListener);
    }

    @Override
    public void onBindViewHolder(CCardVH holder, int position) {
        holder.setCCard(addonCards.get(position), false);
    }

    @Override
    public int getItemCount() {
        return addonCards.size();
    }
}
