package io.github.zkhan93.familyfinance.adapters;

import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.MemberDao;
import io.github.zkhan93.familyfinance.models.Otp;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.NoItemVH;
import io.github.zkhan93.familyfinance.viewholders.OtpVH;

/**
 * Created by zeeshan on 8/7/17.
 */

public class OtpListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = OtpListAdapter.class.getSimpleName();
    private ArrayList<Otp> otps;
    //    private Query otpRef;
    private boolean ignoreChildEvents;
    private ItemInsertedListener itemInsertedListener;
    private int pageSize = 50;
    private int currentPage = -1;
    private String familyId, lastLoadedItem;
    private MemberDao memberDao;
    private boolean loading;

    private String year;
    private String month;

    public OtpListAdapter(App app, String familyId, ItemInsertedListener itemInsertedListener) {
        Calendar calendar = Calendar.getInstance();
        year = String.valueOf(calendar.get(Calendar.YEAR));
        month = String.valueOf(calendar.get(Calendar.MONTH));

        this.itemInsertedListener = itemInsertedListener;
        this.familyId = familyId;
        this.otps = new ArrayList<>();
        if (familyId == null)
            return;
        memberDao = app.getDaoSession().getMemberDao();
        FirebaseDatabase.getInstance().getReference("otps").child(familyId).child(year).child
                (month).keepSynced(true);
        loadFirstPage();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.EMPTY)
            return new NoItemVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
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

    public boolean loadNextPage() {
        if (loading) {
            Log.d(TAG, "already loading currentSize:" + otps.size());
            return false;
        }
        if (lastLoadedItem == null || lastLoadedItem.isEmpty()) {
            Log.d(TAG, "lastLoadedItem is empty probably because loadFirstPage was not called or " +
                    "not completed successfully");
            otps.clear();
            notifyDataSetChanged();
            loadFirstPage();
            return true;
        }
        Util.Log.d(TAG, "items now %d after %d", otps.size(), otps.size() + pageSize);
        Query otpRef = FirebaseDatabase.getInstance().getReference("otps").child(familyId).child
                (year).child(month)
                .orderByKey().endAt(lastLoadedItem).limitToLast(pageSize + 1);
        List<Otp> tmpOtps = new ArrayList<>();
        loading = true;
        otpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Otp otp;
                if (dataSnapshot.getChildrenCount() == 0)
                    noMoreItems();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, ds.getKey());
                    otp = ds.getValue(Otp.class);
                    if (otp == null)
                        return;
                    if (otp.getFromMemberId() == null || otp.getFromMemberId().isEmpty())
                        otp.setFromMemberId(ds.child("from").child("id").getValue(String.class));
                    otp.setId(ds.getKey());
                    otp.setFrom(memberDao.load(otp.getFromMemberId()));
                    if (otp.getClaimedByMemberId() != null && !otp.getClaimedByMemberId().isEmpty())
                        otp.setClaimedby(memberDao.load(otp.getClaimedByMemberId()));
                    tmpOtps.add(otp);
                }
                if (tmpOtps.size() > 1) {
                    tmpOtps.remove(tmpOtps.size() - 1); // skip the last item since it was already
                    // loaded in previous page
                    lastLoadedItem = tmpOtps.get(0).getId();
                    Log.d(TAG, "LastItemLoaded: " + lastLoadedItem);
                    Collections.reverse(tmpOtps);
                    int lastPosition = otps.size();
                    otps.addAll(tmpOtps);
                    notifyItemRangeInserted(lastPosition, otps.size() - 1);
                }
                loading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("cancelled loading");
                loading = false;
            }
        });
        return true;
    }

    private void noMoreItems() {
        loading = false;
    }

    private void loadFirstPage() {

        Query otpRef = FirebaseDatabase.getInstance().getReference("otps").child(familyId).child
                (year).child(month)
                .orderByKey().limitToLast(pageSize);
        List<Otp> tmpOtps = new ArrayList<>();
        loading = true;

        otpRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Otp otp;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d(TAG, ds.getKey());
                    otp = ds.getValue(Otp.class);
                    if (otp == null) {
                        Log.d(TAG, "otp is null");
                        return;
                    }
                    if (otp.getFromMemberId() == null || otp.getFromMemberId().isEmpty())
                        otp.setFromMemberId(ds.child("from").child("id").getValue(String.class));
                    otp.setId(ds.getKey());
                    otp.setFrom(memberDao.load(otp.getFromMemberId()));
                    if (otp.getClaimedByMemberId() != null && !otp.getClaimedByMemberId().isEmpty())
                        otp.setClaimedby(memberDao.load(otp.getClaimedByMemberId()));
                    tmpOtps.add(otp);
                }
                if (tmpOtps.size() > 0) {
                    lastLoadedItem = tmpOtps.get(0).getId();
                    Log.d(TAG, "LastItemLoaded: " + lastLoadedItem);
                    Collections.reverse(tmpOtps);
                    otps.addAll(tmpOtps);
                    notifyDataSetChanged();
                }
                loading = false;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("cancelled loading");
                loading = false;
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key != null && key.equals("filter_sms_by_member")) {
            String memberId = sharedPreferences.getString(key, null);
            filterByMember(memberId);
        }
    }

    private ArrayList<Otp> _otps;
    private boolean isfilterApplied;

    private void filterByMember(String memberId) {
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
