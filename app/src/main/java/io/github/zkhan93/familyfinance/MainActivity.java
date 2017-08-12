package io.github.zkhan93.familyfinance;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.models.Member;

import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static io.github.zkhan93.familyfinance.FragmentMembers.PERMISSION_REQUEST_CODE;

public class MainActivity extends AppCompatActivity implements
        FragmentMembers.OnFragmentInteractionListener, FragmentOtps
        .OnFragmentInteractionListener, FragmentAccounts.OnFragmentInteractionListener,
        FragmentCCards.OnFragmentInteractionListener, FragmentSummary
        .OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();
    public String familyId;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager.OnPageChangeListener pageChangeListener;
    private int activePage;
    private String familyModeratorId;
    private Member me;

    {
        activePage = PAGE_POSITION.SUMMARY;
        pageChangeListener = new ViewPager
                .OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                activePage = position;
                switch (position) {
                    case PAGE_POSITION.SUMMARY:
                        hideFab();
                        break;
                    case PAGE_POSITION.ACCOUNTS:
                        showFab();
                        break;
                    case PAGE_POSITION.CCARDS:
                        showFab();
                        break;
                    case PAGE_POSITION.OTPS:
                        hideFab();
                        break;
                    case PAGE_POSITION.MEMBERS:
                        if (familyModeratorId != null && me != null && me.getId() != null &&
                                familyModeratorId.equals(me.getId()))
                            showFab();
                        else hideFab();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        hideFab();
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(pageChangeListener);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        familyId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("activeFamilyId", null);
        FirebaseDatabase.getInstance().getReference("family").child(familyId).child("moderator")
                .child("id").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) return;
                familyModeratorId = dataSnapshot.getValue(String.class);
                if (familyModeratorId == null) return;
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                        .putString("familyModeratorId", familyModeratorId).apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (familyId == null || user == null) {
            startActivity(new Intent(this, SelectFamilyActivity.class));
            finish();
        }
        me = ((App) getApplication()).getDaoSession().getMemberDao().load(user.getUid());
        if (me == null) {
            me = new Member(user.getUid(), user.getDisplayName(), user.getEmail(), Calendar
                    .getInstance().getTimeInMillis(), false, user
                    .getPhotoUrl().toString());
            ((App) getApplication()).getDaoSession().getMemberDao().insertOrReplace(me);
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest
                .permission.RECEIVE_SMS) & ContextCompat.checkSelfPermission(this, android.Manifest
                .permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest
                    .permission.RECEIVE_SMS)) {
                //explain the need of this permission
                //todo show a dialog and then on positive show request permission
                Log.d(TAG, "lol we need it :D");
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission
                        .READ_PHONE_STATE
                }, PERMISSION_REQUEST_CODE);

            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.RECEIVE_SMS, android.Manifest.permission
                        .READ_PHONE_STATE
                }, PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.action_logout:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // user is now signed out
                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                return true;
            case R.id.action_switch_family:
                PreferenceManager.getDefaultSharedPreferences(this).edit().remove
                        ("activeFamilyId").apply();
                startActivity(new Intent(this, SelectFamilyActivity.class));
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab)
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                String message;
                switch (activePage) {
                    case PAGE_POSITION.ACCOUNTS:
                        DialogFragmentAddAccount.newInstance(familyId).show
                                (getSupportFragmentManager(),
                                        DialogFragmentAddAccount.TAG);
                        break;
                    case PAGE_POSITION.CCARDS:
                        DialogFragmentCcard.newInstance(familyId).show(getSupportFragmentManager
                                (), DialogFragmentCcard.TAG);
                        break;
                    case PAGE_POSITION.MEMBERS:
                        Intent intent = new Intent(this, AddMemberActivity.class);
                        intent.putExtra("familyId", familyId);
                        startActivity(intent);
                        break;
                    default:

                }

                break;
            default:
                Log.d(TAG, "action not implemented");
        }
    }

    public void showFab() {
        if (fab != null)
            fab.show();
    }

    public void hideFab() {
        if (fab != null)
            fab.hide();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Fragment fragment;
            switch (position) {
                case 1:
                    fragment = FragmentAccounts.newInstance(familyId);
                    break;
                case 2:
                    fragment = FragmentCCards.newInstance(familyId);
                    break;
                case 3:
                    fragment = FragmentOtps.newInstance(familyId);
                    break;
                case 4:
                    fragment = FragmentMembers.newInstance(familyId, familyModeratorId);
                    break;
                default: //0 or other
                    fragment = FragmentSummary.newInstance(familyId);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 5 total pages.
            return 5;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case PAGE_POSITION.SUMMARY:
                    return getString(R.string.title_summary);
                case PAGE_POSITION.ACCOUNTS:
                    return getString(R.string.title_accounts);
                case PAGE_POSITION.CCARDS:
                    return getString(R.string.title_ccards);
                case PAGE_POSITION.OTPS:
                    return getString(R.string.title_otps);
                case PAGE_POSITION.MEMBERS:
                    return getString(R.string.title_members);
            }
            return null;
        }


    }

    private interface PAGE_POSITION {
        int SUMMARY = 0;
        int ACCOUNTS = 1;
        int CCARDS = 2;
        int OTPS = 3;
        int MEMBERS = 4;

    }
}
