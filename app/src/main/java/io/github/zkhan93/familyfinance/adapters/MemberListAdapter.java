package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.helpers.MemberItemActionClbk;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.viewholders.MemberVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberVH> {
    public static final String TAG = MemberListAdapter.class.getSimpleName();
    private ArrayList<Member> members;
    private MemberItemActionClbk memberItemActionClbk;

    public MemberListAdapter(ArrayList<Member> members, MemberItemActionClbk memberItemActionClbk) {
        this.members = members == null ? new ArrayList<Member>() : members;
        this.memberItemActionClbk = memberItemActionClbk;
    }

    @Override
    public MemberVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_member, parent, false), memberItemActionClbk);
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
