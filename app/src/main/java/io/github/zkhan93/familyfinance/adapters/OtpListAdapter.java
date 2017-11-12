package io.github.zkhan93.familyfinance.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.models.OtpDao;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.viewholders.EmptyVH;
import io.github.zkhan93.familyfinance.viewholders.OtpVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class OtpListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        LoadFromDbTask
                .Listener<Otp>, ValueEventListener, ChildEventListener, InsertTask.Listener<Otp> {
    public static final String TAG = OtpListAdapter.class.getSimpleName();
    private ArrayList<Otp> otps;
    private String familyId;
    private Query otpRef;
    private OtpDao otpDao;
    private boolean ignoreChildEvents;
    private ItemInsertedListener itemInsertedListener;

    public OtpListAdapter(App app, String familyId, ItemInsertedListener itemInsertedListener) {
        this.itemInsertedListener = itemInsertedListener;
        this.otps = new ArrayList<>();
        this.familyId = familyId;
        if (familyId == null)
            return;
        otpDao = app.getDaoSession().getOtpDao();
        otpRef = FirebaseDatabase.getInstance().getReference("otps").child(familyId).orderByChild
                ("timestamp").limitToLast(10);
        new LoadFromDbTask<>(app.getDaoSession().getOtpDao(), this).execute();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.EMPTY)
            return new EmptyVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_empty, parent, false), "blankOTP");
        return new OtpVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_otp, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_TYPE.NORMAL)
            ((OtpVH) holder).setOtp(otps.get(position));
    }

    @Override
    public int getItemCount() {
        if (otps.size() == 0) return 1;
        return otps.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (otps.size() == 0) return ITEM_TYPE.EMPTY;
        return ITEM_TYPE.NORMAL;
    }

    public interface ITEM_TYPE {
        int NORMAL = 0;
        int EMPTY = 1;
    }

    public void registerForEvents() {
        EventBus.getDefault().register(this);
    }

    public void unregisterFromEvents() {
        EventBus.getDefault().unregister(this);
    }

    private void insertOrUpdate(Otp newOtp) {
        ListIterator<Otp> itr = otps.listIterator();
        int position = 0;
        int insertPosition = -1;//invalid
        boolean found = false;
        Otp oldOtp;
        while (itr.hasNext()) {
            oldOtp = itr.next();
            if (insertPosition == -1 && oldOtp.getTimestamp() <= newOtp.getTimestamp())
                insertPosition = position;
            if (oldOtp.getId().equals(newOtp.getId())) {
                //already exist;
                found = true;
                break;
            }
            position++;

        }
        if (!found) {
            if (insertPosition == -1) insertPosition = 0;
            otps.add(insertPosition, newOtp);
            notifyItemInserted(insertPosition);
            if (itemInsertedListener != null)
                itemInsertedListener.onItemAdded(insertPosition);
        }
    }

    @Override
    public void onLoadTaskComplete(List<Otp> data) {
        // initial load from local db
        otps.clear();
        otps.addAll(data);
        Collections.sort(otps, Otp.BY_TIMESTAMP);
        notifyDataSetChanged();
        otpRef.addListenerForSingleValueEvent(this);
        otpRef.addChildEventListener(this);
        ignoreChildEvents = true;
    }

    @Subscribe()
    public void onOtpAdded(List<Otp> otps) {
        for (Otp otp : otps)
            insertOrUpdate(otp);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvents || !dataSnapshot.exists())
            return;
        Otp otp = dataSnapshot.getValue(Otp.class);
        if (otp != null) {
            otp.setFromMemberId(dataSnapshot.child("from").child("id").getValue(String.class));
            insertOrUpdate(otp);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (!dataSnapshot.exists())
            return;
        Otp otp = dataSnapshot.getValue(Otp.class);
        if (otp != null) {
            otp.setFromMemberId(dataSnapshot.child("from").child("id").getValue(String.class));
            insertOrUpdate(otp);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists())
            return;
        Otp newOtp = dataSnapshot.getValue(Otp.class);
        if (newOtp == null)
            return;
        ListIterator<Otp> itr = otps.listIterator();
        Otp oldOtp;
        int position = 0;
        boolean found = false;
        while (itr.hasNext()) {
            oldOtp = itr.next();
            if (oldOtp.getId().equals(newOtp.getId())) {
                itr.remove();
                found = true;
                break;
            }
            position++;
        }
        if (found) {
            notifyItemRemoved(position);
            otpDao.deleteByKey(newOtp.getId());
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//no shit given
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists())
            return;
        Otp otp;
        List<Otp> otps = new ArrayList<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            otp = ds.getValue(Otp.class);
            if (otp != null) {
                otp.setFromMemberId(ds.child("from").child("id").getValue(String.class));
                otps.add(otp);
            }
        }
        new InsertTask<>(otpDao, this, true).execute(otps.toArray(new Otp[otps.size()]));
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "cancelled");
    }

    @Override
    public void onInsertTaskComplete(List<Otp> items) {
        //initial data load callback
        otps.clear();
        otps.addAll(items);
        Collections.sort(otps, Otp.BY_TIMESTAMP);
        notifyDataSetChanged();
        ignoreChildEvents = false;
    }

    public interface ItemInsertedListener {
        void onItemAdded(int position);
    }
}
