package io.github.zkhan93.familyfinance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.Credential;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.models.MemberDao;
import io.github.zkhan93.familyfinance.models.Message;
import io.github.zkhan93.familyfinance.models.RequestDao;
import io.github.zkhan93.familyfinance.models.Wallet;
import io.github.zkhan93.familyfinance.util.Util;

public class SelectFamilyActivity extends AppCompatActivity {

    public static final String TAG = SelectFamilyActivity.class.getSimpleName();

    EditText edtTxtFamilyId;
    FloatingActionButton btnJoinFamily;
    Button btnCreateFamily;
    Button btnLogout;
    ProgressBar progressBar;
    TextView txtErrorMsg;
    TextView txtWelcome;

    private FirebaseDatabase fbDb;
    private DatabaseReference familyRef, membersRef, requestRef;
    private SharedPreferences sharedPref;
    private String familyId;
    private Member me;
    private MemberDao memberDao;
    private final Continuation<Void, Task<Void>> checkFamilyExistenceTask;
    private final Continuation<Void, Task<Integer>> checkForApprovedRequest;
    private final Continuation<Integer, Task<Integer>> createRequestTask;
    private final Continuation<Integer, Task<Integer>> fetchMembersTask;

    private final View.OnClickListener clickListener;

    private final int USER_REQ_APPROVED = 0;
    private final int USER_REQ_SUBMITTED = 1;
    private final int USER_REQ_NOT_FOUND = 2;

    public SelectFamilyActivity(){
        super();
        checkFamilyExistenceTask = task -> {
            // fetch the moderator ID to check is the familyID exists or not
            TaskCompletionSource<Void> tcs = new TaskCompletionSource<>();
            familyRef.child(familyId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String moderatorID =
                                    dataSnapshot.child("moderator").child("id").getValue(String.class);
                            if (moderatorID != null)
                                tcs.setResult(null);
                            else
                                tcs.setException(new Exception(getString(R.string.msg_family_id_not_exist)));
                            Util.Log.d(TAG, "complete: fetched the moderator ID to check the " +
                                    "existance of family %s", moderatorID);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Util.Log.d(TAG, "fail: fetched the moderator ID to check the " +
                                    "existence of family");
                            tcs.setException(databaseError.toException());
                        }
                    });
            return tcs.getTask();
        };
        checkForApprovedRequest = task -> {
            TaskCompletionSource<Integer> tcs = new TaskCompletionSource<>();
            if (!task.isSuccessful()) {
                Util.Log.d(TAG, "fail: previous task was not successful");
                Exception ex = task.getException() == null ?
                        new Exception(getString(R.string.msg_null_task)) : task.getException();
                tcs.setException(ex);
            } else {
                // look up requests to see if users has access to the family
                // Note: only moderator of family can write the requests.familyID.userID.approve or .block
                requestRef.child(familyId).child(me.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            tcs.setResult(USER_REQ_NOT_FOUND);
                        } else {
                            Boolean approved =
                                    dataSnapshot.child("approved").getValue(Boolean.class);
                            Boolean blocked = dataSnapshot.child("blocked").getValue(Boolean.class);
                            if (blocked != null && blocked) {
                                tcs.setException(new Exception("You have been blocked on this " +
                                        "family"));
                            } else if (approved != null && approved) {
                                tcs.setResult(USER_REQ_APPROVED);
                            } else {
                                tcs.setException(new Exception("Your request is pending for " +
                                        "approval"));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        tcs.setException(databaseError.toException());
                    }
                });
            }
            return tcs.getTask();
        };
        createRequestTask = task -> {
            TaskCompletionSource<Integer> tcs = new TaskCompletionSource<>();
            Util.Log.d(TAG, "task: submit a request");
            if (!task.isSuccessful()) {
                Util.Log.d(TAG, "fail: previous task was not successful");
                Exception ex = task.getException() == null ?
                        new Exception(getString(R.string.msg_null_task)) : task.getException();
                tcs.setException(ex);
            } else {
                // if the request was not found create it
                if (task.getResult() == USER_REQ_NOT_FOUND) {
                    Map<String, Object> updates = new HashMap<>();

                    String basepath = "requests/" + familyId + "/" + me.getId();
                    // add a request in request node for the moderator
                    updates.put(basepath + "/requestedOn", ServerValue.TIMESTAMP);
                    updates.put(basepath + "/updatedOn", ServerValue.TIMESTAMP);
                    updates.put(basepath + "/name", me.getName());
                    updates.put(basepath + "/email", me.getEmail());
                    updates.put(basepath + "/profilePic", me.getProfilePic());

                    basepath = "users/" + me.getId() + "/requests/" + familyId;

                    // add a request item in personal list under users/UID path
                    updates.put(basepath + "/familyId", familyId);
                    updates.put(basepath + "/requestedOn", ServerValue.TIMESTAMP);
                    updates.put(basepath + "/updatedOn", ServerValue.TIMESTAMP);
                    fbDb.getReference().updateChildren(updates,
                            (databaseError, databaseReference) -> {
                                if (databaseError == null) {
                                    // User's request to join the
                                    // family has been submitted
                                    // successfully
                                    tcs.setResult(USER_REQ_SUBMITTED);
                                } else {
                                    // User's request to join the
                                    // family failed
                                    tcs.setException(databaseError.toException());
                                }
                            });
                } else {
                    // otherwise forward the result of previous task to next task
                    tcs.setResult(task.getResult());
                }
            }
            return tcs.getTask();

        };
        fetchMembersTask = task -> {
            // fetching member list of familyID to check if the user is there in the list or not.
            TaskCompletionSource<Integer> tcs = new TaskCompletionSource<>();
            if (!task.isSuccessful()) {
                Util.Log.d(TAG, "fail: previous task was not successful");
                Exception ex = task.getException() == null ?
                        new Exception(getString(R.string.msg_null_task)) : task.getException();
                tcs.setException(ex);
            } else {
                if (task.getResult() == USER_REQ_APPROVED) {
                    // download and save members list
                    membersRef.child(familyId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Member member;
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        if (ds == null) continue;
                                        member = ds.getValue(Member.class);
                                        if (member != null) {
                                            member.setId(ds.getKey());
                                            Util.Log.d(TAG, "inserting member: %s",
                                                    member.toString());
                                            memberDao.insertOrReplace(member);
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Util.Log.d(TAG, "fail: fetched the members of the family to " +
                                            "check the user is a member or not");
                                    Util.Log.e(TAG, databaseError.getMessage());
                                }
                            });
                }
                tcs.setResult(task.getResult());
            }
            return tcs.getTask();
        };
        clickListener = view -> {
            showMessage("", false);
            familyId = edtTxtFamilyId.getText().toString().trim();
            switch (view.getId()) {
                case R.id.btn_join_family:
                    joinFamilyBtnAction();
                    break;
                case R.id.btn_create_family:
                    startFamilyBtnAction();
                    break;
                case R.id.btn_logout:
                    signOut();
                    break;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_family);

        edtTxtFamilyId = findViewById(R.id.edtxt_family_id);
        btnJoinFamily = findViewById(R.id.btn_join_family);
        btnCreateFamily = findViewById(R.id.btn_create_family);
        btnLogout = findViewById(R.id.btn_logout);
        progressBar = findViewById(R.id.progress_bar);
        txtErrorMsg = findViewById(R.id.txt_error_msg);
        txtWelcome = findViewById(R.id.txt_welcome);
        btnJoinFamily.setOnClickListener(clickListener);
        btnCreateFamily.setOnClickListener(clickListener);
        btnLogout.setOnClickListener(clickListener);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            toastText(getString(R.string.msg_user_no_logged_in));
            finish();
            return;
        }
        txtWelcome.setText(getString(R.string.welcome, fbUser.getDisplayName()));
        insertMeInLocalDb(fbUser);

        fbDb = FirebaseDatabase.getInstance();
        familyRef = fbDb.getReference("family");
        requestRef = fbDb.getReference("requests");
        membersRef = fbDb.getReference("members");

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

    }


    @Override
    protected void onStart() {
        super.onStart();
        checkActiveFamily();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void insertMeInLocalDb(@NonNull FirebaseUser fbUser) {
        memberDao = ((App) getApplication()).getDaoSession().getMemberDao();
        me = ((App) getApplication()).getDaoSession().getMemberDao().load(fbUser.getUid());
        if (me == null) {
            // logged in user is not in local db, insert it
            String photoUrl = null;
            if (fbUser.getPhotoUrl() != null)
                photoUrl = fbUser.getPhotoUrl().toString();
            me = new Member(fbUser.getUid(),
                    fbUser.getDisplayName(),
                    fbUser.getEmail(),
                    Calendar.getInstance().getTimeInMillis(),
                    false,
                    photoUrl);
            memberDao.insertOrReplace(me);
        }
    }

    /**
     * implements the following things
     * 1 - The FamilyID should exist - if not show him message to create a new family
     * 2 - User must be present in members list of family - if not then create a request and show
     * him a message
     */
    private void joinFamilyBtnAction() {

        setLoadingUi(true);
        Tasks.<Void>forResult(null)
                .continueWithTask(checkFamilyExistenceTask)
                .continueWithTask(checkForApprovedRequest)
                .continueWithTask(createRequestTask)
                .continueWithTask(fetchMembersTask)
                .addOnSuccessListener(status -> {
                    setLoadingUi(false);
                    if (status == USER_REQ_APPROVED) {
                        sharedPref.edit().putString(getString(R.string.pref_family_id), familyId).apply();
                        showMessage(getString(R.string.msg_request_approved));
                        startHomeActivity();
                    } else if (status == USER_REQ_SUBMITTED) {
                        showMessage(getString(R.string.msg_request_submitted));
                    }
                }).addOnFailureListener(exception -> {
            Util.Log.d(TAG, "fail: User is Member ?: %s", exception.getMessage());
            setLoadingUi(false);
            showMessage(exception.getLocalizedMessage(), true);
        });
    }

    /**
     * - family ID is unique else show error message
     * - create a new family with user and moderator
     * - add an approved request in new family from moderator
     * - add an request in users personal list of requests
     * - start the joinFamilyAction with new family
     */
    private void startFamilyBtnAction() {
        if (familyId.length() <= 5) {
            showMessage("At least 5 characters are required", true);
            return;
        }
        Map<String, Object> updates = new HashMap<>();

        // set moderator of the new family
        updates.put("family/" + familyId + "/moderator", me);

        // add an approved request under that family
        updates.put("requests/" + familyId + "/" + me.getId() + "/approved", true);
        updates.put("requests/" + familyId + "/" + me.getId() + "/name", me.getName());
        updates.put("requests/" + familyId + "/" + me.getId() + "/email", me.getEmail());
        updates.put("requests/" + familyId + "/" + me.getId() + "/profilePic", me.getProfilePic());
        updates.put("requests/" + familyId + "/" + me.getId() + "/requestedOn",
                ServerValue.TIMESTAMP);
        updates.put("requests/" + familyId + "/" + me.getId() + "/updatedOn",
                ServerValue.TIMESTAMP);

        // add a request to personal list of requests
        updates.put("users/" + me.getId() + "/requests/" + familyId + "/approved", true);
        updates.put("users/" + me.getId() + "/requests/" + familyId + "/familyId", familyId);
        updates.put("users/" + me.getId() + "/requests/" + familyId + "/requestedOn",
                ServerValue.TIMESTAMP);
        updates.put("users/" + me.getId() + "/requests/" + familyId + "/updatedOn",
                ServerValue.TIMESTAMP);

        // add myself to list of members
        updates.put("members/" + familyId + "/" + me.getId(), me);

        fbDb.getReference().updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        joinFamilyBtnAction();
                    } else
                        showMessage("Family ID is already in use! Try another one", true);
                });
    }

    private void startHomeActivity() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }

    private void toastText(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    private void setLoadingUi(boolean loading) {
        edtTxtFamilyId.setEnabled(!loading);
        btnCreateFamily.setEnabled(!loading);
        btnJoinFamily.setEnabled(!loading);
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void showMessage(String message) {
        showMessage(message, false);
    }

    private void showMessage(String message, boolean error) {
        if (error) {
            // handle style of error message
            edtTxtFamilyId.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.errorColor)));
            txtErrorMsg.setTextColor(getResources().getColor(R.color.textError));
        } else {
            edtTxtFamilyId.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            txtErrorMsg.setTextColor(getResources().getColor(R.color.textDark));
        }
        txtErrorMsg.setText(message);
    }


    /**
     * if a familyID is saved in preferences, initiate the join Family action, otherwise do nothing
     */
    private void checkActiveFamily() {
        familyId = sharedPref.getString(getString(R.string.pref_family_id), null);
        if (familyId == null) {
            // if no active familyID is set then let the user choose the family
            truncateLocalDb();
            return;
        }
        joinFamilyBtnAction();
    }

    private void truncateLocalDb() {
        Util.Log.d(TAG, "deleting local database");
        DaoSession daoSession = ((App) getApplication()).getDaoSession();

        daoSession.getMemberDao().queryBuilder().where(MemberDao.Properties.Id.notEq(me
                .getId())).buildDelete().executeDeleteWithoutDetachingEntities();
        daoSession.getRequestDao().queryBuilder().where(RequestDao.Properties.UserId.notEq(me
                .getId())).buildDelete().executeDeleteWithoutDetachingEntities();

        daoSession.deleteAll(CCard.class);
        daoSession.deleteAll(Account.class);
        daoSession.deleteAll(DCard.class);
        daoSession.deleteAll(AddonCard.class);
        daoSession.deleteAll(Credential.class);
        daoSession.deleteAll(Wallet.class);
        daoSession.deleteAll(Message.class);
    }

}
