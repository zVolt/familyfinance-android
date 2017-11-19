package io.github.zkhan93.familyfinance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 11/12/17.
 */

public class BankSpinnerAdapter extends BaseAdapter implements ValueEventListener {
    public static final String TAG = BankSpinnerAdapter.class.getSimpleName();
    public static final String OTHER_BANK = "ZZZOTHER";

    private List<BankSpinnerItem> bankSpinnerItems;
    private LayoutInflater inflater;
    private DatabaseReference bankLogoRef;
    private OnLoadCompleteListener onLoadCompleteListener;
    private boolean loaded;


    public BankSpinnerAdapter(Context context) {
        bankSpinnerItems = new ArrayList<>();
        Query bankQuery = FirebaseDatabase.getInstance().getReference("banks").orderByKey();
        bankQuery.addListenerForSingleValueEvent(this);
        bankLogoRef = FirebaseDatabase.getInstance().getReference("images").child("banks");
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return bankSpinnerItems.size();
    }

    @Override
    public Object getItem(int i) {
        return bankSpinnerItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinneritem_bank, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.logo = convertView.findViewById(R.id.logo);
            viewHolder.name = convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();
        BankSpinnerItem bankSpinnerItem = bankSpinnerItems.get(position);
        if (bankSpinnerItem.getId().equals(OTHER_BANK)) {
            viewHolder.logo.setVisibility(View.GONE);
        } else {
            viewHolder.logo.setVisibility(View.VISIBLE);
            bankLogoRef.child(bankSpinnerItem.getId()).addListenerForSingleValueEvent(viewHolder);
        }
        viewHolder.name.setText(bankSpinnerItem.getName());
        return convertView;
    }

    public int getPosition(String id) {
        int i = 0;
        for (BankSpinnerItem item : bankSpinnerItems) {
            if (item.getId().equals(id))
                return i;
            i += 1;
        }
        return -1;
    }

    public String getBankId(int position) {
        if (position >= 0 && position < getCount())
            return bankSpinnerItems.get(position).getId();
        return null;
    }

    public void setOnLoadCompleteListener(OnLoadCompleteListener onLoadCompleteListener) {
        if (loaded)
            onLoadCompleteListener.onLoadComplete();
        else
            this.onLoadCompleteListener = onLoadCompleteListener;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        bankSpinnerItems.clear();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            bankSpinnerItems.add(new BankSpinnerItem(ds.getKey(), ds.getValue(String.class)));
        }
        notifyDataSetChanged();
        loaded = true;
        if (onLoadCompleteListener != null)
            onLoadCompleteListener.onLoadComplete();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private class BankSpinnerItem {
        String name;
        String id;

        BankSpinnerItem(String id, String name) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    private class ViewHolder implements ValueEventListener {
        ImageView logo;
        TextView name;

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot != null) {
                String url = dataSnapshot.getValue(String.class);
                Glide.with(logo.getContext()).load(url).into(logo);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Util.Log.d(TAG, "loading bank logo failed");
        }
    }

    public interface OnLoadCompleteListener {
        void onLoadComplete();
    }
}
