package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.CCardVH;
import io.github.zkhan93.familyfinance.viewholders.OtpVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class CCardListAdapter extends RecyclerView.Adapter<CCardVH> implements LoadFromDbTask
        .Callbacks<CCard> {
    public static final String TAG = CCardListAdapter.class.getSimpleName();
    private ArrayList<CCard> ccards;
    private CCardVH.ItemInteractionListener itemInteractionListener;

    public CCardListAdapter(App app, CCardVH.ItemInteractionListener itemInteractionListener) {
        this.ccards = ccards == null ? new ArrayList<CCard>() : ccards;
        this.itemInteractionListener = itemInteractionListener;

        new LoadFromDbTask<>(app.getDaoSession().getCCardDao(), this).execute();
    }

    @Override
    public CCardVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CCardVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_ccard, parent, false), itemInteractionListener);
    }

    @Override
    public void onBindViewHolder(CCardVH holder, int position) {
        holder.setCCard(ccards.get(position));
    }

    @Override
    public int getItemCount() {
        return ccards.size();
    }

    @Override
    public void onTaskComplete(List<CCard> data) {
        ccards.clear();
        ccards.addAll(data);
        notifyDataSetChanged();
    }

    public void notifyItemChanged(CCard _ccard) {
        int position = 0;
        boolean found = false;
        for (CCard ccard : ccards) {
            if (ccard.getNumber().trim().equals(_ccard.getNumber().trim())) {
                found = true;
                ccard.update(); // fetch the item from database again
                break;
            }
            position++;
        }
        if (found)
            notifyItemChanged(position);
    }
}
