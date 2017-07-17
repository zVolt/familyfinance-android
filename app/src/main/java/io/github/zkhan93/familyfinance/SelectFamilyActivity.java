package io.github.zkhan93.familyfinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.adapters.RequestListAdapter;
import io.github.zkhan93.familyfinance.events.CheckRequestEvent;
import io.github.zkhan93.familyfinance.events.DeleteRequestEvent;
import io.github.zkhan93.familyfinance.events.FamilySetEvent;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.viewholders.RequestVH;

public class SelectFamilyActivity extends AppCompatActivity implements ValueEventListener,
        RequestVH.ItemInteractionListener,PreferenceChangeListener {

    public static final String TAG = SelectFamilyActivity.class.getSimpleName();

    @BindView(R.id.family_id)
    EditText edtTxtFamilyId;
    @BindView(R.id.join_family)
    Button joinFamily;
    @BindView(R.id.start_new)
    Button startNew;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.requests)
    RecyclerView requestList;

    private ProgressDialog progressDialog;
    private DatabaseReference familyRef;
    private DatabaseReference requestRef;
    private String familyId;
    private Member me;
    private RequestListAdapter requestListAdapter;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_family);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        message.setVisibility(View.GONE);
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        FirebaseUser fbUser = FirebaseAuth
                .getInstance().getCurrentUser();
        if (fbUser == null) {
            Log.d(TAG, "user not logged in ");
            Toast.makeText(getApplicationContext(), "You are not logged in", Toast
                    .LENGTH_SHORT).show();
            finish();
        }
        me = ((App) getApplication()).getDaoSession().getMemberDao().load(fbUser.getUid());
        if (me == null)
            Log.d(TAG, "member not found in local db");
        progressDialog = new ProgressDialog(this);
        familyRef = FirebaseDatabase.getInstance().getReference("family");
        requestRef = FirebaseDatabase.getInstance().getReference("requests");

        requestListAdapter = new RequestListAdapter((App) getApplication(), me);
        requestList.setLayoutManager(new LinearLayoutManager(this));
        requestList.setAdapter(requestListAdapter);
    }

    @OnClick({R.id.start_new, R.id.join_family})
    public void onClick(Button button) {
        message.setVisibility(View.GONE);
        switch (button.getId()) {
            case R.id.join_family:
                //TODO: verify family id and add a request
                familyId = edtTxtFamilyId.getText().toString().trim();
                progressDialog.setMessage("Please wait verifying family ...");
                progressDialog.show();
                familyRef.child(familyId)
                        .addListenerForSingleValueEvent(this);
                Log.d(TAG, "joinFamily: " + familyId);
                break;
            case R.id.start_new:
                Log.d(TAG, "startNew family");
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String moderatorId = null;
        if (dataSnapshot != null)
            moderatorId = dataSnapshot.child("moderator").child("id").getValue(String.class);
        if (moderatorId == null) {
            message.setText("Invalid Family Id");
            message.setTextColor(ContextCompat.getColor(this, R.color.md_red_500));
            message.setVisibility(View.VISIBLE);
        } else {
            long now = Calendar.getInstance().getTimeInMillis();
            Map<String, Object> updates = new HashMap<>();

            String partialNode = "requests/" + familyId + "/" + me.getId();
            updates.put(partialNode + "/requestedOn", now);

            updates.put(partialNode + "/updatedOn", now);
            updates.put(partialNode + "/name", me.getName());
            updates.put(partialNode + "/email", me.getEmail());
            updates.put(partialNode + "/profilePic", me.getProfilePic());

            partialNode = "users/" + me.getId() + "/requests/" + familyId;
            updates.put(partialNode + "/approved", false);
            updates.put(partialNode + "/familyId", familyId);
            updates.put(partialNode + "/requestedOn", now);
            updates.put(partialNode + "/updatedOn", now);

            FirebaseDatabase.getInstance().getReference().updateChildren(updates);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString
                    ("requestFamilyId", familyId).apply();
            message.setText("Request send to moderator of the family");
            message.setTextColor(ContextCompat.getColor(this, R.color.md_green_500));
            message.setVisibility(View.VISIBLE);
        }
        progressDialog.hide();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "cancelled");
    }

    @Override
    public void deleteRequest(String familyId) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("users/" + me.getId() + "/requests/" + familyId, null);
        updates.put("requests/" + familyId + "/" + me.getId(), null);
        FirebaseDatabase.getInstance().getReference().updateChildren(updates);
    }

    /**
     * Check whether I am a moderator or an approved member of this familyId
     *
     * @param familyId
     */
    public void checkActiveFamily(String familyId) {
        progressDialog.setMessage("Checking request status");
        progressDialog.show();
        if (familyId == null)
            return;
        //Todo: check for approved status
        requestRef.child(familyId).child(me.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        progressDialog.hide();
                        if (!dataSnapshot.exists()) {
                            message.setText("No such request found, Try requesting first");
                            return;
                        }

                        if (dataSnapshot.hasChild("blocked")) {
                            //You just got blocked :P Lol bad
                            //remove this blocked from firebase to unblock yourself
                        } else if (dataSnapshot.hasChild("approved")) {
                            Boolean approved = dataSnapshot.child("approved").getValue(Boolean
                                    .class);
                            if (approved == null)
                                approved = false;
                            if (approved) {
                                //yeee you are approved
                                message.setText("approved");
                            } else {
                                //yet not approved
                                message.setText("yet not approved");
                            }
                            message.setVisibility(View.VISIBLE);
                        } else {
                            //no such request
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Subscribe
    public void deleteRequest(DeleteRequestEvent deleteRequestEvent) {
        deleteRequest(deleteRequestEvent.getFamilyId());
    }

    @Subscribe
    public void setActiveFamily(FamilySetEvent familySetEvent) {
        if (!familySetEvent.getRequest().getBlocked() && familySetEvent.getRequest().getApproved
                ()) {
            Log.d(TAG, "change family:" + familySetEvent.getRequest().getFamilyId());
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("activeFamilyId",familySetEvent.getRequest().getFamilyId()).apply();
        } else {
            toast.setText(String.format("Cannot join %s right now!", familySetEvent.getRequest()
                    .getFamilyId()));
            toast.show();
            Log.d(TAG, "cannot join");
        }

    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
        if(evt.getKey().equals("activeFamilyId")){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
