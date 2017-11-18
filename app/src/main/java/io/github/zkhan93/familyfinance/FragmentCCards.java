package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import io.github.zkhan93.familyfinance.viewholders.AddonCardVH;
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
        AddonCardVH.ItemInteractionListener, SearchView.OnQueryTextListener {

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
                FragmentCCards.this, this);
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
        switch (item.getItemId()) {
            case R.id.action_search:
                Log.d(TAG, "search something bro");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void copy(CCard cCard) {
        //TODO Copy the card data into clipboard
        Log.d(TAG, "copy: ");
    }

    @Override
    public void delete(CCard cCard) {
        Log.d(TAG, "delete: " + cCard.getNumber());
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
    public void share(CCard cCard) {
        Log.d(TAG, "share: " + cCard.getNumber());
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, cCard.getReadableContent());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string
                .action_share)));
    }

    @Override
    public void edit(CCard cCard) {
        Log.d(TAG, "edit: " + cCard.getNumber());
        DialogFragmentCcard.newInstance(familyId, cCard).show(getFragmentManager(),
                DialogFragmentCcard.TAG);
    }

    @Override
    public void onView(CCard cCard) {
        Log.d(TAG, "view: " + cCard.getNumber());
        DialogFragmentViewCard.newInstance(cCard, familyId).show(getFragmentManager(),
                DialogFragmentCcard.TAG);
    }

    @Override
    public void addAddonCard(CCard cCard) {
        Log.d(TAG, "addAddonCard: " + cCard.getNumber());
        DialogFragmentAddonCard.newInstance(familyId, cCard.getNumber()).show(getFragmentManager(),
                DialogFragmentAddonCard.TAG);
    }

    @Override
    public void delete(AddonCard addonCard) {
        Log.d(TAG, "delete addon" + addonCard.getNumber());
        String title = "You want to delete Addon Card " + addonCard.getNumber();
        DialogFragmentConfirm<AddonCard> dialogFragmentConfirm = new DialogFragmentConfirm<>();
        Bundle bundle = new Bundle();
        bundle.putString(DialogFragmentConfirm.ARG_TITLE, title);
        bundle.putParcelable(DialogFragmentConfirm.ARG_ITEM, addonCard);
        dialogFragmentConfirm.setArguments(bundle);
        dialogFragmentConfirm.show(getActivity().getSupportFragmentManager(),
                DialogFragmentConfirm.TAG);
    }

    @Override
    public void edit(AddonCard addonCard) {
        Log.d(TAG, "edit addon" + addonCard.getNumber());
        DialogFragmentAddonCard.newInstance(familyId, addonCard.getMainCardNumber(), addonCard)
                .show(getFragmentManager(),
                        DialogFragmentAddonCard.TAG);
    }

    @Override
    public void share(AddonCard addonCard) {
        Log.d(TAG, "share addon" + addonCard.getNumber());
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, addonCard.getReadableContent());
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getString(R.string
                .action_share)));
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
