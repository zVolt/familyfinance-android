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

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Request;
import io.github.zkhan93.familyfinance.models.RequestDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.ReceiveRequestVH;

/**
 * Created by zeeshan on 22/7/17.
 */

public class ReceiveRequestListAdapter extends RecyclerView.Adapter<ReceiveRequestVH> implements
        LoadFromDbTask.Listener<Request>, ChildEventListener, ValueEventListener, InsertTask
        .Listener<Request> {

    private List<Request> requests;
    private ReceiveRequestVH.ItemInteractionListener itemInteractionListener;
    private String familyId;
    private DatabaseReference reqRef;
    private boolean ignoreChildEvents;
    private RequestDao requestDao;

    public ReceiveRequestListAdapter(App app, String familyId) {
        this.familyId = familyId;
        requests = new ArrayList<>();
        requestDao = app.getDaoSession().getRequestDao();
        reqRef = FirebaseDatabase.getInstance().getReference().child("requests").child(familyId);
        Query<Request> query = requestDao.queryBuilder().where
                (RequestDao.Properties.FamilyId.eq(familyId)).build();
        new LoadFromDbTask<>(query, this).execute();

    }

    @Override
    public ReceiveRequestVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ReceiveRequestVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_receive_request, parent, false), itemInteractionListener);
    }

    @Override
    public void onBindViewHolder(ReceiveRequestVH holder, int position) {
        holder.setRequest(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    @Override
    public void onLoadTaskComplete(List<Request> data) {
        requests.clear();
        requests.addAll(data);
        notifyDataSetChanged();
        ignoreChildEvents = true;
        reqRef.addListenerForSingleValueEvent(this);
        reqRef.addChildEventListener(this);
    }

    private boolean addOrUpdate(Request newRequest) {
        if (newRequest == null) return false;
        int position = 0;
        boolean found = false;
        for (Request oldRequest : requests) {
            if (oldRequest.getUserId().equals(newRequest.getUserId()) && oldRequest.getFamilyId()
                    .equals(newRequest.getFamilyId())) {
                oldRequest.setName(newRequest.getName());
                oldRequest.setEmail(newRequest.getEmail());
                oldRequest.setProfilePic(newRequest.getProfilePic());
                oldRequest.setApproved(newRequest.getApproved());
                oldRequest.setBlocked(newRequest.getBlocked());
                oldRequest.setUpdatedOn(newRequest.getUpdatedOn());
                found = true;
                break;
            }
        }
        if (found) notifyItemChanged(position);
        else {
            requests.add(newRequest);
            notifyItemInserted(requests.size());
        }
        return found;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvents) return;
        if (dataSnapshot == null) return;
        Request newRequest = dataSnapshot.getValue(Request.class);
        if (newRequest == null) return;
        addOrUpdate(newRequest);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot == null) return;
        Request newRequest = dataSnapshot.getValue(Request.class);
        if (newRequest == null) return;
        addOrUpdate(newRequest);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null)
            return;
        Request request;
        List<Request> requests = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            request = ds.getValue(Request.class);
            if (request != null)
                requests.add(request);
        }
        new InsertTask<>(requestDao, this, true).execute();
    }

    @Override
    public void onInsertTaskComplete(List<Request> items) {
        if (items == null || items.size() == 0)
            return;
        requests.clear();
        requests.addAll(items);
        notifyDataSetChanged();
        ignoreChildEvents = false;
    }
}