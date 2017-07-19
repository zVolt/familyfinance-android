package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.events.InsertEvent;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.CCardDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.CCardVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class CCardListAdapter extends RecyclerView.Adapter<CCardVH> implements LoadFromDbTask
        .Listener<CCard>, InsertTask.Listener<CCard>, ChildEventListener, ValueEventListener {
    public static final String TAG = CCardListAdapter.class.getSimpleName();
    private ArrayList<CCard> ccards;
    private CCardVH.ItemInteractionListener itemInteractionListener;
    private String familyId;
    private DatabaseReference ccardRef;
    private CCardDao cCardDao;
    private boolean ignoreChildEvent;

    public CCardListAdapter(App app, String familyId, CCardVH.ItemInteractionListener
            itemInteractionListener) {
        cCardDao = app.getDaoSession().getCCardDao();
        this.ccards = new ArrayList<>();
        this.itemInteractionListener = itemInteractionListener;
        this.familyId = familyId;
        ccardRef = FirebaseDatabase.getInstance().getReference("ccards").child(familyId);
        ignoreChildEvent = true;
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
    public void onLoadTaskComplete(List<CCard> data) {
        Log.d(TAG, "loaded: " + data.toString());
        ccards.clear();
        ccards.addAll(data);
        notifyDataSetChanged();
        ccardRef.addListenerForSingleValueEvent(this);
        ccardRef.addChildEventListener(this);
    }

    public boolean addOrUpdate(CCard newCcard) {
        int position = 0;
        boolean found = false;
        for (CCard oldCcard : ccards) {
            if (oldCcard.getNumber().trim().equals(newCcard.getNumber().trim())) {
                found = true;
                oldCcard.updateFrom(newCcard); // fetch the item from database again
                oldCcard.update();
                break;
            }
            position++;
        }
        if (found)
            notifyItemChanged(position);
        else {
            ccards.add(newCcard);
            notifyItemInserted(ccards.size());
        }
        return found;
    }

    public void registerForEvent() {
        EventBus.getDefault().register(this);
    }

    public void unregisterForEvent() {
        EventBus.getDefault().unregister(this);
    }

    public void deleteAccount(String accountNumber) {
        ListIterator<CCard> itr = ccards.listIterator();
        int position = 0;
        while (itr.hasNext()) {
            if (itr.next().getNumber().trim().equals(accountNumber.trim())) {
                itr.remove();
                notifyItemRemoved(position);
                ccardRef.child(accountNumber).setValue(null);
                break;
            }
            position++;
        }
    }

    @Subscribe()
    public void onCcardEvent(InsertEvent<CCard> insertEvent) {
        if (insertEvent.getItems() == null)
            return;
        for (CCard cCard : insertEvent.getItems()) {
            if (cCard != null)
                addOrUpdate(cCard);
        }
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvent || !dataSnapshot.exists())
            return;
        CCard cCard = dataSnapshot.getValue(CCard.class);
        if (cCard != null)
            addOrUpdate(cCard);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (!dataSnapshot.exists())
            return;
        CCard cCard = dataSnapshot.getValue(CCard.class);
        if (cCard == null)
            return;
        addOrUpdate(cCard);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists())
            return;
        CCard cCard = dataSnapshot.getValue(CCard.class);
        if (cCard == null)
            return;
        ListIterator<CCard> itr = ccards.listIterator();
        CCard oldcCard;
        int position = 0;
        boolean found = false;
        while (itr.hasNext()) {
            oldcCard = itr.next();
            if (oldcCard.getNumber().trim().equals(cCard.getNumber().trim())) {
                oldcCard.delete();
                itr.remove();
                found = true;
                break;
            }
            position++;
        }
        if (found) {
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //no shit givven
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "operation cancelled" + databaseError.getMessage());
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists())
            return;
        CCard cCard;
        List<CCard> ccards = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            cCard = ds.getValue(CCard.class);
            if (cCard != null)
                ccards.add(cCard);
        }
        Log.d(TAG, "fetched: " + ccards.toString());
        new InsertTask<>(cCardDao, this, true).execute(ccards.toArray(new CCard[ccards.size()]));
    }

    @Override
    public void onInsertTaskComplete(List<CCard> items) {
        ccards.clear();
        ccards.addAll(items);
        notifyDataSetChanged();
        ignoreChildEvent = false;
    }
}
