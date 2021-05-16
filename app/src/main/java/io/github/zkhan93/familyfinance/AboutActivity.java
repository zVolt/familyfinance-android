package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.github.zkhan93.familyfinance.util.Util;

public class AboutActivity extends AppCompatActivity implements ValueEventListener {

    protected TextView title;
    protected TextView content;
    protected TextView versionInfo;


    public static final String TAG = AboutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        title = findViewById(R.id.additional_title);
        content = findViewById(R.id.additional_content);
        versionInfo = findViewById(R.id.version_number);

        Toolbar toolbar = findViewById(R.id.toolbar);
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
