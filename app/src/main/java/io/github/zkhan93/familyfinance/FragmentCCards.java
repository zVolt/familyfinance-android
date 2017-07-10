package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.net.Uri;
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
import io.github.zkhan93.familyfinance.adapters.CCardListAdapter;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Constants;
import io.github.zkhan93.familyfinance.viewholders.CCardVH;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCCards.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCCards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCCards extends Fragment implements CCardVH.ItemInteractionListener {

    public static final String TAG = FragmentCCards.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    //private String mParam1;
    //private String mParam2;

    private OnFragmentInteractionListener mListener;
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
    public static FragmentCCards newInstance() {
        FragmentCCards fragment = new FragmentCCards();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_ccards, container, false);
        ButterKnife.bind(this, rootView);
        cCardListAdapter = new CCardListAdapter((App) getActivity().getApplication(),
                FragmentCCards.this);
        ccardsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        ccardsList.setAdapter(cCardListAdapter);
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            //call the function inside the interface
        }
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
    public void copy(CCard cCard) {
        //TODO Copy the card data into clipboard
        Log.d(TAG, "copy: " + cCard.toString());
    }

    @Override
    public void delete(CCard cCard) {
        //TODO: delete the card from local database and sync the action to cloud(firebase
        // realtime database)
        Log.d(TAG, "delete: " + cCard.toString());
    }

    @Override
    public void share(CCard cCard) {
        //TODO: Fire a Intent with card details as plain text
        Log.d(TAG, "share: " + cCard.toString());
    }

    @Override
    public void edit(CCard cCard) {
        //TODO: Show edit dialog for updating a Card
        //TODO: notify adapter about this update
        Log.d(TAG, "edit: " + cCard.toString());
        cCard.setBank(cCard.getBank() + "X");
        cCard.update();
        cCardListAdapter.notifyItemChanged(cCard);

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
