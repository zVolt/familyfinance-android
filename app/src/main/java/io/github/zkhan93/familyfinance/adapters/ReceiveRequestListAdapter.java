package io.github.zkhan93.familyfinance.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Request;
import io.github.zkhan93.familyfinance.models.RequestDao;
import io.github.zkhan93.familyfinance.tasks.DeleteTask;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.ReceiveRequestVH;

import static io.github.zkhan93.familyfinance.models.Request.BY_UPDATEDON_ASC;

/**
 * Created by zeeshan on 22/7/17.
 */

public class ReceiveRequestListAdapter extends RecyclerView.Adapter<ReceiveRequestVH> implements
        LoadFromDbTask.Listener<Request>, ChildEventListener, ValueEventListener, InsertTask
        .Listener<Request> {
    public static final String TAG = ReceiveRequestListAdapter.class.getSimpleName();

    private final List<Request> requests;
    private final ReceiveRequestVH.ItemInteractionListener itemInteractionListener;
    private final String familyId;
    private String familyModeratorId;
    private final DatabaseReference reqRef;
    private boolean ignoreChildEvents;
    private final RequestDao requestDao;

    public ReceiveRequestListAdapter(App app, String familyId, ReceiveRequestVH
            .ItemInteractionListener itemInteractionListener) {
        this.familyId = familyId;
        this.itemInteractionListener = itemInteractionListener;
        requests = new ArrayList<>();
        requestDao = app.getDaoSession().getRequestDao();
        reqRef = FirebaseDatabase.getInstance().getReference().child("requests").child(familyId);
        FirebaseDatabase.getInstance().getReference().child("family").child(familyId).child
                ("moderator").child
                ("id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    familyModeratorId = dataSnapshot.getValue(String.class);
                    startDataLoad();
                } else {
                    //no such family exist
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void startDataLoad() {
        Query<Request> query = requestDao.queryBuilder().where
                (RequestDao.Properties.FamilyId.eq(familyId), RequestDao.Properties.UserId.notEq
                        (familyModeratorId)).orderAsc(RequestDao.Properties.RequestedOn).build();
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
        Log.d(TAG, "data:" + data.toString());
        notifyDataSetChanged();
        ignoreChildEvents = true;
        reqRef.orderByChild("requestedOn").addListenerForSingleValueEvent(this);
        reqRef.orderByChild("requestedOn").addChildEventListener(this);
    }

    public boolean addOrUpdate(Request newRequest) {
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
            position++;
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
        newRequest.setUserId(dataSnapshot.getKey());
        newRequest.setFamilyId(familyId);
        if (newRequest.getUserId().equals(familyModeratorId)) return;
        addOrUpdate(newRequest);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot == null) return;
        Request newRequest = dataSnapshot.getValue(Request.class);
        if (newRequest == null) return;
        newRequest.setUserId(dataSnapshot.getKey());
        newRequest.setFamilyId(familyId);
        if (newRequest.getUserId().equals(familyModeratorId)) return;
        addOrUpdate(newRequest);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        Request newRequest = dataSnapshot.getValue(Request.class);
        if (newRequest == null) return;
        newRequest.setUserId(dataSnapshot.getKey());
        newRequest.setFamilyId(familyId);

        ListIterator<Request> itr = requests.listIterator();
        Request request;
        int position = 0;
        while (itr.hasNext()) {
            request = itr.next();
            if (request.getFamilyId().equals(newRequest.getFamilyId()) && request.getUserId()
                    .equals(newRequest.getUserId())) {
                itr.remove();
                DeleteQuery<Request> query = requestDao.queryBuilder().where(RequestDao.Properties
                        .FamilyId.eq(newRequest.getFamilyId()), RequestDao
                        .Properties.UserId.eq(newRequest.getUserId())).buildDelete();
                new DeleteTask<>(query, null);
                notifyItemRemoved(position);
                break;
            }
            position++;
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Log.d(TAG, "datachanged" + dataSnapshot);
        if (dataSnapshot == null)
            return;
        Request request;
        List<Request> requests = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            request = ds.getValue(Request.class);
            if (request != null) {
                requests.add(request);
                request.setUserId(ds.getKey());
                request.setFamilyId(familyId);
            }
        }
        new InsertTask<>(requestDao, this, true).execute(requests.toArray(new Request[requests
                .size()]));
    }

    @Override
    public void onInsertTaskComplete(List<Request> items) {
        if (items == null || items.size() == 0)
            return;
        ListIterator<Request> itr = items.listIterator();
        while (itr.hasNext()) {
            if (itr.next().getUserId().equals(familyModeratorId)) {
                itr.remove();
            }
        }
        requests.clear();
        Collections.sort(items, BY_UPDATEDON_ASC);
        requests.addAll(items);
        notifyDataSetChanged();
        ignoreChildEvents = false;
    }
}
