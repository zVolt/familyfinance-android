package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.AccountListAdapter;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.util.Constants;
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
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";


//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private AccountListAdapter accountListAdapter;

    @BindView(R.id.list)
    RecyclerView accountsList;

    public FragmentAccounts() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentAccounts.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAccounts newInstance() {
        FragmentAccounts fragment = new FragmentAccounts();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_accounts, container, false);
        ButterKnife.bind(this, rootView);
        accountListAdapter = new AccountListAdapter(Constants.getDummyAccounts(),
                FragmentAccounts.this);
        accountsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext
                ()));
        accountsList.setAdapter(accountListAdapter);
        return rootView;
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
        Log.d(TAG, "copy: " + account.toString());
    }

    @Override
    public void delete(Account account) {
        Log.d(TAG, "delete: " + account.toString());
    }

    @Override
    public void share(Account account) {
        Log.d(TAG, "share: " + account.toString());
    }

    @Override
    public void edit(Account account) {
        Log.d(TAG, "edit: " + account.toString());
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
