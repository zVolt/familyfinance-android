package io.github.zkhan93.familyfinance;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.DCardListAdapter;
import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.viewholders.DCardVH;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDCards extends Fragment implements DCardVH.ItemInteractionListener,
        SearchView.OnQueryTextListener {

    public static final String TAG = FragmentDCards.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";

    private String familyId;
    private DCardListAdapter dCardListAdapter;
    private DatabaseReference dCardsRef;

    @BindView(R.id.list)
    RecyclerView dCardsList;

    public FragmentDCards() {
        // Required empty public constructor
    }

    public static FragmentDCards newInstance(String familyId) {
        FragmentDCards fragment = new FragmentDCards();
        Bundle args = new Bundle();
        args.putString(ARG_FAMILY_ID, familyId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            familyId = bundle.getString(ARG_FAMILY_ID, null);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        dCardListAdapter.startListening();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onStop() {
        super.onStop();
        dCardListAdapter.stopListening();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ccards, container, false);
        ButterKnife.bind(this, rootView);
        dCardsRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("dcards")
                .child(familyId);
        FirebaseRecyclerOptions<DCard> options = new FirebaseRecyclerOptions.Builder<DCard>()
                .setQuery(dCardsRef, DCard.class)
                .build();
        dCardListAdapter = new DCardListAdapter((App) getActivity().getApplication(),
                FragmentDCards.this, options);
        dCardsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        dCardsList.setAdapter(dCardListAdapter);
        setHasOptionsMenu(true);
        return rootView;
    }
    /**
     * Events fired from DialogFragmentConfirm
     */
    @Subscribe()
    public void deleteActiveDcardConfirmed(DeleteConfirmedEvent event) {
        if (event == null || event.getItem() == null) return;
        if (event.getItem() instanceof DCard) {
            DCard dCard = (DCard) event.getItem();
            dCardsRef.child(dCard.getNumber()).setValue(null);
        }
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void delete(DCard dCard) {
        String title = "You want to delete Debit Card " + dCard.getNumber();
        DialogFragmentConfirm<DCard> dialogFragmentConfirm = new DialogFragmentConfirm<>();
        Bundle bundle = new Bundle();
        bundle.putString(DialogFragmentConfirm.ARG_TITLE, title);
        bundle.putParcelable(DialogFragmentConfirm.ARG_ITEM, dCard);
        dialogFragmentConfirm.setArguments(bundle);
        dialogFragmentConfirm.show(getActivity().getSupportFragmentManager(),
                DialogFragmentConfirm.TAG);
    }

    @Override
    public void edit(DCard dCard) {
        DialogFragmentDcard.newInstance(familyId, dCard).show(getFragmentManager(),
                DialogFragmentDcard.TAG);
    }

    @Override
    public void onView(DCard dCard) {
        DialogFragmentViewDCard.newInstance(dCard, familyId).show(getFragmentManager(),
                DialogFragmentDcard.TAG);
    }

    @Override
    public void onCopyCardToClipboard(DCard dCard) {

    }
}
