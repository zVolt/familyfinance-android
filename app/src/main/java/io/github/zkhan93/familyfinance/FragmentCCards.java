package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.github.zkhan93.familyfinance.adapters.CCardListAdapter;
import io.github.zkhan93.familyfinance.events.ConfirmDeleteEvent;
import io.github.zkhan93.familyfinance.events.CreateEvent;
import io.github.zkhan93.familyfinance.events.DeleteEvent;
import io.github.zkhan93.familyfinance.events.UpdateEvent;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.ItemInteractionListener;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.vm.AppState;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCCards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCCards extends Fragment implements ItemInteractionListener<CCard>,
        SearchView.OnQueryTextListener {

    public static final String TAG = FragmentCCards.class.getSimpleName();

    private static final String ARG_FAMILY_ID = "familyId";

    private String familyId;
    private CCardListAdapter cCardListAdapter;
    AppState appState;
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
                    PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(ARG_FAMILY_ID, null);
        }
        appState = new ViewModelProvider(requireActivity()).get(AppState.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ccards, container, false);
        ccardsList = rootView.findViewById(R.id.list);
        cCardListAdapter = new CCardListAdapter((App) requireActivity().getApplication(), familyId,
                FragmentCCards.this);
        ccardsList.setLayoutManager(new LinearLayoutManager(requireActivity().getApplicationContext()));
        ccardsList.setAdapter(cCardListAdapter);
        setHasOptionsMenu(true);
        initFab();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initFab();
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


    private void initFab() {
        appState.enableFab(R.drawable.ic_add_white_24dp, TAG);
        appState.getFabAction().observe(getViewLifecycleOwner(), event -> {

            String id = event.getContentIfNotHandled();
            Util.Log.d(TAG, "fab click for: %s", id);
            if (id != null && id.equals(TAG))
                DialogFragmentCcard.newInstance(familyId).show(getParentFragmentManager(),
                        DialogFragmentCcard.TAG);
        });
    }

    @Override
    public void delete(CCard cCard) {
        String title = "You want to delete Credit Card " + cCard.getNumber();
        DialogFragmentConfirm<CCard> dialogFragmentConfirm = new DialogFragmentConfirm<>();
        Bundle bundle = new Bundle();
        bundle.putString(DialogFragmentConfirm.ARG_TITLE, title);
        bundle.putParcelable(DialogFragmentConfirm.ARG_ITEM, cCard);
        dialogFragmentConfirm.setArguments(bundle);
        dialogFragmentConfirm.show(requireActivity().getSupportFragmentManager(),
                DialogFragmentConfirm.TAG);
    }

    @Override
    public void edit(CCard cCard) {
        DialogFragmentCcard.newInstance(familyId, cCard).show(getParentFragmentManager(),
                DialogFragmentCcard.TAG);
    }

    @Override
    public void view(CCard cCard) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("card", cCard);
        bundle.putString("familyId", familyId);
        NavController navController = Navigation.findNavController(requireActivity(),
                R.id.nav_host_fragment);
        navController.navigate(R.id.ccard_detail, bundle);
    }

    //TODO: call this from relevant place
    public void addAddonCard(CCard cCard) {
        DialogFragmentAddonCard.newInstance(familyId, cCard.getNumber()).show(getParentFragmentManager(),
                DialogFragmentAddonCard.TAG);
    }

    @Override
    public void copyToClipboard(CCard cCard) {
        Util.quickCopy(requireActivity().getApplicationContext(), cCard);
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

    /**
     * Events fired from DialogFragmentConfirm
     */
    @Subscribe()
    public void deleteCCard(DeleteEvent event) {
        if (event == null || event.getItem() == null) return;
        if (event.getItem() instanceof CCard) {
            CCard cCard = (CCard) event.getItem();
            ((App) requireActivity().getApplication()).getDaoSession().getCCardDao().deleteByKey
                    (cCard.getNumber());
            cCardListAdapter.deleteCcard(cCard.getNumber());
        } else if (event.getItem() instanceof AddonCard) {
            AddonCard addonCard = (AddonCard) event.getItem();
            ((App) requireActivity().getApplication()).getDaoSession().getAddonCardDao().deleteByKey
                    (addonCard.getNumber());
            cCardListAdapter.deleteCcard(addonCard.getNumber(), true);
        }
    }
    @Subscribe()
    public void confirmDelete(ConfirmDeleteEvent event) {
        if (event == null || event.getItem() == null) return;
        if (event.getItem() instanceof CCard) {
            CCard card = (CCard) event.getItem();
            delete(card);
        }
    }
    @Subscribe()
    public void createCCard(CreateEvent event) {
        if (event == null || event.getItem() == null) return;
        if (event.getItem() instanceof CCard) {
            CCard card = (CCard) event.getItem();
            FirebaseDatabase.getInstance()
                    .getReference("ccards")
                    .child(familyId)
                    .child(card.getNumber())
                    .setValue(card);
        }
    }
    @Subscribe()
    public void updateCCard(UpdateEvent event) {
        if (event == null || event.getItem() == null) return;
        if (event.getItem() instanceof CCard) {
            CCard card = (CCard) event.getItem();
            FirebaseDatabase.getInstance()
                    .getReference("ccards")
                    .child(familyId)
                    .child(card.getNumber())
                    .setValue(card);
        }
    }
}
