package io.github.zkhan93.familyfinance;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.OtpListAdapter;
import io.github.zkhan93.familyfinance.util.FabHost;
import io.github.zkhan93.familyfinance.util.InfiniteScrollListener;
import io.github.zkhan93.familyfinance.util.Util;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSms#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSms extends Fragment implements
        SearchView.OnQueryTextListener {
    public static final String TAG = FragmentSms.class.getSimpleName();

    private static final String ARG_FAMILY_ID = "familyId";

    private String familyId;
    private OtpListAdapter otpListAdapter;
    private InfiniteScrollListener infiniteScrollListener;

    @BindView(R.id.list)
    RecyclerView otpsList;

    {
        infiniteScrollListener = new InfiniteScrollListener(10) {
            @Override
            public boolean onLoadMore(int totalItemsCount) {
                return otpListAdapter.loadNextPage();
            }
        };
    }

    public FragmentSms() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSms.
     */

    public static FragmentSms newInstance(String familyId) {
        FragmentSms fragment = new FragmentSms();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyId = getArguments().getString(ARG_FAMILY_ID, null);
        }
        if (familyId == null) {
            familyId =
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ARG_FAMILY_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_otps, container, false);
        ButterKnife.bind(this, rootView);
        otpListAdapter = new OtpListAdapter((App) getActivity().getApplication(),
                familyId, null);
        otpsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        otpsList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration
                .VERTICAL));
        otpsList.setAdapter(otpListAdapter);

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity parentActivity = getActivity();
        if (parentActivity != null) {
            FabHost fab = (FabHost) parentActivity;
            if (fab != null)
                fab.hideFab();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        otpsList.addOnScrollListener(infiniteScrollListener);
        PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .registerOnSharedPreferenceChangeListener(otpListAdapter);
    }

    @Override
    public void onStop() {
        Util.Log.d(TAG, "onStop");
        super.onStop();
        otpsList.removeOnScrollListener(infiniteScrollListener);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (getActivity().getApplicationContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(otpListAdapter);
        sharedPreferences.edit().remove("filter_sms_by_member").apply();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_sms, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                //show dialog to set filter
                DialogFragmentSmsFilter.newInstance(familyId).show(getActivity()
                                .getSupportFragmentManager(),
                        DialogFragmentSmsFilter.TAG);
                return true;
            case R.id.action_search:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        otpListAdapter.filterByString(query.toLowerCase());
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        otpListAdapter.filterByString(newText.toLowerCase());
        return true;
    }

}
