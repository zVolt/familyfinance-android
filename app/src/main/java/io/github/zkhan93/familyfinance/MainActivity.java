package io.github.zkhan93.familyfinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        FragmentMembers.OnFragmentInteractionListener, FragmentOtps
        .OnFragmentInteractionListener, FragmentAccounts.OnFragmentInteractionListener,
        FragmentCCards.OnFragmentInteractionListener, FragmentSummary
        .OnFragmentInteractionListener {

    public static final String TAG = MainActivity.class.getSimpleName();


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
                        showFab();
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
                        message = "New account dialog";
                        break;
                    case PAGE_POSITION.CCARDS:
                        message = "New Ccard dialog";
                        break;
                    case PAGE_POSITION.MEMBERS:
                        message = "New member dialog";
                        break;
                    default:
                        message = "action not supported";
                }
                Snackbar.make(view, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
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
            switch (position) {
                default:
                    return FragmentSummary.newInstance();
                case 1:
                    return FragmentAccounts.newInstance();
                case 2:
                    return FragmentCCards.newInstance();
                case 3:
                    return FragmentOtps.newInstance();
                case 4:
                    return FragmentMembers.newInstance();
            }

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

    public interface PAGE_POSITION {
        int SUMMARY = 0;
        int ACCOUNTS = 1;
        int CCARDS = 2;
        int OTPS = 3;
        int MEMBERS = 4;

    }
}
