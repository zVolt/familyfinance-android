package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.viewholders.MemberVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberVH> {
    public static final String TAG = MemberListAdapter.class.getSimpleName();
    private List<Member> members;
    private MemberVH.ItemInteractionListener itemInteractionListener;

    public MemberListAdapter(List<Member> members, MemberVH.ItemInteractionListener itemInteractionListener) {
        this.members = members == null ? new ArrayList<Member>() : members;
        this.itemInteractionListener = itemInteractionListener;
    }

    @Override
    public MemberVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_member, parent, false), itemInteractionListener);
    }

    @Override
    public void onBindViewHolder(MemberVH holder, int position) {
        holder.setMember(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }
}
