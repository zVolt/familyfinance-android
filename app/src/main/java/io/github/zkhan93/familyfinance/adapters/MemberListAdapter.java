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

public class MemberListAdapter extends RecyclerView.Adapter<MemberVH> implements
        MemberItemActionClbk {
    public static final String TAG = MemberListAdapter.class.getSimpleName();
    ArrayList<Member> members;

    public MemberListAdapter(ArrayList<Member> members) {
        this.members = members == null ? new ArrayList<Member>() : members;
    }

    @Override
    public MemberVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MemberVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_member, parent, false), this);
    }

    @Override
    public void onBindViewHolder(MemberVH holder, int position) {
        holder.setMember(members.get(position));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    @Override
    public void removeMember(String memberId) {
        Log.d(TAG, "remove member: " + memberId);
    }

    @Override
    public void toggleMemberSms(String memberId) {
        Log.d(TAG, "toggle sms for member: " + memberId);
    }
}
