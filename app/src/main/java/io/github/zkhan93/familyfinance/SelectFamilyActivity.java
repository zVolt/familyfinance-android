package io.github.zkhan93.familyfinance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.adapters.RequestListAdapter;
import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.Request;
import io.github.zkhan93.familyfinance.viewholders.RequestVH;

public class SelectFamilyActivity extends AppCompatActivity implements ValueEventListener,
        RequestVH.ItemInteractionListener {

    public static final String TAG = SelectFamilyActivity.class.getSimpleName();

    @BindView(R.id.family_id)
    EditText edtTxtFamilyId;
    @BindView(R.id.join_family)
    Button joinFamily;
    @BindView(R.id.start_new)
    Button startNew;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
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
        toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            Log.d(TAG, "user not logged in ");
            Toast.makeText(getApplicationContext(), "You are not logged in", Toast
                    .LENGTH_SHORT).show();
            finish();
            return;
        }
        me = ((App) getApplication()).getDaoSession().getMemberDao().load(fbUser.getUid());
        if (me == null)
            Log.d(TAG, "member not found in local db");
        progressDialog = new ProgressDialog(this);
        familyRef = FirebaseDatabase.getInstance().getReference("family");
        requestRef = FirebaseDatabase.getInstance().getReference("requests");

        requestListAdapter = new RequestListAdapter((App) getApplication(), me, this);
        requestList.setLayoutManager(new LinearLayoutManager(this));
        requestList.setAdapter(requestListAdapter);
        checkActiveFamily();
    }

    private void showMessageOnSnackBar(String message) {
        final Snackbar snackbar = Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    @OnClick({R.id.start_new, R.id.join_family})
    public void onClick(Button button) {
        familyId = edtTxtFamilyId.getText().toString().trim();
        switch (button.getId()) {
            case R.id.join_family:
                progressDialog.setMessage("Please wait verifying family ...");
                progressDialog.show();
                familyRef.child(familyId)
                        .addListenerForSingleValueEvent(this);
                Log.d(TAG, "joinFamily: " + familyId);
                break;
            case R.id.start_new:
                //todo start new from family
                Log.d(TAG, "startNew family " + familyId);
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

    /**
     * called after user clicks on send request button in UI
     * the function checks if the familyId provided in UI exists or not, if the family exists
     * then send a request to add this user to the family
     *
     * @param dataSnapshot
     */
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        String moderatorId = null;
        if (dataSnapshot != null)
            moderatorId = dataSnapshot.child("moderator").child("id").getValue(String.class);
        if (moderatorId == null) {
            showMessageOnSnackBar("Invalid Family Id");
        } else {
            if (moderatorId.equals(me.getId())) {
                //I am the moderator of this family log me in directly
                //passing a dummy request to pass the validation
                switchFamily(new Request(familyId, true, false, -1, -1));
                return;
            }
            //the family does exists then send a request the moderator is moderatorId
            long now = Calendar.getInstance().getTimeInMillis();
            Map<String, Object> updates = new HashMap<>();

            String partialNode = "requests/" + familyId + "/" + me.getId();
            //add a request in request node for the moderator
            updates.put(partialNode + "/requestedOn", now);
            updates.put(partialNode + "/updatedOn", now);
            updates.put(partialNode + "/name", me.getName());
            updates.put(partialNode + "/email", me.getEmail());
            updates.put(partialNode + "/profilePic", me.getProfilePic());

            partialNode = "users/" + me.getId() + "/requests/" + familyId;
            //add a request item in personal list unders users/Uid node
            updates.put(partialNode + "/familyId", familyId);
            updates.put(partialNode + "/requestedOn", now);
            updates.put(partialNode + "/updatedOn", now);


            FirebaseDatabase.getInstance().getReference().updateChildren(updates);
            showMessageOnSnackBar("Request send to moderator of the family");
        }
        progressDialog.hide();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(TAG, "cancelled");
    }


    /**
     * Check whether I am a moderator or an approved member of this familyId
     */
    public void checkActiveFamily() {
        familyId = PreferenceManager.getDefaultSharedPreferences(this).getString
                ("activeFamilyId", null);
        Log.d(TAG, "switching to: " + familyId);
        //if no active family is set then fail silently and let the user choose the family
        if (familyId == null)
            return;
        requestRef.child(familyId).child(me.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            //invalid family Id present in preferences
                            //TODO: delete the activeFamilyId preference
                            Log.d(TAG, "data does not exist");
                            return;
                        }

                        if (dataSnapshot.hasChild("blocked")) {
                            //You just got blocked :P Lol bad
                            //remove this blocked from firebase to unblock yourself
                            //Todo: remove activeFamilyId from preferences and show them requests
                            // list, also they are blocked from the family
                            Log.d(TAG, "you are blocked");

                        } else if (dataSnapshot.hasChild("approved")) {
                            Boolean approved = dataSnapshot.child("approved").getValue(Boolean
                                    .class);
                            if (approved == null)
                                approved = false;
                            if (approved) {
                                //yeee you are approved start MainActivity
                                startActivity(new Intent(SelectFamilyActivity.this, MainActivity
                                        .class));
                                finish();
                            } else {
                                //yet not approved do nothing
                                Log.d(TAG, "not approved yet");
                            }
                        } else {
                            //no such request
                            Log.d(TAG, "no such request");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(TAG, "request cancelled");
                        progressDialog.hide();
                    }
                });
    }

    @Override
    public void deleteRequest(Request request) {
        DialogFragmentConfirm<Request> dialogFragmentConfirm = new DialogFragmentConfirm<>();
        Bundle args = new Bundle();
        args.putString(DialogFragmentConfirm.ARG_TITLE, "Do you want to revoke request from " +
                request.getFamilyId());
        args.putParcelable(DialogFragmentConfirm.ARG_ITEM, request);
        dialogFragmentConfirm.setArguments(args);
        dialogFragmentConfirm.show(getSupportFragmentManager(), DialogFragmentConfirm.TAG);
    }

    @Override
    public void switchFamily(Request request) {
        if (!request.getBlocked() && request.getApproved()) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString
                    ("activeFamilyId", request.getFamilyId()).apply();
            checkActiveFamily();
        } else {
            toast.setText(String.format("Cannot join %s right now!", request.getFamilyId()));
            toast.show();
            Log.d(TAG, "cannot join");
        }
    }

    @Subscribe()
    public void confirmRequestDelete(DeleteConfirmedEvent<Request> deleteConfirmedEvent) {
        String familyId = deleteConfirmedEvent.getItem().getFamilyId();
        ((App) getApplication()).getDaoSession().getRequestDao().deleteByKey(deleteConfirmedEvent
                .getItem().getFamilyId());
        requestListAdapter.removeRequest(deleteConfirmedEvent.getItem());
        Map<String, Object> updates = new HashMap<>();
        updates.put("users/" + me.getId() + "/requests/" + familyId, null);
        updates.put("requests/" + familyId + "/" + me.getId(), null);
        FirebaseDatabase.getInstance().getReference().updateChildren(updates);
    }

}
