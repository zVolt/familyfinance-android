package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.MemberVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberVH> implements LoadFromDbTask.Listener<Member> {
    public static final String TAG = MemberListAdapter.class.getSimpleName();
    private List<Member> members;
    private MemberVH.ItemInteractionListener itemInteractionListener;

    public MemberListAdapter(App app, MemberVH.ItemInteractionListener itemInteractionListener) {
        this.members = new ArrayList<>();
        this.itemInteractionListener = itemInteractionListener;
        new LoadFromDbTask<>(app.getDaoSession().getMemberDao(), this).execute();
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

    @Override
    public void onLoadTaskComplete(List<Member> data) {
        members.clear();
        members.addAll(data);
        notifyDataSetChanged();
    }

    public void notifyItemChanged(Member member) {
        int position = 0;
        boolean found = false;
        for (Member _member : members) {
            if (_member.getId().trim().equals(member.getId().trim())) {
                found = true;
                break;
            }
            position++;
        }
        if (found)
            notifyItemChanged(position);
    }
}
