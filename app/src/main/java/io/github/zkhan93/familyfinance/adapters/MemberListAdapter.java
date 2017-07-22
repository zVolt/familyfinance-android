package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.MemberDao;
import io.github.zkhan93.familyfinance.models.RequestDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.MemberVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class MemberListAdapter extends RecyclerView.Adapter<MemberVH> implements LoadFromDbTask
        .Listener<Member>, InsertTask.Listener<Member>, ChildEventListener, ValueEventListener {
    public static final String TAG = MemberListAdapter.class.getSimpleName();
    private List<Member> members;
    private MemberVH.ItemInteractionListener itemInteractionListener;
    private String familyId;
    private DatabaseReference membersRef;
    private boolean ignoreChildEvents;
    private MemberDao memberDao;

    public MemberListAdapter(App app, String familyId, MemberVH.ItemInteractionListener
            itemInteractionListener) {
        this.familyId = familyId;
        this.members = new ArrayList<>();
        this.itemInteractionListener = itemInteractionListener;
        membersRef = FirebaseDatabase.getInstance().getReference().child("members").child(familyId);
        ignoreChildEvents = true;
        memberDao = app.getDaoSession().getMemberDao();
        Query<Member> query = memberDao.queryBuilder().build();
        new LoadFromDbTask<>(query, this).execute();
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
        membersRef.addListenerForSingleValueEvent(this);
        membersRef.addChildEventListener(this);

    }

    public void addOrUpdate(Member newMember) {
        int position = 0;
        boolean found = false;
        for (Member oldMember : members) {
            if (oldMember.getId().trim().equals(newMember.getId().trim())) {
                found = true;
                break;
            }
            position++;
        }
        if (found)
            notifyItemChanged(position);
        else {
            members.add(newMember);
            notifyItemInserted(members.size());
        }
    }

    public void removeMember(Member member) {
        int position = 0;
        ListIterator<Member> itr = members.listIterator();
        Member oldMember;
        while (itr.hasNext()) {
            oldMember = itr.next();
            if (oldMember.getId().equals(member.getId())) {
                itr.remove();
                memberDao.deleteByKey(member.getId());
                notifyItemRemoved(position);
                break;
            }
            position++;
        }
    }

    @Override
    public void onInsertTaskComplete(List<Member> items) {
        members.clear();
        for (Member member : items) {
            if (member == null) continue;
            members.add(member);
        }
        notifyDataSetChanged();
        ignoreChildEvents = false;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot == null) return;
        Member newMember = dataSnapshot.getValue(Member.class);
        if (newMember == null) return;
        addOrUpdate(newMember);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot == null) return;
        Member newMember = dataSnapshot.getValue(Member.class);
        if (newMember == null) return;
        addOrUpdate(newMember);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        Member member = dataSnapshot.getValue(Member.class);
        if (member == null) return;
        removeMember(member);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        Member member;
        List<Member> members = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds == null) continue;
            member = ds.getValue(Member.class);
            if (member == null) continue;
            members.add(member);
        }
        new InsertTask<>(memberDao, this, true).execute(members.toArray(new Member[members.size()
                ]));
    }
}
