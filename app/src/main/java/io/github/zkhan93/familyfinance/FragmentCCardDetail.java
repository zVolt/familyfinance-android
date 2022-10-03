package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import io.github.zkhan93.familyfinance.adapters.AddonCardListAdapter;
import io.github.zkhan93.familyfinance.models.AddonCard;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.CCardDao;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.util.Util;
import io.github.zkhan93.familyfinance.vm.AppState;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCCardDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCCardDetail extends Fragment {

    public static String TAG = FragmentCCardDetail.class.getSimpleName();
    private static final String ARG_CARD = "card";

    private CCard card;

    RecyclerView cards;

    TextView phoneNumber;
    TextView billDate;
    TextView userID;

    TextView password;
    TextView cardLimit;


    private CCardDao cCardDao;
    private AppState appState;
    private String familyId;

    public FragmentCCardDetail() {
    }

    /**
     * @param card a credit card.
     * @return A new instance of fragment CCardDetailFragment.
     */
    public static FragmentCCardDetail newInstance(CCard card) {
        FragmentCCardDetail fragment = new FragmentCCardDetail();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD, card);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            card = getArguments().getParcelable(ARG_CARD);
        }
        appState = new ViewModelProvider(requireActivity()).get(AppState.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        cCardDao = ((App) context.getApplicationContext()).getDaoSession().getCCardDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_ccard_detail, container, false);
        cards = rootView.findViewById(R.id.cards);

        phoneNumber = rootView.findViewById(R.id.phone_number);
        billDate = rootView.findViewById(R.id.bill_date);
        userID = rootView.findViewById(R.id.userid);

        password = rootView.findViewById(R.id.password);
        cardLimit = rootView.findViewById(R.id.card_limit);

        Bundle bundle = getArguments();
        if (bundle != null) {
            card = bundle.getParcelable("card");
            if (card != null)
                card.__setDaoSession((DaoSession) cCardDao.getSession());
            familyId = bundle.getString("familyId");
        }
        setCardDetails();
        setUpCards();
        initFab();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        initFab();
    }

    private void initFab() {
        appState.enableFab(R.drawable.ic_add_white_24dp, TAG);
        appState.getFabAction().observe(getViewLifecycleOwner(), event -> {
            String id = event.getContentIfNotHandled();
            if (id != null && id.equals(TAG)){
                Util.Log.d(TAG, "addon add click for: %s", id);
                DialogFragmentAddonCard.newInstance(familyId, card.getNumber()).show(getParentFragmentManager(),
                        DialogFragmentCcard.TAG);
            }
        });
    }

    private void setUpCards() {
        List<CCard> cardItems = new ArrayList<CCard>();
        cardItems.add(card);
        for (AddonCard ac : card.getAddonCards()) {
            cardItems.add(new CCard(ac.getNumber(), ac.getName(), card.getBank(), ac.getName(),
                    null, null, String.valueOf(ac.getCvv()), ac.getPhoneNumber(),
                    null, card.getType(), 0, ac.getExpiresOn(), card.getPaymentDay(),
                    card.getBillingDay(), card.getMaxLimit(), card.getConsumedLimit(),
                    ac.getUpdatedByMemberId()));
        }
        AddonCardListAdapter adapter = new AddonCardListAdapter(null, cards);
        adapter.setItems(cardItems);
        cards.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        cards.setAdapter(adapter);
        SnapHelper snap = new PagerSnapHelper();
        snap.attachToRecyclerView(cards);
    }

    private void setCardDetails() {
        String UNSET = "Not Provided";
        String value = card.getPhoneNumber();
        if (value == null || value.isEmpty())
            value = UNSET;
        phoneNumber.setText(value);

        value = getNextBillDate();
        if (value == null || value.isEmpty())
            value = UNSET;
        billDate.setText(value);

        value = card.getUserid();
        if (value == null || value.isEmpty())
            value = UNSET;
        userID.setText(value);

        value = card.getPassword();
        if (value == null || value.isEmpty())
            value = UNSET;
        password.setText(value);

        value = NumberFormat.getCurrencyInstance().format(card.getMaxLimit());
        cardLimit.setText(value);
    }

    private String getNextBillDate() {
        if (card.getBillingDay() != -1) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, card.getBillingDay());
            if (cal.before(Calendar.getInstance().getTime())) {
                cal.add(Calendar.MONTH, 1);
            }
            return new SimpleDateFormat("dd MMM, yyyy", Locale.US).format(cal.getTime());
        }
        return null;
    }

}
