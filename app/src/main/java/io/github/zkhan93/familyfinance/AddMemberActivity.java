package io.github.zkhan93.familyfinance;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.ReceiveRequestListAdapter;
import io.github.zkhan93.familyfinance.models.Request;
import io.github.zkhan93.familyfinance.viewholders.ReceiveRequestVH;

public class AddMemberActivity extends AppCompatActivity implements ReceiveRequestVH
        .ItemInteractionListener {

    public static final String TAG = AddMemberActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list)
    RecyclerView requestList;

    private String familyId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        familyId = getIntent().getStringExtra("familyId");
        ReceiveRequestListAdapter receiveRequestListAdapter = new ReceiveRequestListAdapter((App) getApplication(),
                familyId,
                this);
        requestList.setLayoutManager(new LinearLayoutManager(this));
        requestList.setAdapter(receiveRequestListAdapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait..");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showMessageOnSnackbar(String message) {
        if (message == null || message.trim().length() == 0) return;
        final Snackbar snackbar = Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).show();
    }

    @Override
    public void approve(final Request request) {
        Log.d(TAG, "approve" + request);
        progressDialog.show();
        Map<String, Object> updates = new HashMap<>();
        updates.put("requests/" + familyId + "/" + request.getUserId() + "/approved", true);
        updates.put("users/" + request.getUserId() + "/requests/" + familyId + "/approved", true);
        updates.put("members/" + familyId + "/" + request.getUserId(), request);
        FirebaseDatabase.getInstance().getReference().updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessageOnSnackbar(String.format("%s added to family", request
                                    .getName()));
                        }
                        progressDialog.hide();
                    }
                });
    }

    @Override
    public void block(final Request request) {
        Log.d(TAG, "block" + request);
        progressDialog.show();
        Map<String, Object> updates = new HashMap<>();
        updates.put("requests/" + familyId + "/" + request.getUserId() + "/blocked", true);
        updates.put("users/" + request.getUserId() + "/requests/" + familyId + "/blocked", true);
        updates.put("members/" + familyId + "/" + request.getUserId(), null);
        FirebaseDatabase.getInstance().getReference().updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessageOnSnackbar(String.format("%s blocked from family", request
                                    .getName()));
                        }
                        progressDialog.hide();
                    }
                });
    }

    @Override
    public void revoke(final Request request) {
        Log.d(TAG, "approve" + request);
        progressDialog.show();
        Map<String, Object> updates = new HashMap<>();
        updates.put("requests/" + familyId + "/" + request.getUserId() + "/approved", false);
        updates.put("users/" + request.getUserId() + "/requests/" + familyId + "/approved", false);
        updates.put("members/" + familyId + "/" + request.getUserId(), null);
        FirebaseDatabase.getInstance().getReference().updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessageOnSnackbar(String.format("%s's approval revoked", request
                                    .getName()));
                        }
                        progressDialog.hide();
                    }
                });
    }

    @Override
    public void unblock(final Request request) {
        Log.d(TAG, "unblock" + request);
        progressDialog.show();
        Map<String, Object> updates = new HashMap<>();
        updates.put("requests/" + familyId + "/" + request.getUserId() + "/blocked", false);
        updates.put("users/" + request.getUserId() + "/requests/" + familyId + "/blocked", false);
        updates.put("members/" + familyId + "/" + request.getUserId(), null);
        FirebaseDatabase.getInstance().getReference().updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showMessageOnSnackbar(String.format("%s unblocked", request
                                    .getName()));
                        }
                        progressDialog.hide();
                    }
                });

    }
}
