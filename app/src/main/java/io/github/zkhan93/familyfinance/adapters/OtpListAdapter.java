package io.github.zkhan93.familyfinance.adapters;

import android.content.SharedPreferences;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.MemberDao;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.tasks.InsertTask;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.EmptyVH;
import io.github.zkhan93.familyfinance.viewholders.OtpVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class OtpListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ValueEventListener, ChildEventListener, InsertTask.Listener<Otp>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = OtpListAdapter.class.getSimpleName();
    private ArrayList<Otp> otps;
    private Query otpRef;
    private boolean ignoreChildEvents;
    private ItemInsertedListener itemInsertedListener;
    private int pageLoadSize = 40;
    private int currentPage = -1;
    private String familyId;
    private MemberDao memberDao;
    private boolean loading;

    public OtpListAdapter(App app, String familyId, ItemInsertedListener itemInsertedListener) {
        this.itemInsertedListener = itemInsertedListener;
        this.familyId = familyId;
        this.otps = new ArrayList<>();
        if (familyId == null)
            return;
        otpRef = FirebaseDatabase.getInstance().getReference("otps").child(familyId);
        ignoreChildEvents = true;
        loading = true;
        otpRef.orderByKey().limitToLast(pageLoadSize).addListenerForSingleValueEvent(this);
//        otpRef.orderByKey().limitToLast(1).addChildEventListener(this);
        memberDao = app.getDaoSession().getMemberDao();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.EMPTY)
            return new EmptyVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_empty, parent, false), "blankOTP");
        return new OtpVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .listitem_otp, parent, false), familyId);
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

    public boolean loadNextPage() {
        if (loading) return false;
        Util.Log.d(TAG, "current page %s, items now %d after %d", currentPage,
                otps.size(), otps.size() + pageLoadSize);
        Util.Log.d(TAG, "endAt %s", otps.get(otps.size() - 1).getId());
        FirebaseDatabase.getInstance().getReference("otps")
                .child(familyId)
                .orderByKey()
                .endAt(otps.get(otps.size() - 1).getId())
                .limitToLast(pageLoadSize + 1)
                .addListenerForSingleValueEvent(this);
        loading = true;
        return true;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvents || !dataSnapshot.exists())
            return;
        Otp otp = dataSnapshot.getValue(Otp.class);
        if (otp != null) {
            if (otp.getFromMemberId() == null || otp.getFromMemberId().isEmpty())
                otp.setFromMemberId(dataSnapshot.child("from").child("id").getValue(String.class));
            otp.setId(dataSnapshot.getKey());
            otp.setFrom(memberDao.load(otp.getFromMemberId()));
            if (otp.getClaimedByMemberId() != null && !otp.getClaimedByMemberId().isEmpty())
                otp.setClaimedby(memberDao.load(otp.getClaimedByMemberId()));
            otps.add(0, otp);
            notifyItemInserted(0);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        if (!dataSnapshot.exists())
            return;
        Otp otp = dataSnapshot.getValue(Otp.class);
        if (otp != null) {
            if (otp.getFromMemberId() == null || otp.getFromMemberId().isEmpty())
                otp.setFromMemberId(dataSnapshot.child("from").child("id").getValue(String.class));
            otp.setId(dataSnapshot.getKey());
            Util.Log.d(TAG, "otp updated %s", otp.toString());
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        if (!dataSnapshot.exists())
            return;
        Otp newOtp = dataSnapshot.getValue(Otp.class);
        if (newOtp == null)
            return;
        newOtp.setId(dataSnapshot.getKey());
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
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        //no shit given
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Util.Log.d(TAG, "onDataChange on %d page %d children", currentPage, dataSnapshot
                .getChildrenCount());
        if (!dataSnapshot.exists()) {
            Log.d(TAG, "no snapshot return");
            return;
        }
        Otp otp;
        int startPos = otps.size();
        boolean overridinglastItem = false;
        if (startPos > 0) {
            overridinglastItem = true;
            startPos -= 1;
            otps.remove(startPos);
        }
        Log.d(TAG, "strart pos" + startPos);
        int itemCount = 0;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            otp = ds.getValue(Otp.class);
            if (otp != null) {
                if (otp.getFromMemberId() == null || otp.getFromMemberId().isEmpty())
                    otp.setFromMemberId(ds.child("from").child("id").getValue(String.class));
                otp.setId(ds.getKey());
                otp.setFrom(memberDao.load(otp.getFromMemberId()));
                if (otp.getClaimedByMemberId() != null && !otp.getClaimedByMemberId().isEmpty())
                    otp.setClaimedby(memberDao.load(otp.getClaimedByMemberId()));
                Util.Log.d(TAG, "item added at %d %s - on %s", otps.size(), otp.getId(),
                        SimpleDateFormat.getDateInstance().format(new Date(otp.getTimestamp())));
                otps.add(startPos, otp);
                itemCount++;
            }
        }
        ignoreChildEvents = false;
        loading = false;
        currentPage = otps.size() / pageLoadSize;
        if (overridinglastItem) {
            itemCount -= 1;//did not updated the last item already in list
            startPos += 1;
        }

        Util.Log.d(TAG, "inserted staring from %d, added %d", startPos, itemCount - 1);
        Util.Log.d(TAG, "page is now %d size is %d\n------------", currentPage, otps.size());
        notifyItemRangeInserted(startPos, itemCount - 1);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "hmm a preference has changed with key: " + key);
        if (key != null && key.equals("filter_sms_by_member")) {
            String memberId = sharedPreferences.getString(key, null);
            filterByMember(memberId);
        }
    }

    private ArrayList<Otp> _otps;
    private boolean isfilterApplied;

    private void filterByMember(String memberId) {
        Log.d(TAG, "is see you want to filter by " + memberId);
        if (memberId == null || memberId.isEmpty()) {
            if (isfilterApplied &&
                    _otps != null &&
                    otps.size() > 0
                    ) {
                otps = _otps;
                _otps = null;
                isfilterApplied = false;
                notifyDataSetChanged();
            }
            return;
        }
        if (_otps == null) {
            _otps = new ArrayList<>();
            _otps.addAll(otps);
        } else {
            otps = new ArrayList<>();
            otps.addAll(_otps);
        }
        ListIterator<Otp> itr = otps.listIterator();
        Otp otp;
        while (itr.hasNext()) {
            otp = itr.next();
            if (!otp.getFromMemberId().equals(memberId)) {
                itr.remove();
                isfilterApplied = true;
            }
        }
        if (isfilterApplied)
            notifyDataSetChanged();
    }

    private boolean isSearchApplied;
    private ArrayList<Otp> __otps;

    public void filterByString(String text) {
        Log.d(TAG, "is see you want to filter by " + text);
        if ((text == null || text.isEmpty()) && isfilterApplied) {
            otps = __otps;
            __otps = null;
            isSearchApplied = false;
            notifyDataSetChanged();
            return;
        }
        if (__otps == null) {
            __otps = new ArrayList<>();
            __otps.addAll(otps);
        } else {
            otps = new ArrayList<>();
            otps.addAll(__otps);
        }
        ListIterator<Otp> itr = otps.listIterator();
        Otp otp;
        String name, content;
        while (itr.hasNext()) {
            otp = itr.next();
            name = otp.getFrom().getName().toLowerCase();
            content = otp.getContent().toLowerCase();
            if (!name.contains(text) &&
                    !text.contains(name) &&
                    !content.contains(text) &&
                    !text.contains(content)
                    ) {
                itr.remove();
                isSearchApplied = true;
            }
        }
        if (isSearchApplied)
            notifyDataSetChanged();
    }

    public interface ItemInsertedListener {
        void onItemAdded(int position);
    }
}
