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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.github.zkhan93.familyfinance.adapters.MyFirebaseRecyclerAdapter;
import io.github.zkhan93.familyfinance.adapters.AccountListAdapterOld;
import io.github.zkhan93.familyfinance.events.DeleteEvent;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.util.ItemInteractionListener;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.vm.AppState;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAccounts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAccounts extends Fragment implements ItemInteractionListener<Account>,
        SearchView.OnQueryTextListener {

    public static final String TAG = FragmentAccounts.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";


    private String familyId;
    private AccountListAdapterOld accountListAdapter;
    RecyclerView accountsList;
    private AppState appState;
    ValueEventListener connectionEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot == null)
                return;
            Boolean connected = snapshot.getValue(Boolean.class);
            if (connected != null && connected) {
                Log.d(TAG, "connected");
            } else {
                Log.d(TAG, "not connected");
            }
        }

        @Override
        public void onCancelled(DatabaseError error) {
            Log.d(TAG, "Listener was cancelled");
        }
    };


    public FragmentAccounts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentAccounts.
     */
    public static FragmentAccounts newInstance(String familyId) {
        FragmentAccounts fragment = new FragmentAccounts();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            familyId = getArguments().getString(ARG_FAMILY_ID);
        }
        if (familyId == null) {
            familyId =
                    PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ARG_FAMILY_ID, null);
        }
        appState = new ViewModelProvider(requireActivity()).get(AppState.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        accountsList = rootView.findViewById(R.id.list);

        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener
                (connectionEventListener);
        accountListAdapter = new AccountListAdapterOld((App) getActivity().getApplication(), familyId,
                FragmentAccounts.this);
        accountsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext
                ()));
        accountsList.setAdapter(accountListAdapter);
        setHasOptionsMenu(true);
        initFab();
        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        accountListAdapter.registerForEvents();
    }


    @Override
    public void onStop() {
        accountListAdapter.unregisterForEvents();
        EventBus.getDefault().unregister(this);
        super.onStop();
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

    private void initFab() {
        appState.enableFab(R.drawable.ic_add_white_24dp, TAG);
        appState.getFabAction().observe(getViewLifecycleOwner(), event -> {
            String id = event.getContentIfNotHandled();
            Util.Log.d(TAG, "fab click for: %s", id);
            if (id != null && id.equals(TAG))
                DialogFragmentAddAccount.newInstance(familyId).show
                        (getParentFragmentManager(),
                                DialogFragmentAddAccount.TAG);
        });

    }

    @Override
    public void delete(Account account) {
        String title = "You want to delete account " + account.getAccountNumber();
        DialogFragmentConfirm<Account> dialogFragmentConfirm = new DialogFragmentConfirm<>();
        Bundle bundle = new Bundle();
        bundle.putString(DialogFragmentConfirm.ARG_TITLE, title);
        bundle.putParcelable(DialogFragmentConfirm.ARG_ITEM, account);
        dialogFragmentConfirm.setArguments(bundle);
        dialogFragmentConfirm.show(getActivity().getSupportFragmentManager(),
                DialogFragmentConfirm.TAG);
    }

    @Override
    public void edit(Account account) {
        Log.d(TAG, "edit: " + account.toString());
        DialogFragmentAddAccount.newInstance(familyId, account).show(getFragmentManager(),
                DialogFragmentAddAccount.TAG);
    }

    @Override
    public void view(Account account) {
        DialogFragmentViewAccount.newInstance(account, familyId).show(getFragmentManager(),
                DialogFragmentAddAccount.TAG);
    }

    @Override
    public void copyToClipboard(Account account) {
        Util.quickCopy(getActivity().getApplicationContext(), account);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        accountListAdapter.onSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        accountListAdapter.onSearch(newText);
        return true;
    }


    /**
     * Events fired from DialogFragmentConfirm
     */
    @Subscribe()
    public void deleteAccount(DeleteEvent<Account> event) {
        if (event.getItem() != null) {
            Account account = (Account)event.getItem();
            ((App) getActivity().getApplication()).getDaoSession().getAccountDao().deleteByKey
                    (account.getAccountNumber());
            accountListAdapter.deleteAccount(account.getAccountNumber());
        }
    }

    @Subscribe()
    public void createAccount(DeleteEvent<Account> event) {
        if (event.getItem() != null) {
            Account account = (Account)event.getItem();

        }
    }

    @Subscribe()
    public void updateAccount(DeleteEvent<Account> event) {
        if (event.getItem() != null) {
            Account account = (Account)event.getItem();

        }
    }


}
