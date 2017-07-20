package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.AccountListAdapter;
import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.viewholders.AccountVH;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAccounts.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAccounts#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAccounts extends Fragment implements AccountVH.ItemInteractionListener {

    public static final String TAG = FragmentAccounts.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familiyID";


    private String familyId;

    private OnFragmentInteractionListener mListener;
    private AccountListAdapter accountListAdapter;
    private String accountToDelete;
    private Toast toast;
    @BindView(R.id.list)
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        ButterKnife.bind(this, rootView);
        toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
        FirebaseDatabase.getInstance().getReference(".info/connected").addValueEventListener
                (connectionEventListener);
        accountListAdapter = new AccountListAdapter((App) getActivity().getApplication(), familyId,
                FragmentAccounts.this);
        accountsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext
                ()));
        accountsList.setAdapter(accountListAdapter);
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void copy(Account account) {
        //TODO Copy the Account data into clipboard
        Log.d(TAG, "copy: " + account.toString());
    }

    @Override
    public void delete(Account account) {
        accountToDelete = account.getAccountNumber();
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
    public void share(Account account) {
        Log.d(TAG, "share: " + account.toString());
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, account.getReadableContent());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string
                .action_share)));
    }

    @Override
    public void edit(Account account) {
        Log.d(TAG, "edit: " + account.toString());
        DialogFragmentAddAccount.newInstance(familyId, account).show(getFragmentManager(),
                DialogFragmentAddAccount.TAG);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

    }
}
