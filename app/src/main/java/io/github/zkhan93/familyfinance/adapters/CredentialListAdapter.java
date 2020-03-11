package io.github.zkhan93.familyfinance.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import io.github.zkhan93.familyfinance.App;
import io.github.zkhan93.familyfinance.R;
import io.github.zkhan93.familyfinance.models.Credential;
import io.github.zkhan93.familyfinance.models.CredentialDao;
import io.github.zkhan93.familyfinance.models.CredentialType;
import io.github.zkhan93.familyfinance.models.CredentialTypeDao;
import io.github.zkhan93.familyfinance.viewholders.CredentialTypeVH;
import io.github.zkhan93.familyfinance.viewholders.CredentialVH;

/**
 * Created by zeeshan on 12/23/17.
 */

public class CredentialListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ChildEventListener,
        ValueEventListener {
    public static final String TAG = CredentialListAdapter.class.getSimpleName();
    private String familyId;
    private List<CredentialWrapper> wrappedCredentials;
    private List<CredentialType> credentialTypes;
    private HashMap<String, List<Credential>> typeToCredentialsMap;
    private boolean ignoreChildEvents;
    private CredentialDao credentialDao;
    private CredentialTypeDao credentialTypeDao;
    private DatabaseReference credRef;
    private ValueEventListener credentialTypeValueListener;

    private CredentialVH.CredentialInteraction credentialInteraction;
    private View.OnClickListener groupClickListener;

    {
        credentialTypeValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CredentialType credentialType;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    credentialType = ds.getValue(CredentialType.class);
                    if (credentialType == null) continue;
                    credentialType.setId(ds.getKey());
                    credentialType.expanded = false;
                    credentialTypes.add(credentialType);
                    wrappedCredentials.add(new CredentialWrapper(TYPE.HEADER, null,
                            credentialType));
                    typeToCredentialsMap.put(credentialType.getId(), new ArrayList<Credential>());
                }
                credRef.addChildEventListener(CredentialListAdapter.this);
                credRef.addListenerForSingleValueEvent(CredentialListAdapter.this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled" + databaseError.getMessage());
            }
        };
        groupClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: close all other group and remove their childs
                String groupKey = (String) view.getTag();
                Log.d(TAG, "groupClick" + groupKey);
                //extract the group object and its position in list
                CredentialType clickedCredentialType = null;
                int clickedGroupIndex = 0;

                for (CredentialWrapper wrappedCredential : wrappedCredentials) {
                    if (wrappedCredential.type == TYPE.HEADER && wrappedCredential.credentialType
                            .getId().equals(groupKey)) {
                        clickedCredentialType = wrappedCredential.credentialType;
                        break;
                    }
                    clickedGroupIndex++;
                }
                Log.d(TAG, "group at " + clickedGroupIndex);
                if (clickedCredentialType == null) return;
                if (clickedCredentialType.expanded) {
                    Log.d(TAG, "hiding");
                    int numberOfChilds = typeToCredentialsMap.get(groupKey).size();
                    int childStartFrom = clickedGroupIndex + 1;
                    for (int i = 0; i < numberOfChilds; i++) {
                        wrappedCredentials.remove(childStartFrom);
                        notifyItemRemoved(childStartFrom);
                        Log.d(TAG, "removing from " + childStartFrom);
                    }
                    clickedCredentialType.expanded = false;
                } else {
                    Log.d(TAG, "expanding");
                    int i = 1; //need to insert after the group heading
                    for (Credential credential : typeToCredentialsMap.get(groupKey)) {
                        wrappedCredentials.add(clickedGroupIndex + i, new CredentialWrapper(TYPE
                                .CHILD,
                                credential, null));
                        notifyItemInserted(clickedGroupIndex + i);
                        Log.d(TAG, "inserted at " + (clickedGroupIndex + i));
                        i++;
                    }
                    clickedCredentialType.expanded = true;
                }

                StringBuilder strb = new StringBuilder();
                CredentialWrapper cw;
                for (int x = 0; x < wrappedCredentials.size(); x++) {
                    cw = wrappedCredentials.get(x);
                    strb.append(String.valueOf(x));
                    strb.append(":");
                    strb.append(cw.type == TYPE.HEADER ? "HEADER" : "CHILD");
                    strb.append(", ");
                }
            }
        };
    }

    public CredentialListAdapter(Context context, CredentialVH.CredentialInteraction
            credentialInteraction, String familyId) {
        this.familyId = familyId;
        this.credentialInteraction = credentialInteraction;
        wrappedCredentials = new ArrayList<>();
        typeToCredentialsMap = new HashMap<>();
        credentialTypes = new ArrayList<>();
        ignoreChildEvents = true;
        credentialDao = ((App) context.getApplicationContext()).getDaoSession().getCredentialDao();
        credRef = FirebaseDatabase.getInstance().getReference("credentials").child
                (familyId);
        DatabaseReference credTypeRef = FirebaseDatabase.getInstance().getReference
                ("credentialTypes");
        credTypeRef.addListenerForSingleValueEvent(credentialTypeValueListener);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE.HEADER) {
            return new CredentialTypeVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_header, parent, false), groupClickListener);

        } else if (viewType == TYPE.CHILD) {
            return new CredentialVH(LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .listitem_credential, parent, false), credentialInteraction);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CredentialTypeVH) {
            CredentialType credentialType = wrappedCredentials.get(position).credentialType;
            ((CredentialTypeVH) holder).setView(credentialType, typeToCredentialsMap.get
                    (credentialType.getId()).size());
        } else if (holder instanceof CredentialVH) {
            ((CredentialVH) holder).setView(wrappedCredentials.get(position).credential);
        }
    }

    @Override
    public int getItemCount() {
        return wrappedCredentials.size();
    }

    @Override
    public int getItemViewType(int position) {
        return wrappedCredentials.get(position).type;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "added " + dataSnapshot.toString());
        if (ignoreChildEvents) return;
        Credential credential = dataSnapshot.getValue(Credential.class);
        if (credential == null) return;
        credential.setId(dataSnapshot.getKey());
        List<Credential> credentials = typeToCredentialsMap.get(credential.getTypeId());
        if (credentials == null) return; //todo: add credntials with missing typeId to others
        credentials.add(credential);
        int insertAt = 0;
        for (CredentialWrapper cw : wrappedCredentials) {
            insertAt++;
            if (cw.type == TYPE.HEADER &&
                    cw.credentialType.getId().equals(credential.getTypeId())) {
                if (cw.credentialType.expanded) {
                    wrappedCredentials.add(insertAt, new CredentialWrapper(TYPE.CHILD, credential,
                            null));
                    notifyItemChanged(insertAt - 1);
                    notifyItemInserted(insertAt);
                } else {
                    notifyItemChanged(insertAt - 1);
                }
                break;
            }
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "changed " + dataSnapshot.toString());
        Credential credential = dataSnapshot.getValue(Credential.class);
        if (credential == null) return;
        credential.setId(dataSnapshot.getKey());
        List<Credential> credentials = typeToCredentialsMap.get(credential.getTypeId());
        if (credentials == null) return; //todo: add credntials with missing typeId to others
        int i = 0;
        for (Credential c : credentials) {
            if (c.getId().equals(credential.getId())) {
                credentials.set(i, credential);
                break;
            }
            i++;
        }
        int insertAt = 0;
        for (CredentialWrapper cw : wrappedCredentials) {
            if (cw.type == TYPE.CHILD && cw.credential.getId().equals(credential.getId())) {
                wrappedCredentials.set(insertAt, new CredentialWrapper(TYPE.CHILD, credential,
                        null));
                notifyItemChanged(insertAt);
                break;
            }
            insertAt++;
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.d(TAG, "removed " + dataSnapshot.toString());
        Credential credential = dataSnapshot.getValue(Credential.class);
        if (credential == null) return;
        credential.setId(dataSnapshot.getKey());
        List<Credential> credentials = typeToCredentialsMap.get(credential.getTypeId());
        if (credentials == null) return;
        int i = 0;
        ListIterator<Credential> itr=credentials.listIterator();
        Credential cred;
        while (itr.hasNext()) {
            cred = itr.next();
            if (cred.getId().equals(credential.getId())) {
                itr.remove();
                break;
            }
            i++;
        }
        ListIterator<CredentialWrapper> cwItr = wrappedCredentials.listIterator();
        CredentialWrapper cw;
        int index=0;
        while (cwItr.hasNext()) {
            cw = cwItr.next();
            if (cw.type == TYPE.CHILD && cw.credential.getId().equals(credential.getId())) {
                cwItr.remove();
                notifyItemRemoved(index);
                break;
            }
            index++;
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        if (ignoreChildEvents) return;
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "onCancelled cred " + databaseError);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        Credential credential;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            credential = ds.getValue(Credential.class);
            if (credential == null) continue;
            credential.setId(ds.getKey());
            List<Credential> credentials = typeToCredentialsMap.get(credential.getTypeId());
            if (credentials == null)
                credentials = new ArrayList<>();
            credentials.add(credential);
            typeToCredentialsMap.put(credential.getTypeId(), credentials);

        }
        for (Map.Entry<String, List<Credential>> entry : typeToCredentialsMap.entrySet()) {
            Log.d(TAG, entry.getKey() + " " + entry.getValue().size() + " " + entry.getValue()
                    .toString());
        }
        notifyDataSetChanged();
        ignoreChildEvents = false;

    }

    public interface TYPE {
        int HEADER = 0;
        int CHILD = 1;
    }

    public class CredentialWrapper {
        int type;
        public Credential credential;
        public CredentialType credentialType;

        public CredentialWrapper(int type, Credential credential, CredentialType credentialType) {
            this.type = type;
            this.credential = credential;
            this.credentialType = credentialType;
        }
    }
}
