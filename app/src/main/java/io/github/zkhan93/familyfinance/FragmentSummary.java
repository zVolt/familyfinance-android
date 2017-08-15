package io.github.zkhan93.familyfinance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.greendao.query.Query;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.zkhan93.familyfinance.models.Account;
import io.github.zkhan93.familyfinance.models.CCard;
import io.github.zkhan93.familyfinance.models.DaoSession;
import io.github.zkhan93.familyfinance.tasks.LoadFromDbTask;
import io.github.zkhan93.familyfinance.util.Constants;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSummary.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSummary#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSummary extends Fragment {
    public static final String TAG = FragmentSummary.class.getSimpleName();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FAMILY_ID = "familyId";

    @BindView(R.id.grand_total)
    public TextView grandTotal;
    @BindView(R.id.account_total)
    public TextView accountTotal;
    @BindView(R.id.consumed_cc_limit)
    public TextView consumedCCTotal;
    @BindView(R.id.remaining_cc_limit)
    public TextView remainingCCTotal;
    @BindView(R.id.account_title)
    public TextView accountTitle;
    @BindView(R.id.card_title)
    public TextView cardTitle;

    @BindView(R.id.next_payment1)
    public View nextPayment1;
    @BindView(R.id.next_payment2)
    public View nextPayment2;
    @BindView(R.id.next_payment3)
    public View nextPayment3;

    private String familyId;
    private OnFragmentInteractionListener mListener;
    private DaoSession daoSession;
    private float amountTotal, amountConsumedCC, amountRemainingCC, amountAccount;
    private List<CCard> cCards;
    private List<Account> accounts;
    private ChildEventListener ccardChildEventListener, accountChildEventListener;

    public FragmentSummary() {
        // Required empty public constructor
        accounts = new ArrayList<>();
        cCards = new ArrayList<>();
        ccardChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                CCard cCard = dataSnapshot.getValue(CCard.class);
                if (cCard == null) return;
                cCard.setNumber(dataSnapshot.getKey());
                cCards.add(cCard);
                recalculate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                CCard oldCard;
                CCard cCard = dataSnapshot.getValue(CCard.class);
                if (cCard == null) return;
                cCard.setNumber(dataSnapshot.getKey());
                ListIterator<CCard> itr = cCards.listIterator();
                while (itr.hasNext()) {
                    oldCard = itr.next();
                    if (oldCard.getNumber().equals(cCard.getNumber())) {
                        itr.set(cCard);
                        break;
                    }
                }
                recalculate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                CCard cCard = dataSnapshot.getValue(CCard.class);
                if (cCard == null) return;
                cCard.setNumber(dataSnapshot.getKey());
                CCard oldCard;
                ListIterator<CCard> itr = cCards.listIterator();
                while (itr.hasNext()) {
                    oldCard = itr.next();
                    if (oldCard.getNumber().equals(cCard.getNumber())) {
                        itr.remove();
                        break;
                    }
                }
                recalculate();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        accountChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Account account = dataSnapshot.getValue(Account.class);
                if (account == null) return;
                account.setAccountNumber(dataSnapshot.getKey());
                accounts.add(account);
                recalculate();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Account oldAccount;
                Account account = dataSnapshot.getValue(Account.class);
                if (account == null) return;
                account.setAccountNumber(dataSnapshot.getKey());
                ListIterator<Account> itr = accounts.listIterator();
                while (itr.hasNext()) {
                    oldAccount = itr.next();
                    if (oldAccount.getAccountNumber().equals(account.getAccountNumber())) {
                        itr.set(account);
                        break;
                    }
                }
                recalculate();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Account oldAccount;
                Account account = dataSnapshot.getValue(Account.class);
                if (account == null) return;
                account.setAccountNumber(dataSnapshot.getKey());
                ListIterator<Account> itr = accounts.listIterator();
                while (itr.hasNext()) {
                    oldAccount = itr.next();
                    if (oldAccount.getAccountNumber().equals(account.getAccountNumber())) {
                        itr.remove();
                        break;
                    }
                }
                recalculate();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentSummary.
     */

    public static FragmentSummary newInstance(String familyId) {
        FragmentSummary fragment = new FragmentSummary();
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
        View rootView = inflater.inflate(R.layout.fragment_summary, container, false);
        ButterKnife.bind(this, rootView);
        daoSession = ((App) getActivity().getApplication()).getDaoSession();
        if (savedInstanceState != null) {
            amountTotal = savedInstanceState.getFloat("amountTotal", amountTotal);
            amountAccount = savedInstanceState.getFloat("amountAccount", amountAccount);
            amountConsumedCC = savedInstanceState.getFloat("amountConsumedCC", amountConsumedCC);
            amountRemainingCC = savedInstanceState.getFloat("amountRemainingCC", amountRemainingCC);
            familyId = savedInstanceState.getString("familyId", familyId);
            consumedCCTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountConsumedCC));
            remainingCCTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountRemainingCC));
            accountTotal.setText(NumberFormat.getCurrencyInstance().format
                    (amountAccount));
            grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                    R.color.md_green_400 : R.color.md_red_400));
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        registerChildListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterChildListeners();
    }

    private void unregisterChildListeners() {
        FirebaseDatabase.getInstance().getReference("ccards").child(familyId)
                .removeEventListener(ccardChildEventListener);
        FirebaseDatabase.getInstance().getReference("accounts").child(familyId)
                .removeEventListener(accountChildEventListener);
        cCards.clear();
        accounts.clear();
    }

    private void registerChildListeners() {
        FirebaseDatabase.getInstance().getReference("ccards").child(familyId)
                .addChildEventListener(ccardChildEventListener);
        FirebaseDatabase.getInstance().getReference("accounts").child(familyId)
                .addChildEventListener(accountChildEventListener);
    }

    private void recalculate() {
        //for cards
        int cardCount = cCards.size();
        cardTitle.setText(String.format(Locale.ENGLISH, "%d Cards", cardCount));
        amountRemainingCC = 0;
        amountConsumedCC = 0;
        for (CCard cCard : cCards) {
            amountConsumedCC += cCard.getConsumedLimit();
            amountRemainingCC += cCard.getRemainingLimit();
        }
        consumedCCTotal.setText(NumberFormat.getCurrencyInstance().format
                (amountConsumedCC));
        remainingCCTotal.setText(NumberFormat.getCurrencyInstance().format
                (amountRemainingCC));
        amountTotal = amountAccount - amountConsumedCC;
        grandTotal.setText(NumberFormat.getCurrencyInstance().format(amountTotal));
        grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                R.color.md_green_400 : R.color.md_red_400));
        Collections.sort(cCards, CCard.BY_PAYMENT_DATE);
        View[] views = new View[]{
                nextPayment1,
                nextPayment2, nextPayment3
        };
        TextView txtView;
        CCard cCard;
        String str;
        for (int i = 0; i < views.length && i < cCards.size(); i++) {
            cCard = cCards.get(i);
            txtView = ButterKnife.findById(views[i], R.id.amount);
            txtView.setText(NumberFormat.getCurrencyInstance().format(cCard
                    .getConsumedLimit()));
            txtView = ButterKnife.findById(views[i], R.id.card_name);

            str = String.format("%s - %s", cCard.getCardholder(), cCard.getBank());
            txtView.setText(str);
            txtView = ButterKnife.findById(views[i], R.id.date);
            txtView.setText(Constants.PAYMENT_DATE.format(cCard.getPaymentDate()));
        }
        //for accounts
        int accountCount = accounts.size();
        amountAccount = 0;
        for (Account account : accounts) {
            amountAccount += account.getBalance();
        }
        accountTitle.setText(String.format(Locale.ENGLISH, "%d Accounts", accountCount));
        accountTotal.setText(NumberFormat.getCurrencyInstance().format
                (amountAccount));
        amountTotal = amountAccount - amountConsumedCC;
        grandTotal.setText(NumberFormat.getCurrencyInstance().format
                (amountTotal));
        grandTotal.setTextColor(ContextCompat.getColor(getActivity(), amountTotal >= 0 ?
                R.color.md_green_400 : R.color.md_red_400));

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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("amountTotal", amountTotal);
        outState.putFloat("amountAccount", amountAccount);
        outState.putFloat("amountConsumedCC", amountConsumedCC);
        outState.putFloat("amountRemainingCC", amountRemainingCC);
        outState.putString("familyId", familyId);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
