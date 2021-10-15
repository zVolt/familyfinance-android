package io.github.zkhan93.familyfinance;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.zkhan93.familyfinance.adapters.AccountListAdapter;
import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.AccountVH;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAccounts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAccounts extends Fragment implements AccountVH.ItemInteractionListener,
        SearchView.OnQueryTextListener {

    public static final String TAG = FragmentAccounts.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";


    private String familyId;
    private AccountListAdapter accountListAdapter;
    private Toast toast;
    RecyclerView accountsList;
    ValueEventListener connectionEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (snapshot == null)
                return;
            Boolean connected = snapshot.getValue(Boolean.class);
            if (connected != null && connected) {
                toast.cancel();
                toast.setText("connected");
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
            } else {
                toast.cancel();
                toast.setText("not connected");
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
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
        if(familyId==null){
            familyId = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(ARG_FAMILY_ID, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        accountsList = rootView.findViewById(R.id.list);
        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener
                (connectionEventListener);
        accountListAdapter = new AccountListAdapter((App) getActivity().getApplication(), familyId,
                FragmentAccounts.this);
        accountsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext
                ()));
        accountsList.setAdapter(accountListAdapter);
        setHasOptionsMenu(true);
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
        super.onStop();
        EventBus.getDefault().unregister(this);
        accountListAdapter.unregisterForEvents();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_cards, menu);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);
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
    public void onLongPress(Account account) {
        Util.quickCopy(getActivity().getApplicationContext(), account);
    }

    /**
     * Events fired from DialogFragmentConfirm
     */
    @Subscribe()
    public void deleteActiveAccountConfirmed(DeleteConfirmedEvent<Account> event) {
        if (event.getItem() != null) {
            ((App) getActivity().getApplication()).getDaoSession().getAccountDao().deleteByKey
                    (event.getItem().getAccountNumber());
            accountListAdapter.deleteAccount(event.getItem().getAccountNumber());
        }

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

}
