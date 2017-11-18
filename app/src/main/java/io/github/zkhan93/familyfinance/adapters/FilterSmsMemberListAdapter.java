package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.viewholders.FilterSMSMemberVH;
import io.github.zkhan93.familyfinance.viewholders.MemberVH;

/**
 * Created by zeeshan on 11/19/17.
 */

public class FilterSmsMemberListAdapter extends MemberListAdapter {

    private FilterSMSMemberVH.ItemInteractionListener itemInteractionListener;

    public FilterSmsMemberListAdapter(App app, String familyId, FilterSMSMemberVH
            .ItemInteractionListener itemInteractionListener) {
        super(app, familyId);
        this.itemInteractionListener = itemInteractionListener;
        comparator = Member.BY_NAME;
    }

    @Override
    public FilterSMSMemberVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FilterSMSMemberVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_filter_member, parent, false), itemInteractionListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((FilterSMSMemberVH) holder).setMember(members.get(position));
    }
}
