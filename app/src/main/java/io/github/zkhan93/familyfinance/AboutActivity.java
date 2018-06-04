package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.util.Util;

public class AboutActivity extends AppCompatActivity implements ValueEventListener {

    @BindView(R.id.additional_title)
    protected TextView title;
    @BindView(R.id.additional_content)
    protected TextView content;
    @BindView(R.id.version_number)
    protected TextView versionInfo;


    public static final String TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        loadContent();
        String version = String.format(Locale.getDefault(), "Code: %d \nName: %s", BuildConfig.VERSION_CODE,
                BuildConfig.VERSION_NAME);

        versionInfo.setText((version));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadContent();
    }

    private void loadContent() {
        FirebaseDatabase.getInstance().getReference("about").child("0")
                .addListenerForSingleValueEvent(this);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if (dataSnapshot == null || !dataSnapshot.exists()) {
            return;
        }
        if (dataSnapshot.child("title").exists()) {
            String titleTxt = dataSnapshot.child("title").getValue(String.class);
            if (titleTxt != null)
                title.setText(titleTxt);
        }
        if (dataSnapshot.child("content").exists()) {
            String contentTxt = dataSnapshot.child("content").getValue(String.class);
            if (contentTxt != null)
                content.setText(contentTxt);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Util.Log.d(TAG, "fetching content cancelled %s", databaseError.getMessage());
    }
}
