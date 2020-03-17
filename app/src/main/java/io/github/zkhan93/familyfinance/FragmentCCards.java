package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.CCardListAdapter;
import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.CCardVH;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCCards.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCCards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCCards extends Fragment implements CCardVH.ItemInteractionListener,
        SearchView.OnQueryTextListener {

    public static final String TAG = FragmentCCards.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";
    //private static final String ARG_PARAM2 = "param2";

    //private String mParam1;
    private String familyId;
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
        inflater.inflate(R.menu.fragment_ccard, menu);
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
        DialogFragmentViewCard.newInstance(cCard, familyId).show(getFragmentManager(),
                DialogFragmentCcard.TAG);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {

    }

}
