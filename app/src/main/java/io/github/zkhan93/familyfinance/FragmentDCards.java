package io.github.zkhan93.familyfinance;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.adapters.DCardListAdapter;
import io.github.zkhan93.familyfinance.events.DeleteConfirmedEvent;
import io.github.zkhan93.familyfinance.models.DCard;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.viewholders.DCardVH;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentDCards extends Fragment {

    public static final String TAG = FragmentDCards.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";

    private String familyId;
    private DCardListAdapter dCardListAdapter;
    private DatabaseReference baseCardRef;
    private DCardListAdapter.AdapterInteraction adapterInteraction;
    private ValueEventListener noContentImageUrlListener;
    private DCardVH.ItemInteractionListener cardInteractionListener;
    @BindView(R.id.list)
    RecyclerView dCardsList;

    @BindView(R.id.no_content)
    ImageView noContent;

    public FragmentDCards() {
        // Required empty public constructor
        adapterInteraction = () -> {
            if (dCardListAdapter.getItemCount() == 0) {
                Util.Log.d(TAG, "show blank image");
                noContent.setVisibility(View.VISIBLE);
                dCardsList.setVisibility(View.GONE);
            } else {
                Util.Log.d(TAG, "show list");
                noContent.setVisibility(View.GONE);
                dCardsList.setVisibility(View.VISIBLE);
            }
        };
        noContentImageUrlListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                if (url != null) {
                    Glide.with(noContent.getContext()).load(url).into(noContent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Util.Log.d(TAG, "loading of no content URL cancelled");
            }
        };
        cardInteractionListener = new DCardVH.ItemInteractionListener() {
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
        };
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
        Util.Log.d(TAG, "Count of Debit cards: %d", dCardListAdapter.getItemCount());
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
        View rootView = inflater.inflate(R.layout.fragment_dcard, container, false);
        ButterKnife.bind(this, rootView);
        baseCardRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("dcards")
                .child(familyId);
        dCardsList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        setupCardAdapter(baseCardRef);
        FirebaseDatabase.getInstance()
                .getReference()
                .child("images")
                .child("blank").child("ccard").addListenerForSingleValueEvent(noContentImageUrlListener);
        return rootView;
    }

    private void setupCardAdapter(Query query) {
        if (dCardListAdapter != null)
            dCardListAdapter.stopListening();
        FirebaseRecyclerOptions<DCard> options = new FirebaseRecyclerOptions.Builder<DCard>()
                .setQuery(query, DCard.class)
                .build();
        dCardListAdapter = new DCardListAdapter((App) getActivity().getApplicationContext(),
                cardInteractionListener, options, adapterInteraction);
        dCardsList.setAdapter(dCardListAdapter);
        dCardListAdapter.startListening();
    }

    /**
     * Events fired from DialogFragmentConfirm
     */
    @Subscribe()
    public void deleteActiveDcardConfirmed(DeleteConfirmedEvent event) {
        if (event == null || event.getItem() == null) return;
        if (event.getItem() instanceof DCard) {
            DCard dCard = (DCard) event.getItem();
            baseCardRef.child(dCard.getNumber()).setValue(null);
        }
    }
}
