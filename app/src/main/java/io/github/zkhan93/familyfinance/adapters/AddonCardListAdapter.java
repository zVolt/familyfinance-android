package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.viewholders.AddonCardVH;

/**
 * Created by zeeshan on 28/7/17.
 */

public class AddonCardListAdapter extends RecyclerView.Adapter<AddonCardVH> {
    public static String TAG = AddonCardListAdapter.class.getSimpleName();
    private List<AddonCard> addonCards;
    private AddonCardVH.ItemInteractionListener itemInteractionListener;

    public AddonCardListAdapter(AddonCardVH.ItemInteractionListener itemInteractionListener) {
        this.itemInteractionListener = itemInteractionListener;
        this.addonCards = new ArrayList<>();
    }

    public void setItems(List<AddonCard> addonCards) {
        this.addonCards.clear();
        this.addonCards.addAll(addonCards);
        notifyDataSetChanged();
    }

    @Override
    public AddonCardVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AddonCardVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_addon_card, parent, false), itemInteractionListener);
    }

    @Override
    public void onBindViewHolder(AddonCardVH holder, int position) {
        holder.setAddonCard(addonCards.get(position));
    }

    @Override
    public int getItemCount() {
        return addonCards.size();
    }
}
