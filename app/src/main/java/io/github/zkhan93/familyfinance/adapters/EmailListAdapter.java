package io.github.zkhan93.familyfinance.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.callbacks.SubscribeEmailCallback;
import io.github.zkhan93.familyfinance.models.Email;
import io.github.zkhan93.familyfinance.viewholders.EmailVH;
import io.github.zkhan93.familyfinance.viewholders.EmptyVH;
import io.github.zkhan93.familyfinance.viewholders.SubscribeEmailVH;

/**
 * Created by zeeshan on 10/29/17.
 */

public class EmailListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ChildEventListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String TAG = EmailListAdapter.class.getSimpleName();
    private final static String PREF_KEY_SUBSCRIBED = "subscribed";
    private List<Email> emails;
    private SharedPreferences sharedPreferences;
    private SubscribeEmailCallback subscribeEmailCallback;
    private boolean subscribed;

    public EmailListAdapter(Context context, String familyId, SubscribeEmailCallback
            subscribeEmailCallback) {
        emails = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("emails").child(familyId).addChildEventListener(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        subscribed = sharedPreferences.getBoolean(PREF_KEY_SUBSCRIBED, false);
        this.subscribeEmailCallback = subscribeEmailCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "itemType:" + viewType);
        switch (viewType) {
            case ITEM_TYPE.EMPTY:
                return new EmptyVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .listitem_empty, parent, false), "blankEmail");
            case ITEM_TYPE.SUBSCRIBE:
                return new SubscribeEmailVH(LayoutInflater.from(parent.getContext()).inflate(R
                        .layout
                        .listitem_subscribe_email, parent, false), subscribeEmailCallback);
            default:
                return new EmailVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .listitem_email, parent, false));
        }
    }

    public void registerPreferenceChange() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public void unregisterPreferenceChange() {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "bindView: " + position + " : " + getItemViewType(position));
        if (getItemViewType(position) == ITEM_TYPE.NORMAL) {
            ((EmailVH) holder).setEmail(emails.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (emails.size() == 0 && position == 0) {
            if (!subscribed)
                return ITEM_TYPE.SUBSCRIBE;
            return ITEM_TYPE.EMPTY;
        }
        return ITEM_TYPE.NORMAL;
    }

    /**
     * Subscribed -> YES
     * show all emails
     * if no email show 1 empty
     * Subscribed -> False
     * show all item plus one subscribe item at top
     * if no email show only subscribe item
     *
     * @return
     */
    @Override
    public int getItemCount() {
        int size = emails == null ? 0 : emails.size();
        if (!subscribed)
            size += 1;
        return size == 0 ? 1 : size;
    }

    private interface ITEM_TYPE {
        int NORMAL = 0;
        int EMPTY = 1;
        int SUBSCRIBE = 2;
    }

    //Shared Preference
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_KEY_SUBSCRIBED)) {
            subscribed = sharedPreferences.getBoolean(PREF_KEY_SUBSCRIBED, false);
        }
    }

    //child event listener
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        if (dataSnapshot == null) {
            Log.d(TAG, "null datasnapshot");
            return;
        }
        Email email = dataSnapshot.getValue(Email.class);
        if (email == null) {
            Log.d(TAG, "cannot construct email from" + dataSnapshot.toString());
            return;
        }
        Log.d(TAG, "email added" + email.toString());
        emails.add(email);
        notifyItemInserted(emails.size());
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildChanged: " + dataSnapshot.toString());
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "onChildRemoved: " + dataSnapshot.toString());
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "onChildMoved: " + dataSnapshot.toString());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "onCacelled: " + databaseError.toString());
    }
}
