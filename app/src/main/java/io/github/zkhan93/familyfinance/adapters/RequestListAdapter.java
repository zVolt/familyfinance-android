package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Request;
import io.github.zkhan93.familyfinance.models.RequestDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.RequestVH;

/**
 * Created by zeeshan on 16/7/17.
 */

public class RequestListAdapter extends RecyclerView.Adapter<RequestVH> implements InsertTask
        .Listener<Request>, LoadFromDbTask.Listener<Request>,
        ChildEventListener, ValueEventListener {
    public static String TAG = RequestListAdapter.class.getSimpleName();
    private List<Request> requests;
    private Member me;
    private boolean ignoreChildAddedCalls;
    private RequestDao requestDao;
    private RequestVH.ItemInteractionListener itemInteractionListener;

    public RequestListAdapter(App app, Member me, RequestVH.ItemInteractionListener
            itemInteractionListener) {
        this.me = me;
        this.itemInteractionListener = itemInteractionListener;
        ignoreChildAddedCalls = true;
        this.requests = new ArrayList<>();
        requestDao = app.getDaoSession().getRequestDao();
        new LoadFromDbTask<>(requestDao, this).execute();
    }

    @Override
    public RequestVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RequestVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_requests, parent, false), itemInteractionListener);
    }

    @Override
    public void onBindViewHolder(RequestVH holder, int position) {
        holder.setRequest(requests.get(position));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    private void addOrUpdate(Request newRequest) {
        ListIterator<Request> itr = requests.listIterator();
        Request oldRequest;
        int position = 0;
        boolean found = false;
        while (itr.hasNext()) {
            oldRequest = itr.next();
            if (oldRequest.getFamilyId().trim().equals(newRequest.getFamilyId().trim())) {
                found = true;
                oldRequest.setApproved(newRequest.getApproved());
                oldRequest.setBlocked(newRequest.getBlocked());
                notifyItemChanged(position);
                break;
            }
            position++;
        }
        if (!found) {
            requests.add(newRequest);
            notifyItemInserted(requests.size());
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildAddedCalls)
            return;
        Request request = dataSnapshot.getValue(Request.class);
        addOrUpdate(request);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (!dataSnapshot.exists()) return;
        Request request = dataSnapshot.getValue(Request.class);
        if (request == null) return;
        request.setFamilyId(dataSnapshot.getKey());
        addOrUpdate(request);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists()) return;
        Request request = dataSnapshot.getValue(Request.class);
        if (request == null) return;
        request.setFamilyId(dataSnapshot.getKey());
        ListIterator<Request> itr = requests.listIterator();
        Request req;
        int position = 0;
        while (itr.hasNext()) {
            req = itr.next();
            if (req.getFamilyId().trim().equals(request.getFamilyId().trim())) {
                itr.remove();
                notifyItemRemoved(position);
                break;
            }
            position++;
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //no shit given
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
//no shit given
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Request> _requests = new ArrayList<>();
        if (dataSnapshot.exists()) {
            Request request;
            Boolean value;
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                request = ds.getValue(Request.class);
                if (request == null) continue;

                value = ds.child("approved").getValue(Boolean.class);
                if (value == null) value = false;
                request.setApproved(value);

                value = ds.child("blocked").getValue(Boolean.class);
                if (value == null) value = false;
                request.setBlocked(value);

                request.setFamilyId(ds.getKey());
                _requests.add(request);
            }
        }
        if (_requests.size() > 0)
            new InsertTask<>(requestDao, this).execute(_requests.toArray(new
                    Request[_requests.size()]));
        else
            onInsertTaskComplete(_requests);
    }

    @Override
    public void onInsertTaskComplete(List<Request> items) {
        ignoreChildAddedCalls = false;
        for (Request request : items)
            addOrUpdate(request);
    }

    @Override
    public void onLoadTaskComplete(List<Request> data) {
        requests.clear();
        requests.addAll(data);
        notifyDataSetChanged();
        FirebaseDatabase.getInstance().getReference().child("users").child(me.getId()).child
                ("requests").addListenerForSingleValueEvent(this);
        FirebaseDatabase.getInstance().getReference().child("users").child(me.getId()).child
                ("requests").addChildEventListener(this);
    }


    public void removeRequest(Request request) {
        //remove from current UI list
        ListIterator<Request> itr = requests.listIterator();
        Request _request;
        int position = 0;
        while (itr.hasNext()) {
            _request = itr.next();
            if (_request.getFamilyId().trim().equals(request.getFamilyId().trim())) {
                itr.remove();
                notifyItemRemoved(position);
                break;
            }
            position++;
        }
    }
}
