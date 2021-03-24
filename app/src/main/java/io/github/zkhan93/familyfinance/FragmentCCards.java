package io.github.zkhan93.familyfinance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.CCardListAdapter;
import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.FabHost;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.CCardVH;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCCards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCCards extends Fragment implements CCardVH.ItemInteractionListener,
        SearchView.OnQueryTextListener {

    public static final String TAG = FragmentCCards.class.getSimpleName();

    private static final String ARG_FAMILY_ID = "familyId";


    private String familyId;
    private CCardListAdapter cCardListAdapter;

    @BindView(R.id.list)
    RecyclerView ccardsList;

    public FragmentCCards() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentCCards.
     */
    public static FragmentCCards newInstance(String familyId) {
        FragmentCCards fragment = new FragmentCCards();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            familyId = bundle.getString(ARG_FAMILY_ID, null);
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
        View rootView = inflater.inflate(R.layout.fragment_ccards, container, false);
        ButterKnife.bind(this, rootView);
        cCardListAdapter = new CCardListAdapter((App) getActivity().getApplication(), familyId,
                FragmentCCards.this);
        ccardsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        ccardsList.setAdapter(cCardListAdapter);
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_cards, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        cCardListAdapter.registerForEvent();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        cCardListAdapter.unregisterForEvent();
    }

    @Override
    public void onResume() {
        super.onResume();
        Activity parentActivity = getActivity();
        if (parentActivity != null) {
            FabHost fab = (FabHost) parentActivity;
            if (fab != null)
                fab.showFab();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void delete(CCard cCard) {
        String title = "You want to delete account " + cCard.getNumber();
        DialogFragmentConfirm<CCard> dialogFragmentConfirm = new DialogFragmentConfirm<>();
        Bundle bundle = new Bundle();
        bundle.putString(DialogFragmentConfirm.ARG_TITLE, title);
        bundle.putParcelable(DialogFragmentConfirm.ARG_ITEM, cCard);
        dialogFragmentConfirm.setArguments(bundle);
        dialogFragmentConfirm.show(getActivity().getSupportFragmentManager(),
                DialogFragmentConfirm.TAG);
    }

    @Override
    public void edit(CCard cCard) {
        DialogFragmentCcard.newInstance(familyId, cCard).show(getFragmentManager(),
                DialogFragmentCcard.TAG);
    }

    @Override
    public void onView(CCard cCard) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("card", cCard);
        NavController navController = Navigation.findNavController(getActivity(),
                R.id.nav_host_fragment);
        navController.navigate(R.id.ccard_detail, bundle);
    }

    @Override
    public void addAddonCard(CCard cCard) {
        DialogFragmentAddonCard.newInstance(familyId, cCard.getNumber()).show(getFragmentManager(),
                DialogFragmentAddonCard.TAG);
    }

    @Override
    public void onLongPress(CCard cCard) {
        Util.quickCopy(getActivity().getApplicationContext(), cCard);
    }

    /**
     * Events fired from DialogFragmentConfirm
     */
    @Subscribe()
    public void deleteActiveCcardConfirmed(DeleteConfirmedEvent event) {
        if (event == null || event.getItem() == null) return;
        if (event.getItem() instanceof CCard) {
            CCard cCard = (CCard) event.getItem();
            ((App) getActivity().getApplication()).getDaoSession().getCCardDao().deleteByKey
                    (cCard.getNumber());
            cCardListAdapter.deleteCcard(cCard.getNumber());
        } else if (event.getItem() instanceof AddonCard) {
            AddonCard addonCard = (AddonCard) event.getItem();
            ((App) getActivity().getApplication()).getDaoSession().getAddonCardDao().deleteByKey
                    (addonCard.getNumber());
            cCardListAdapter.deleteCcard(addonCard.getNumber(), true);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.d(TAG, "saarch for: " + query);
        cCardListAdapter.onSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "search for: " + newText);
        cCardListAdapter.onSearch(newText);
        return true;
    }

}
