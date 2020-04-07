package io.github.zkhan93.familyfinance;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.zkhan93.familyfinance.models.Member;
import io.github.zkhan93.familyfinance.util.Util;

import static io.github.zkhan93.familyfinance.FragmentMembers.PERMISSION_REQUEST_CODE;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = MainActivity.class.getSimpleName();
    public String familyId;
    public int PIN_CHECK_REQUEST_CODE = 435;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container)
    ViewPager mViewPager;

    @BindView(R.id.fab)
    FloatingActionButton fab;
    @Nullable
    @BindView(R.id.tabs)
    TabLayout tabLayout;

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */

    private ViewPager.OnPageChangeListener pageChangeListener;
    private int activePage;
    private String familyModeratorId;
    private Member me;
    private ValueEventListener keywordsListener, otpCharsListener, otpLengthListener;
    private int compilePatternAfterNoOfCallback;
    private SharedPreferences sharedPreferences;

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
                    case PAGE_POSITION.SMS:
                        hideFab();
                        break;
                    case PAGE_POSITION.MEMBERS:
                        if (familyModeratorId != null && me != null && me.getId() != null &&
                                familyModeratorId.equals(me.getId()))
                            showFab();
                        else hideFab();
                        break;
//                    case PAGE_POSITION.EMAILS:
//                        hideFab();
//                    case PAGE_POSITION.CHAT_ROOM:
//                        hideFab();
//                        break;
                    case PAGE_POSITION.CREDENTIALS:
                        showFab();
                        break;
                    case PAGE_POSITION.DCARDS:
                        showFab();
                        break;
//                    case PAGE_POSITION.WALLETS:
//                        showFab();
//                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        otpCharsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getChildrenCount() == 0) return;
                StringBuilder otpChars = new StringBuilder();
                String otpChar;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    otpChar = ds.getValue(String.class);
                    if (otpChar == null) continue;
                    otpChars.append(otpChar);
                }
                sharedPreferences.edit().putString("otpChars", otpChars.toString()).apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
            }
        };
        keywordsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getChildrenCount() == 0) return;
                Set<String> keywords = new HashSet<>();
                String keyword;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    keyword = ds.getValue(String.class);
                    if (keyword == null) continue;
                    keywords.add(keyword);
                }
                sharedPreferences.edit().putStringSet("keywords", keywords).apply();
                compilePatternAfterNoOfCallback -= 1;
                tryCompilePattern();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        otpLengthListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getChildrenCount() == 0) return;
                StringBuilder lengths = new StringBuilder();
                Long length;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    length = ds.getValue(Long.class);
                    if (length == null) continue;
                    lengths.append(length);
                    lengths.append(",");
                }
                if (lengths.length() > 0)
                    lengths.deleteCharAt(lengths.length() - 1);
                sharedPreferences.edit().putString("otpLengths", lengths.toString()).apply();
                compilePatternAfterNoOfCallback -= 1;
                tryCompilePattern();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter
                (getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        hideFab();
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(pageChangeListener);
        if (tabLayout != null)
            tabLayout.setupWithViewPager(mViewPager);
        Intent intent = getIntent();
        if (intent != null) {
            int currentFragment = intent.getIntExtra("FragmentPosition", 0);
            mViewPager.setCurrentItem(currentFragment);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needsPinVerification()) {
            startActivityForResult(new Intent(PinActivity.ACTIONS.CHECK_PIN, null, this, PinActivity
                    .class), PIN_CHECK_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        familyId = sharedPreferences.getString(getString(R.string.pref_family_id), null);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        compilePatternAfterNoOfCallback = 2;
        //TODO: download the a json from https://gist.githubusercontent.com/zkhan93/500fc1fbcbd00724f8e856c6d0dac702/raw/card_brands.json as save it in preference as a string
        ref.child("otpChars").addListenerForSingleValueEvent
                (otpCharsListener);
        ref.child("otpLength").addListenerForSingleValueEvent
                (otpLengthListener);
        ref.child("keywords").addListenerForSingleValueEvent
                (keywordsListener);
        ref.child("family").child(familyId).child("moderator")
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
            return;
        }
        String photoUrl = null;
        if (user.getPhotoUrl() != null)
            photoUrl = user.getPhotoUrl().toString();
        me = ((App) getApplication()).getDaoSession().getMemberDao().load(user.getUid());
        if (me == null) {
            me = new Member(user.getUid(),
                    user.getDisplayName(),
                    user.getEmail(),
                    Calendar.getInstance().getTimeInMillis(),
                    false,
                    photoUrl);
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
    protected void onPause() {
        super.onPause();
        if (verified) {

            PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("lastActive",
                    Calendar
                            .getInstance().getTimeInMillis()).apply();
            verified = !verified;
        }
    }

    private boolean needsPinVerification() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Long lastActive = sharedPreferences.getLong("lastActive", -1);
        boolean pinEnabled = sharedPreferences.getBoolean(getString(R.string.pref_key_pin), false);
        //default 10 sec min in setting
        long seconds = 10;
        try {
            String tmp = sharedPreferences.getString(getString(R.string.pref_key_autolock), "10");
            seconds = Integer.parseInt(tmp);

        } catch (ClassCastException ex) {
            Log.e(TAG, "cannot cast seconds: " + sharedPreferences.getString
                    (getString(R.string.pref_key_autolock), ""));
        }
        return pinEnabled &&
                (lastActive == -1 || lastActive < Calendar.getInstance().getTimeInMillis() -
                        seconds * 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
                        (getString(R.string.pref_family_id)).apply();
                startActivity(new Intent(this, SelectFamilyActivity.class));
                finish();
                return true;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
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
                    case PAGE_POSITION.DCARDS:
                        DialogFragmentDcard.newInstance(familyId).show(getSupportFragmentManager
                                (), DialogFragmentCcard.TAG);
                        break;
//                    case PAGE_POSITION.WALLETS:
//                        DialogFragmentDcard.newInstance(familyId).show(getSupportFragmentManager
//                                (), DialogFragmentCcard.TAG);
//                        break;
                    case PAGE_POSITION.MEMBERS:
                        Intent intent = new Intent(this, AddMemberActivity.class);
                        intent.putExtra(getString(R.string.pref_family_id), familyId);
                        startActivity(intent);
                        break;
                    case PAGE_POSITION.CREDENTIALS:
                        DialogFragmentCredential.getInstance(null, familyId)
                                .show(getSupportFragmentManager(), DialogFragmentViewCard.TAG);
                    default:
                        break;
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
                case PAGE_POSITION.ACCOUNTS:
                    fragment = FragmentAccounts.newInstance(familyId);
                    break;
                case PAGE_POSITION.CCARDS:
                    fragment = FragmentCCards.newInstance(familyId);
                    break;
                case PAGE_POSITION.SMS:
                    fragment = FragmentSms.newInstance(familyId);
                    break;
//                case PAGE_POSITION.EMAILS:
//                    fragment = FragmentEmails.newInstance(familyId);
//                    break;
                case PAGE_POSITION.MEMBERS:
                    fragment = FragmentMembers.newInstance(familyId, familyModeratorId);
                    break;
//                case PAGE_POSITION.CHAT_ROOM:
//                    fragment = FragmentChatroom.newInstance(familyId);
//                    break;
                case PAGE_POSITION.CREDENTIALS:
                    fragment = FragmentCredentials.newInstance(familyId);
                    break;
                case PAGE_POSITION.DCARDS:
                    fragment = FragmentDCards.newInstance(familyId);
                    break;
//                case PAGE_POSITION.WALLETS:
//                    fragment = FragmentWallets.newInstance(familyId);
//                    break;
                default: //0 or other
                    fragment = FragmentSummary.newInstance(familyId);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return PAGE_POSITION.class.getFields().length;
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
                case PAGE_POSITION.SMS:
                    return getString(R.string.title_sms);
                case PAGE_POSITION.MEMBERS:
                    return getString(R.string.title_members);
//                case PAGE_POSITION.EMAILS:
//                    return getString(R.string.title_emails);
//                case PAGE_POSITION.CHAT_ROOM:
//                    return getString(R.string.title_chat_room);
                case PAGE_POSITION.CREDENTIALS:
                    return getString(R.string.title_credentials);
                case PAGE_POSITION.DCARDS:
                    return getString(R.string.title_dcards);
//                case PAGE_POSITION.WALLETS:
//                    return getString(R.string.title_wallets);
            }
            return null;
        }


    }

    public interface PAGE_POSITION {
        int SUMMARY = 0;
        int CCARDS = 1;
        int SMS = 2;
        int DCARDS = 3;
        //        int WALLETS = 4;
        int CREDENTIALS = 4;
        int ACCOUNTS = 5;
        //        int EMAILS = 7;
        int MEMBERS = 6;
//      int CHAT_ROOM = 9;
    }

    private boolean verified = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PIN_CHECK_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                verified = true;
                PreferenceManager.getDefaultSharedPreferences(this).edit().putLong("lastActive",
                        Calendar
                                .getInstance().getTimeInMillis()).apply();
            } else {
                finish();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void tryCompilePattern() {
        if (compilePatternAfterNoOfCallback == 0) {
            Util.readOtpRegexValuesAndCompilePattern(getApplicationContext());
        }
    }
}
