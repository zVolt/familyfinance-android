package io.github.zkhan93.familyfinance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.CredentialType;
import io.github.zkhan93.familyfinance.util.Util;

/**
 * Created by zeeshan on 1/28/18.
 */

public class CredentialTypeSpinnerAdapter extends BaseAdapter implements ValueEventListener {
    public static final String TAG = CredentialTypeSpinnerAdapter.class.getSimpleName();
    private List<CredentialType> credentialTypeList;
    private String selectCredId;
    private CredentialTypeSpinnerAdapter.OnLoadCompleteListener onLoadCompleteListener;

    public CredentialTypeSpinnerAdapter(OnLoadCompleteListener onLoadCompleteListener) {
        this.credentialTypeList = new ArrayList<>();
        this.selectCredId = selectCredId;
        this.onLoadCompleteListener = onLoadCompleteListener;
        FirebaseDatabase.getInstance().getReference().child("credentialTypes")
                .addListenerForSingleValueEvent(this);
    }

    @Override
    public int getCount() {
        return credentialTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return credentialTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                    .spinneritem, viewGroup, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        ImageView icon = convertView.findViewById(R.id.logo);
        CredentialType credentialType = credentialTypeList.get(position);
        name.setText(credentialType.getName());
        if (URLUtil.isValidUrl(credentialType.getIconUrl()))
            Glide.with(icon.getContext()).load(credentialType.getIconUrl()).into(icon);
        return convertView;
    }

    public int getIndexOfCredentialType(String typeId) {
        for (int i = 0; i < credentialTypeList.size(); i++) {
            if (credentialTypeList.get(i).getId().equals(typeId)) {
                return i;
            }
        }
        return -1;
    }

    public int getIndexOfType(CredentialType credentialType) {
        return getIndexOfCredentialType(credentialType.getId());
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null) return;
        CredentialType credentialType;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            credentialType = ds.getValue(CredentialType.class);
            if (credentialType == null) continue;
            credentialType.setId(ds.getKey());
            credentialTypeList.add(credentialType);
        }
        notifyDataSetChanged();
        if (onLoadCompleteListener != null)
            onLoadCompleteListener.OnCredentialTypesLoadComplete();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Util.Log.d(TAG, "onCancelled %s", databaseError.getMessage());
    }

    public interface OnLoadCompleteListener {
        void OnCredentialTypesLoadComplete();
    }
}
