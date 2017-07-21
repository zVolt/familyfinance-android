package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.ReceiveRequestListAdapter;

public class AddMemberActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.list)
    RecyclerView requestList;

    private String familyId;
    private ReceiveRequestListAdapter receiveRequestListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        familyId = getIntent().getStringExtra("familyId");
        receiveRequestListAdapter = new ReceiveRequestListAdapter((App) getApplication(), familyId);
        requestList.setLayoutManager(new LinearLayoutManager(this));
        requestList.setAdapter(receiveRequestListAdapter);
    }

}
