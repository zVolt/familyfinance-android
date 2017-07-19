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

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
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
        this.ccards = ccards == null ? new ArrayList<CCard>() : ccards;
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
        ccards.clear();
        ccards.addAll(data);
        notifyDataSetChanged();
        ccardRef.addListenerForSingleValueEvent(this);
        ccardRef.addChildEventListener(this);
    }

    public void notifyItemChanged(CCard _ccard) {
        int position = 0;
        boolean found = false;
        for (CCard ccard : ccards) {
            if (ccard.getNumber().trim().equals(_ccard.getNumber().trim())) {
                found = true;
                ccard.update(); // fetch the item from database again
                break;
            }
            position++;
        }
        if (found)
            notifyItemChanged(position);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvent || !dataSnapshot.exists())
            return;
        CCard cCard = dataSnapshot.getValue(CCard.class);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        if (!dataSnapshot.exists())
            return;
        CCard cCard;
        List<CCard> cCards = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            cCard = ds.getValue(CCard.class);
            if (cCard != null)
                ccards.add(cCard);
        }
        new InsertTask<>(cCardDao, this, true).execute(cCards.toArray(new CCard[cCards.size()]));
    }

    @Override
    public void onInsertTaskComplete(List<CCard> items) {
        ccards.clear();
        ccards.addAll(items);
        notifyDataSetChanged();
        ignoreChildEvent = false;
    }
}
